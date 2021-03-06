<?php

/*
 * Querying groups that belong to a personId.
*/

$response = array ();

if(!isset($_GET['personId']))
{
	$response ["success"] = 0;
	echo json_encode($response);
}

// include db connect class
require_once __DIR__ .'/db_connect.php';

// connecting to db
$db = new DB_CONNECT ();

$personId = $_GET['personId'];
$result = mysql_query("SELECT g.groupId, g.personId, g.name, g.info, g.picture
		FROM groups g join privilege p using (groupId) WHERE p.personId = '$personId'") or die(mysql_error());

// check for required fields
if(mysql_num_rows($result) > 0)
{
	$response["groups"] = array();

	while ($row = mysql_fetch_array($result))
	{
		$group = array();
		$groups["groupId"] = $row["groupId"];
		$groups["personId"] = $row["personId"];
		$groups["name"] = html_entity_decode($row["name"], ENT_QUOTES, 'UTF-8');
		$groups["info"] = html_entity_decode($row["info"], ENT_QUOTES, 'UTF-8');
		$groups["picture"] = $row["picture"];

		array_push($response["groups"], $groups);
	}

	$response ["success"] = 1;

	echo json_encode($response);
}
else
{
	$response ["success"] = 0;
	$response["gname"] = "You are not in a group.";
	echo json_encode($response);
}

?>