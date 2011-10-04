(function(){
	
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
	
}());