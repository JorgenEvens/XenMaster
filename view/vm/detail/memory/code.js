(function( $, app ){
	
	var tpl = this,
		dom = $(tpl.dom),
		ctl = {
			memory: dom.find( '#memory_assigned' ),
		},
		vm = null,
		mem_units = [ 'B', 'KB', 'MB', 'GB', 'TB' ];
	
	tpl.onshow = function() {
		var memory = 0,
			unit = 0;
		
		vm = tpl.vm;
		
		memory = vm.maximumDynamicMemory;
		
		while( memory > 1024 ) {
			unit++;
			memory /= 1024;
		}
		
		ctl.memory.val( memory + mem_units[unit] );
	};
	
});