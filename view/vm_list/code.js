(function( $, app ){
	
	this.addVMs = function( vms ) {
		console.log( vms );
		var i = null,
			me = this;
		
		for( i in vms ) {
			(function(vm){
				var item = $('<li></li>').html( vm['nameLabel'] ).get(0);
				item.dataset.reference = vm.reference;
				item.dataset.action = 'vm_clicked';
				item.dataset.entity = vm;
				$(me.dom).find( 'ul' ).append( item );
			}(vms[i]));
		}
	};
	
});