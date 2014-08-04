<?php
 /*
  * A member leaves a group. 
  * This will remove a member from a selected group. 
  * */
$response = array ();

if(!isset($_GET['personId']))
{
	$response ["success"] = 0;
	echo json_encode($response);
}

require_once __DIR__ . '/db_connect.php';

$db = new DB_CONNECT ();

$personId = $_GET ['personId'];
$groupId = $_GET ['groupId'];

$result = mysql_query("DELETE FROM privilege WHERE groupId = '$groupId' AND personId = '$personId'") or die (mysql_error());

if ($result) {
	$response ["success"] = "1";

	echo json_encode ( $response );
} else {
	$response ["success"] = "0";

	echo json_encode ( $response );
}

?>