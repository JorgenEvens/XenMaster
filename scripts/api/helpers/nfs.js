(function( ready, app ){
	app.load( 'js://api/entity', function( Entity ){
		
		var NFS = new Entity( 'helpers.NFS' );
		
		NFS.createStaticMethod( 'mountISORepository' );
		
		ready( NFS );
		
	});
});