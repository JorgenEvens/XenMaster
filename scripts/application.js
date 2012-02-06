/*
 * application.js
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
 * application.js
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

(function(){
	
	/*
	 * An application instance.
	 * 
	 * Loads additional code and components.
	 */
	Application = function( options ){
		options = options || {};
		
		this.proxy = options.proxy || '/resources';
		this.base = options.base || '/';
		
		/*
		 * Resource cache
		 */
		this.cache = {
			'js': {},
			'css': {}
		};
		
		/*
		 * Resources we are waiting for to return
		 */
		this.queue = {};
		
		/*
		 * Resource request buffer
		 * We are ging to try and bundle resource requests.
		 */
		this.buffer = new Buffer({
			timeout: 250,
			proxy: this.proxy
		});
		
		// Start the loop.
		this.buffer.flush();
		
	};
	
	/*
	 * Put the resources in a list and give it to the callback function
	 * Handles dynamic loading of compoments.
	 */
	Application.prototype.load = function() {
		
		var args = Util.argumentsToArray( arguments ),
		
			me = this,
		
			/*
			 * The amount of resources still being loaded over the network.
			 */
			wait_for = 0,
		
			/*
			 * Callback always is last argument and is required.
			 */
			callback = args.pop(),
			
			
			arg_count = args.length,
			
			/*
			 * Selected resources
			 */
			resources = [],
			
			/*
			 * Add resource to the stack
			 */
			addResource = function( uri ) {
				var index = args.indexOf( uri );
				
				uri = uri.split( '://' );
				
				resources[index] = me.cache[ uri[0] ][ uri[1] ];
			},
			
			/*
			 * Tests if loading has finished, if so it calls the callback.
			 */
			checkReady = function() {
				if( !wait_for ){
					callback.apply( window, resources );
				}
			},

			/*
			 * Used for looping over arguments
			 */
			i,
			uri;
			
		
		if( arg_count == 1 && Util.isArray( args[0] ) ) {
			args = args[0];
		}
		for( i=arg_count; i--; ) {
			uri = args[i];
			
			uri = uri.split('://');
			
			if( uri.length != 2 ) {
				continue;
			}
			
			if( !this.cache[ uri[0] ] ) {
				this.cache[ uri[0] ] = {};
			}
			
			if( this.cache[ uri[0] ][ uri[1] ] ) {
				/*
				 * The requested resource is available from cache.
				 */
				addResource( args[i] );
			} else {
				/*
				 * The requested resource is not present in the cache.
				 * Perform a XHR request to retrieve the resource.
				 */ 
				wait_for++;
				this.import( args[i], function( uri ) {
					addResource( uri );
					wait_for--;
					checkReady();
				});
			}
		};
		
		/*
		 * Test if we are waiting for a resource to finish loading.
		 * If not call the callback function.
		 */ 
		checkReady();
		
	};
	
	Application.prototype.import = function( uri, callback ){
		var me = this;
		
		if( !Util.isArray( this.queue[ uri ] ) ) {
			this.queue[ uri ] = [];
		};
		
		this.queue[ uri ].push( callback );
		
		this.buffer.request( uri, function( data ){
			me.parseResource( uri, data );
		});
	};
	
	Application.prototype.parseResource = function( uri, data ) {
		var parts = uri.split('://'),
			type = parts[0],
			me = this;
		
		if( this.cache[ type ][ parts[1] ] ) {
			/*
			 * Prevent parsing resources when they have already been processed.
			 */
			return;
		}
		
		if( type == 'js' ) {
			/*
			 * Initialise plugin with a reference to the application that loaded it.
			 */
			data = eval( data );
	
			data( function( value ){
				me.ready( uri, value );
			}, this );
		} else if( type == 'tpl' ) {
			/*
			 * Templates come in a JSON container.
			 * {
			 * 		view: '<html>',
			 * 		code: '<javascript>'
			 * }
			 */
			data = JSON ? JSON.parse( data ) : eval( '(' + data + ')' );
			data.code = eval( data.code );
			
			this.ready( uri, data );
		} else {
			/*
			 * Simply save the data as plain text
			 */
			this.ready( uri, data );
		}
	};
	
	Application.prototype.ready = function( uri, value ) {
		var callbacks = this.queue[ uri ],
			i = 0,
			uri_parts = uri.split( '://' );
		
		if( !this.cache[ uri_parts[0] ] ) {
			this.cache[ uri_parts[ 0 ] ] = {};
		}
		
		this.cache[ uri_parts[0] ][ uri_parts[1] ] = value;
		
		if( !callbacks ) {
			return;
		}
		
		for( i=callbacks.length; i--; ) {
			if( typeof callbacks[i] === 'function' ) {
				callbacks[i]( uri, value );
			}
		}
		
		delete this.queue[ uri ];
	};
	
	var Buffer = function( options ) {
		options = options || {};
		
		this.content = {};
		this.timeout = options.timeout || 250;
		this.timeout_id = null;
		this.proxy = options.proxy || '/';
		this.notification = options.notification || Util.byId( 'loading' );
	};
	
	Buffer.prototype.updateUI = function() {
		var i = null;
		
		if( this.notification ) {
			this.notification.style.opacity = i != null ? '1' : '0';
		}
	};
	
	Buffer.prototype.request = function( uri, callback ) {
		this.content[uri] = callback;
		this.updateUI();
	};

	Buffer.prototype.flush = function() {
		var content = this.content,
			uri = null,
			path = null,
			parts = null,
			me = this;
		
		// Clear buffer
		this.content = {};

		for( uri in content ) {
			parts = uri.split( '://' );
			
			if( parts[0] == 'js' ) {
				path = 'scripts/';
			} else if( parts[0] == 'css' ) {
				path = 'styles/';
			} else if( parts[0] == 'tpl' ) {
				path = 'view/';
			} else {
				path = '';
			}
			
			if( parts[0] == 'tpl' ) {
				path += parts[1];
			} else {
				path +=  parts[1] + '.' + parts[0];
			}
			
			this.retrieve( path, content[ uri ] );
		}
		
		if( uri != null ) {
			this.updateUI();
		}
		
		// Reset timer.
		if( this.timeout_id ) {
			window.clearTimeout( this.timeout_id );
		}
		
		this.timeout_id = window.setTimeout( function(){
			me.flush();
		}, this.timeout );
	};
	
	Buffer.prototype.retrieve = function( resource, callback ) {
		var req = null;
		try {
			req = new XMLHttpRequest();
		} catch( ex ) {
			req = new ActiveXObject("Microsoft.XMLHTTP");
		}
		
		if( !req ) {
			throw 'Your browser does not support XMLHttpRequest';
		};
		
		req.open( 'GET', resource );
		
		req.onreadystatechange = function() {
			if( req.readyState == 4 && req.status == 200 ) {
				callback( req.responseText );
			}
		};
		
		req.send( null );
	};
	
}());