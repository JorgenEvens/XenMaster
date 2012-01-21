(function( ready, app ){
	
	var Bindable = function( object, get, set, events ) {
			var concreetType = null;
			if( arguments.length == 5 ) {
				concreetType = Bindable[arguments[0]];
				return new concreetType( arguments[1], arguments[2], arguments[3], arguments[4] );
			}
			
			this._object = object;
			this._get = get;
			this._set = set;
			this._events = Util.isArray( events ) ? events : [ events ];
			this._handlers = [];

			this.attach();
		};
	
	/**
	 * Create an inheriting instance
	 */
	Bindable.create = function() {
		var target = function(){
				Bindable.apply( this, arguments );
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
		
		if( typeof this._object[this._get] == 'function' ) {
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
		
		if( typeof this._object[this._set] == 'function' ) {
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
		this.bind(function( value ){
			bindable.set( value );
		});
		if( twoWay ) {
			bindable.bind(function( value ){
				this.set( value );
			});
		}
	};
	
	Bindable.prototype.unlink = function() {
		this._handlers = [];
	};
	
	
	/* Concreet Bindables */
	
	Bindable.jQuery = (function(){
		var binding = Bindable.create(),
			p = binding.prototype;
		
		p.attach = function() {
			var me = this,
				handler = function() { me._handler(); };
			
			this.release = function() {
				this._object.off( this._events.join(' '), handler );
			};
			
			this._object.on( this._events.join(' '), handler);
		};
		
		p.release = function() {};
		
		return binding;
	}());
	
	ready( Bindable );
	
});