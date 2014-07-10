<?php

/*
 * Creating one person in event
 */
$response = array ();

require_once __DIR__ . '/db_connect.php';

$db = new DB_CONNECT ();

$personId = $_GET ['personId'];
$eventId = $_GET ['eventId'];
$result = mysql_query ( "Insert into eventPerson(personId, eventId) values('$personId', '$eventId')" ) or die ( mysql_error () );
if ($result) {
	$response ["success"] = "1";
	$response ["message"] = "Group successfully created.";

	echo json_encode ( $response );
} else {
	$response ["success"] = "0";
	$response ["message"] = "Oops! An error occurred.";

	echo json_encode ( $response );
}

?>