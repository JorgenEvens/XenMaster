(function( ready, app ){
	app.load( 'js://api/entity', function( Entity ){
		
		var VDI = new Entity( 'VDI' );
		
		VDI.createStaticMethod( 'build', VDI );
		
		VDI.createMethod( 'create', VDI );
		
		ready( VDI );
	});
});