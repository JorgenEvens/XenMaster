(function( $, app ){
	
	var tpl = this,
		dom = $(tpl.dom),
		ctl = {
			cpu: dom.find( '#processor_count' )
		},
		vm = null;
	
	tpl.onshow = function() {
		vm = tpl.vm;
		
		ctl.cpu.val( vm.maxVCPUs );
	};
	
});