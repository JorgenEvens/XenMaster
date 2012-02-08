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
		canvas = dom.find('#vnc_screen').get(0),
		
		rfb = null,
		
		connect = function() {
			disconnect();
			
			if( tpl.vm.powerState != 'RUNNING' ) return;
			
			app.load( 'js://ui/novnc', function( VNC_RFB ){
				rfb = VNC_RFB({target: canvas});

				rfb.connect( tpl.vm.reference );
				
				// TODO evaluate this, maybe in relation to strange pointer offsets
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
	
	app.load('js://ui/fullscreenable', function (fs) {
		var button = dom.find('#vnc_fullscreen');
		var screen = dom.find('#vnc_screen');
		
		var fullscreen = new fs;
		fullscreen.targetElement = dom.find('#vnc_console').get(0);
		fullscreen.eventListener = function() {
			if (fullscreen.isFullscreen()) {
				screen.width(rfb.get_screen_width());
				screen.height(rfb.get_screen_height());
				rfb.get_display().resize(rfb.get_screen_width(), rfb.get_screen_height());
				button.text('Exit fullscreen');
			} else {
				screen.width(300);
				screen.height(200);
				rfb.get_display().resize(300, 200);
				button.text('Fullscreen');
			}
		};
		
		button.click(function(){
			if (!rfb) return;
			
			if (fullscreen.isFullscreen()) {
				fullscreen.exitFullscreen();
			} else {
				fullscreen.goFullscreen();
			}
		});
	});
		
	this.onshow = function(){
		
		connect();
		
	};
	
});