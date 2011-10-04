(function( ready ){
	var path = 'scripts/lib/modernizr.lib.js',
		head,
		script;

	/*
	 * If window.Modernizr is already loaded, notify the cache of this.
	 */
	if( window.Modernizr ) {
		ready( window.Modernizr );
	} else {
		/*
		 * Append the jQuery script to the head tag.
		 */
		head = Util.head;
		script = Util.create( 'script' );
		script.src = path;
		
		Util.on( 'load', script, function(){
			// jQuery failed loading for some reason, to bad :(
			if( !window.Modernizr ) {
				throw 'Unable to load the window.Modernizr library';
			}
			
			ready( window.Modernizr );
		});
		
		head.appendChild( script );
	}
});