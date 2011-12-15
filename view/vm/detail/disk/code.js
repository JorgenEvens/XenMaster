(function( $, app ){

	var tpl = this,
		dom = $(this.dom),
		ctl = {
			name: dom.find('#disk_name')
		};
	
	this.onshow = function() {
		
		var disk = tpl.device;
		
		ctl.name.val( disk.name );
		
	};
	
});