<?php
	$templateName = $_GET['tpl_name'];
	
	$tpl = array( 'view' => '', 'code' => '' );
	
	$tpl['view'] = file_get_contents( 'view/' . $templateName . '.html' );
	$tpl['code'] = file_get_contents( 'view/' . $templateName . '.html.js' );
	
	echo json_encode( $tpl );