(function( ready, app ){
	
	app.load( 'js://lib/jquery', function( $ ){
		var LoadIndicator = function( options ) {
			this.canvas = options.canvas || Util.create( 'canvas' );
			this.data = options.dataset || {};

			this.style = {};
			
			this.canvas.height = $(this.canvas).height();
			this.canvas.width = $(this.canvas).width();
			
			this.draw();
		};
		
		LoadIndicator.prototype.draw = function() {
			this.drawLoads();
		};

		LoadIndicator.prototype.drawLoads = function() {
			var c = this.canvas.getContext('2d'),
				x = this.canvas.width / 2,
				y = this.canvas.height / 2,
				r = x > y ? y-5 : x-5,
				i = 0,
				percentage = {
					green: 0,
					orange: 0,
					red: 0
				},
				total = 0,
				arc = Math.PI*1.2,
				base = Math.PI*0.9,
				val = 0;

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

			c.strokeStyle = 'black';
			for( i in percentage ) {
				val = percentage[i]/total;
				
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
			c.textBaseline = 'bottom';
			c.fillText( 'GROUPNAME', x, y );
			
			c.textBaseline = 'hanging';
			c.fillText( total + ' VMs', x, y );
			
		};
		
		LoadIndicator.prototype.update = function() {
			var c = this.canvas.getContext( '2d' );
			
			c.clearRect( 0, 0, this.canvas.width, this.canvas.height );
			this.draw();
		};
		
		ready( LoadIndicator );
	});
	
});