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
		canvas = dom.find('#vnc_console').get(0),
		
		rfb = null,
		
		connect = function() {
			disconnect();
			
			if( tpl.vm.powerState != 'RUNNING' ) return;
			
			app.load( 'js://ui/novnc', function( VNC_RFB ){
				rfb = VNC_RFB({target: canvas});

				rfb.connect( tpl.vm.reference );
				
				window.setTimeout(function(){
					rfb.get_mouse().set_scale( dom.width()/rfb.get_display().get_width() );
				}, 1000 );
			});
		},
		
		disconnect = function() {
			if( !rfb ) return;
			
			rfb.disconnect();
		};
	
	app.load( 'js://net/xmconnection', function( xm ) {
		xm = xm.getInstance();
		
		xm.addHook( 'log', 'event', function( data ) {
			if( data.entityType != 'vm' ) return;
			if( data.reference != tpl.vm.reference ) return;
			if( !data.changes.powerState ) return;
			
			tpl.vm.powerState = data.changes.powerState;
			
			connect();
		});
	});
		
	this.onshow = function(){
		
		connect();
		
	};
	
});