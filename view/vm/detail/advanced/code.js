(function( $, app ){
	
	var tpl = this,
		dom = $(tpl.dom),
		ctl = {
			platform: {
				apic: dom.find( '#adv_apic' ),
				acpi: dom.find( '#adv_acpi' ),
				pae: dom.find( '#adv_pae' ),
				viridian: dom.find( '#adv_viridian' )
			}
		},
		vm = null;
	
	tpl.onshow = function() {
		vm = tpl.vm;
		
		vm.getPlatform(function( platform ) {
			var i = null;
			
			for( i in ctl.platform ) {
				ctl.platform[i].attr('checked', platform[i]?'checked':'' );
			}
		});
	};
	
	tpl.capture( 'change' );
	
	tpl.on( 'platform_changed', function() {
		var i = null,
			platform = {};
		
		for( i in ctl.platform ) {
			platform[i] = ctl.platform[i].attr('checked') == 'checked';
		}
		
		vm.setPlatform( platform );
	});
	
});