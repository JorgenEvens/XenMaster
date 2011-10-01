(function( $, undefined ){
		
	$(document).ready(function(){
		MASTER = new Application({
			'proxy': '/resources',
			'base': '/code',
			'buffer_timeout': 100
		});
	});
	
}( jQuery ));