/*
 * code.js
 * Copyright (C) 2011,2012 Jorgen Evens
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

(function( $, app ){
	
	var tpl = this,
		dom = $(tpl.dom),
		ctl = {
			platform: {
				apic: dom.find( '#adv_apic' ),
				acpi: dom.find( '#adv_acpi' ),
				pae: dom.find( '#adv_pae' ),
				viridian: dom.find( '#adv_viridian' )
			},
			actionOnCrash: dom.find( '#vm_crash' ),
			actionOnReboot: dom.find('#vm_reboot'),
			actionOnShutdown: dom.find('#vm_shutdown'),
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
				
				
				var vm = vm_bindable,
				
				/**
				 * Custom methods for manipulating get and set.
				 */
					checked = {
						get: function() { return this.is(':checked'); },
						set: function( v ) { v ? this.attr('checked','checked') : this.removeAttr('checked'); }
					},
					platform = {
						get: function() { var ret={},i=null;
								for( i in ctl.platform )
									ret[i] = checked.get.apply( ctl.platform[i] );
								return ret;
							},
						set: function( v ) { var i=null;
								for( i in v )  // TODO: Temp fix for backend.. should remove eval
									checked.set.call( ctl.platform[i], eval( v[i] ) );
							}
					};
				
				new Binding( vm, {get:'actionsAfterCrash',set:'setActionsAfterCrash'}, bindjQuery( ctl.actionOnCrash ), 'val', true );
				new Binding( vm, {get:'actionsAfterReboot',set:'setActionsAfterReboot'}, bindjQuery( ctl.actionOnReboot), 'val', true );
				new Binding( vm, {get:'actionsAfterShutdown',set:'setActionsAfterShutdown'}, bindjQuery( ctl.actionOnShutdown ), 'val', true );
				
				new Binding( vm, {get:'platform',set:'setPlatform'}, bindjQuery( ctl.platform.acpi ), platform, true );
				new Binding( vm, {get:'platform',set:'setPlatform'}, bindjQuery( ctl.platform.apic ), platform, true );
				new Binding( vm, {get:'platform',set:'setPlatform'}, bindjQuery( ctl.platform.pae ), platform, true );
				new Binding( vm, {get:'platform',set:'setPlatform'}, bindjQuery( ctl.platform.viridian ), platform, true );

			});
		};
	
	tpl.onshow = function() {
		vm_entity = tpl.vm;
		
		updateBinding();
		
		/*
		vm_entity.getPlatform(function( platform ) {
			var i = null;
			
			for( i in ctl.platform ) {
				ctl.platform[i].attr('checked', platform[i] );
			}
		});
		
		ctl.actionOnCrash.val( vm_entity.actionsOnCrash );
		ctl.actionOnReboot.val( vm_entity.actionsOnReboot );
		ctl.actionOnShutdown.val( vm_entity.actionsOnShutdown );
		*/
	};
	
	tpl.capture( 'change' );
	
	tpl.on( 'platform_changed', function() {
		var i = null,
			platform = {};
		
		for( i in ctl.platform ) {
			platform[i] = ctl.platform[i].attr('checked') == 'checked';
		}
		
		vm_entity.setPlatform( platform );
	});
	
});