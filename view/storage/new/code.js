(function( $, app){
	
	var tpl = this,
		dom = $(tpl.dom),
		
		showFields = function(){
			dom.find( '> div:not(.form_line)' ).hide();
			dom.find( '.' + dom.find('#sr_type').val().toLowerCase() ).show();
		},
		
		creation = {},
		
		clearFields = function() {
			dom.find('input,select,textarea').val('');
		};
		
	creation.nfs = function( name, host, data, type ) {
		var description = type == 'iso' ? 'ISO Repository: ' + name : name;
		Util.chain(
			function(){
				app.load( 'js://api/sr', this.next );
			},
			function( SR ) {
				SR.build({
					name: name,
					description: description,
					smconfig: {
						location: data.host + ':' + data.path
					},
					otherConfig: {
						storageType: 'nfs'
					}
				}, this.next );
			},
			function( sr ) {
				sr.introduce( type.toUpperCase(), type, true, this.next );
			},
			function( sr ) {
				console.log( 'sr created: ', sr );
			}
		).start();
	};
	
	creation.iscsi = function( name, host, data, type ) {
		app.load( 'js://api/helpers/iscsi', 'js://api/sr',
				function( iSCSI, SR ){
			var iscsi = null,
				sr = null,
			
				handler = function(){
					if( !iscsi || !sr ) return;
					
					sr.create( host, iscsi, type, true, 0,
							function( r ) {
						clearFields();
						if( console ) console.log( 'sr result ', r );
					});
				};
			
			if( data.port ) {
				data.port = parseFloat( data.port );
			}
			if( data.LUNs ) {
				data.LUNs = data.LUNs.split(',');
			}
			
			iSCSI.build( data, function( result ) {
				iscsi = result;
				handler();
			});
			
			SR.build({name: name}, function( result ) {
				sr = result;
				handler();
			});
		});
	};
	
	creation.partition = function( name, host, data, typye ) {
		app.load( 'js://api/sr', function( SR ) {
			SR.build({name: name}, function( sr ) {
				sr.create( host, data, 'Ext', type, true, 0, function( result ){
					clearFields();
					if( console ) console.log( result );
				});
			});
		});
	};
	
	creation.directory = function( name, host, data, type ) {
		app.load( 'js://api/sr', function( SR ) {
			SR.build({name: name}, function( sr ) {
				sr.create( host, data, 'File', type, true, 0, function( result ){
					clearFields();
					if( console ) console.log( result );
				});
			});
		});
	};
	
	creation.lvm = function( name, host, data, type ) {
		data = {
			device: data.volumes
		};
		
		app.load( 'js://api/sr', function( SR ) {
			SR.build({name: name}, function( sr ) {
				sr.create( host, data, 'Lvm', type, true, 0, function( result ){
					clearFields();
					if( console ) console.log( result );
				});
			});
		});
	};
	
	dom.find('select').change(showFields);
	showFields();
	
	tpl.capture( 'click' );
	
	tpl.on( 'sr_create', function() {
		var info = {},
			name = dom.find('#sr_name').val(),
			type = dom.find('#sr_type').val().toLowerCase(),
			content_type = dom.find('#sr_content').val().toLowerCase();
		
		dom
			.find('.' + type + ' input, .' + type + ' select')
			.each(function(){
				var item = $(this);
				if( item.val().length < 1 ) return;
				
			info[item.attr('name')] = item.val();
		});
		
		app.load( 'js://api/session', function( Session, Helper ) {
			Session.getThisHost(function( host ){
				if( typeof creation[ type ] === 'function' ) {
					creation[type]( name, host, info, content_type );
				}
			});
		});
		
	});
	
})