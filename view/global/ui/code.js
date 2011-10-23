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
	
	this.bind( 'ui_vm_create', function() {
		
		app.load( 'tpl://global/vm_create', 'js://ui/template', function( vm_create, Template ){
			
			var create = new Template({ resource: vm_create });
			create.show();
			
		});
		
	});
	
});