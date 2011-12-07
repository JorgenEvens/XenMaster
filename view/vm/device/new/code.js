(function( $, app ){
	
	var tpl = this,
		dom = $(tpl.dom),
		
		deviceReady = function( device ) {
			tpl.action( 'vm_device_ready', [device], device, tpl );
		};
		
	tpl.capture( 'click' );
	
	tpl.on( 'vm_device_create', function() {
		deviceReady({usb:false,PCI:true});
	});
	
});