(function( ready, app ){
	
	app.load( 'js://lib/jquery', function( $ ) {
		
		var Template = function( options ) {
			options = options || {};
			
			var res = options.resource || {};
			
			this.dom = $(res.view).get(0);
			
			this.action = options.action || function(){ return defaultHandler.apply( this, arguments ); };
			this.capture( options.events );
			
			if( typeof res.code === 'function' ) {
				res.code.call( this, $ );
			}
		},
		defaultHandler = function( action, target, source ) {
			$(this).trigger({
					type: action,
					action: action,
					bindTo: target,
					source: source
			});
		};
		
		Template.prototype.capture = function( events ) {
			if( typeof events === 'string' ) {
				events = [ events ];
			}
			
			events = events || {};
			
			var i = null,
				me = this,
				handler = function( e ) { return me.capture.handler.call( me, e ); };
			
			for( i in events ) {
				$(this.dom).bind( events[i], handler );
			};
		};
		
		Template.prototype.capture.handler = function( e ) {
			var source = e.target,
				target = source,
				action = $(target).attr( 'data-action' ),
				root = this.dom.parentNode;
			
			while( target != null && target != root && action == null ) {
				target = target.parentNode;
				if( target != null ) {
					action = $(target).attr( 'data-action' );
				}
			}
			
			if( target == null || target == root ) {
				return;
			}
			
			this.action( action, target, source );
			
		};
		
		Template.prototype.bind = function( action, callback ) {
			$(this).bind( action, callback );
		};
		
		Template.prototype.on = Template.prototype.bind;
		
		Template.prototype.unbind = function( action, callback ) {
			$(this).unbind( action, callback );
		};
		
		ready( Template );
		
	});
	
});