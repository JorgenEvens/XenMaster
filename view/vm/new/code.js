(function( $, app ){

	var tpl = this,
		dom = $(tpl.dom);
	
	tpl.capture( 'click' );
	
	tpl.bind('vm_create',function( e ){
		var info = {},
			cpus = 1;
		
		dom.find('input, select, textarea').each(function(){
			var item = $(this);
			info[item.attr('name')] = item.val();
		});
		
		info.minimumStaticMemory = info.maximumStaticMemory = info.memory;
		delete info.memory;
		
		cpus = info.cpus;
		delete info.cpus;
		
		app.load( 'js://api/vm', function( VM ){
			VM.build(info,function( vm ) {
				vm.create(cpus,function( resp ) {
					console.log( resp );
				});
			});
		});
	});
});