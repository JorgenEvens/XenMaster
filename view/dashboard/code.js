(function( $, app ){
	var tpl = this,
		record = $(tpl.dom).find('.vm_template' ).remove();
	
	app.load( 'js://net/XmConnection', 'js://ui/dataset', function( XmCon, Dataset ) {
		
		var con = XmCon.getInstance();
		
		con.send( 'xen://Session[].getThisHost.getResidentVMs', function( result ){
			var r = null,
				vm = null,
				i = null,
				ds = null;
			
			for( i in result ) {
				vm = result[i];
				
				r = record.clone();
				
				r.find('h4').html( vm.nameLabel );
				r.find('.values li:first').html(Math.round(Math.random()*99));
				r.find('.values li:last').html(Math.round(Math.random()*99));
				
				ds = Dataset.get( r.get(0) );
				ds.config = vm;
				
				$(tpl.dom).find('.summary').append( r );
			}
		});
		
		tpl.capture( 'click' );
		tpl.bind( 'vm_clicked', function( e ) {
			console.log( e.dataset );
		});
		
	});
	
});