<?php

$response = array ();

if(!isset($_GET['notificationsId']))
{
	$response ["success"] = 0;
	echo json_encode($response);
}

require_once __DIR__ . '/db_connect.php';

$db = new DB_CONNECT ();

$notificationsId = $_GET ['notificationsId'];

$result = mysql_query("UPDATE notifications SET isRead = '1' WHERE notificationsId = '$notificationsId'") or die (mysql_error ());

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