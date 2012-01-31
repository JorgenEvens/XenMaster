(function( $, app ){

	var tpl = this,
		dom = $(tpl.dom),
		ctl = {
			type: dom.find('#vm_type'),
			kernel: dom.find('#pv_kernel'),
			ramdisk: dom.find('#pv_ramdisk')
		},
		builder = {},
		
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
	
	builder.hvm = function( data ) {
		var d = {
			name: data.name,
			description: data.description,
			hvmbootpolicy: 'BIOS order',
			hvmbootparam: {
				order: null
			},
			minimumStaticMemory: data.memory,
			maximumStaticMemory: data.memory,
			minimumDynamicMemory: data.memory,
			maximumDynamicMemory: data.memory
		},
		i=0,
		boot_order = '';
		
		for( i; i<3; i++ ) {
			if( 'hvm_boot[' + i + ']' in data ) {
				boot_order += data['hvm_boot[' + i + ']'];
			}
		}
		
		d.hvmbootparam.order = boot_order;

		return d;
	};
	
	builder.pv = function( data ) {
		data.minimumStaticMemory = data.memory;
		data.maximumStaticMemory = data.memory;
		data.minimumDynamicMemory = data.memory;
		data.maximumDynamicMemory = data.memory;
		
		delete data.memory;
		
		return data;
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
	
	tpl.bind('vm_create',function( e ){
		var data = {},
			cpus = 1,
			type = 'hvm';
		
		dom.find('input, select, textarea').each(function(){
			var item = $(this);
			if( item.filter(':checkbox').length && !item.filter( ':checked' ).length ) {
				return;
			}
			data[item.attr('name')] = item.val();
		});
		
		cpus = data.cpus;
		delete data.cpus;
		
		type = data.type;
		delete data.type;
		
		data = builder[type]( data );
		console.log( 'new vm data', data );
		Util.chain(function(){
			app.load( 'js://api/vm', this.next );
		},function( VM ) {
			this.VM = VM;
			VM.build( data, this.next );
		}, function( vm ) {
			vm.create( cpus, this.next );
		}, function( vm ) {
			new this.VM( vm, this.next );
		}, function( vm ) {
			this.vm = vm;
			console.log( 'created vm', this.vm );
			app.load( 'tpl://vm/list', 'js://ui/template', this.next );
		}, function( vm_list, Template ) {
			var list = new Template({ resource: vm_list });
			//list.loadVM( this.vm );
			//list.show();
		}).start();
		
		/*app.load( 'js://api/vm', function( VM ){
			VM.build(data,function( vm ) {
				vm.create(cpus,function( vm ) {
					console.log( vm );
				});
			});
		});*/
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