(function( $, undefined ){
	
	var init_app = function() {
		/*
		 * Initialize application
		 ********************************************************************************************
		 */
		M.load( 'tpl://global/ui', 'tpl://dashboard', 'js://ui/template', function( ui, dashboard, Template ) {
			
			var global_ui = new Template({ resource: ui });
			global_ui.show();
			
			var dashboard_ui = new Template({ resource: dashboard });
			dashboard_ui.show();
			
		});
	};
	
	$(document).ready(function(){
		MASTER = new Application({
			'proxy': 'resources.php',
			'base': '/code',
			'buffer_timeout': 100
		});
		
		M = MASTER;
		
		/*
		 * Setup a connection with backend
		 */
		M.load( 'js://net/XmConnection', function( XmCon ){
			
			var xm = new XmCon(document.location.host + '/wwscp');
			xm.open();
			
			xm.onopen = function(){
				init_app();
			};
			// Instance generated
			// TODO: attach error handlers.
		});
		
	});
	
}( jQuery ));