(function(){
	
	this.addVMs = function( vms ) {
		console.log( vms );
		var i = null,
			me = this;
		
		for( i in vms ) {
			(function(vm){
				console.log( vm );
				$(me.dom).find( 'ul' ).append( $('<li></li>').html( vm['nameLabel'] ).click(function(){
					me.vmClick( vm );
				}));
			}(vms[i]));
		}
	};
	
	this.vmClick = function(){};
	
});