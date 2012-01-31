(function( ready, app ) {
	
	/**
	 * Bindable declaration
	 * 
	 * Sits at either end of a binding, allowing automatic updating.
	 */
	var Bindable = function( src, evt_binder, evt_release ) {
			var me = this;
			
			this._handlers = [];
			this._evt = {
				bind: evt_binder || function(){},
				release: evt_release || function(){}
			};
			
			this._handler = function() {
				var i = null;
				for( i in me._handlers ) {
					me._handlers[i]();
				}
			};
			
			this.setSource( src );
		};
	
	/**
	 * Bind a handler that will be notified when src changes.
	 * A binding will attach its handler here.
	 * 
	 * @param handler
	 */
	Bindable.prototype.addHandler = function( handler ) {
		this._handlers.push( handler );
	};
	
	/**
	 * Detach a handler from being notified.
	 * 
	 * @param handler
	 */
	Bindable.prototype.removeHandler = function( handler ) {
		var index = this._handlers.indexOf( handler );
		delete this._handlers[ index ];
	};
	
	/**
	 * Release all attached handlers.
	 * 
	 */
	Bindable.prototype.releaseHandlers = function() {
		this._handlers = [];
	};
	
	/**
	 * Gets the source element that is about to change.
	 */
	Bindable.prototype.getSource = function() {
		return this._src;
	};
	
	/**
	 * Changes source for this bindable.
	 * This triggers a release on the source and a rebind.
	 */
	Bindable.prototype.setSource = function( src ) {
		// Release old handler
		if( this._src != null ) {
			this._evt.release.call( src, this._handler );
		}

		// Bind handler
		this._evt.bind.call( src, this._handler );
		this._src = src;
		
		this.update();
	};
	
	Bindable.prototype.update = function() {
		this._handler();
	};
	
	ready( Bindable );
	
});