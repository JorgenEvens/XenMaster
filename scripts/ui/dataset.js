(function( ready, app ){
	
	var Container = function( element ) {
			var now = new Date();
			
			this.element = element;
			
			this.id = now.getMilliseconds() + '.' + Math.round(Math.random()*now.getSeconds()*100);
			this.data = {};
			
			element.dataset.dsid = this.id;
			Dataset.sets[this.id] = this;
		},
		Dataset = function(){
			throw "Dataset should not be instantiated";
		};
		
	Dataset.sets = {};
	
	Dataset.get = function( element ) {
		var id = null,
			container = null;
		
		if( element == null ) {
			return null;
		}
		
		id = element.dataset.dsid;
		
		if( id == null || !Dataset.sets[ id ] ) {
			container = new Container(element);
			return container.data;
		}
		
		return Dataset.sets[ id ].data;
	};
	
	Dataset.exists = function( element ) {
		var id = null;
		
		if( element == null ) {
			return false;
		}
		
		id = element.dataset.dsid;

		if( id == null || !Dataset.sets[ id ] ) {
			return false;
		}
		
		return true;
	};
	
	ready( Dataset );
	
});