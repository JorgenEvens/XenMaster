/*
 * modernizr.js
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
 * modernizr.js
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
	var path = 'scripts/lib/modernizr.lib.js',
		head,
		script;

	/*
	 * If window.Modernizr is already loaded, notify the cache of this.
	 */
	if( window.Modernizr ) {
		ready( window.Modernizr );
	} else {
		/*
		 * Append the jQuery script to the head tag.
		 */
		head = Util.head;
		script = Util.create( 'script' );
		script.src = path;
		
		Util.on( 'load', script, function(){
			// jQuery failed loading for some reason, to bad :(
			if( !window.Modernizr ) {
				throw 'Unable to load the window.Modernizr library';
			}
			
			ready( window.Modernizr );
		});
		
		head.appendChild( script );
	}
});