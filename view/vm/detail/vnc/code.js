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
        canvas: dom.find('.vnc_screen'),
        container: dom.find('.vnc_container'),
        console: dom.find('.vnc_console'),
        fullscreen: dom.find('.vnc_fullscreen')
    },
		
    rfb = null,
		
    onrescale = function( data, display ) {
        if( !rfb ) {
            return;
        }
        
        rfb.get_mouse().set_scale( display.scale );
    },
		
    onresolutionchanged = function( data, display ) {
        display.resize( ctl.console.width(), ctl.console.height() );
    },
		
    connect = function() {
        disconnect();
			
        if( tpl.vm.powerState != 'RUNNING' ) return;
			
        app.load( 'js://ui/novnc', function( VNC_RFB ){
            rfb = VNC_RFB({
                target: ctl.canvas.get(0)
            });

            rfb.get_display().on( 'resolutionchanged', onresolutionchanged );
            rfb.get_display().on( 'scale', onrescale);

            rfb.connect( tpl.vm.reference );
        });
    },
		
    disconnect = function() {
        if( !rfb ) return;
			
        rfb.get_display().off( 'scale', onrescale );
        rfb.get_display().off( 'resolutionchanged', onresolutionchanged );
        rfb.disconnect();
    },
		
    fullscreenChanged = function(){
        var display = rfb.get_display();
			
        if( fullscreen.isFullscreen() ) {
            ctl.fullscreen.text( 'Exit fullscreen' );
				
            if( display.width > screen.width-2 || display.height > screen.height-2 ) {
                setTimeout(function(){
                    display.resize( screen.width-2, screen.height-2 );
                },100 );
            } else {
                display.rescale( 1 );
            }
				
        } else {
            ctl.fullscreen.text( 'Fullscreen' );
            onresolutionchanged( null, display );
        }
    },
		
    fullscreen = null;
	
    app.load( 'js://net/xmconnection', 'js://ui/fullscreen', function( xm, Fullscreen ) {
        xm = xm.getInstance();
		
        xm.addHook( 'log', 'event', function( data ) {
            if( data.entityType != 'vm' ) return;
            if( data.reference != tpl.vm.reference ) return;
            if( !data.changes.powerState ) return;
			
            tpl.vm.powerState = data.changes.powerState;
			
            connect();
        });
		
        fullscreen = new Fullscreen( ctl.container[0], fullscreenChanged );
		
        ctl.fullscreen.click(function(){
            if( fullscreen.isFullscreen() ) {
                fullscreen.exit();
            } else {
                fullscreen.activate();
            }
        });
    });
		
    this.onshow = function(){
        connect();
    };
	
});