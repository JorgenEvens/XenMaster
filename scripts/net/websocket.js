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
		var data = window.JSON ? JSON.parse( e.data ) : eval( '(' + e.data + ')' ),
			callback = this.sent[ data.tag ],
			hook_list = this.hooks[ data.handler ],
			i = null;
		
		if( callback ) {
			callback( data );
		};
		
		for( i in hook_list ) {
			hook_list[i]( data );
		}
	};
	
	/*
	 * Add a hook that handles messages broadcasted by the backend.
	 */
	Socket.prototype.addHook = function( handler, callback ) {
		if( !this.hooks[ handler ] ) {
			this.hooks[ handler ] = [];
		}
		
		this.hooks[ handler ].push( callback );
	};
	
	/*
	 * Remove a previously added hook.
	 */
	Socket.prototype.removeHook = function( handler, callback ) {
		var hook_list = this.hooks[ handler ],
			i = null;
		
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