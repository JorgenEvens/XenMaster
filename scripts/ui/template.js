(function( ready, app ){
	
	app.load( 'js://lib/jquery', 'js://ui/dataset', function( $, Dataset ) {
		
		var Template = function( options ) {
			options = options || {};
			
			var res = options.resource || {};
			
			if( res.instance != null && !options.force_new ) {
				return res.instance;
			}
			res.instance = this;

			this.dom = $(res.view).get(0);
			
			this.action = options.action || function(){ return defaultHandler.apply( this, arguments ); };
			this.capture( options.events );
			this.onshow = function(){};
			
			if( typeof res.code === 'function' ) {
				res.code.call( this, $, app );
			}
		},
		defaultHandler = function( action, data, target, source ) {
			$(this).trigger({
					type: action,
					dataset: data,
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
				root = this.dom.parentNode,
				dataset = {};
			
			while( target != null && target != root && action == null ) {
				target = target.parentNode;
				if( target != null ) {
					action = $(target).attr( 'data-action' );
				}
			}
			
			if( target == null || target == root ) {
				return;
			}
			
			if( Dataset.exists( target ) ) {
				dataset = Dataset.get( target );
			}
			
			this.action( action, dataset, target, source );
			
		};
		
		Template.prototype.bind = function( action, callback ) {
			$(this).bind( action, callback );
		};
		
		Template.prototype.on = Template.prototype.bind;
		
		Template.prototype.unbind = function( action, callback ) {
			$(this).unbind( action, callback );
		};
		
		Template.prototype.show = function( placeholder ) {
			placeholder = placeholder || 'main';
			placeholder = $('.placeholder.' + placeholder );
			
			placeholder
				.children()
				.detach();
			
			placeholder.append( this.dom );
			
			this.onshow();
		};
		
		ready( Template );
		
	});
	
});