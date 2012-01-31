(function( ready, app ){
	
	app.load('js://tools/bindable', function( Bindable ) {	
		var jQueryBindable = function( $, event ) {
			event = event || 'blur';
			
			var attach = function( callback ) {
					$.on( event, callback );
				},
				release = function( callback ) {
					$.off( event, callback );
				};
					
				return new Bindable( $, attach, release );
			};
			
		ready( jQueryBindable );
	});
	
});