(function( ready, app ){
	app.load( 'js://api/entity', function( Entity ){
		
		var iSCSI = new Entity( 'helpers.iSCSI' );
		
		iSCSI.createStaticMethod( 'build', iSCSI );
		
		ready( iSCSI );
		
	});
});