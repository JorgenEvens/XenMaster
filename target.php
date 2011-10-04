<?php
	session_start();
	
	if( isset( $_POST['target'] ) ) {
		$_SESSION['server_target'] = $_POST['target'];
	}
	
	if( isset( $_GET['get_target'] ) ) {
		echo isset( $_SESSION['server_target'] ) ? $_SESSION['server_target'] : 'localhost:12345/wwscp';
		exit();
	}
?>
<html>
	<head>
		<title>Set Frontend target</title>
	</head>
	<body>
		<form action="" method="POST">
			<label for="server">XenMaster backend address:</label>
			<input type="text" name="target" value="<?php echo isset( $_SESSION['server_target'] ) ? $_SESSION['server_target'] : 'localhost:12345/wwscp'; ?>" />
			<br />
			<input type="submit" value="Set Target" />
		</form>
	</body>
</html>
	