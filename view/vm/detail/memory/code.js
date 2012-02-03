(function( $, app ){
	
	var tpl = this,
		dom = $(tpl.dom),
		ctl = {
			memory: dom.find( '#memory_assigned' ),
		},
		vm_entity = null,
		vm_bindable = null,
		
		mem_units = { 'B': 1/(1024*1024), 'KB': 1/1024, 'MB': 1, 'GB': 1024, 'TB': Math.pow( 1024, 2 ) },
		
		updateBinding = function() {
			if( !vm_bindable ) {
				return createBinding();
			}
			
			vm_bindable.setSource( vm_entity );
		},
		
		createBinding = function() {
			app.load( 'js://api/entity_bindable', 'js://ui/jquery_bindable', 'js://tools/binding',
					function( bindEntity, bindjQuery, Binding ) {
				
				vm_bindable = bindEntity( vm_entity );
				var vm = vm_bindable,
				
					memory = {
						get: function() { var v=/(\d+)\s?([KMGTkmgt]?B)/.exec(this.val()); if(!v)return 0; return v[1] * mem_units[v[2].toUpperCase()]; },
						set: function(v) { var i=null;
							for( i in mem_units ) {
								if( v < 1024 ) break;
								v /= 1024;
							}
							this.val( v + ' ' + i );
						}
					};
				
				new Binding( vm, {get:'maximumDynamicMemory',set:'setMaximumDynamicMemory'}, bindjQuery( ctl.memory ), memory, true );
			});
		};
	
	tpl.onshow = function() {
		vm_entity = tpl.vm;
		
		updateBinding();
	};
	
});