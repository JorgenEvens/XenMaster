/*
 * websocket.js
 * Copyright (C) 2011,2012 Jorgen Evens
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

/*
 * websocket.js
 * Copyright (C) 2011,2012 Jorgen Evens
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

(function( ready ){
	
	var Socket = function( options ) {
		options = options || {};
		
		if( !options.address ) {
			throw 'Invalid address specified for this WebSocket';
		}

		/*
		 * We already have a connection to this host.
		 */
		if( Socket.sockets[ options.address ] ) {
			return Socket.sockets[ options.address ];
		};
		
		/*
		 * Basic connection configuration
		 */
		this.address = options.address;
		this.secure = options.secure ? options.secure : false;
		this.connected = false;
		this.protocol = options.protocol;
		
		/*
		 * Commands that have been sent and are waiting for the result.
		 */
		this.sent = {};
		
		/*
		 * Hooks to messages sent from the backend.
		 */
		this.hooks = {};
	},
	UUIDGen = function() {
		  var s = [], itoh = '0123456789ABCDEF';
		 
		  /*
		   * Make array of random hex digits. The UUID only has 32 digits in it, but we
		   * allocate extra items to make room for the '-'s we'll be inserting.
		   */
		  for (var i = 0; i <36; i++) s[i] = Math.floor(Math.random()*0x10);
		 
		  // Conform to RFC-4122, section 4.4
		  s[14] = 4;  // Set 4 high bits of time_high field to version
		  s[19] = (s[19] & 0x3) | 0x8;  // Specify 2 high bits of clock sequence
		 
		  // Convert to hex chars
		  for (var i = 0; i <36; i++) s[i] = itoh[s[i]];
		 
		  // Insert '-'s
		  s[8] = s[13] = s[18] = s[23] = '-';
		 
		  return s.join('');
	};
	
	/*
	 * Open the connection
	 * Trying to fight the race condition in WebSocket API.
	 */
	Socket.prototype.open = function() {
		if( this.connected ) {
			return true;
		}
		
		var me = this;
		
		try {
			this.socket = new WebSocket( ( this.secure ? 'wss' : 'ws' ) + '://' + this.address, this.protocol );
			this.socket.onopen = function() {
				me.connected = true; me.onopen();
			};
			this.socket.onerror = function(e) { me.onerror(e); };
			this.socket.onclose = function() { me.onclose(); };
			this.socket.onmessage = function(e) { me.onmessage(e); };
			
			Socket.sockets[ this.address ] = this;
		} catch( e ) {
			return false;
		}
		return true;
	};
	
	/*
	 * Basic handlers
	 */
	Socket.prototype.onopen = function() {};
	
	Socket.prototype.onerror = function( e ) {
		throw 'Error on websocket (' + this.address + '): ' + e.data;
	};
	
	Socket.prototype.onclose = function() {
		this.connected = false;
		if( !this.open() ) {
			this.onerror({data: 'Could not reopen connection with websocket'});
		}
	};
	
	/*
	 * Handle messages that come in over the socket.
	 */
	Socket.prototype.onmessage = function( e ) {
		var data = window.JSON ? JSON.parse( e.data ) : eval( '(' + e.data + ')' );

		if( data.name && data.handler && typeof this.oncommand == 'function' ) {
			this.oncommand( data );
		}
		
		if( data.type && data.result && typeof this.onresult == 'function' ) {
			this.onresult( data );
		}
	};
	
	/*
	 * Handle commands coming in from the backend
	 */
	Socket.prototype.oncommand = function( data ) {
		var hook_list = this.hooks[ data.handler ],
			i = null;
		
		if( !hook_list ) {
			return;
		}
		
		hook_list = hook_list[ data.name ];
		if( !hook_list ) {
			return;
		}
		
		for( i in hook_list ) {
			hook_list[i]( data.data );
		}
	};
	
	/*
	 * Handle results coming in from the backend
	 */	
	Socket.prototype.onresult = function( data ) {
		var callback = this.sent[ data.tag ];
		
		delete this.sent[ data.tag ];

		if( callback ) {
			callback( data );
		};
	};
	
	/*
	 * Add a hook that handles messages broadcasted by the backend.
	 */
	Socket.prototype.addHook = function( handler, commandName, callback ) {		
		if( !this.hooks[ handler ] ) {
			this.hooks[ handler ] = [];
		}
		handler = this.hooks[ handler ];
		
		if( !handler[ commandName ] ) {
			handler[ commandName ] = [];
		}
		
		handler[ commandName ].push( callback );
	};
	
	/*
	 * Remove a previously added hook.
	 */
	Socket.prototype.removeHook = function( handler, commandName, callback ) {
		var hook_list = this.hooks[ handler ],
			i = null;
		
		if( !hook_list ) {
			return;
		}
		
		hook_list = hook_list[ commandName ];
		if( !hook_list ) {
			return;
		}
		
		for( i in hook_list ) {
			if( hook_list[ i ] == callback ) {
				delete hook_list[ i ];
			}
		}
	};
	
	/*
	 * Acquire a tag to help identify packages when they return.
	 */
	Socket.prototype.getTag = function() {
		return UUIDGen();
	};
	
	/*
	 * Send a package to the backend.
	 * {
	 *		name: 		'<cmdname>',
	 *		handler:	'<handler>',
	 *		callback:	function(){}
	 *		data:		{}
	 * }
	 */
	Socket.prototype.send = function( options ) {
		if( !this.connected ) {
			throw 'The connection is not open. Could not transmit.';
		}
		
		if( !options || !options.name || !options.handler ) {
			throw 'Invalid request packet';
		};
		
		options.tag = this.getTag();

		if( options.callback ) {
			this.sent[ options.tag ] = options.callback;
			delete options.callback;
		}
		
		this.socket.send( window.JSON ? JSON.stringify( options ) : options.toSource() );
	};
	
	Socket.sockets = {};
	
	ready( Socket );
});