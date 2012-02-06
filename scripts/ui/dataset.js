/*
 * dataset.js
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
	
	var Container = function( element ) {
			var now = new Date();
			
			this.element = element;
			
			this.id = now.getMilliseconds() + '.' + Math.round(Math.random()*now.getSeconds()*100);
			this.data = {};
			
			element.dataset.dsid = this.id;
			Dataset.sets[this.id] = this;
		},
		Dataset = function(){
			throw "Dataset should not be instantiated";
		};
		
	Dataset.sets = {};
	
	Dataset.get = function( element ) {
		var id = null,
			container = null;
		
		if( element == null ) {
			return null;
		}
		
		id = element.dataset.dsid;
		
		if( id == null || !Dataset.sets[ id ] ) {
			container = new Container(element);
			return container.data;
		}
		
		return Dataset.sets[ id ].data;
	};
	
	Dataset.exists = function( element ) {
		var id = null;
		
		if( element == null ) {
			return false;
		}
		
		id = element.dataset.dsid;

		if( id == null || !Dataset.sets[ id ] ) {
			return false;
		}
		
		return true;
	};
	
	ready( Dataset );
	
});