(function( ready, app ){
	app.load( 'js://api/entity', function( Entity ){
		
		var VDI = new Entity( 'VDI' );
		
		VDI.createStaticMethod( 'build', VDI );
		
		VDI.createMethod( 'create', VDI );
		
		VDI.prototype.getVBDs = function() {
			var args = arguments,
				me = this;
			app.load( 'js://api/vbd', function( VBD ) {
				VDI.createMethod( 'getVBDs', VBD );
				VDI.prototype.getVBDs.apply( me, args );
			});
		};
		
		ready( VDI );
		
	});
});