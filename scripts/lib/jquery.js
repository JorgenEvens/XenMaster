/*
 * jquery.js
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
 * jquery.js
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

(function( ready ){
	var path = 'http://ajax.googleapis.com/ajax/libs/jquery/1.7.1/jquery.min.js',
		local = 'scripts/lib/jquery.lib.js',
		head = Util.head,
		script = null,
		
		success = function(){
			// jQuery failed to load for some reason, to bad :(
			if( !window.jQuery ) {
				throw 'Error while loading jQuery.';
			}
			
			ready( window.jQuery );
		},
		
		error = function() {
			/*
			 * Remove script tag from page
			 */
			Util.head.removeChild( script );
			
			if( script.src != local ) {
				/*
				 * Try a local copy of jQuery
				 */
				load( local );
			} else {
				throw 'Unable to load the jQuery library';
			}
		},
		
		load = function( path ) {
			/*
			 * Append the jQuery script to the head tag.
			 */
			script = Util.create( 'script' );
			script.src = path;
			
			Util.on( 'error', script, error );
			Util.on( 'load', script, success );
			
			head.appendChild( script );
		};
	
	/*
	 * If jQuery is already loaded, notify the cache of this.
	 */
	if( window.jQuery ) {
		ready( window.jQuery );
	} else {
		load( path );
	}
});