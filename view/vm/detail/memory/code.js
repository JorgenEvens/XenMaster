(function( $, app ){
	
	var tpl = this,
		dom = $(tpl.dom),
		memory = dom.find( '#memory_assigned' ),
		vm = null;
	
	tpl.onshow = function() {
		vm = tpl.vm;
		
		memory.val( vm.minimumStaticMemory );
	};
	
});