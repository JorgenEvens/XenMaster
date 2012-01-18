(function( $, app ){
	
	this.show = function() {
		$('body')
			.append( this.dom );
	};
	
	this.capture( 'click' );
	this.bind( 'ui_dashboard', function() {
		app.load( 'tpl://dashboard', 'js://ui/template', function( dashboard, Template ) {
			
			var dashboard_ui = new Template({ resource: dashboard });
			dashboard_ui.show();
			
		});
	});
	
	
	this.bind( 'ui_storage', function(){

		app.load( 'tpl://storage/list', 'js://ui/template', function( list, Template ){
			var sr_list = new Template({ resource: list });
			sr_list.show();
			
		});
		
	});
	
	this.bind( 'ui_machines', function(){

		app.load( 'tpl://vm/list', 'js://ui/template', function( list, Template ){
			var vm_list = new Template({ resource: list });
			vm_list.show();
			
		});
		
	});
	
});