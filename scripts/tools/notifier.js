/*
 * notifier.js
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