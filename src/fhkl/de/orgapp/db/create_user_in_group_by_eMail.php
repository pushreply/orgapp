<?php

/*
 * Following code will create a new person in a group
 */

// array for JSON response
$response = array ();

$groupId = $_GET ['groupId'];
$eMail = $_GET ['eMail'];
$memberSince = $_GET ['memberSince'];

// include db connect class
require_once __DIR__ . '/db_connect.php';

// connecting to db
$db = new DB_CONNECT ();

// mysql inserting a new row
$result = mysql_query ( "INSERT INTO privilege(personId, groupId, memberSince,
		privilegeManagement, memberInvitation, memberlistEditing,
		eventCreation, eventEditing, eventDeleting, commentEdition,
		commentDeleting)
		VALUES((select personId from person where eMail = '$eMail'), '$groupId', '$memberSince',
		true, true, true, true, true, true, true, true)" );

// check if row inserted or not
if ($result) {
	// successfully inserted into database
	$response ["success"] = 1;
	$response ["message"] = "Person in group successfully created.";

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