(function( ready, app ){
	
	app.load( 'js://net/socket', function( Socket ) {
		
		var XmConnection = function( address ) {
			Socket.call( this, {
				address: address,
				protocol: 'WWSCP'
			});
			
			XmConnection.instance = this;
		},
		sendRaw,
		proto = function(){};
		proto.prototype = Socket.prototype,
		
		createCallback = function( callback ) {
			// Result parsing
			return function( data ){
				if( data.type == 'ERROR' ) {
					app.load( 'js://tools/notifier', function( Notifier ) {
						Notifier.publish( 'XenMaster', 'Request to backend failed! ' + data.result.message );
					});
				} else {
					callback( data.result );
				}
			};
		};
		
		XmConnection.prototype = new proto();
		
		sendRaw = XmConnection.prototype.send;
		
		XmConnection.prototype.send = function() {
			var args = Util.argumentsToArray( arguments ),
				command = null,
				handler = null,
				data = { args: [] },
				callback = null,
				parts = null;
			
			if( typeof args[0] === 'string' && typeof args[1] === 'string' ) {
				command = args.shift();
				handler = args.shift();
			} else if( typeof args[0] === 'string' ) {
				parts = args.shift().split( '://' );
				command = parts[1];
				handler = parts[0];
			} else {
				throw 'No command supplied!';
			}
			
			if( typeof args[args.length-1] === 'function' ) {
				callback = args.pop();
			}
			
			if( args.length > 1 ) {
				throw 'Too many arguments supplied';
			} else if( args.length == 1 ) {
				args = args[0];
				
				for( i in args ) {
					data[i] = args[i];
				}
			}
			
			if( callback ) {
				callback = createCallback( callback );
			}
			
			sendRaw.call( this, {
				name: command,
				handler: handler,
				data: data,
				callback: callback
			});
		};
		
		XmConnection.getInstance = function() {
			if( !this.instance ) {
				throw "Connection not initialized yet!";
			}
			
			return this.instance;
		};
		
		ready( XmConnection );
		
	});
	
});