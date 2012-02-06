/*
 * binding.js
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
 * binding.js
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
	
	var Binding = function( src, src_prop, target, target_prop, two_way ) {		
			if( typeof src_prop == 'string' ) {
				src_prop = {
					get: src_prop,
					set: src_prop
				};
			}
			
			if( typeof target_prop == 'string' ) {
				target_prop = {
					get: target_prop,
					set: target_prop
				};
			}

			this._two_way = !!two_way;
			
			this._src = {
				o: src,
				prop: src_prop,
				handler: null
			};
			
			this._target = {
				o: target,
				prop: target_prop,
				handler: null
			};
			
			this.attach();
		},
		TARGET = 'target',
		SOURCE = 'src',
		
		getValue = function( obj, value ) {
			var src = obj.o.getSource(),
				prop = obj.prop.get,
				type = getPropType( src, prop );

			if( type == 'func' ) {
				return prop.apply( src );
			} else if( type == 'method' ) {
				return src[prop]();
			};
			return src[prop];
		},
		
		setValue = function( obj, value ) {
			var src = obj.o.getSource(),
				prop = obj.prop.set,
				type = getPropType( src, prop );
			
			if( type == 'func' ) {
				prop.call( src, value );
			} else if( type == 'method' ) {
				src[prop]( value );
			} else {
				src[prop] = value;
			}
		},
		
		getPropType = function( obj, prop ) {
			if( typeof prop == 'function' ) {
				return 'func';
			} else if( typeof obj[prop] == 'function' ) {
				return 'method';
			}
			return 'prop';
		};
		
	Binding.prototype.attach = function() {
		var me = this,
			src = this._src,
			target = this._target;
		
		src.handler = function() {
			me.set( TARGET );
		};
		src.o.addHandler( src.handler );
		
		if( this._two_way ) {
			target.handler = function() {
				me.set( SOURCE );
			};
			target.o.addHandler( target.handler );
		}

		this.update();
	};
	
	Binding.prototype.update = function() {
		this.set( TARGET );
	};
	
	Binding.prototype.release = function() {
		var src = this._src,
			target = this._target;
		
		src.o.removeHandler( src.handler );
		
		if( target.handler ) {
			target.o.removeHandler( target.handler );
		}
	};
	
	Binding.prototype.set = function( direction ) {
		var target = this['_' + direction],
			source = this['_' + (direction==TARGET?SOURCE:TARGET)];
		
		setValue( target, getValue( source ) );
	};
	
	ready( Binding );
	
});