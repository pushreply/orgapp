<?php

/*
 * Querying all events of a group.
*/

$response = array ();

if(!isset($_GET['groupId']))
{
	$response ["success"] = 0;
	echo json_encode($response);
}

// include db connect class
require_once __DIR__ .'/db_connect.php';

// connecting to db
$db = new DB_CONNECT ();

$groupId = $_GET['groupId'];
$result = mysql_query("select eventDate, eventTime, name from event where groupId = '$groupId'") or die(mysql_error());

// check for required fields
if(mysql_num_rows($result) > 0)
{
	$response["groupevents"] = array();

	while ($row = mysql_fetch_array($result))
	{
		$groupevents = array();

		$groupevents["edate"] = $row["eventDate"];
		$groupevents["etime"] = $row["eventTime"];
		$groupevents["ename"] = html_entity_decode($row["name"], ENT_QUOTES, 'UTF-8');

		array_push($response["groupevents"], $groupevents);
	}

	$response ["success"] = 1;
	
	echo json_encode($response);
}
else
{
	$response ["success"] = 0;
	$response["groupeventname"] = "You are not in a group.";
	echo json_encode($response);
}

?>