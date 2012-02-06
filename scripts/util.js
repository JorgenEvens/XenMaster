/*
 * util.js
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
 * util.js
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