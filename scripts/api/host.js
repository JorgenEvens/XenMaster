(function( ready, app ){
	app.load( 'js://api/entity', 'js://api/vm', function( Entity, VM ){
		
		var Host = new Entity( 'Host' );
		
		Host.createMethod( 'getResidentVMs', VM, true );
		
		Host.createMethod( 'reboot' );
		
		Host.createMethod( 'shutdown' );
		
		ready( Host );
		
	});
});