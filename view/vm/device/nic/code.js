/*
 * code.js
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

(function( $, app ){
	
	var tpl = this,
		dom = $(tpl.dom),
		
		generateMAC = function() {
			var start = '00:16:3e',
				random = Math.round(Math.random()*16581375).toString(16);

			while( random.length < 6 ) {
				random = '0' + random;
			}
			
			return start + random.replace( /([\dA-F]{2})/ig, ':$1' );
		},
		
		networks = null;
	
	app.load( 'js://api/network', 'js://ui/dataset', function( Network, Dataset ) {
		networks = [];
		var nets = networks;
		
		Network.getAll(function( networks ){
			var i = null,
				network = null,
				list = dom.find( '#dev_network' );
			
			for( i in networks ) {
				network = networks[i];
			
				$('<option></option>')
					.appendTo( list )
					.val( network.reference )
					.text( network.name );
				
				nets[network.reference] = network;
			}
			
			list.change();
		});
	});
	
	this.onshow = function(){
		$('#dev_mac').val( generateMAC() );
	};

	tpl.capture(['click', 'change']);
	
	tpl.on( 'dev_create', function() {
		var data = {},
			network = null,
			device = null;
		
		dom.find('input,select,textarea').each(function(){
			var me = $(this),
				name = me.attr('name'),
				val = me.val();
			
			if( val && name ) {
				data[name] = val;
			}
		});
		
		network = data.network;		
		delete data.network;
		
		Util.chain(
			function(){
				app.load( 'js://api/vif', this.next );
			},
			function( VIF ) {
				VIF.build(data, this.next );
			},
			function( vif ) {
				vif.create( tpl.vm, network, this.next );
			},
			function( vif ) {
				tpl.vm.vifs.push(vif.reference);
				tpl.action( 'device_ready', vif );
			}
		).start();
		
	});
	
	tpl.on( 'network_select', function( e ) {
		dom.find('#dev_mtu').val( networks[$(e.source).val()].mtu );
	});
	
});