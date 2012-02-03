(function( $, app ){
	
	var tpl = this,
		dom = $(tpl.dom),
		ctl = {
			cpu: dom.find( '#processor_count' )
		},
		vm_entity = null,
		vm_bindable = null,
	
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
				var vm = vm_bindable;

				new Binding( vm, 'maxVCPUs', bindjQuery( ctl.cpu ), 'val' );
			});
		};
	
	tpl.onshow = function() {
		vm_entity = tpl.vm;
		
		updateBinding();
	};
	
});