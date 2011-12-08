(function(){
	var global = this;
	
	Util = function(){};
	
	Util.head = document.getElementsByTagName( 'head' )[0];
	
	Util.isArray = function( value ) {
		return Object.prototype.toString.call(value) === "[object Array]";
	};
	
	Util.argumentsToArray = function( value ) {
		return Array.prototype.slice.call( value );
	};
	
	Util.byId = function( id ) {
		return document.getElementById( id );
	};
	
	Util.create = function( type ) {
		return document.createElement( type );
	};
	
	Util.on = function( event, target, listener ) {
		if( target.addEventListener ) {
			target.addEventListener( event, listener, true );
		} else if( target.attachEvent ) {
			target.attachEvent( 'on' + event, listener );
		} else {
			target['on'+event] = listener;
		}
	};
	
	Util.chain = function(){
		var args = null,
			instance, splice, concat;
		
		if( arguments.length > 0 || this == global ) {
			splice = Array.prototype.splice;
			
			instance = new Util.chain();
			args = Util.argumentsToArray( arguments );
			args = [0,0].concat( args );

			splice.apply( instance, args );
			return instance;
		}
	};
	
	Util.chain.prototype = [];
	
	Util.chain.prototype.start = function() {
		var counter = 0,
			chain = this;
			next = function(){
				return chain[counter++].apply(context,arguments);
			},
			context = {next:next};
			
		next.apply(null,arguments);
	};
	
}());