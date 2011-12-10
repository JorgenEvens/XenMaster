(function( $, app ){
	
	var tpl = this,
		dom = $(tpl.dom);
	
	app.load( 'js://api/network', function( Network ) {
		Network.getAll(function( networks ){
			var i = null,
				list = dom.find( '#dev_network' );
			for( i in networks ) {
				i = networks[i];
			
				$('<option></option>')
					.appendTo( list )
					.val( i.reference )
					.text( i.name );
			}
		});
	});
	
	this.onshow = function(){
		$('#dev_name').val( tpl.vm.name );
	};

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
				app.load( 'js://api/vif', this.next );
			},
			function( VIF ) {
				VIF.build({name:data.name}, this.next );
			},
			function( vif ) {
				vif.create( '', data.network, this.next );
			},
			function( vif ) {
				tpl.vm.VIFs.push(vif.reference);
			}
		).start();
		
	});
	
});