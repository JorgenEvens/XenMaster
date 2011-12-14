(function( $, app ){
	
	var tpl = this,
		dom = $(tpl.dom),
		canvas = dom.find('#vnc_console').get(0);
	
	this.onshow = function(){
		app.load( 'js://ui/novnc', function( VNC_RFB ){
			var rfb = VNC_RFB({target: canvas});

			rfb.connect( tpl.vm.reference );
			
			window.setTimeout(function(){
				rfb.get_mouse().set_scale( dom.width()/rfb.get_display().get_width() );
			}, 1000 );
		});
	};
	
});