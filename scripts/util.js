(function(){
	
	
	
	Util = function(){};
	
	Util.isArray = function( value ) {
		return Object.prototype.toString.call(value) === "[object Array]";
	};
	
	Util.argumentsToArray = function( value ) {
		return Array.prototype.slice.call( value );
	};
	
}());