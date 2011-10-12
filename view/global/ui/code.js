(function( $, app ){
	
	this.content = $(this.dom).find('#content').get(0);
	
	this.show = function() {
		$('body').append( this.dom );
	};
	
	this.setContent = function( tpl ) {
		$(this.content)
			.html('')
			.append( tpl.dom );
	};
	
});