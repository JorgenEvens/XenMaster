(function( $, app ){
	
	var tpl = this,
		dom = $(tpl.dom);
	
	app.load( 'js://api/sr', function( SR ) {
		SR.getAll(function( repos ){
			var i = null,
				list = dom.find( '#dev_repository' );
			for( i in repos ) {
				i = repos[i];
				if( i.contentType != 'user' ) continue;
				
				$('<option></option>')
					.appendTo( list )
					.val( i.reference )
					.text( i.name );
			}
		});
	});
	
	tpl.capture('click');
	
	tpl.on( 'dev_create', function() {
		var data = {};
		
		dom.find('input,select,textarea').each(function(){
			var me = $(this),
				name = me.attr('name'),
				val = me.val();
			
			if( val && name ) {
				data[name] = val;
			}
		});
		
		Util.chain(
			function(){
				app.load( 'js://api/vdi', 'js://api/vbd', this.next );
			},
			function( VDI, VBD ) {
				this.VBD;
				
				VDI.build({}, this.next );
			},
			function( vdi ) {
				vdi.create( data.size, 'USER', data.repo, true, false, this.next );
			},
			function( vdi ) {
				this.vdi = vdi;
				
				this.VBD.build({mode:'RW',type:'DISK'}, this.next);
			},
			function( vbd ) {
				vbd.create( tpl.vm, this.vdi, this.next );
			},
			function( vbd ) {
				tpl.vm.VBDs.push(vbd);
				this.action( 'device_ready', device );
			}
		).start();
		
	});
	
});