(function( ready, app ){
	app.load( 'js://api/entity', function( Entity ){
		
		var VBD = new Entity( 'VBD' );
		
		VBD.createStaticMethod( 'build', VBD );
		
		VBD.createMethod( 'create', VBD );
		
		ready( VBD );
	});
});