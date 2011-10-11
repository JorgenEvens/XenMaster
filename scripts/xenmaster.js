(function( $, undefined ){
		
	$(document).ready(function(){
		MASTER = new Application({
			'proxy': 'resources.php',
			'base': '/code',
			'buffer_timeout': 100
		});
		
		M = MASTER;
		
		/*
		 * Initialize application
		 ********************************************************************************************
		 */
		M.load( 'tpl://global/ui', 'js://ui/template', function( ui, Template ) {
			
			var global_ui = new Template({ resource: ui });
			global_ui.show();
			
		});
		
	});
	
}( jQuery ));