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
		vm_entity = null,
		
		vm_bindable = null,
		
		ctl = {
			// General
			name: dom.find('#vm_name'),
			description: dom.find( '#vm_description' ),
			poweron: dom.find( '#vm_poweron' ),
			
			// Virtualization type
			type: dom.find('#vm_type'),
			
			// HVM Virtualization
			hvm: {
				hdd: dom.find( '#hvm_boot_harddisk' ),
				cd: dom.find( '#hvm_boot_disk' ),
				net: dom.find( '#hvm_boot_network' )
			},
			
			// PV Virtualization
			pv: {
				kernel: dom.find('#pv_kernel'),
				ramdisk: dom.find('#pv_ramdisk')
			}
			
		},
		
		// Property updating functions
		update = {
			hvm: {},
			pv: {}
		},
		
		pv_data_loaded = false,
		
		loadKernels = function( host ){
			app.load( 'js://api/plugins/filesystem', function( FS ){
				FS.getKernels(host, function( kernels ){
					var i = null,
						option = null;
					
					for( i in kernels ) {
						i = kernels[i];
						
						option = $('<option></option>').val( i )
							.text(i)
							.appendTo( ctl.kernel );
						
						if( i == vm_entity.pvKernel ) {
							option.attr( 'selected', 'selected' );
						}
					}
				});
			});
		},
		
		loadRamdisks = function( host ){
			app.load( 'js://api/plugins/filesystem', function( FS ){
				FS.getRamdisks(host, function( disks ){
					var i = null,
						option = null;
					
					for( i in disks ) {
						i = disks[i];
						
						option = $('<option></option>').val( i )
							.text(i)
							.appendTo( ctl.ramdisk );
						
						if( i == vm_entity.pvRamdisk ) {
							option.attr( 'selected', 'selected' );
						}
					}
				});
			});
		},
		
		updateBinding = function() {
			if( !vm_bindable ) {
				return createBinding();
			}
			
			vm_bindable.setSource( vm_entity );
		},
		
		createBinding = function() {
			app.load( 'js://api/entity_bindable', 'js://ui/jquery_bindable', 'js://tools/binding',
					function( bindEntity, bindjQuery, Binding ) {
				
				vm_bindable = bindEntity( vm_entity );
				
				
				var vm = vm_bindable,
				
				/**
				 * Custom methods for manipulating get and set.
				 */
					checked = {
						get: function() { return this.is(':checked'); },
						set: function( v ) { v ? this.attr('checked','checked') : this.removeAttr('checked'); }
					},
					bootPolicy = {
						get: function() { return this.val() == 'hvm' ? 'BIOS order' : ''; },
						set: function( v ) { this.val( v.length > 0 ? 'hvm' : 'pv' ); }
					};
				
				new Binding( vm, {get:'name',set:'setName'}, bindjQuery( ctl.name ), 'val', true );
				new Binding( vm, {get:'description',set:'setDescription'}, bindjQuery( ctl.description ), 'val', true );
				new Binding( vm, {get:'autoPowerOn',set:'autoPowerOn'}, bindjQuery( ctl.poweron ), checked, true );
				new Binding( vm, {get:'hvmBootPolicy',set:'setHVMBootPolicy'},bindjQuery( ctl.type ), bootPolicy, true );
				new Binding( vm, {get:'pvKernel',set:'setPVKernel'},bindjQuery( ctl.pv.kernel ), 'val', true );
				new Binding( vm, {get:'pvRamdisk',set:'setPVRamdisk'},bindjQuery( ctl.pv.ramdisk ), 'val', true );
			});
		};
		
	update.hvm.bootOrder = function() {
		boot_order = '';
		
		for( i; i<3; i++ ) {
			if( 'hvm_boot[' + i + ']' in data ) {
				boot_order += data['hvm_boot[' + i + ']'];
			}
		}
	};
	
	// Event handling
	tpl.capture(['click','change']);
	
	tpl.bind( 'tpl_show', function( e ){
		vm_entity = tpl.vm;
		
		updateBinding();
		
		var bootorder = vm_entity.hvmBootParam?vm_entity.hvmBootParam.order:'';		
		
		ctl.hvm.cd
			.attr('checked', bootorder.indexOf( 'd' ) > -1 );
		
		ctl.hvm.hdd
			.attr('checked', bootorder.indexOf( 'c' ) > -1 );
		
		ctl.hvm.net
			.attr('checked', bootorder.indexOf( 'n' ) > -1 );
		
		ctl.pv.kernel.val( vm_entity.pvKernel );
		ctl.pv.ramdisk.val( vm_entity.pvRamdisk );
		
		ctl.type.change();
	});
	
	tpl.bind( 'hvm_boot_up', function( e ) {
		var elem = $(e.source),
			checkbox = elem.siblings('input:checkbox'),
			container = elem.parent(),
			id = null,
			regex = /\[(\d+)\]/,
			prev = container.prev('.hvm_boot_option');

		if( prev ) {
			id = regex.exec( checkbox.attr('name') )[1];
			checkbox.attr('name', 'hvm_boot[' + (id-1) + ']');
			
			prev
				.before( container )
				.find('input:checkbox')
					.attr('name', 'hvm_boot[' + id + ']');
		}
	});
	
	tpl.bind( 'hvm_boot_down', function( e ) {
		var elem = $(e.source),
			checkbox = elem.siblings('input:checkbox'),
			container = elem.parent(),
			id = null,
			regex = /\[(\d+)\]/,
			next = container.next('.hvm_boot_option');

		if( next ) {
			id = regex.exec( checkbox.attr('name') )[1];
			checkbox.attr('name', 'hvm_boot[' + (id+1) + ']');
			
			next
				.after( container )
				.find('input:checkbox')
					.attr('name', 'hvm_boot[' + id + ']');
		}
	});
	
	tpl.bind('vm_type_changed',function( e ){
		dom.find('.type')
			.hide()
			.filter('.' + ctl.type.val() )
				.show();
		
		if( ctl.type.val() == 'pv' && !pv_data_loaded ) {
			pv_data_loaded = true;
			app.load( 'js://api/session', function( Session ){
				Session.getThisHost(function( host ){
					loadKernels(host);
					loadRamdisks(host);
				});
			});
		}
	});
});