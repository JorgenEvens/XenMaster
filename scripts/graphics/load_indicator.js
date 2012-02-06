/*
 * load_indicator.js
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
 * load_indicator.js
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
	
	app.load( 'js://lib/jquery', function( $ ){
		var LoadIndicator = function( options ) {
			this.canvas = options.canvas || Util.create( 'canvas' );
			this.data = options.dataset || {};
			this.animation_duration = options.duration || 250;
			
			this.style = {};
			
			this.canvas.height = $(this.canvas).height();
			this.canvas.width = $(this.canvas).width();
			
			this.target = {
				green: 0,
				orange: 0,
				red: 0	
			};
			
			this.current = {
				green: 0.33,
				orange: 0.33,
				red: 0.33	
			};
			
			this.setTarget();
			
			this.draw();
		};
		
		LoadIndicator.prototype.draw = function() {
			this.advance();
			this.drawLoads();
		};
		
		LoadIndicator.prototype.advance = function() {
			var i = null,
				val = null,
				animate = true,
				is_ready = false,
				me = this,
				target = null,
				abs_speed = null,
				speed = null;
				
			
			for( i in this.current ) {
				current = this.current[i];
				target = this.target[i];
				is_ready = false;
				speed = this.animation[i];
				abs_speed = Math.abs( speed );
				
				if( current > target - abs_speed && current < target + abs_speed ) {
					this.current[i] = target;
					is_ready = true;
				} else {
					this.current[i] += speed;
				}
				
				animate = is_ready && animate;
			}
			
			if( !animate ) {
				window.setTimeout( function(){ me.draw(); }, 1000/60 );
			};
		};
		
		LoadIndicator.prototype.setTarget = function() {
			var percentage = {
					green: 0,
					orange: 0,
					red: 0
				},
				i = null,
				total = 0,
				fps = 60,
				frames = Math.round( this.animation_duration * ( fps / 1000 ) );
			
			for( i in this.data ) {
				if( this.data[i] <= 0.3 ) {
					percentage.green++;
				} else if ( this.data[i] <= 0.6 ) {
					percentage.orange++;
				} else {
					percentage.red++;
				};
				total++;
			}
			
			for( i in percentage ) {
				this.target[i] = percentage[i]/total;
			}
			
			this.animation = { // calculate step sizes
					green: ( this.target['green'] - this.current['green'] ) / frames,
					orange: ( this.target['orange'] - this.current['orange'] ) / frames,
					red: ( this.target['red'] - this.current['red'] ) / frames
			};
		};

		LoadIndicator.prototype.drawLoads = function() {
			var c = this.canvas.getContext('2d'),
				x = this.canvas.width / 2,
				y = this.canvas.height / 2,
				percentage = this.current,
				r = x > y ? y-5 : x-5,
				i = 0,
				arc = Math.PI*1.2,
				base = Math.PI*0.9,
				val = 0;
			
			if( r < 0 ) {
				r = 0;
			}
				
			c.clearRect( 0, 0, this.canvas.width, this.canvas.height );

			c.strokeStyle = 'black';
			for( i in percentage ) {
				val = percentage[i];
				
				c.beginPath();
				c.moveTo( x, y );
				c.arc( x, y, r, base, base+(arc*val) );
				c.fillStyle = i;
				c.closePath();
				c.fill();
				c.stroke();
				base = base+(arc*val);
			}
			
			c.beginPath();
			c.arc( x, y, r-r*0.3, 0, Math.PI*2 );
			c.fillStyle = 'white';
			c.lineWidth = 1;
			c.closePath();
			c.fill();
			c.stroke();
			base = base+(arc*val);
			
			c.fillStyle = 'black';
			c.textAlign = 'center';
			c.font = Math.round( r/6  ) + 'px sans-serif';
			c.textBaseline = 'bottom';
			c.fillText( 'GROUPNAME', x, y );
			
			c.textBaseline = 'hanging';
			//c.fillText( total + ' VMs', x, y );
			
		};
		
		LoadIndicator.prototype.update = function() {
			this.setTarget();

			this.draw();
		};
		
		ready( LoadIndicator );
	});
	
});