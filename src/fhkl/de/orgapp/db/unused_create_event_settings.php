<?php

/*
 * Creating a notification settings for a new registered user
 */
$response = array ();

// include db connect class
require_once __DIR__ . '/db_connect.php';

// connecting to db
$db = new DB_CONNECT ();

$personId = $_GET ['personId'];

$result = mysql_query ("Insert into eventSettings (personId) values('$personId')") or die ( mysql_error () );
if ($result) {
	// successfully inserted into database
	$response ["success"] = 1;
	$response ["message"] = "Event Settings successfully created.";

	// echoing JSON response
	echo json_encode ( $response );
} else {
	// failed to insert row
	$response ["success"] = 0;
	$response ["message"] = "Oops! An error occurred.";

	// echoing JSON response
	echo json_encode ( $response );
}
?>