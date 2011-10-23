(function( $, app ){
	
	var dom = $(this.dom),
		canvas = dom.find('canvas.graph'),
		vm_data = null,
		show = this.show,
		showDetail = function( detail ) {
			app.load( 'tpl://detail/vm/' + detail, 'js://ui/template', 'js://tools/notifier', function( tpl_detail, Template, N ) {
				
				var view = new Template({ resource: tpl_detail });
				view.vm = vm_data;
				view.show( 'vm_detail_panel' );
				
			});
		},
		VMState = {
			RUNNING: 1,
			STOPPED: 2,
			PAUSED: 4,
			ABORTED: 8,
			REBOOT: 16
		},
		changeVMState = function( state ) {
			var vm = vm_data;
			
			app.load( 'js://net/xmconnection', 'js://tools/notifier', function( xm, Notifier ) {
				var action = null,
					args = null;
				
				if( state == VMState.RUNNING ) {
					action = 'start';
					args = [false];
				} else if ( state == VMState.STOPPED ) {
					action = 'stop';
					args = [true];
				} else if ( state == VMState.PAUSED ) {
					action = 'pause';
				} else if ( state == VMState.ABORTED ) {
					action = 'stop';
					args = [false];
				} else if ( state == VMState.REBOOT ) {
					action = 'reboot';
				} else {
					return;
				}
				
				Notifier.publish( vm.nameLabel, 'Changing state of virtual machine.' );
				
				xm.getInstance().send( 'xen://VM.' + action, { ref: vm.reference, args: args }, function( result ) {
					Notifier.publish( vm.nameLabel, 'Changed state to ' + action );
				});
			});
		};
	
	dom.find( 'ul.hardware li' )
		.click( function() {
			dom.find( 'ul.hardware li' ).removeClass( 'selected' );
			$(this).addClass( 'selected' );
		});
	
	this.capture( 'click' );
	
	this.bind('vm_device_selected',function( e ){
		showDetail( e.source.dataset.devicetype );
	});
	
	this.bind( 'vm_state', function( e ){
		console.log( VMState[e.source.dataset.state], e.source.dataset.state );
		changeVMState( VMState[e.source.dataset.state] );
	});
	
	this.show = function( vm ) {
		show.call(this);
		vm_data = vm;
		
		console.log( vm );
		
		dom
			.find('.vm_name')
			.text( vm.nameLabel );
		
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