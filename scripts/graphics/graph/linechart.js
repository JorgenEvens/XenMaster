/*
 * linechart.js
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
		var LineChart = function( options ) {
			this.canvas = options.canvas || Util.create( 'canvas' );
			this.data = options.dataset || {};

			this.style = {
				yAxis: true,
				yAxisWidth: 20,
				yAxisColor: '#000',
				yAxisMax: null,
				yAxisMin: null,
				yAxisStep: null,
				
				xAxis: false,
				xAxisHeight: 20,
				xAxisColor: '#000',
					
				pointColor: '#0F0',
				lineColor: '#00F',
				fillColor: 'rgba( 132, 132, 243, 0.4 )',
					
				pointRadius: 3,
				lineWidth: 1
			};
			
			this.calculate( options );
			
			this.canvas.height = $(this.canvas).height();
			this.canvas.width = $(this.canvas).width();
			
			this.draw();
		};
		
		LineChart.prototype.draw = function() {
			if( this.style.yAxis ) {
				this.drawAxis();
			}
			this.drawLine();
		};
		
		LineChart.prototype.calculate = function( options ) {
			var i = null,
				style = options.style || {},
				value = 0,
				max_given = style.yAxisMax || style.yAxisMax === 0,
				min_given = style.yAxisMin || style.yAxisMin === 0,
				max = max_given ? style.yAxisMax : null,
				min = min_given ? style.yAxisMin : null,
				count = 0;
			
			for( i in this.data ) {
				value = this.data[i];
				
				if( ( max < value || max === null ) && !max_given ) {
					max = value;
				}
				
				if( ( min > value || min === null ) && !min_given ) {
					min = value;
				}
				
				count++;
			}
			
			$.extend( this.style, {
				yAxisMax: max,
				yAxisMin: min,
				yAxisStep: (max-min)/count
			}, options.style );
		};
		
		/*
		 * style = 
		 * {
		 * 		yAxisWidth: 0,
		 * 		yAxisColor: '#000',
		 * 		yAxisMax: 1,
		 * 		yAxisMin: 0,
		 * 		yAxisStep: 0.1,
		 * 		xAxisHeight: 0,
		 * 		xAxisColor: '#000'
		 */
		LineChart.prototype.drawAxis = function() {
			var c = this.canvas.getContext('2d'),
				x = this.style.yAxisWidth+0.5,
				y = this.canvas.height-this.style.xAxisHeight+0.5,
				i = 0,
				steps = this.style.yAxisStep != 0 ? Math.ceil( ( this.style.yAxisMax - this.style.yAxisMin ) / this.style.yAxisStep ) : 0,
				step_size = (y - 10) / steps;
			
			// Set stroke style
			c.lineWidth = 1;

			c.beginPath();
			// Y axis
			c.moveTo( x, 5 );
			c.lineTo( x, y );
			c.strokeStyle = this.style.yAxisColor;
			c.stroke();
			
			c.beginPath();
			// X axis
			c.moveTo( x, y );
			c.lineTo( this.canvas.width, y );
			c.strokeStyle = this.style.xAxisColor;
			c.stroke();
			
			c.beginPath();
			// Y axis steps
			for( i=steps+1; i--; ) {
				c.moveTo( x, y );
				c.lineTo( x-x*0.2, y );
				c.fillText( this.style.yAxisMax - i*this.style.yAxisStep, 0, y+2 );
				
				y -= step_size;
			}
			c.strokeStyle = this.style.yAxisColor;
			c.stroke();
			
		};
		
		LineChart.prototype.drawLine = function() {
			var range = this.style.yAxisMax - this.style.yAxisMin,
				factor = ( this.canvas.height-this.style.xAxisHeight-10 ) / range,
				i = null,
				value = null,
				min = this.style.yAxisMin,
				step_size = 0,
				steps = 0,
				radius = this.style.pointRadius,
				x =  this.style.yAxisWidth + 0.5 + radius,
				y = this.canvas.height-this.style.xAxisHeight+0.5,
				c = this.canvas.getContext( '2d' ),
				first = true;
			
			for( i in this.data ) {
				steps++;
			}
			
			step_size = ( this.canvas.width - this.style.yAxisWidth - radius*2 ) / ( steps - 1 );
			
			c.beginPath();
			c.strokeStyle = this.style.lineColor;
			c.lineWidth = this.style.lineWidth;

			for( i in this.data ) {
				value = ( this.data[i] - min ) * factor;
				
				if( first ) {
					first = false;
					c.moveTo( x-radius, y - value );
				}
				c.lineTo( x, y-value );
				x+=step_size;
			}
			c.stroke();
			c.lineTo( x-step_size+radius, y-value );
			c.lineTo( x-step_size+radius, y );
			c.lineTo( this.style.yAxisWidth + 0.5, y );
			
			c.closePath();
			c.fillStyle = this.style.fillColor;
			c.fill();
			
			x = this.style.yAxisWidth + 0.5 + radius;
			
			c.fillStyle = this.style.pointColor;
			c.beginPath();
			for( i in this.data ) {				
				value = ( this.data[i] - min ) * factor;
				c.moveTo( x, y - value );
				c.arc( x, y - value, radius, 0, Math.PI*2, false );
				
				x+=step_size;
			}
			c.fill();
		};
		
		LineChart.prototype.update = function() {
			
		};
		
		ready( LineChart );
	})
	
});