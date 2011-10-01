(function( ready ){
	var path = 'http://ajax.googleapis.com/ajax/libs/jquery/1.6.4/jquery.min.js',
		head,
		script;
	
	/*
	 * If jQuery is already loaded, notify the cache of this.
	 */
	if( jQuery ) {
		ready( jQuery );
	} else {
		/*
		 * Append the jQuery script to the head tag.
		 */
		head = Util.head;
		script = Util.create( 'head' );
		script.src = path;
		
		Util.on( 'load', script, function(){
			// jQuery failed loading for some reason, to bad :(
			if( !jQuery ) {
				throw 'Unable to load the jQuery library';
			}
			
			ready( jQuery );
		});
		
		head.appendChild( script );
	}
});