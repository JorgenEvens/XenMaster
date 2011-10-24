(function( $, app ){
	var tpl = this,
		record = $(tpl.dom).find('.vm_template' ).remove();
	
	app.load( 'js://api/session', 'js://ui/dataset', function( Session, Dataset ) {
		
		Session.getThisHost().getResidentVMs(function( result ){
			var r = null,
				vm = null,
				i = null,
				ds = null;
			
			for( i in result ) {
				vm = result[i];
				
				r = record.clone();
				
				r.find('td:first').html( vm.name );
				r.find('td:nth(1)').html(Math.round(Math.random()*99));
				r.find('td:last').html(Math.round(Math.random()*99));
				
				ds = Dataset.get( r.get(0) );
				ds.config = vm;
				
				$(tpl.dom).find('tbody').append( r );
			}
		});
		
		tpl.capture( 'click' );
		tpl.bind( 'vm_clicked', function( e ) {
			app.load( 'tpl://detail/vm', 'js://ui/template', function( vm, Template ) {
				
				var vm_ui = new Template({ resource: vm });
				vm_ui.show( e.dataset.config );
				
			});
		});
		
	});
	
});