(function( $ ){
	
	/*
	 * An application instance.
	 * 
	 * Loads additional code and components.
	 */
	Application = function( options ){
		var	me = this,
			buffer = {};
		
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
		 * Resource request buffer
		 * We are ging to try and bundle resource requests.
		 */
		this.buffer = buffer;
		buffer.content = {};
		buffer.timeout = options.buffer_timeout || 250;
		buffer.timeout_id = null;
		buffer.flush = function( callback ) {
			var content = this.content,
				resources = [],
				uri;
			
			// Load uri's into the to-load resources collection.
			for( uri in content ) {
				resources.push( uri );
			}
			
			// Perform call
			$.getJSON( me.proxy, resources, function( data ){
				// Call the callbacks
				for( uri in data ) {
					content[uri]( data[uri] );
				}
			});
			
			// Clear buffer
			buffer.content = {};
			
			// Reset timer.
			if( buffer.timeout_id ) {
				window.clearTimeout( buffer.timeout_id );
			}
			this.timeout_id = window.setTimeout( function(){
				buffer.flush();
			}, this.timeout );
		};
		
		// Start the loop.
		buffer.flush();
		
	};
	
	/*
	 * Put the resources in a list and give it to the callback function
	 * Handles dynamic loading of compoments.
	 */
	Application.prototype.load = function() {
		
		var args = Util.argumentsToArray( arguments ),
			arg_count = args.length,
		
			/*
			 * The amount of resources still being loaded over the network.
			 */
			wait_for = 0,
		
			/*
			 * Callback always is last argument and is required.
			 */
			callback = args.pop(),
			
			/*
			 * Selected resources
			 */
			resources = {},
			
			/*
			 * Add resource to the stack
			 */
			addResource = function( uri ) {
				// Split on the directory separator ( e.g. \ and / ) to find the basename
				name = uri[1].replace( '\\', '/' ).split('/').pop();
				resources[name] = this.cache[ uri[0] ][ uri[1] ];
			},
			
			/*
			 * Tests if loading has finished, if so it calls the callback.
			 */
			checkReady = function() {
				if( !wait_for ){
					callback( resources );
				}
			},

			/*
			 * Used for looping over arguments
			 */
			i,
			uri,
			name;
			
		
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
		this.buffer.content[ uri ] = callback;
	};
	
}( jQuery ));