(function( ready, app ){
	app.load( 'js://api/entity',
			function( Entity ){
		
		var SR = new Entity( 'SR' );
		
		SR.createStaticMethod( 'getAll', SR, true );
		
		SR.createMethod( 'destroy' );
		
		SR.createMethod( 'forget' );

		ready( SR );
	});
});