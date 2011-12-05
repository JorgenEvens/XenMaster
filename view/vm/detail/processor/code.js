(function( $, app ){
	
	var tpl = this,
		dom = $(tpl.dom),
		CPU = dom.find( '#processor_count' ),
		vm = null;
	
	tpl.onshow = function() {
		vm = tpl.vm;
		
		CPU.val( vm.maxVCPUs );
	};
	
});