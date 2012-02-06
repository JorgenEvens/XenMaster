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