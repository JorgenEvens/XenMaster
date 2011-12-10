(function( ready, app ){
	app.load( 'js://api/entity', function( Entity ){
		
		var Network = new Entity( 'Network' );
		
		Network.createStaticMethod( 'build', Network );
		
		Network.createStaticMethod( 'getAll', Network, true );
		
		Network.createMethod( 'create', Network );

		ready( Network );
		
	});
});