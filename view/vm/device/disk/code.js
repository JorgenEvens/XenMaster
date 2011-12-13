(function( $, app ){
	
	var tpl = this,
		dom = $(tpl.dom),
		images = null;
	
	app.load( 'js://api/sr', function( SR ) {
		SR.getAll(function( repos ){
			var i = null,
				list = dom.find( '#dev_repository' );
			for( i in repos ) {
				i = repos[i];
				if( i.contentType != 'iso' ) continue;
				
				$('<option></option>')
					.appendTo( list )
					.val( i.reference )
					.text( i.name );
			}
			
			list.change();
		});
	});

	tpl.capture('click','change');
	
	tpl.on( 'dev_repo_select', function( e ) {
		Util.chain(
			function(){
				app.load( 'js://api/sr', this.next );
			},
			function( SR ) {
				new SR( $(e.source).val(), this.next );
			},
			function( sr ) {
				sr.getVDIs( this.next );
			},
			function( vdis ) {
				var list = dom.find( '#dev_disk').html('');
				
				images = [];
				
				for( i in vdis ) {
					i = vdis[i];
					if( !i.name ) continue;
					images[i.reference] = i;
					
					$('<option></option>')
						.appendTo( list )
						.val( i.reference )
						.text( i.name );
				}
			}
		).start();
	});
	
	tpl.on( 'dev_create', function() {
		var vdi = null;
		
		Util.chain(
			function(){
				app.load( 'js://api/vbd', 'js://api/vdi', this.next );
			},
			function( VBD, VDI ) {
				vdi = new VDI( $('#dev_disk').val() );
				VBD.build({mode:'RO',type:'CD'}, this.next);
			},
			function( vbd ) {
				vbd.create( tpl.vm, vdi, '', this.next );
			},
			function( vbd ) {
				tpl.vm.VBDs.push(vbd.reference);
				tpl.action( 'device_ready', vbd );
			}
		).start();
		
	});
	
});