<?php

/*
 * Removing an user from a group
 */
$response = array();

if(!isset($_GET['groupId']))
{
	$response ["success"] = 0;
	echo json_encode($response);
}

require_once __DIR__ . '/db_connect.php';

$db = new DB_CONNECT();

$groupId = $_GET['groupId'];

$result = mysql_query("Delete FROM groups WHERE groupId = '$groupId'") or die (mysql_error());

if($result)
{
	$response ["success"] = "1";

	echo json_encode($response);
}
else
{
	$response ["success"] = "0";

	echo json_encode($response);
}

?>