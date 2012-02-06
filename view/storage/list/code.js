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

(function( $, app ){
	var tpl = this,
		dom = $(tpl.dom),
		base = dom.find('.repo tr:first').clone();
	
	tpl.capture( 'click' );
		
	app.load( 'js://api/sr', function( SR ) {
		
		tpl.on( 'sr_remove', function() {
			dom.find( '.repo input:checked' ).each(function(){
				var ref = $(this).val();
				
				if( ref.length > 0 ) {
					(new SR(ref)).destroy(function( result ){
						if( console ) console.log( result );
					});
				}
			});
		});
		
		tpl.on( 'sr_detach', function() {
			dom.find( '.repo input:checked' ).each(function(){
				var ref = $(this).val();
				
				if( ref.length > 0 ) {
					(new SR(ref)).destroy(function( result ){
						if( console ) console.log( result );
					});
				}
			});
		});
		
		tpl.on( 'sr_new', function(){
			app.load( 'js://ui/template', 'tpl://storage/new', function( Template, tpl ){
				var view = new Template({resource: tpl});
					view.show( 'sr_create' );
			});
		});
		
		base.find('td').filter(':not(.selection)').html('');
	});
	
	tpl.on( 'tpl_show', function() {
		app.load( 'js://api/sr', function( SR ) {
			dom.find('.repo').html('');
			SR.getAll( function( result ) {
				for( item in result ) {
					item = result[item];
					
					var row = base.clone(),
						fields = row.find('td');
					
					fields.filter('.selection').find('input').val( item.reference );
					fields.filter('.name').html( item.name );
					fields.filter('.location').html( item.otherConfig.location );
					fields.filter('.type').html( item.type );
					
					dom.find('.repo').append( row );
				}
			});
		});
	});
})