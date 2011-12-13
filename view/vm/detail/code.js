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
		showDetail = function( detail ) {
			if( !detail ) return;
			
			app.load( 'tpl://vm/detail/' + detail, 'js://ui/template', 'js://tools/notifier', function( tpl_detail, Template, N ) {
				
				var view = new Template({ resource: tpl_detail });
				view.vm = vm_data;
				view.show( 'vm_detail_panel' );
				
			});
		},
		
		/*
		 * Enum of possible VM states.
		 */
		VMState = {
			RUNNING: [ 'start', [false], 'running' ],
			STOPPED: [ 'stop', [true], 'stopped' ],
			ABORTED: [ 'stop', [false], 'aborted' ],
			PAUSED: [ 'pause', [], 'paused' ],
			SUSPENDED: [ 'suspend', [], 'suspended' ],
			REBOOT: [ 'reboot', [], 'rebooting' ]
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
			dom.find('.hardware .harddisk').remove();
			app.load( 'js://api/vbd', function( VBD ){
				var i = null,
					add = $('.hardware .add');
				
				add.siblings('.disk')
					.remove();
				
				for( i in vm_data.VBDs ) {
					(new VBD(vm_data.VBDs[i])).getVDI(function( vdi ){
						var alternateName = 'HDD ( ' + (vdi.virtualSize/(1024*1024)) + 'MB )',
							type = vdi.type=='iso' ? 'disk' : 'harddisk';
						
						$('<li></li>')
							.addClass(type)
							.attr('data-devicetype',type)
							.insertBefore( add )
							.text( vdi.name||alternateName );
					});
				}
			});
		},
		
		/*
		 * Load VIF information and display it.
		 */
		loadVIFs = function(){
			Util.chain(
				function(){
					app.load( 'js://api/network', this.next );
				},
				function( Network ){
					this.add = $('.hardware .add');
					this.Network = Network;
					
					vm_data.getVIFs( this.next );
				},
				function( vifs ) {
					var Network = this.Network,
						add = this.add;
					
					add.siblings('.nic')
						.remove();
					
					for( i in vifs ) {
						(function( vif ){
							new Network( vif.network, function( net ) {
								alternateName = 'NIC ' + vif.deviceIndex + ': ' + net.name;
								
								$('<li></li>')
									.addClass('nic')
									.attr('data-devicetype','nic')
									.insertBefore( add )
									.text( i.name||alternateName );
							});
						}(vifs[i]));
					}
				}
			).start();
		};
		
	
	/*
	 * Setup template
	 */
	dom.find( 'ul.hardware' )
		.delegate( 'li', 'click', function() {
			dom
				.find( 'ul.hardware li' )
				.removeClass( 'selected' );
			
			$(this).addClass( 'selected' );
		});
	
	/*
	 * Capture commands on click
	 */
	this.capture( ['click','keydown'] );
	
	this.bind( 'ui_alternate', function( e ){

	});
	
	this.bind('vm_device_selected',function( e ){
		showDetail( e.source.dataset.devicetype );
	});
	
	this.bind('vm_device_add', function( e ) {
		app.load( 'tpl://vm/device/new', 'js://ui/template', 'js://tools/notifier', function( tpl_add, Template, N ) {
			var view = new Template({ resource: tpl_add });
			view.bind( 'vm_device_ready', function( e ) {
				tpl.show( vm_data );
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
	
	this.show = function( vm ) {
		show.call(this);
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