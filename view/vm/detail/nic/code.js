(function( $, app ){

	var tpl = this,
		dom = $(this.dom),
		ctl = {
			network: dom.find('#nic_network')
		};
	
	this.onshow = function() {
		
		var vif = tpl.device;
		
		app.load( 'js://api/network', function( Network ){
			new Network( vif.network, function( net ) {
				ctl.network.val( net.name );
			});
		});
		
	};
	
});