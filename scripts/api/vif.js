(function( ready, app ){
	app.load( 'js://api/entity', function( Entity ){
		
		var VIF = new Entity( 'VIF' );
		
		VIF.createStaticMethod( 'build', VIF );
		
		VIF.createMethod( 'create', VIF );
		
		ready( VIF );
	});
});