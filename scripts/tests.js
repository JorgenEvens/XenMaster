/*
		 * Graphics test
		 ********************************************************************************************
		 */
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
		
		/*
		 * Backend test
		 ********************************************************************************************
		 */
		M.load( 'js://net/XmConnection', 'js://lib/jquery', function( XmCon, $ ){
			var login = function(){
						socket.send('xen://Session.loginWithPassword', { args: ['',''] }, getHost );
					},
				getHost = function( data ) {
						socket.send( 'xen://Session.getThisHost', getVMs );
					},
				getVMs = function( data ) {
						socket.send( 'xen://Host.getResidentVMs', {	ref: data.reference },
								function( data ){
									console.log( data );
								});
					},
				socket = null;
					
			$.ajax({
				url: 'target.php?get_target=1',
				dataType: 'text',
				success: function( data ){
					/*
					 * Connect backend.
					 */
					socket = new XmCon( data );
					socket.onopen = login;
					
					if(!socket.open()){
						alert( 'auwch' );
					}
					/*
					 * End connect
					 */
				}
			});
		});
		
		/*
		 * Template test
		 ********************************************************************************************
		 */
		M.load( 'js://ui/template', 'tpl://user_interface', function( Template, user_interface ) {
			var t = new Template({
				resource: user_interface,
				events: ['click']
			});
			
			t.bind( 'stop_vm', function( e ) {
				console.log( e );
			});
			
			$('body').append( t.dom );
		});