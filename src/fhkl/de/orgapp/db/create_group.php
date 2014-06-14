<?php

/*
 * Creating a new group
 */
$response = array ();

if (isset ( $_GET ['name'] ) && isset ( $_GET ['info'] )) {
	$response ["success"] = 0;
	echo json_encode ( $response );

	// include db connect class
	require_once __DIR__ . '/db_connect.php';

	// connecting to db
	$db = new DB_CONNECT ();

	$personId = $_GET ['personId'];
	$name = $_GET ['name'];
	$info = $_GET ['info'];

	$result = mysql_query ( "Insert into groups(personId, name, info) values('$personId', '$name', '$info')" ) or die ( mysql_error () );

	if ($result) {
		// successfully inserted into database
		$response ["success"] = 1;
		$response ["message"] = "Group successfully created.";

		// echoing JSON response
		echo json_encode ( $response );
	} else {
		// failed to insert row
		$response ["success"] = 0;
		$response ["message"] = "Oops! An error occurred.";

		// echoing JSON response
		echo json_encode ( $response );
	}
} else {
	// required field is missing
	$response ["success"] = 0;
	$response ["message"] = "Required field(s) is missing";

	// echoing JSON response
	echo json_encode ( $response );
}
?>