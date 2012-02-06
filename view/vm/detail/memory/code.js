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