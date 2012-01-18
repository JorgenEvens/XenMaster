(function( $, app ){
	
	var tpl = this,
		dom = $(this.dom),
		actions = dom.find('h2 li'),
		canvas = dom.find('canvas.graph'),
		vm_data = null,
		show = this.show,
		
		/*
		 * Get a content panel for the specific piece of hardware.
		 */
		showDetail = function( detail, device ) {
			if( !detail ) return;
			
			app.load( 'tpl://vm/detail/' + detail, 'js://ui/template', 'js://tools/notifier', function( tpl_detail, Template, N ) {
				
				var view = new Template({ resource: tpl_detail });
				view.vm = vm_data;
				view.device = device;
				view.show( 'vm_detail_panel' );
				
			});
		},
		
		/*
		 * Enum of possible VM states.
		 */
		VMState = {
			RUNNING: [ 'start', [false, false], 'running' ],
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
			var vm = vm_data;
			
			app.load( 'js://tools/notifier', function( Notifier ) {
				var action = state[0],
					args = state[1];
				
				Notifier.publish( vm.name, 'Changing state of virtual machine to <b>' + state[2] + '</b>.' );
				
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
					
					vm_data.getVBDs( this.next );
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
					
					vm_data.getVIFs( this.next );
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
		};
		
	
	/*
	 * Setup template
	 */
	
	/*
	 * Capture commands on click
	 */
	this.capture( ['click','keydown'] );
	
	this.bind('vm_device_selected',function( e ){
		$(e.source)
			.addClass('selected')
			.siblings()
				.removeClass( 'selected' );
		
		app.load( 'js://ui/dataset', function( Dataset ) {
			var data = Dataset.get( e.source );
			showDetail( e.source.dataset.devicetype, data.device );
		});
	});
	
	this.bind('vm_device_add', function( e ) {
		app.load( 'tpl://vm/device/new', 'js://ui/template', 'js://tools/notifier', function( tpl_add, Template, N ) {
			var view = new Template({ resource: tpl_add });
			
			/*
			 * Device has been created.
			 */
			view.bind( 'vm_device_ready', function( e ) {
				tpl.show( vm_data ); // reload view.
			});
			
			view.vm = vm_data;
			
			view.show( 'vm_detail_panel' );
			
		});
	});
	
	this.bind( 'vm_state', function( e ){
		changeVMState( VMState[e.source.dataset.state] );
	});
	
	$(document).keydown(function(e){
		if( e.keyCode != 16 ) return;
		actions.parent('.onshift').show();
		actions.parent(':not(.onshift)').hide();
	});
	
	$(document).keyup(function(e){
		actions.parent('.onshift').hide();
		actions.parent(':not(.onshift)').show();
	});
	
	this.onshow = function() {
		var vm = tpl.vm;
		vm_data = vm;
		
		loadVBDs();
		loadVIFs();
		
		dom
			.find('.vm_name')
			.text( vm.name );
		
		showDetail( 'general' );
		
		app.load('js://graphics/graph/linechart', function( Linechart ) {
			var chart = new Linechart({
				canvas: canvas.get(0),
				dataset: [99,33,66,33,33,12,23,12,12,12,12,12,21,45,32,66,77],
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