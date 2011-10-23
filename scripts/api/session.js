(function( ready, app ){
	app.load( 'js://api/entity', 'js://api/host', 
			function( Entity, Host ) {
		
		var Session = new Entity( 'Session[]' );
		
		Session.createMethod( 'getThisHost', Host );
		
		ready( new Session() );
		
	});
});