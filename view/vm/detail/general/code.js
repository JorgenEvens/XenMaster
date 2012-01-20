(function( $, app ){

	var tpl = this,
		dom = $(tpl.dom),
		ctl = {
			type: dom.find('#vm_type'),
			kernel: dom.find('#pv_kernel'),
			ramdisk: dom.find('#pv_ramdisk')
		},
		update = {
			hvm: {},
			pv: {}
		},
		
		pv_data_loaded = false,
		
		loadKernels = function( host ){
			app.load( 'js://api/plugins/filesystem', function( FS ){
				FS.getKernels(host, function( kernels ){
					var i = null;
					for( i in kernels ) {
						i = kernels[i];
						
						$('<option></option>').val( i )
							.text(i)
							.appendTo( ctl.kernel );
					}
				});
			});
		},
		
		loadRamdisks = function( host ){
			app.load( 'js://api/plugins/filesystem', function( FS ){
				FS.getRamdisks(host, function( disks ){
					var i = null;
					for( i in disks ) {
						i = disks[i];
						
						$('<option></option>').val( i )
							.text(i)
							.appendTo( ctl.ramdisk );
					}
				});
			});
		};
		
	update.hvm.bootOrder = function() {
		boot_order = '';
		
		for( i; i<3; i++ ) {
			if( 'hvm_boot[' + i + ']' in data ) {
				boot_order += data['hvm_boot[' + i + ']'];
			}
		}
	};
	
	tpl.capture(['click','change']);
	
	tpl.bind( 'hvm_boot_up', function( e ) {
		var elem = $(e.source),
			checkbox = elem.siblings('input:checkbox'),
			container = elem.parent(),
			id = null,
			regex = /\[(\d+)\]/,
			prev = container.prev('.hvm_boot_option');

		if( prev ) {
			id = regex.exec( checkbox.attr('name') )[1];
			checkbox.attr('name', 'hvm_boot[' + (id-1) + ']');
			
			prev
				.before( container )
				.find('input:checkbox')
					.attr('name', 'hvm_boot[' + id + ']');
		}
	});
	
	tpl.bind( 'hvm_boot_down', function( e ) {
		var elem = $(e.source),
			checkbox = elem.siblings('input:checkbox'),
			container = elem.parent(),
			id = null,
			regex = /\[(\d+)\]/,
			next = container.next('.hvm_boot_option');

		if( next ) {
			id = regex.exec( checkbox.attr('name') )[1];
			checkbox.attr('name', 'hvm_boot[' + (id+1) + ']');
			
			next
				.after( container )
				.find('input:checkbox')
					.attr('name', 'hvm_boot[' + id + ']');
		}
	});
	
	tpl.bind('vm_type_changed',function( e ){
		dom.find('.type')
			.hide()
			.filter('.' + ctl.type.val() )
				.show();
		
		if( ctl.type.val() == 'pv' && !pv_data_loaded ) {
			pv_data_loaded = true;
			app.load( 'js://api/session', function( Session ){
				Session.getThisHost(function( host ){
					loadKernels(host);
					loadRamdisks(host);
				});
			});
		}
	});
	
	ctl.type.change();
});