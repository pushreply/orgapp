<?php

/*
 * Following code will create a new notification
 */

// array for JSON response
$response = array ();

$eMail = $_GET ['eMail'];
$classification = $_GET ['classification'];
$message = $_GET ['message'];
$syncInterval = $_GET ['syncInterval'];

// include db connect class
require_once __DIR__ . '/db_connect.php';

// connecting to db
$db = new DB_CONNECT ();

// mysql inserting a new row
$result = mysql_query ( "INSERT INTO notifications(personId, classification, message, syncInterval)
		VALUES((select personId from person where eMail = '$eMail'), '$classification', '$message', '$syncInterval')" );

// check if row inserted or not
if ($result) {
	// successfully inserted into database
	$response ["success"] = 1;
	$response ["message"] = "Notification successfully created.";

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