(function( $, app ){

	var tpl = this,
		dom = $(tpl.dom),
		vm_data = null,
		
		ctl = {
			// General
			name: dom.find('#vm_name'),
			description: dom.find( '#vm_description' ),
			poweron: dom.find( '#vm_poweron' ),
			
			// Virtualization type
			type: dom.find('#vm_type'),
			
			// HVM Virtualization
			hvm: {
				hdd: dom.find( '#hvm_boot_harddisk' ),
				cd: dom.find( '#hvm_boot_disk' ),
				net: dom.find( '#hvm_boot_network' )
			},
			
			// PV Virtualization
			pv: {
				kernel: dom.find('#pv_kernel'),
				ramdisk: dom.find('#pv_ramdisk')
			}
			
		},
		
		// Property updating functions
		update = {
			hvm: {},
			pv: {}
		},
		
		pv_data_loaded = false,
		
		loadKernels = function( host ){
			app.load( 'js://api/plugins/filesystem', function( FS ){
				FS.getKernels(host, function( kernels ){
					var i = null,
						option = null;
					
					for( i in kernels ) {
						i = kernels[i];
						
						option = $('<option></option>').val( i )
							.text(i)
							.appendTo( ctl.kernel );
						
						if( i == vm_data.pvKernel ) {
							option.attr( 'selected', 'selected' );
						}
					}
				});
			});
		},
		
		loadRamdisks = function( host ){
			app.load( 'js://api/plugins/filesystem', function( FS ){
				FS.getRamdisks(host, function( disks ){
					var i = null,
						option = null;
					
					for( i in disks ) {
						i = disks[i];
						
						option = $('<option></option>').val( i )
							.text(i)
							.appendTo( ctl.ramdisk );
						
						if( i == vm_data.pvRamdisk ) {
							option.attr( 'selected', 'selected' );
						}
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
	
	// Event handling
	tpl.capture(['click','change']);
	
	tpl.bind( 'tpl_show', function( e ){
		vm_data = tpl.vm;
		
		var bootorder = vm_data.hvmBootParam?vm_data.hvmBootParam.order:'';
		
		ctl.name.val( vm_data.name );
		ctl.description.val( vm_data.description );
		ctl.poweron.attr( 'checked', vm_data.autoPowerOn );
		ctl.type.val( vm_data.hvmBootPolicy?'hvm':'pv' );
		
		ctl.hvm.cd
			.attr('checked', bootorder.indexOf( 'd' ) > -1 );
		
		ctl.hvm.hdd
			.attr('checked', bootorder.indexOf( 'c' ) > -1 );
		
		ctl.hvm.net
			.attr('checked', bootorder.indexOf( 'n' ) > -1 );
		
		ctl.pv.kernel.val( vm_data.pvKernel );
		ctl.pv.ramdisk.val( vm_data.pvRamdisk );
		
		ctl.type.change();
	});
	
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
});