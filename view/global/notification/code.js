(function( $ ){
	
	var view = $(this.dom),
		app = view.find('.application'),
		msg = view.find('.message'),
		fadeout = function(){
			view.fadeOut(250);
		};
	
	$('body').append( view.hide() );
	
	this.show = function( application, message ) {
		view.fadeOut(250, function(){
			app.html(application);
			msg.html(message);
			view.fadeIn(250);
			
			window.setTimeout(fadeout, 2000);
		});
	};
	
});