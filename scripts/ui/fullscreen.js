(function(ready, app) {

	var event = 'fullscreenchange',
		
		isFullScreen = function() { return document.fullScreen; },
		requestFullScreen = document.exitFullscreen ? 'requestFullscreen' : null,
		exitFullScreen = document.exitFullscreen ? 'exitFullscreen' : null;
	
	// Mozilla
	if( document.mozCancelFullScreen ) {
		event = 'mozfullscreenchange';
		isFullScreen = function() { return document.mozFullScreen; };
		requestFullScreen = 'mozRequestFullScreen';
		exitFullScreen = 'mozCancelFullScreen';
	}
	
	// WebKit
	else if( document.webkitCancelFullScreen ) {
		event = 'webkitfullscreenchange';
		isFullScreen = function() { return document.webkitIsFullScreen; };
		requestFullScreen = 'webkitRequestFullScreen';
		exitFullScreen = 'webkitCancelFullScreen';
	}

	// Other
	if( !requestFullScreen ) {
		throw 'FullScreen API not supported';
		// TODO: Fallback
	}
	
	function FullScreenView( content, handler ) {
		var me = this;
		
		this.content = content;
		this.handler = handler;
		
		Util.on( event, document, function(){
			me.handler();
		});
	}
	
	FullScreenView.prototype.activate = function() {
		this.content[requestFullScreen]();
	};
	
	FullScreenView.prototype.isFullscreen = isFullScreen;
	
	FullScreenView.prototype.exit = function() {
		document[exitFullScreen]();
	};
	
	ready(FullScreenView);
});