(function( $, app ){
	
	var dom = $(this.dom),
		actions = dom.find('h2 li'),
		canvas = dom.find('canvas.graph'),
		vm_data = null,
		show = this.show,
		
		/*
		 * Get a content panel for the specific piece of hardware.
		 */
		showDetail = function( detail ) {
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
		};
	
	/*
	 * Setup template
	 */
	dom.find( 'ul.hardware li' )
		.click( function() {
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
		console.log( e );
	});
	
	this.bind('vm_device_selected',function( e ){
		showDetail( e.source.dataset.devicetype );
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