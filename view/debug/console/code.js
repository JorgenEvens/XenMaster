(function( $, app ) {
	
	var dom = $(this.dom),
		cmd = dom.find('#command'),
		ref = dom.find('#reference'),
		data = dom.find('#data'),
		debug_pre = dom.find('#debug'),
		
		print = function( obj, depth ) {
			depth = depth||'';
			
			if( typeof obj != 'object' ) {
				debug_pre.append(obj);
			} else {
				debug_pre.append("{\n" );
				depth += "\t";
				for( i in obj ) {
					if( obj.hasOwnProperty( i ) ) {
						if( typeof obj[i] == 'object' ) {
							print( obj[i], depth );
						} else {
							debug_pre.append( depth + i + ': ' + obj[i] + ",\n");
						}
					}
				}
				debug_pre.append("},\n" );
				depth = depth.substring(0,depth.length-1);
			};
		
		};
	
	app.load( 'js://net/xmconnection', function( xm ) {
		xm = xm.getInstance();
		
		dom.find('.submit').click(function(){
			var param = data.val().split(','),
				args = [],
				reference = null;
			
			for( var i=0; i<param.length; i++ ) {
				param[i] = param[i].replace(/^\s+|\s+$/g,"");
				if( param[i].length > 0 ) {
					args.push( param[i] );
				}
			}
			
			if( ref.val().length > 0 ) {
				reference = ref.val()();
			}
			

			param = { args: args.length > 0 ? args : null, ref: reference };
			
			xm.send('xen://' + cmd.val(), param, function( result ) {
				console.log( result );
				debug_pre.text('');
				print( result );			
			});
		});
	});
	
});