<?php

/*
 * Creating a new event
 */
$response = array ();
// include db connect class
require_once __DIR__ . '/db_connect.php';

// connecting to db
$db = new DB_CONNECT ();

$personId = $_GET ['personId'];
$groupId = $_GET ['groupId'];
$name = htmlentities ( $_GET ['name'] );
$eventDate = $_GET ['eventDate'];
$eventTime = $_GET ['eventTime'];
$regularityDate = $_GET ['regularityDate'];
$regularity = $_GET ['regularity'];
$eventLocation = $_GET ['eventLocation'];

$result = mysql_query ( "Insert into event(personId, groupId, name, eventDate, eventTime, regularityDate, regularity, eventLocation)
		 values('$personId', '$groupId', $name', '$eventDate', '$eventTime', '$regularityDate', '$regularity', '$eventLocation')" ) or die ( mysql_error () );
if ($result) {
	// successfully inserted into database
	$response ["success"] = mysql_insert_id ();
	$response ["message"] = "Group successfully created.";

	// echoing JSON response
	echo json_encode ( $response );
} else {
	// failed to insert row
	$response ["success"] = "0";
	$response ["message"] = "Oops! An error occurred.";

	// echoing JSON response
	echo json_encode ( $response );
}

?>