(function( $, app ){

	var tpl = this,
		dom = $(this.dom),
		ctl = {
			name: dom.find('#disk_name'),
			size: dom.find('#disk_size')
		};
	
	this.onshow = function() {
		
		var disk = tpl.device;
		
		ctl.name.val( disk.name );
		ctl.size.val( disk.virtualSize );
		
	};
	
});