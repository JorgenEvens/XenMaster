(function(){
	var global = this,
		document = global.document,
	
		/*
		 * We cannot use normal inheritance when inheriting Array.
		 * Extend each generated array object with the next function.
		 */
		Chain = (function(){
			var start = function() {
					var counter = 0,
					chain = this,
					context = {next:null},
					next = function(){
						return chain[counter++].apply(context,arguments);
					};
					
				context.next = next;
				next.apply(null,arguments);
			};
			
			return function() {
				var args = [];
				
				if( arguments.length > 0 ) {
					args = Util.argumentsToArray( arguments );
				}
				
				args.start = start;
				
				return args;
			};
		}());
	
	/*
	 * Util is the only global we use, because we need it a lot.
	 */
	Util = {
		head: document.getElementsByTagName( 'head' )[0],
		
		isArray: function( value ) {
			return Object.prototype.toString.call(value) === "[object Array]";
		},
		
		argumentsToArray: function( value ) {
			return Array.prototype.slice.call( value );
		},
		
		byId: function( id ) {
			return document.getElementById( id );
		},
		
		create: function( type ) {
			return document.createElement( type );
		},
		
		on: function( event, target, listener ) {
			if( target.addEventListener ) {
				target.addEventListener( event, listener, true );
			} else if( target.attachEvent ) {
				target.attachEvent( 'on' + event, listener );
			} else {
				target['on'+event] = listener;
			}
		},
		
		chain: Chain
	};
	
}());