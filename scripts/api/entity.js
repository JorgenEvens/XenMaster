/*
 * entity.js
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
 * entity.js
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
	
	app.load( 'js://net/xmconnection', function( xm ){
		xm = xm.getInstance();
		
		var EntityProto = function(){},
			
			createReturnBase = function( returnType, list ){
				list = list || false;	
				
				var f = function(){};
				
				if( returnType ) {
					f.prototype = returnType.prototype;
				} else {
					f.prototype = EntityProto.prototype;
				}
				f.prototype = new f();
				
				f.prototype.go = function() {
					var args = Util.argumentsToArray( arguments ),
						callback = function(){},
						i = null;
						
					if( typeof args[args.length-1] === 'function' ) {
						callback = args.pop();
					}
					
					if( args.length == 1 && Util.isArray( args[0] ) ) {
						args = args[0];
					}
					
					// Convert entities to their reference
					for( i in args ) {					
						if( args[i] instanceof EntityProto && args[i].reference ) {
							args[i] = args[i].reference;
						}
					}
					
					this.send( this.xm_resource, args, function( result ) {
						var i = null;
	
						if( list ) {
							for( i in result ) {
								result[i] = new returnType( result[i] );
							}
						} else if( returnType ) {
							result = new returnType( result );
						}
						
						return callback( result );
					});
				};
				
				return f;
			},
		
			createMethod = function( name, resultType, list ) {
				var f = createReturnBase( resultType, list );
				
				this.prototype[name] = function(){
					var instance = new f();
					instance.xm_resource = this.xm_resource + '.' + name;
					instance.reference = this.reference;
					
					if( arguments.length > 0 ) {
						return instance.go.apply( instance, arguments );
					}
					
					return instance;
				};
			},
			
			createStaticMethod = function( name, resultType, list ) {
				var f = createReturnBase( resultType, list );
				
				this[name] = function(){
					var instance = new f();
					instance.xm_resource = this.xm_resource + '.' + name;
					
					if( arguments.length > 0 ) {
						return instance.go.apply( instance, arguments );
					}
					
					return instance;
				};
			},
		
			Entity = function( base ) {
				var Entity = function( data, callback ){			
					this.xm_resource = 'xen://' + base;
					this.xm_type = base;
					
					if( typeof data === 'object' ) {
						this.attach( data );
					} else if( data ) {
						this.reference = data;
						if( callback ) {
							this.go( callback );
						}
					}
				};
				
				Entity.xm_resource = 'xen://' + base;
				
				Entity.createMethod = createMethod;
				
				Entity.createStaticMethod = createStaticMethod;
				
				Entity.prototype = new EntityProto();
				
				return Entity;
			};
			
		EntityProto.prototype = {
			attach: function( data ) {
				var i = null;
				
				for( i in data ) {
					this[i] = data[i];
				}
			},
			go: function( callback ) {
				var me = this;
				
				this.send( this.xm_resource, function( result ){
					me.attach( result );
					callback( me );
				});
			},
			send: function() {
				var args = Util.argumentsToArray( arguments ),
					method = args.shift(),
					callback = null;
				
				if( typeof args[args.length-1] === 'function' ) {
					callback = args.pop();
				}
				
				if( args.length == 1 && Util.isArray( args[0] ) ) {
					args = args[0];
				}
				
				args = { args: args, ref: this.reference };
				
				if( callback ) {
					xm.send( method, args, callback );
				} else {
					xm.send( method, args );
				}
			}
		};
			
		ready( Entity );
	});
});