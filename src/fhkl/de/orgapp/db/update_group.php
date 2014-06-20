<?php

/*
 * Following code will update a member's privileges
 */
$response = array ();

require_once __DIR__ . '/db_connect.php';

$db = new DB_CONNECT ();

$groupId = $_GET ['groupId'];
$name = htmlentities($_GET ['name']);
$info = htmlentities($_GET ['info']);

$result = mysql_query ( "UPDATE groups SET name = '$name', info = '$info' WHERE groupId = '$groupId'" ) or die ( mysql_error () );

if ($result == 1) {
	$response ["success"] = 1;

	echo json_encode ( $response );
} else {
	$response ["success"] = 0;
	echo json_encode ( $response );
}

?>