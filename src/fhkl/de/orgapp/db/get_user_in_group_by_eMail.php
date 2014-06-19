<?php

/*
 * Following code will check, whether a person is in a group
 */
$response = array ();

// include db connect class
require_once __DIR__ . '/db_connect.php';

// connecting to db
$db = new DB_CONNECT ();

$groupId = $_GET ['groupId'];
$eMail = htmlentities ( $_GET ['eMail'] );
$result = mysql_query ( "SELECT * FROM privilege p, person pers WHERE p.groupId = '$groupId'
		 and p.personId = pers.personId and pers.eMail = '$eMail'" ) or die ( mysql_error () );

if (mysql_num_rows ( $result ) > 0) {

	$response ["member"] = array ();
	$row = mysql_fetch_array ( $result );
	$member = array ();
	$member ["privilegeManagement"] = $row ["privilegeManagement"];
	$member ["memberInvitation"] = $row ["memberInvitation"];
	$member ["memberlistEditing"] = $row ["memberlistEditing"];
	$member ["eventCreating"] = $row ["eventCreating"];
	$member ["eventEditing"] = $row ["eventEditing"];
	$member ["eventDeleting"] = $row ["eventDeleting"];
	$member ["commentEditing"] = $row ["commentEditing"];
	$member ["commentDeleting"] = $row ["commentDeleting"];

	array_push ( $response ["member"], $member );

	$response ["success"] = 1;

	echo json_encode ( $response );
} else {
	$response ["success"] = 0;
	echo json_encode ( $response );
}

?>