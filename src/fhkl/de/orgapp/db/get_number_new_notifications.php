<?php

$response = array ();

if(!isset($_GET['personId']))
{
	$response ["success"] = 0;
	echo json_encode($response);
}

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

$result = mysql_query("SELECT * FROM notifications WHERE personId = '$personId' AND isRead = '0'
						AND classification IN
						('$groupInvites', '$groupEdited', '$groupRemoved', '$eventsAdded', '$eventsEdited',
						'$eventsRemoved', '$commentsAdded', '$commentsEdited', '$commentsRemoved', '$privilegeGiven')
						order by notificationsId desc") or die (mysql_error ());

$response ["success"] = 1;
$response ["numberNewNotifications"] = mysql_num_rows($result);


echo json_encode($response);

?>