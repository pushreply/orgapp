<?php

/*
 * Removing an user from a group
 */
$response = array ();

require_once __DIR__ . '/db_connect.php';

$db = new DB_CONNECT ();

$personId = $_GET ['personId'];
$groupId = $_GET ['groupId'];

$result = mysql_query ( "Delete from privilege where groupId = '$groupId' and personId = '$personId'" ) or die ( mysql_error () );
if ($result) {
	$response ["success"] = "1";

	echo json_encode ( $response );
} else {
	$response ["success"] = "0";

	echo json_encode ( $response );
}

?>