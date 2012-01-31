(function( ready, app ){
	
	app.load('js://tools/bindable', 'js://net/xmconnection', function( Bindable, xm ) {
		xm = xm.getInstance();
		
		var handlers = [];
			EntityBindable = function( entity ) {
				var entityType = entity.xm_type.toLowerCase(),
					handler = null,
					attach = function( callback ) {
						var me = this;
						
						handler = function( data ) {
							var i = null;
							if( data.reference == me.reference && 
									data.entityType == entityType ) {
								for( i in data.changes ) {
									me[i] = data.changes[i];
								}
								callback();
							};
						};
						
						handlers.push( handler );
					},
					release = function( callback ) {
						var index = handlers.indexOf( handler );
						delete handlers[index];
					};
					
				return new Bindable( entity, attach, release );
			};
				
		xm.addHook( 'log', 'event', function( data ) {
			var i = null;
			for( i in handlers ) {
				handlers[i](data);
			};
		});
		
		ready( EntityBindable );
		
	});
	
});