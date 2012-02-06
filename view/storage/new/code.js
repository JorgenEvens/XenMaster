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

(function( $, app){
	
	var tpl = this,
		dom = $(tpl.dom),
		
		showFields = function(){
			dom.find( '> div:not(.form_line)' ).hide();
			dom.find( '.' + dom.find('#sr_type').val().toLowerCase() ).show();
		},
		
		creation = {},
		
		clearFields = function() {
			dom.find('input[type!=button],select,textarea').val('');
		},
		
		creationReady = function( sr ) {
			clearFields();
			app.load( 'tpl://storage/list', 'js://ui/template', function( list, Template ){
				var list_ui = new Template({resource: list});
				list_ui.show();
			});
			dom.detach();
		};
		
	creation.nfs = function( name, host, data ) {
		var description = type == 'iso' ? 'ISO Repository: ' + name : name,
			type = data.content;
		delete data.content;
		
		Util.chain(
			function(){
				app.load( 'js://api/sr', this.next );
			},
			function( SR ) {
				SR.build({
					name: name,
					description: description,
					smconfig: {
						location: data.host + ':' + data.path
					},
					otherConfig: {
						storageType: 'nfs'
					}
				}, this.next );
			},
			function( sr ) {
				sr.create( host, {
					server: data.host,
					serverpath: data.path,
					location: data.host + ':' + data.path
				}, type, type=='ISO'?'iso':'user', true, 0, this.next );
			},
			function( sr ) {
				creationReady( sr );
			}
		).start();
	};
	
	creation.iscsi = function( name, host, data ) {
		app.load( 'js://api/helpers/iscsi', 'js://api/sr',
				function( iSCSI, SR ){
			var iscsi = null,
				sr = null,
			
				handler = function(){
					if( !iscsi || !sr ) return;
					
					sr.create( host, iscsi, 'user', true, 0,
							function( r ) {
						creationReady();
					});
				};
			
			if( data.port ) {
				data.port = parseFloat( data.port );
			}
			if( data.LUNs ) {
				data.LUNs = data.LUNs.split(',');
			}
			
			iSCSI.build( data, function( result ) {
				iscsi = result;
				handler();
			});
			
			SR.build({name: name}, function( result ) {
				sr = result;
				handler();
			});
		});
	};
	
	creation.partition = function( name, host, data, typye ) {
		app.load( 'js://api/sr', function( SR ) {
			SR.build({name: name}, function( sr ) {
				sr.create( host, data, 'Ext', 'user', true, 0, function( result ){
					creationReady();
				});
			});
		});
	};
	
	creation.directory = function( name, host, data ) {
		app.load( 'js://api/sr', function( SR ) {
			SR.build({name: name}, function( sr ) {
				sr.create( host, data, 'File', 'user', true, 0, function( result ){
					creationReady();
				});
			});
		});
	};
	
	creation.lvm = function( name, host, data ) {
		data = {
			device: data.volumes
		};
		
		app.load( 'js://api/sr', function( SR ) {
			SR.build({name: name}, function( sr ) {
				sr.create( host, data, 'Lvm', 'user', true, 0, function( result ){
					creationReady();
				});
			});
		});
	};
	
	dom.find('select').change(showFields);
	showFields();
	
	tpl.capture( 'click' );
	
	tpl.on( 'sr_create', function() {
		var info = {},
			name = dom.find('#sr_name').val(),
			type = dom.find('#sr_type').val().toLowerCase();
		
		dom
			.find('.' + type + ' input, .' + type + ' select')
			.each(function(){
				var item = $(this);
				if( item.val().length < 1 ) return;
				
			info[item.attr('name')] = item.val();
		});
		
		app.load( 'js://api/session', function( Session, Helper ) {
			Session.getThisHost(function( host ){
				if( typeof creation[ type ] === 'function' ) {
					creation[type]( name, host, info );
				}
			});
		});
		
	});
	
	tpl.on( 'tpl_show', function(){
		clearFields();
	});
	
})