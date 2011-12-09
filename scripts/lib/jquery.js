(function( ready ){
	var path = 'http://ajax.googleapis.com/ajax/libs/jquery/1.7.1/jquery.min.js',
		local = 'scripts/lib/jquery.lib.js',
		head = Util.head,
		script = null,
		
		success = function(){
			// jQuery failed to load for some reason, to bad :(
			if( !window.jQuery ) {
				throw 'Error while loading jQuery.';
			}
			
			ready( window.jQuery );
		},
		
		error = function() {
			/*
			 * Remove script tag from page
			 */
			Util.head.removeChild( script );
			
			if( script.src != local ) {
				/*
				 * Try a local copy of jQuery
				 */
				load( local );
			} else {
				throw 'Unable to load the jQuery library';
			}
		},
		
		load = function( path ) {
			/*
			 * Append the jQuery script to the head tag.
			 */
			script = Util.create( 'script' );
			script.src = path;
			
			Util.on( 'error', script, error );
			Util.on( 'load', script, success );
			
			head.appendChild( script );
		};
	
	/*
	 * If jQuery is already loaded, notify the cache of this.
	 */
	if( window.jQuery ) {
		ready( window.jQuery );
	} else {
		load( path );
	}
});