(function( ready, app ){
	app.load( 'js://api/entity', 'js://api/vdi',
			function( Entity, VDI ){
		
		var SR = new Entity( 'SR' );
		
		SR.createStaticMethod( 'getAll', SR, true );
		
		SR.createStaticMethod( 'build', SR );
		
		SR.createMethod( 'create' );
		
		SR.createMethod( 'destroy' );
		
		SR.createMethod( 'forget' );
		
		SR.createMethod( 'getVDIs', VDI, true );
		
		SR.createMethod( 'introduce', SR );
		
		ready( SR );
	});
});