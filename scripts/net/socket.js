(function( ready, app ){
	
	app.load( 'js://lib/modernizr', function( Modernizr ) {
		var socketType = 'websocket';

		if( window.MozWebSocket ) {
			window.WebSocket = window.MozWebSocket;
		}
		
		if( !Modernizr.websockets ) {
			socketType = 'restsocket';
		}
		
		M.load( 'js://net/' + socketType, function( socket ) {
			ready( socket );
		});
		
	});
	
});