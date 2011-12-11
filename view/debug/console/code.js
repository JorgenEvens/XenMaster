(function( $, app ) {
	
	var dom = $(this.dom),
		cmd = dom.find('#command'),
		ref = dom.find('#reference'),
		data = dom.find('#data'),
		debug_pre = dom.find('#debug'),
		
		print = function( obj, depth ) {
			depth = depth||'';
			
			var is_array = Util.isArray( obj ),
				otag = is_array ? '[' : '{',
				ctag = is_array ? ']' : '}',
				inset = '    ',
				first = true;
			
			if( typeof obj != 'object' ) {
				debug_pre.append(obj);
			} else {
				debug_pre.append(otag);
				depth += inset;
				for( i in obj ) {
					if( first ) { debug_pre.append( "\n" ); first = false; }
					
					if( obj.hasOwnProperty( i ) ) {
						if( typeof obj[i] == 'object' ) {
							debug_pre.append( depth + '<b>' + i + '</b>' + ': ' );
							print( obj[i], depth );
						} else {
							debug_pre.append( depth + '<b>' + i + '</b>' + ': ' + obj[i] + ",\n");
						}
					}
				}
				depth = depth.substring(0,depth.length-inset.length);
				if( !first ) {
					debug_pre.append(depth+ctag+",\n" );
				} else {
					debug_pre.append(ctag+",\n");
				}
			};
		
		};
	
	app.load( 'js://net/xmconnection', function( xm ) {
		xm = xm.getInstance();
		
		dom.find('.submit').click(function(){
			var args = JSON.parse( '[' + data.val() + ']' ),
				reference = null,
				param;
			
			if( ref.val().length > 0 ) {
				reference = ref.val();
			}
			
			param = { args: args.length > 0 ? args : null, ref: reference };
			
			xm.send('xen://' + cmd.val(), param, function( result ) {
				if( console ) console.log( result );
				debug_pre.text('');
				print( result );			
			});
		});
	});
	
});