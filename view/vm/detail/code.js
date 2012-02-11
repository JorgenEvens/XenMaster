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
		dom = $(this.dom),
		actions = dom.find('h2 li'),
		canvas = dom.find('canvas.graph'),
		vm_entity = null,
		vm_bindable = null,
		show = this.show,
		
		/*
		 * Get a content panel for the specific piece of hardware.
		 */
		showDetail = function( detail, device, tab ) {
			if( !detail ) return;
			console.log( tab );
			$(tab)
				.addClass('selected')
				.siblings()
					.removeClass( 'selected' );
			
			app.load( 'tpl://vm/detail/' + detail, 'js://ui/template', function( tpl_detail, Template ) {
				
				var view = new Template({ resource: tpl_detail });
				view.vm = vm_entity;
				view.device = device;
				view.show( 'vm_detail_panel' );
				
			});
		},
		
		/*
		 * Enum of possible VM states.
		 */
		VMState = {
			RUNNING: [ 'start', [false, false], 'running' ],
			RESUME: [ 'resume', [], 'running' ],
			STOPPED: [ 'stop', [true], 'stopped' ],
			ABORTED: [ 'stop', [false], 'aborted' ],
			PAUSED: [ 'pause', [], 'paused' ],
			SUSPENDED: [ 'suspend', [], 'suspended' ],
			REBOOT: [ 'reboot', [false], 'rebooting' ]
		},
		
		/*
		 * Handles state changes of the VM
		 */
		changeVMState = function( state ) {
			var vm = vm_entity;
			
			app.load( 'js://tools/notifier', function( Notifier ) {
				var action = state[0],
					args = state[1];
				
				Notifier.publish( 'XenMaster', 'CHANGE', vm.name, 'Changing state of virtual machine to <b>' + state[2] + '</b>.' );
				
				vm[action](args, function() {
					Notifier.publish( vm.name, 'State changed to <b>' + state[2] + '</b>' );
				});
			});
		},
		
		/*
		 * Load VDIs for VBDs and display them.
		 */
		loadVBDs = function() {
			var add = $('.hardware .add');			
			
			Util.chain(
				function() {
					app.load('js://ui/dataset', this.next );
				},
				function( Dataset ) {
					this.Dataset = Dataset;
					
					vm_entity.getVBDs( this.next );
				},
				function( vbds ) {
					var Dataset = this.Dataset;
						i = null;
					
					add.siblings( '.disk, .harddisk' ).remove();
					for( i in vbds ) {
						(function( vbd ){
							vbd.getVDI(function(vdi){
								var alternateName = 'HDD ( ' + (vdi.virtualSize/(1024*1024)) + 'MB )',
									type = vbd.type=='CD' ? 'disk' : 'harddisk',
									el = $('<li></li>')
										.addClass(type)
										.attr('data-devicetype',type)
										.insertBefore( add )
										.text( vdi.name||alternateName ),
									data = Dataset.get( el.get(0) );
								
								data.device = vdi;
							});
						}(vbds[i]));
					}
				}
			).start();
		},
		
		/*
		 * Load VIF information and display it.
		 */
		loadVIFs = function(){
			Util.chain(
				function(){
					app.load( 'js://api/network', 'js://ui/dataset', this.next );
				},
				function( Network, Dataset ){
					this.add = $('.hardware .add');
					this.Network = Network;
					this.Dataset = Dataset;
					
					vm_entity.getVIFs( this.next );
				},
				function( vifs ) {
					var Network = this.Network,
						Dataset = this.Dataset,
						add = this.add;
					
					add.siblings('.nic').remove();
					
					for( i in vifs ) {
						(function( vif ){
							new Network( vif.network, function( net ) {
								var alternateName = 'NIC ' + vif.deviceIndex + ': ' + net.name,
									el = $('<li></li>')
										.addClass('nic')
										.attr('data-devicetype','nic')
										.insertBefore( add )
										.text( i.name||alternateName ),
									data = Dataset.get( el.get(0) );
								
								data.device = vif;
							});
						}(vifs[i]));
					}
				}
			).start();
		},
		
		stateButtonMap = {
			'HALTED': actions.filter('.start'),
			'PAUSED': actions.filter('.resume, .stop, .reboot, .kill'),
			'RUNNING': actions.filter('.stop, .reboot, .pause, .kill, .suspend'),
			'SUSPENDED': actions.filter('.start, .stop, .reboot, .kill')
		},
		
		setState = function( state ) {
			var hvm = vm_entity.hvmBootPolicy.length > 0,
				buttons = stateButtonMap[state];
			
			dom.find('.vm_state span').text( state );
			actions.hide();
			if( hvm ) {
				buttons.not('.stop').show();
			} else {
				buttons.not('.kill').show();
			}
		},
		
		updateBinding = function() {
			if( !vm_bindable ) {
				return createBinding();
			}
			
			vm_bindable.setSource( vm_entity );
		},
		
		createBinding = function() {
			app.load( 'js://api/entity_bindable', 'js://tools/bindable', 'js://ui/jquery_bindable', 'js://tools/binding',
					function( bindEntity, Bindable, bindjQuery, Binding ) {
				
				vm_bindable = bindEntity( vm_entity );
				
				new Binding( vm_bindable, 'powerState', new Bindable({state:setState}), 'state' );
				new Binding( vm_bindable, 'name', bindjQuery( dom.find('.vm_name') ), 'text' );
			});
		};
		
	
	/*
	 * Setup template
	 */
	
	/*
	 * Capture commands on click
	 */
	this.capture( ['click','keydown'] );
	
	this.bind('vm_device_selected',function( e ){

		app.load( 'js://ui/dataset', function( Dataset ) {
			var data = Dataset.get( e.source );
			showDetail( e.source.dataset.devicetype, data.device, e.source );
		});
	});
	
	// TODO: Fixup dev_ready more cleanly
	// TODO: Fixup vm_device_add to use backend event based updating instead of the template event.
	var dev_ready = null;
	this.bind('vm_device_add', function( e ) {
		app.load( 'tpl://vm/device/new', 'js://ui/template', function( tpl_add, Template ) {
			var view = new Template({ resource: tpl_add });
			dev_ready = function( e ) {
				view.unbind( 'vm_device_ready', dev_ready );
				tpl.vm = vm_entity;
				tpl.show();
			};	
			
			/*
			 * Device has been created.
			 */
			view.bind( 'vm_device_ready', dev_ready);
			
			view.vm = vm_entity;
			
			view.show( 'vm_detail_panel' );
			
		});
	});
	
	this.bind( 'vm_state', function( e ){
		changeVMState( VMState[e.source.dataset.state] );
	});
	
	this.bind( 'vm_delete', function( e ) {
		
		app.load( 'tpl://vm/detail/delete', 'js://ui/template', function( tpl_del, Template ){
			var view_del = new Template({resource: tpl_del});
			view_del.capture( 'click' );
			view_del.bind( 'vm_delete', function( e ) {
				vm_entity.destroy(); // TODO: Shouldn't this be a forget?
				dom.detach();
			});
			view_del.bind( 'vm_delete_close', function( e ) {
				showDetail('general', vm_entity, $('.hardware .general'));
			});
			view_del.show( 'vm_detail_panel' );
		}); 
		
	});
	
	/*$(document).keydown(function(e){
		if( e.keyCode != 16 ) return;
		actions.parent('.onshift').show();
		actions.parent(':not(.onshift)').hide();
	});
	
	$(document).keyup(function(e){
		actions.parent('.onshift').hide();
		actions.parent(':not(.onshift)').show();
	});*/
	
	this.onshow = function() {
		var vm = tpl.vm;
		vm_entity = vm;
		
		updateBinding();
		
		loadVBDs();
		loadVIFs();
		
		showDetail('general', vm_entity, $('.hardware .general'));
		
		app.load('js://graphics/graph/linechart', function( Linechart ) {
			var chart = new Linechart({
				canvas: canvas.get(0),
				dataset: [0,0],
				style: {
					yAxis: false,
					yAxisWidth: 0,
					yAxisMax: 100,
					yAxisMin: 0,
					xAxisHeight: 0,
					pointColor: 'navy',
					lineWidth: 2,
					lineColor: '#5151AA'
				}
			});
		});
	};
});