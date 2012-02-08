(function(ready, app) {

	// Contains parts from :
	// https://github.com/robnyman/robnyman.github.com/blob/master/fullscreen/js/base.js

	function FullscreenAble() {
		this.targetElement = {};
		this.eventListener = function() {
		};
	};

	FullscreenAble.prototype.goFullscreen = function() {
		if (this.targetElement) {
			document.addEventListener("fullscreenchange", this.eventListener, false);
			document.addEventListener("mozfullscreenchange", this.eventListener, false);
			document.addEventListener("webkitfullscreenchange", this.eventListener, false);
			
			if (this.targetElement.requestFullscreen) {
				this.targetElement.requestFullscreen();
			} else if (this.targetElement.mozRequestFullScreen) {
				this.targetElement.mozRequestFullScreen();
			} else if (this.targetElement.webkitRequestFullScreen) {
				this.targetElement.webkitRequestFullScreen();
			}
		}
	};
	
	FullscreenAble.prototype.isFullscreen = function() {
		return document.fullScreen || document.mozFullScreen || document.webkitIsFullScreen;
	};

	FullscreenAble.prototype.exitFullscreen = function() {
		if (document.exitFullscreen) {
			document.exitFullscreen();
		} else if (document.mozCancelFullScreen) {
			document.mozCancelFullScreen();
		} else if (document.webkitCancelFullScreen) {
			document.webkitCancelFullScreen();
		}
	};

	ready(FullscreenAble);
});