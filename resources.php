<?php
	// Temporary script serving resources.
	
	$resources = $_REQUEST[ 'resources' ];
	
	$output = array();
	
	// Create artificial latency
	sleep( 1 );
	
	foreach( $resources as $resource ) {
		// $resource = js://relative/path/to/resource
		
		$name = explode( '://', $resource );
		$path = '';
		
		if( $name[0] == 'js' ) {
			$path = 'scripts/';
		}
		
		$path .= $name[1] . '.' . $name[0];
		
		// Return real directory to frontend
		$output[$resource] = $path;
	}
	
	echo json_encode( $output );