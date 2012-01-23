(function( $, app ){
	
	var dom = $(this.dom);
	var	logEventHandler = function(data) {
		// It's rather safe to assume proper titles have more than 3 chars
		if (data.title.length > 3) {
			var eventLine = document.createElement('div');
			$(eventLine).text(data.level.substr(0, 1) + ' ' + data.title);
			$(eventLine).addClass('event');
			$(eventLine).addClass(data.level.toLowerCase());
			$(eventLine).attr("data-ref", data.reference);
			dom.prepend(eventLine);
		}
	};
	
	var errorHandler = function(appKey, messageType, message, data) {
		if (messageType == 'ERROR' && data.result.title) {
			var eventLine = document.createElement('div');
			$(eventLine).text('E ' + data.result.title);
			$(eventLine).addClass('event');
			$(eventLine).addClass('error');
			dom.prepend(eventLine);
		}
	};
	
	app.load('js://net/xmconnection', function(xm) {
		xm = xm.getInstance();
		
		xm.addHook('log', 'event', logEventHandler);
	});
	
	app.load('js://tools/notifier', function(Notifier) {
		Notifier.subscribe(errorHandler);
	});
	
});