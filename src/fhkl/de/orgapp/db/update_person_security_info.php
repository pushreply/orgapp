<?php

/*
 * Following code will check, whether a person exists
*/

$response = array ();

if(!isset($_GET['personId']) || !isset($_GET['eMail']))
{
	$response ["success"] = 0;
	echo json_encode($response);
}

// include db connect class
require_once __DIR__ .'/db_connect.php';

// connecting to db
$db = new DB_CONNECT ();

$personId = $_GET['personId'];
$eMail = $_GET['eMail'];

$result = mysql_query("UPDATE person SET eMail = '$eMail' WHERE personId = '$personId'") or die(mysql_error());

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