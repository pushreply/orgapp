<?php

/*
 * Removing one attending user from an event
 */
$response = array ();

require_once __DIR__ . '/db_connect.php';

$db = new DB_CONNECT ();

$personId = $_GET ['personId'];
$eventId = $_GET ['eventId'];

$result = mysql_query ( "DELETE FROM eventPerson WHERE eventId = '$eventId' and personId = '$personId'") or die ( mysql_error () );
if ($result) {
	$response ["success"] = "1";

	echo json_encode ( $response );
} else {
	$response ["success"] = "0";

	echo json_encode ( $response );
}

?>