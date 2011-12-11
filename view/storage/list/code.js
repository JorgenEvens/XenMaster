(function( $, app ){
	var tpl = this,
		dom = $(tpl.dom);
	
	tpl.capture( 'click' );
		
	app.load( 'js://api/sr', function( SR ) {
		
		var base = dom.find('.repo tr:first').clone();
		
		tpl.on( 'sr_remove', function() {
			dom.find( '.repo input:checked' ).each(function(){
				var ref = $(this).val();
				
				if( ref.length > 0 ) {
					(new SR(ref)).destroy(function( result ){
						if( console ) console.log( result );
					});
				}
			});
		});
		
		tpl.on( 'sr_detach', function() {
			dom.find( '.repo input:checked' ).each(function(){
				var ref = $(this).val();
				
				if( ref.length > 0 ) {
					(new SR(ref)).destroy(function( result ){
						if( console ) console.log( result );
					});
				}
			});
		});
		
		tpl.on( 'sr_new', function(){
			app.load( 'js://ui/template', 'tpl://storage/new', function( Template, tpl ){
				var view = new Template({resource: tpl});
					view.show( 'sr_create' );
			});
		});
		
		base.find('td').filter(':not(.selection)').html('');
		
		SR.getAll( function( result ) {
			for( item in result ) {
				item = result[item];
				
				var row = base.clone(),
					fields = row.find('td');
				
				fields.filter('.selection').find('input').val( item.reference );
				fields.filter('.name').html( item.name );
				fields.filter('.location').html( item.otherConfig.location );
				fields.filter('.type').html( item.type );
				
				dom.find('.repo').append( row );
			}
		});
	});
})