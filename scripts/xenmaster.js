(function( $, undefined ){
		
	$(document).ready(function(){
		MASTER = new Application({
			'proxy': 'resources.php',
			'base': '/code',
			'buffer_timeout': 100
		});
		
		M = MASTER;
		
		M.load( 'js://graphics/graph/linechart', 'js://graphics/load_indicator', function( Chart, LI ){
			/* 
			 * Draw my chart
			 */
			
			var c = new Chart({
				canvas: $('#graph').get(0),
				dataset: {
					'Year 1': 100,
					'Year 2': 300,
					'Year 3': 660,
					'Year 4': 344,
					'': 300,
					'a': 200
				},
				style: {
					yAxisWidth: 30,
					yAxisMin: 0,
					yAxisMax: 400,
					yAxisStep: 50,
					pointColor: 'navy',
					lineWidth: 2,
					lineColor: '#5151AA'
				}
			});
			
			var vms = {
					vm1: 0.33,
					vm2: 0.12,
					vm3: 0.8,
					vm4: 0.44
			};
			
			var l = new LI({
				canvas: $('#load_indicator').get(0),
				dataset: vms
			});
			
			window.setTimeout(function(){
				$.extend( vms, {
					vm5: 0.88,
					vm6: 0.33,
					vm7: 0.23,
					vm1: 1
				});
				l.update();
			}, 3000);
			
		});
		
		M.load( 'js://net/socket', 'js://lib/jquery', function( WS, $ ){
			$.ajax({
				url: 'target.php?get_target=1',
				dataType: 'text',
				success: function( data ){
					/*
					 * Connect backend.
					 */
					var socket = new WS({ address: data, protocol: 'WWSCP' });
					socket.onopen = function(){
						socket.send({
							name: 'Session.loginWithPassword',
							handler: 'xen',
							data : {
								args: ['','']
							},
							callback: function( data ) {
								socket.send({
									name: 'Session.getThisHost',
									handler: 'xen',
									data: { args: [] },
									callback: function( data ){
										console.log( data );
									}
								});
								console.log( data );
							}
						});
					};
					
					if(!socket.open()){
						alert( 'auwch' );
					}
					/*
					 * End connect
					 */
				}
			});
		});
	});
	
}( jQuery ));