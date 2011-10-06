(function( $, undefined ){
		
	$(document).ready(function(){
		MASTER = new Application({
			'proxy': 'resources.php',
			'base': '/code',
			'buffer_timeout': 100
		});
		
		M = MASTER;
		
		/*
		 * Initialize application
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
			socket = new XmCon( window.location.host + '/wwscp' );
				
			XmCon.instance = socket;
			socket.onopen = login;
			
			if(!socket.open()){
				alert( 'auwch' );
			}
			
			var t = new Template({
				resource: user_interface,
				events: ['click']
			});
			
			t.bind( 'vm_clicked', function( e ) {
				socket.send('xen://VM.start', { args: [ false ], ref: e.dataset.reference }, function(data){console.log( data ); });
			});
			
			$('body').append( t.dom );
		});
		
	});
	
}( jQuery ));