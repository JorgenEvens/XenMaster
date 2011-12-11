(function( ready, app ) {
	
	var view = null,
		callback = function( app_key, message ) {
			view.show( app_key, message );
			if( console ) console.log( app_key, message );
		};
	
	app.load( 'tpl://global/notification', 'js://ui/template', 'js://tools/notifier',
			function( notification, Template, Notifier ){
		
		view = new Template({resource: notification});
		
		Notifier.subscribe( callback );
		
		ready( true );
		
	});
		
});