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
		record = $(tpl.dom).find('.vm_template' ).remove();
	
	app.load( 'js://api/vm', 'js://ui/dataset', function( VM, Dataset ) {
		
		// Session[].getThisHost.getResidentVMs
		VM.getAll(function( result ){
			var r = null,
				vm = null,
				i = null,
				ds = null;
			
			for( i in result ) {
				vm = result[i];
				if( vm.template ) continue;
				
				r = record.clone();
				
				r.find('td:first').html( vm.name );
				r.find('td:nth(1)').html(Math.round(Math.random()*99));
				r.find('td:last').html(Math.round(Math.random()*99));
				
				ds = Dataset.get( r.get(0) );
				ds.config = vm;
				
				$(tpl.dom).find('tbody').append( r );
			}
		});
		
		tpl.capture( 'click' );
		tpl.bind( 'vm_clicked', function( e ) {
			app.load( 'tpl://vm/list', 'js://ui/template', function( list, Template ) {
				
				var vm_list = new Template({ resource: list });
				
				vm_list.bind( 'tpl_show', function(){
					vm_list.loadVM( e.dataset.config );
				});
				
				vm_list.show();
			});
		});
		
		tpl.bind('all_show', function(e){
			app.load( 'tpl://vm/list', 'js://ui/template', function( list, Template ) {
				var list_ui = new Template({resource: list});
				list_ui.show();
			});
		});
		
	});
	
});