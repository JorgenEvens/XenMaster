(function( ready, app ){
	app.load( 'js://api/entity', 'js://api/vdi', function( Entity, VDI ){
		
		var VBD = new Entity( 'VBD' );
		
		VBD.createStaticMethod( 'build', VBD );
		
		VBD.createMethod( 'create', VBD );
		
		VBD.createMethod( 'getVDI', VDI );
		
		ready( VBD );
	});
});