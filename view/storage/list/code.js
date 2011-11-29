(function( $, app ){
	var dom = $(this.dom);
	
	app.load( 'js://api/sr', function( SR ) {
		
		var base = dom.find('.repo header ul').clone();
		base.find('li').html('');
		
		SR.getAll( function( result ) {
			for( item in result ) {
				item = result[item];
				
				var row = base.clone(),
					fields = row.find('li');
				
				$(fields[0]).text( item.name );
				$(fields[1]).text( item.otherConfig.location );
				$(fields[2]).text( item.type );
				
				dom.find('.repo').append( row );
			}
		});
	});
})