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

$result = mysql_query("SELECT * FROM notifications WHERE personId = '$personId' AND isRead = '0'") or die (mysql_error ());

if (mysql_num_rows($result) > 0)
	$response ["hasNewNotifications"] = 1;
else
	$response ["hasNewNotifications"] = 0;

$response ["success"] = 1;
echo json_encode($response);

?>