(function( $, app ){
	
	var tpl = this,
		dom = $(tpl.dom),
		
		deviceReady = function( device ) {
			tpl.action( 'vm_device_ready', device );
		};
		
	tpl.capture( 'click' );
	
	tpl.on( 'device_type_selected', function() {
		var type = dom.find('#vm_device_type').val();
		if( !type || type.length == 0 ) return;
		
		app.load( 'tpl://vm/device/' + type, 'js://ui/template', function( tpl_dev, Template ){
			var config = new Template({resource: tpl_dev});
			config.show('vm_device_config');
			config.vm = tpl.vm;
			
			config.on( 'device_ready', function( e ){
				deviceReady( e.dataset );
			});
		});
	});
	
});