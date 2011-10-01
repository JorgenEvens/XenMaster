<?php
	// Temporary script serving resources.
	
	$resources = $_REQUEST[ 'resources' ];
	
	$output = array();
	
	sleep( 1 );
	
	foreach( $resources as $resource ) {
		$name = explode( '://', $resource );
		$path = '';
		
		if( $name[0] == 'js' ) {
			$path = 'scripts/';
		}
		
		$path .= $name[1] . '.' . $name[0];
		
		$content = file_get_contents( $path );
		$output[$resource] = $content;
	}
	
	echo json_encode( $output );