(function( ready, app ){
	
	var instance = null,
		subscribers = [],
		Notifier = function(){
			if( instance ) {
				return instance;
			}
			
			instance = this;
		};
		
	Notifier.prototype.subscribe = function( callback ) {
		subscribers.push( callback );
	};
	
	Notifier.prototype.unsubscribe = function( callback ) {
		var i = null;

		for( i in subscribers ) {
			if( subscribers[i] == callback ) {
				delete( subscribers[i] );
				return;
			}
		}
	};
	
	Notifier.prototype.publish = function( app_key, msg_type, message, data ) {
		var i = null;
		
		for( i in subscribers ) {
			subscribers[i]( app_key, msg_type, message, data );
		}
	};
	
	ready( new Notifier() );
	
});