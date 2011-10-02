(function( ready ){
	
	M.load( 'js://lib/modernizr', function( Modernizr ) {
		var socketType = 'websocket';
		
		if( !Modernizr.websocket ) {
			socketType = 'restsocket';
		}
		
		M.load( 'js://net/' + socketType, function( socket ) {
			ready( socket );
		});
		
	});
	
});