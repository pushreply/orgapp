<?php

/*
 * Creating a new user in group
 */
$response = array ();

// include db connect class
require_once __DIR__ . '/db_connect.php';

// connecting to db
$db = new DB_CONNECT ();

$personId = $_GET ['personId'];
$groupId = $_GET ['groupId'];
$memberSince = $_GET ['memberSince'];

$result = mysql_query ( "Insert into privilege(personId, groupId, memberSince,
		privilegeManagement, memberInvitation, memberlistEditing,
		eventCreation, eventEditing, eventDeleting, commentEdition,
		commentDeleting)
		values('$personId', '$groupId', '$memberSince', true, true, true, true, true, true, true, true)" ) or die ( mysql_error () );
if ($result) {
	// successfully inserted into database
	$response ["success"] = "1";
	$response ["message"] = "User in Group successfully created.";

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