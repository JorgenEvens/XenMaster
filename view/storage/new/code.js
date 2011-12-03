(function( $, app){
	
	var tpl = this,
		dom = $(tpl.dom),
		
		showFields = function(){
			dom.find( '> div' ).hide();
			dom.find( '.' + dom.find('#sr_type').val().toLowerCase() ).show();
		};
		
	dom.find('select').change(showFields);
	showFields();
	
	tpl.capture( 'click' );
	
	tpl.on( 'sr_create', function() {
		var info = {},
			name = dom.find('#sr_name').val(),
			type = dom.find('#sr_type').val().toLowerCase();
		
		dom
			.find('.' + type + ' input')
			.each(function(){
				var item = $(this);
				if( item.val().length < 1 ) return;
				
			info[item.attr('name')] = item.val();
		});
		
		app.load( 'js://api/session', 'js://api/helpers/' + type,
				function( Session, Helper ) {
			Session.getThisHost(function( host ){
				
				if( type == 'nfs' ) {
					Helper.mountISORepository( name, info.host, info.path, host, function(r){
						console.log( 'iso mount result: ', r );
					});
				} else if ( type == 'iscsi' ) {
					info.port = parseFloat( info.port );
					
					Helper.build(info, function( result ) {
						console.log( 'iscsi result: ', result );
						app.load( 'js://api/sr', function( SR ){
							SR.build({ name: name }, function( new_sr ) {
								new_sr.create( host, result, 'user', true, 0, function( r ) {
									console.log( 'sr result ', r );
								});
							});
							
						});
					});
				}
			});
		});
		
	});
	
})