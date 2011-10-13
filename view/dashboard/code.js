(function( $, app ){
	var tpl = this,
		record = $(tpl.dom).find('.vm_template' ).remove();
	
	app.load( 'js://net/XmConnection', 'js://ui/dataset', 'js://graphics/load_indicator', function( XmCon, Dataset, load ) {
		
		var con = XmCon.getInstance();
		
		con.send( 'xen://Session[].getThisHost.getResidentVMs', function( result ){
			var r = null,
				vm = null,
				i = null,
				ds = null;
			
			for( i in result ) {
				vm = result[i];
				
				r = record.clone();
				
				r.find('td:first').html( vm.nameLabel );
				r.find('td:nth(1)').html(Math.round(Math.random()*99));
				r.find('td:last').html(Math.round(Math.random()*99));
				
				ds = Dataset.get( r.get(0) );
				ds.config = vm;
				
				$(tpl.dom).find('tbody').append( r );
			}
		});
		
		var vms = {
				test: 0.12,
				test2: 0.23,
				test4: 0.12,
				test5: 0.99,
				test6: 0.55,
				test7: 0.50
		};
		
		var li = new load({
				canvas: $('canvas').get(0),
				dataset: vms
			});
		
		window.setTimeout( function(){
			$.extend( vms, {
				test8: 1.00,
				test9: 0.91,
				test10: 1.00,
				test91: 0.91,
				test11: 1.00,
				test12: 0.91
			});
			
			li.update();
		}, 3000 );
		
		
		
		tpl.capture( 'click' );
		tpl.bind( 'vm_clicked', function( e ) {
			app.load( 'tpl://detail/vm', 'js://ui/template', function( vm, Template ) {
				
				var vm_ui = new Template({ resource: vm });
				vm_ui.show( e.dataset.config );
				
			});
		});
		
	});
	
});