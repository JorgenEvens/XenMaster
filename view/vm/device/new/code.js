(function( $, app ){
	
	var tpl = this,
		dom = $(tpl.dom),
		
		// TODO: try to cleanup tpl_conf binding cleaner.
		tpl_conf = null,
		deviceReady = function( e ) {
			tpl.action( 'vm_device_ready', e.dataset/*device*/ );
		};
		
	tpl.capture(['click', 'change']);
	
	tpl.on( 'device_type_selected', function() {
		var type = dom.find('#vm_device_type').val();
		if( !type || type.length == 0 ) return;
		
		app.load( 'tpl://vm/device/' + type, 'js://ui/template', function( tpl_dev, Template ){
			tpl_conf = new Template({resource: tpl_dev});
			tpl_conf.unbind( 'device_ready', deviceReady );
			
			tpl_conf.vm = tpl.vm;
			
			tpl_conf.on( 'device_ready', deviceReady);
			
			tpl_conf.show('vm_device_config');
		});
	});
	
});