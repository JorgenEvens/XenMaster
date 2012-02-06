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
	
	this.show = function() {
		$('body')
			.append( this.dom );
	};
	
	this.capture( 'click' );
	this.bind( 'ui_dashboard', function() {
		app.load( 'tpl://dashboard', 'js://ui/template', function( dashboard, Template ) {
			
			var dashboard_ui = new Template({ resource: dashboard });
			dashboard_ui.show();
			
		});
	});
	
	
	this.bind( 'ui_storage', function(){

		app.load( 'tpl://storage/list', 'js://ui/template', function( list, Template ){
			var sr_list = new Template({ resource: list });
			sr_list.show();
			
		});
		
	});
	
	this.bind( 'ui_machines', function(){

		app.load( 'tpl://vm/list', 'js://ui/template', function( list, Template ){
			var vm_list = new Template({ resource: list });
			vm_list.show();
			
		});
		
	});
	
});