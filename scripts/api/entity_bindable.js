/*
 * entity_bindable.js
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
	
	app.load('js://tools/bindable', 'js://net/xmconnection', function( Bindable, xm ) {
		xm = xm.getInstance();
		
		var handlers = [];
			EntityBindable = function( entity ) {
				var entityType = entity.xm_type.toLowerCase(),
					handler = null,
					attach = function( callback ) {
						var me = this;
						
						handler = function( data ) {
							var i = null;
							if( data.reference == me.reference && 
									data.entityType == entityType ) {
								for( i in data.changes ) {
									me[i] = data.changes[i];
								}
								callback();
							};
						};
						
						handlers.push( handler );
					},
					release = function( callback ) {
						var index = handlers.indexOf( handler );
						delete handlers[index];
					};
					
				return new Bindable( entity, attach, release );
			};
				
		xm.addHook( 'log', 'event', function( data ) {
			var i = null;
			for( i in handlers ) {
				handlers[i](data);
			};
		});
		
		ready( EntityBindable );
		
	});
	
});