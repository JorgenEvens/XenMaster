/*
 * xmconnection.js
 * Copyright (C) 2011,2012 Jorgen Evens
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

/*
 * xmconnection.js
 * Copyright (C) 2011,2012 Jorgen Evens
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

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
						Notifier.publish( 'XenMaster', data.type, 'Request to backend failed! ' + data.result.message, data );
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