(function( ready, app ){
	
	var Bindable = function( object, get, set ) {
			var concreetType = Bindable[object];
			if( concreetType ) {
				return new concreetType( [].slice.call( arguments, 1 ) );
			}
			
			this._object = object;
			this._get = get;
			this._set = set;
			this._handlers = [];

			this.attach();
		};
	
	/**
	 * Create an inheriting instance
	 */
	Bindable.create = function( constructor ) {
		var target = function( args ){
				constructor.apply( this, args );
				Bindable.apply( this, args );
			},
			f = function(){};
		f.prototype = Bindable.prototype;
		target.prototype = new f();
		return target;
	};
	
	/**
	 * Bindable
	 */
	
	Bindable.prototype.get = function() {
		if( !this._get ) throw "No getter property supplied";
		
		if( typeof this._get == 'function' ) {
			this.get = function() {
				return this._get.apply( this._object );
			};
		} else if( typeof this._object[this._get] == 'function' ) {
			this.get = function() {
				return this._object[this._get]();
			};
		} else {
			this.get = function() {
				return this._object[this._get];
			};
		}
		return this.get();
	};
	
	Bindable.prototype.set = function( value ) {
		if( !this._set ) throw "No setter property supplied";
		
		if( typeof this._set == 'function' ) {
			this.set = function( value ) {
				this._set.call( this._object, value );
			};
		} else if( typeof this._object[this._set] == 'function' ) {
			this.set = function( value ) {
				this._object[this._set]( value );
			};
		} else {
			this.set = function( value ) {
				this._object[this._set] = value;
			};
		}
		return this.set( value );
	};
	
	/* Bindings attach here */
	Bindable.prototype.bind = function( callback ) {
		this._handlers.push( callback );
	};
	
	Bindable.prototype.unbind = function( callback ) {
		delete this._handlers[ this._handlers.indexOf( callback ) ];
	};
	
	Bindable.prototype._handler = function() {
		var i = null,
			newValue = this.get();

		for( i in this._handlers ) {
			this._handlers[i]( newValue );
		};
	};
	
	Bindable.prototype.attach = function() {
		throw "Not implemented";
	};
	
	Bindable.prototype.release = function() {
		throw "Not implemented";
	};
	
	Bindable.prototype.link = function( bindable, twoWay ) {
		var me = this;
		
		this.bind(function( value ){
			bindable.set( value );
		});
		if( twoWay ) {
			bindable.bind(function( value ){
				me.set( value );
			});
		}
		
		bindable.set( me.get() );
	};
	
	Bindable.prototype.unlink = function() {
		this._handlers = [];
	};
	
	
	/* Concreet Bindables */
	
	Bindable.jQuery = (function(){
		var binding = Bindable.create(function( object, get, set, events ){
				this._events = [];
				if( Util.isArray( events ) ) {
					this._events = [];
				} else if( events != null ) {
					this._events.push( events );
				}
			}),
			p = binding.prototype;
		
		p.attach = function() {
			this.release();
			
			var me = this,
				handler = function() { me._handler(); };
			
			this.release = function() {
				me._object.off( this._events.join(' '), handler );
			};
			
			this._object.on( this._events.join(' '), handler);
		};
		
		p.release = function() {};
		
		return binding;
	}());
	
	Bindable.Entity = (function(){
		var binding = Bindable.create(function(){});
			p = binding.prototype;
			
		p.attach = function() {
			this.release();
			
			var me = this,
			handler = function( data ) {
				if( data.reference == me._object.reference && 
					data.entityType == 'vm' &&
					me._get in data.changes ) {
					
					me._object[me._get] = data.changes[me._get];
					me._handler();
				}
			};
			
			// TODO: Insert middleman ( EntityObserver )
			app.load( 'js://net/xmconnection', function( xm ) {
				xm = xm.getInstance();

				xm.addHook( 'log', 'event', handler );
			});
			
			this.release = function() {
				xm.removeHook( 'log', 'event', handler );
			};
		};
		
		p.release = function() {};
		
		return binding;
	}());
	
	Bindable.Static = (function(){
		var binding = Bindable.create(function(){}),
			p = binding.prototype;
		
		p.attach = function(){};
		p.release = function(){};
		
		return binding;
	}());
	
	ready( Bindable );
	
});