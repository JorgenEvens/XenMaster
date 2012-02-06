/*
 * template.js
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
	
	app.load( 'js://lib/jquery', 'js://ui/dataset', function( $, Dataset ) {
		
		var Template = function( options ) {
			options = options || {};
			
			var res = options.resource || {};
			
			if( res.instance != null && !options.force_new ) {
				return res.instance;
			}
			res.instance = this;

			this.dom = $(res.view).get(0);
			
			this.action = options.action || defaultHandler;
			this.capture( options.events );
			this.onshow = function(){};
			this.placeholder = 'main';
			
			if( typeof res.code === 'function' ) {
				res.code.call( this, $, app );
			}
			
			// TODO: Deprecated
			var tpl = this;
			this.bind( 'tpl_show', function(){
				tpl.onshow();
			});
		},
		defaultHandler = function( action, data, target, source ) {
			$(this).trigger({
					type: action,
					dataset: data,
					action: action,
					bindTo: target,
					source: source
			});
		};
		
		Template.prototype.capture = function( events ) {
			if( typeof events === 'string' ) {
				events = [ events ];
			}
			
			events = events || {};
			
			var i = null,
				me = this,
				handler = function( e ) { return me.capture.handler.call( me, e ); };
			
			for( i in events ) {
				$(this.dom).on( events[i], handler );
			};
		};
		
		Template.prototype.capture.handler = function( e ) {
			var source = e.target,
				target = source,
				action = $(target).attr( 'data-action' ),
				root = this.dom.parentNode,
				dataset = {};
			
			while( target != null && target != root && action == null ) {
				target = target.parentNode;
				if( target != null ) {
					action = $(target).attr( 'data-action' );
				}
			}
			
			if( target == null || target == root ) {
				return;
			}
			
			if( Dataset.exists( target ) ) {
				dataset = Dataset.get( target );
			}
			
			this.action( action, dataset, target, source );
			
		};
		
		Template.prototype.bind = function( action, callback ) {
			$(this).bind( action, callback );
		};
		
		Template.prototype.on = Template.prototype.bind;
		
		Template.prototype.unbind = function( action, callback ) {
			$(this).unbind( action, callback );
		};
		
		Template.prototype.show = function( placeholder ) {
			this.placeholder = placeholder || this.placeholder;
			placeholder = $('.placeholder.' + this.placeholder );
			
			placeholder
				.children()
				.detach();
			
			placeholder.append( this.dom );
			
			this.action( 'tpl_show', Dataset.get( this.dom ), this, this );
		};
		
		Template.prototype.isVisible = function(){
			return $(this.dom).parents('body').length > 0;
		};
		
		ready( Template );
		
	});
	
});