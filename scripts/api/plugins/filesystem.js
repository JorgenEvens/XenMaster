(function( ready, app ){
	app.load( 'js://api/entity', function( Entity ){
		
		var FS = new Entity( 'plugins.FileSystem' );
		
		FS.createStaticMethod( 'getKernels' );
		
		FS.createStaticMethod( 'getRamdisks' );
		
		ready( FS );
		
	});
});