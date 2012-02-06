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
		dom = $(tpl.dom);
	
	app.load( 'js://api/sr', function( SR ) {
		SR.getAll(function( repos ){
			var i = null,
				list = dom.find( '#dev_repository' );
			for( i in repos ) {
				i = repos[i];
				if( i.contentType != 'user' ) continue;
				
				$('<option></option>')
					.appendTo( list )
					.val( i.reference )
					.text( i.name );
			}
		});
	});
	
	this.onshow = function(){
		$('#dev_name').val( tpl.vm.name );
	};

	tpl.capture('click');
	
	tpl.on( 'dev_create', function() {
		var data = {};
		
		dom.find('input,select,textarea').each(function(){
			var me = $(this),
				name = me.attr('name'),
				val = me.val();
			
			if( val && name ) {
				data[name] = val;
			}
		});
		
		Util.chain(
			function(){
				app.load( 'js://api/vdi', 'js://api/vbd', this.next );
			},
			function( VDI, VBD ) {
				this.VBD = VBD;
				
				VDI.build({name:data.name}, this.next );
			},
			function( vdi ) {
				vdi.create( data.size, 'USER', data.repo, true, false, this.next );
			},
			function( vdi ) {
				this.vdi = vdi;
				
				this.VBD.build({mode:'RW',type:'DISK'}, this.next);
			},
			function( vbd ) {
				vbd.create( tpl.vm, this.vdi, '', this.next );
			},
			function( vbd ) {
				tpl.vm.vbds.push(vbd.reference);
				tpl.action( 'device_ready', vbd );
			}
		).start();
		
	});
	
});