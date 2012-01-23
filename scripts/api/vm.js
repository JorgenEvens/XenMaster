(function( ready, app ){
	app.load( 'js://api/entity', 'js://api/vbd', 'js://api/vif',
			function( Entity, VBD, VIF ){
		
		var VM = new Entity( 'VM' );
		
		VM.createStaticMethod( 'build', VM );
		
		VM.createStaticMethod( 'getAll', VM, true );
		
		VM.createMethod( 'create' );
		
		VM.createMethod( 'destroy' );
		
		VM.createMethod( 'start' );
		
		VM.createMethod( 'pause' );
		
		VM.createMethod( 'resume' );
		
		VM.createMethod( 'stop' );
		
		VM.createMethod( 'reboot' );
		
		VM.createMethod( 'suspend' );
		
		VM.createMethod( 'wake' );
		
		VM.createMethod( 'getVBDs', VBD, true );
		
		VM.createMethod( 'getVIFs', VIF, true );

		VM.createMethod( 'setPlatform' );
		
		VM.createMethod( 'getPlatform' );
		
		VM.createMethod( 'setName' );
		
		VM.createMethod( 'setDescription' );
		
		ready( VM );
	});
});