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
$result = mysql_query("SELECT * FROM groups g, privilege p WHERE g.groupId=p.groupId and p.personId = '$personId'") or die(mysql_error());

// check for required fields
if(mysql_num_rows($result) > 0)
{
	$response["groups"] = array();

	while ($row = mysql_fetch_array($result))
	{
		$group = array();

		$group["groupId"] = $row["groupId"];
		$group["name"] = $row["name"];
		$group["info"] = $row["info"];
		$group["picture"] = $row["picture"];

		array_push($response["group"], $group);
	}

	$response ["success"] = 1;
	
	echo json_encode($response);
}
else
{
	$response ["success"] = 0;
	echo json_encode($response);
}

?>