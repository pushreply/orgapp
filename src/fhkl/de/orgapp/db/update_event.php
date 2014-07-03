<?php

/*
 * Following code will update an event
 */
$response = array ();

require_once __DIR__ . '/db_connect.php';

$db = new DB_CONNECT ();

$eventId = $_GET ['eventId'];
$name = htmlentities($_GET ['name']);
$eventDate = $_GET ['eventDate'];
$eventTime = $_GET ['eventTime'];
$eventLocation = htmlentities($_GET ['eventLocation']);

$result = mysql_query ( "UPDATE event SET name = '$name', eventDate = '$eventDate', eventTime = '$eventTime',
		eventLocation = '$eventLocation' WHERE eventId = '$eventId'" ) or die ( mysql_error () );

if ($result == 1) {
	$response ["success"] = 1;

	echo json_encode ( $response );
} else {
	$response ["success"] = 0;
	echo json_encode ( $response );
}

?>