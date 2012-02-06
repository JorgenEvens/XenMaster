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
		
		// TODO: try to cleanup tpl_conf binding cleaner.
		tpl_conf = null,
		deviceReady = function( e ) {
			tpl.action( 'vm_device_ready', e.dataset/*device*/ );
		};
		
	tpl.capture(['click', 'change']);
	
	tpl.on( 'device_type_selected', function() {
		var type = dom.find('#vm_device_type').val();
		if( !type || type.length == 0 ) return;
		
		app.load( 'tpl://vm/device/' + type, 'js://ui/template', function( tpl_dev, Template ){
			tpl_conf = new Template({resource: tpl_dev});
			tpl_conf.unbind( 'device_ready', deviceReady );
			
			tpl_conf.vm = tpl.vm;
			
			tpl_conf.on( 'device_ready', deviceReady);
			
			tpl_conf.show('vm_device_config');
		});
	});
	
});