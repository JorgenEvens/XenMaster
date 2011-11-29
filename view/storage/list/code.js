(function( $, app ){
	var tpl = this,
		dom = $(tpl.dom);
	
	tpl.capture( 'click' );
	
	tpl.on( 'sr_new', function( e ){
		console.log( arguments );
	});
	
	
	
	app.load( 'js://api/sr', function( SR ) {
		
		var base = dom.find('.repo header ul').clone();
		
		tpl.on( 'sr_remove', function( e ) {
			dom.find( '.repo input:checked' ).each(function(){
				var ref = $(this).val();
				
				if( ref.length > 0 ) {
					(new SR(ref)).destroy(function( result ){
						console.log( result );
					});
				}
			});
		});
		
		tpl.on( 'sr_detach', function( e ) {
			dom.find( '.repo input:checked' ).each(function(){
				var ref = $(this).val();
				
				if( ref.length > 0 ) {
					(new SR(ref)).destroy(function( result ){
						console.log( result );
					});
				}
			});
		});
		
		base.find('li').filter(':not(.selection)').html('');
		
		SR.getAll( function( result ) {
			for( item in result ) {
				item = result[item];
				
				var row = base.clone(),
					fields = row.find('li');
				
				fields.filter('.selection input').val( item.reference );
				fields.filter('.name').text( item.name );
				fields.filter('.location').text( item.otherConfig.location );
				fields.filter('.type').text( item.type );
				
				dom.find('.repo').append( row );
			}
		});
	});
})