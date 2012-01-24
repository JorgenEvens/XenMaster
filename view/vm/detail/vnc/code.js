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