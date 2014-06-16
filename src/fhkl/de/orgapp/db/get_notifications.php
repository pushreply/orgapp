<?php
$response = array ();

require_once __DIR__ . '/db_connect.php';

$db = new DB_CONNECT ();

$personId = $_GET ['personId'];
$groupInvites = $_GET ["groupInvites"];
$groupEdited = $_GET ["groupEdited"];
$groupRemoved = $_GET ["groupRemoved"];
$eventsAdded = $_GET ["eventsAdded"];
$eventsEdited = $_GET ["eventsEdited"];
$eventsRemoved = $_GET ["eventsRemoved"];
$commentsAdded = $_GET ["commentsAdded"];
$commentsEdited = $_GET ["commentsEdited"];
$commentsRemoved = $_GET ["commentsRemoved"];
$privilegeGiven = $_GET ["privilegeGiven"];


if (isset ( $_GET ['shownEntries'] )) {
	$shownEntries = $_GET ['shownEntries'];
	$result = mysql_query ( "SELECT * FROM notifications where personId = '$personId' and classification in
			('$groupInvites', '$groupEdited', '$groupRemoved', '$eventsAdded', '$eventsEdited', '$eventsRemoved', '$commentsAdded', '$commentsEdited', '$commentsRemoved', '$privilegeGiven') order by notificationsId desc" ) or die ( mysql_error () );

	if (mysql_num_rows ( $result ) > 0) {

		$response ["notification"] = array ();

		for($i = 0; $i < $shownEntries & $row = mysql_fetch_array ( $result ); $i ++) {
			$notification = array ();
			$notification ["notificationsId"] = $row ["notificationsId"];
			$notification ["personId"] = $row ["personId"];
			$notification ["classification"] = $row ["classification"];
			$notification ["message"] = html_entity_decode ( $row ["message"], ENT_QUOTES, 'UTF-8' );
			$notification ["syncinterval"] = $row ["syncinterval"];
			array_push ( $response ["notification"], $notification );
		}

		$response ["success"] = 1;

		echo json_encode ( $response );
	} else {
		$response ["success"] = 0;
		$response ["message"] = "No notifications found";

		echo json_encode ( $response );
	}
} else {
	$result = mysql_query ( "SELECT * FROM notifications where personId = '$personId'" ) or die ( mysql_error () );

	if (mysql_num_rows ( $result ) > 0) {

		$response ["notification"] = array ();
		while ( $row = mysql_fetch_array ( $result ) ) {

			$notification = array ();
			$notification ["notificationsId"] = $row ["notificationsId"];
			$notification ["personId"] = $row ["personId"];
			$notification ["classification"] = $row ["classification"];
			$notification ["message"] = html_entity_decode ( $row ["message"], ENT_QUOTES, 'UTF-8' );
			$notification ["syncinterval"] = $row ["syncinterval"];
			array_push ( $response ["notification"], $notification );
		}
		$response ["success"] = 1;

		echo json_encode ( $response );
	} else {
		$response ["success"] = 0;
		$response ["message"] = "No notifications found";

		echo json_encode ( $response );
	}
}

?>