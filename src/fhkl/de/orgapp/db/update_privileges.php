<?php

/*
 * Following code will update a member's privileges
 */
$response = array ();

require_once __DIR__ . '/db_connect.php';

$db = new DB_CONNECT ();

$personId = $_GET ['personId'];
$groupId = $_GET ['groupId'];
$memberInvitation = $_GET ['memberInvitation'];
$memberlistEditing = $_GET ['memberlistEditing'];
$eventCreating = $_GET ['eventCreating'];
$eventEditing = $_GET ['eventEditing'];
$eventDeleting = $_GET ['eventDeleting'];
$commentEditing = $_GET ['commentEditing'];
$commentDeleting = $_GET ['commentDeleting'];
$privilegeManagement = $_GET ['privilegeManagement'];
$result = mysql_query ( "UPDATE privilege SET memberInvitation = '$memberInvitation', memberlistEditing = '$memberlistEditing',
		eventCreating = '$eventCreating', eventEditing = '$eventEditing', eventDeleting = '$eventDeleting',
		commentEditing = '$commentEditing', commentDeleting = '$commentDeleting', privilegeManagement = '$privilegeManagement'
		WHERE personId = '$personId' and groupId = '$groupId'" ) or die ( mysql_error () );

if ($result == 1) {
	$response ["success"] = 1;

	echo json_encode ( $response );
} else {
	$response ["success"] = 0;
	echo json_encode ( $response );
}

?>