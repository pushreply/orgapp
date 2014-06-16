<?php

/*
 * Following code will update a person's notification settings
*/

$response = array ();

// include db connect class
require_once __DIR__ .'/db_connect.php';

// connecting to db
$db = new DB_CONNECT ();

$personId = $_GET['personId'];
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
$shownEntries = $_GET ['shownEntries'];

$result = mysql_query("UPDATE notificationSettings
		SET shownEntries = '$shownEntries', groupInvites= '$groupInvites',
		groupEdited = '$groupEdited', groupRemoved = '$groupRemoved', eventsAdded = '$eventsAdded',
		eventsEdited = '$eventsEdited', eventsRemoved = '$eventsRemoved', commentsAdded = '$commentsAdded',
		commentsEdited = '$commentsEdited', commentsRemoved = '$commentsRemoved', privilegeGiven = '$privilegeGiven'
		WHERE personId = '$personId'") or die(mysql_error());

if($result == 1)
{
	$response ["success"] = 1;

	echo json_encode($response);
}
else
{
	$response ["success"] = 0;
	echo json_encode($response);
}

?>