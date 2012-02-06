/*
 * bindable.js
 * Copyright (C) 2011,2012 Jorgen Evens
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

/*
 * bindable.js
 * Copyright (C) 2011,2012 Jorgen Evens
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

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