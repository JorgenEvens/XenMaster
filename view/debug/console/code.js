/*
 * code.js
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

(function( $, app ) {
	
	var dom = $(this.dom),
		cmd = dom.find('#command'),
		ref = dom.find('#reference'),
		data = dom.find('#data'),
		debug_pre = dom.find('#debug'),
		
		print = function( obj, depth ) {
			depth = depth||'';
			
			var is_array = Util.isArray( obj ),
				otag = is_array ? '[' : '{',
				ctag = is_array ? ']' : '}',
				inset = '    ',
				first = true;
			
			if( typeof obj != 'object' ) {
				debug_pre.append(obj);
			} else {
				debug_pre.append(otag);
				depth += inset;
				for( i in obj ) {
					if( first ) { debug_pre.append( "\n" ); first = false; }
					
					if( obj.hasOwnProperty( i ) ) {
						if( typeof obj[i] == 'object' ) {
							debug_pre.append( depth + '<b>' + i + '</b>' + ': ' );
							print( obj[i], depth );
						} else {
							debug_pre.append( depth + '<b>' + i + '</b>' + ': ' + obj[i] + ",\n");
						}
					}
				}
				depth = depth.substring(0,depth.length-inset.length);
				if( !first ) {
					debug_pre.append(depth+ctag+",\n" );
				} else {
					debug_pre.append(ctag+",\n");
				}
			};
		
		};
	
	app.load( 'js://net/xmconnection', function( xm ) {
		xm = xm.getInstance();
		
		dom.find('.submit').click(function(){
			var args = JSON.parse( '[' + data.val() + ']' ),
				reference = null,
				param;
			
			if( ref.val().length > 0 ) {
				reference = ref.val();
			}
			
			param = { args: args.length > 0 ? args : null, ref: reference };
			
			xm.send('xen://' + cmd.val(), param, function( result ) {
				if( console ) console.log( result );
				debug_pre.text('');
				print( result );			
			});
		});
	});
	
	app.load( 'js://tools/bindable', function( Bindable ) {
		var obj1 = $('<div>test</div>'),
			obj2 = $('<span>abc</span>'),
			
			b_obj1 = new Bindable( 'jQuery', obj1, 'text', 'text', 'change' ),
			b_obj2 = new Bindable( 'jQuery', obj2, 'text', 'text' );
		
		b_obj1.link( b_obj2 );
		
		console.log( 'obj1.text(): ', b_obj1.get() );
		console.log( 'obj2.text(): ', b_obj2.get() );
		
		console.log( 'obj1.change(): ', obj1.change() );
		
		console.log( 'obj1.text(): ', b_obj1.get() );
		console.log( 'obj2.text(): ', b_obj2.get() );
	});
	
});