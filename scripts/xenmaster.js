(function( $, undefined ){
		
	$(document).ready(function(){
		MASTER = new Application({
			'proxy': 'resources.php',
			'base': '/code',
			'buffer_timeout': 100
		});
		
		M = MASTER;
		
		M.load( 'js://lib/jquery', 'js://view', function( $, v ){
			console.log( 'View logic: ' + v );
		});
	});
	
}( jQuery ));