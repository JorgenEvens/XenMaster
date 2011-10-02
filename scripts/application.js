(function( $ ){
	
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
				uri = uri.split( '://' );
				resources.push( me.cache[ uri[0] ][ uri[1] ] );
			},
			
			/*
			 * Tests if loading has finished, if so it calls the callback.
			 */
			checkReady = function() {
				if( !wait_for ){
					callback.apply( window, resources.reverse() );
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
			
			if( this.cache[ uri[0] ][ uri[1] ] ) {
				/*
				 * The requested resource is available from cache.
				 */
				addResource( uri );
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
			/*
			 * Initialise plugin with a reference to the application that loaded it.
			 */
			if( uri.substring( 0, 5 ) == 'js://' ) {
				data = eval( data );
				
				data( function( value ){
					me.ready( uri, value );
				}, me );
			} else {
				me.ready( uri, data );
			}
		});
	};
	
	Application.prototype.ready = function( uri, value ) {
		var callbacks = this.queue[ uri ],
			i = 0,
			uri_parts = uri.split( '://' );
		
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
			for( i in this.content ) {
				break;
			}
			this.notification.style.opacity = i != null ? '1' : '0';
		}
	};
	
	Buffer.prototype.request = function( uri, callback ) {
		this.content[uri] = callback;
		this.updateUI();
	};

	Buffer.prototype.flush = function() {
		var content = this.content,
			resources = [],
			uri = null,
			me = this;
	
		// Load uri's into the to-load resources collection.
		for( uri in content ) {
			resources.push( uri );
		}
		
		if( resources.length > 0 ) {
			// Clear buffer
			this.content = {};
			
			// Perform call
			$.ajax({
				url: this.proxy,
				data: {
					resources: resources
				},
				dataType: 'json',
				success: function( data ){
					// Call the callbacks
					for( uri in data ) {
						me.retrieve( data[ uri ], content[ uri ] );
					}
					me.updateUI();
				}
			});
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
		$.ajax({
			url: resource,
			dataType: 'text',
			success: callback
		});
	};
	
}( jQuery ));