(function( $, undefined ){
		
	$(document).ready(function(){
		MASTER = new Application({
			'proxy': 'resources.php',
			'base': '/code',
			'buffer_timeout': 100
		});
		
		M = MASTER;
		
		/*
		 * Template test
		 ********************************************************************************************
		 */
		M.load( 'js://ui/template', 'tpl://vm_list', 'js://net/XmConnection', function( Template, user_interface, XmCon ) {
				var login = function(){
						socket.send('xen://Session.loginWithPassword', { args: ['',''] }, getHost );
					},
				getHost = function( data ) {
						console.log( data );
						socket.send( 'xen://Session.getThisHost', getVMs );
					},
				getVMs = function( data ) {
						console.log( data );
						socket.send( 'xen://Host.getResidentVMs', {	ref: data.reference },
								function( data ){
									t.addVMs( data );
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
			
			var t = new Template({
				resource: user_interface,
				events: ['click']
			});
			
			t.vmClick = function( vm ) {
				socket.send('xen://VM.start', { args: [ false ], ref: vm.reference }, function(data){console.log( data ); });
			};
			
			t.bind( 'stop_vm', function( e ) {
				console.log( e );
			});
			
			$('body').append( t.dom );
		});
		
	});
	
}( jQuery ));