(function( $, app ){
	var tpl = this,
		record = $(tpl.dom).find('.vm_template' ).remove();
	
	app.load( 'js://api/vm', 'js://ui/dataset', function( VM, Dataset ) {
		
		// Session[].getThisHost.getResidentVMs
		VM.getAll(function( result ){
			var r = null,
				vm = null,
				i = null,
				ds = null;
			
			for( i in result ) {
				vm = result[i];
				if( vm.template ) continue;
				
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
			app.load( 'tpl://vm/list', 'js://ui/template', function( list, Template ) {
				
				var vm_list = new Template({ resource: list });
				
				vm_list.bind( 'tpl_show', function(){
					vm_list.loadVM( e.dataset.config );
				});
				
				vm_list.show();
			});
		});
		
		tpl.bind('all_show', function(e){
			app.load( 'tpl://vm/list', 'js://ui/template', function( list, Template ) {
				var list_ui = new Template({resource: list});
				list_ui.show();
			});
		});
		
	});
	
});