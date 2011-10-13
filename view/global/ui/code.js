(function( $, app ){
	
	this.show = function() {
		$('body')
			.html('')
			.append( this.dom );
	};
	
	this.capture( 'click' );
	this.bind( 'ui_dashboard', function() {
		app.load( 'tpl://dashboard', 'js://ui/template', function( dashboard, Template ) {
			
			var dashboard_ui = new Template({ resource: dashboard });
			dashboard_ui.show();
			
		});
	});
	
});