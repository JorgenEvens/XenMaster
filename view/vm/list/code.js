(function( $, app ){
	var tpl = this,
		record = $(tpl.dom)
				.find('.vm_template' )
				.removeClass('vm_template')
				.remove();
	
	app.load( 'js://api/vm', 'js://ui/dataset', function( VM, Dataset ) {
		
		VM.getAll(function( result ){
			var r = null,
				vm = null,
				i = null,
				ds = null;
			
			for( i in result ) {
				vm = result[i];
				if( vm.template ) continue;
				
				r = record.clone();
				
				r.find('td.name').html( vm.name );
				
				ds = Dataset.get( r.get(0) );
				ds.config = vm;
				
				$(tpl.dom).find('tbody').append( r );
			}
		});
		
		tpl.capture( 'click' );
		tpl.bind( 'vm_clicked', function( e ) {
			tpl.loadVM( e.dataset.config );
		});
		
		
		tpl.bind( 'vm_create', function() {
			app.load( 'tpl://vm/new', 'js://ui/template', function( vm_create, Template ){
				
				var create = new Template({ resource: vm_create, force_new: true });
				create.show('vm_editor');
			});
		});
		
	});
	
	this.loadVM = function( vm_data ) {
		app.load( 'tpl://vm/detail', 'js://ui/template', function( vm, Template ) {
			
			var vm_ui = new Template({ resource: vm });
			vm_ui.vm = vm_data;
			vm_ui.show( 'vm_editor' );
			
		});
	};
	
});