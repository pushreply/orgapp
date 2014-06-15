<?php

/*
 * Following code will check, whether a person exists
*/

$response = array ();

if(!isset($_GET['eMail']))
{
	$response ["success"] = 0;
	echo json_encode($response);
}

// include db connect class
require_once __DIR__ .'/db_connect.php';

// connecting to db
$db = new DB_CONNECT ();

$eMail = htmlentities($_GET['eMail']);
$result = mysql_query("SELECT * FROM person WHERE eMail = '$eMail'") or die(mysql_error());

// check for required fields
if(mysql_num_rows($result) > 0)
{
	$response["person"] = array();

	while ($row = mysql_fetch_array($result))
	{
		$person = array();

		$person["personId"] = $row["personId"];
		$person["eMail"] = html_entity_decode($row["eMail"], ENT_QUOTES, 'UTF-8');
		$person["password"] = $row["password"];
		$person["firstName"] = html_entity_decode($row["firstName"], ENT_QUOTES, 'UTF-8');
		$person["lastName"] = html_entity_decode($row["lastName"], ENT_QUOTES, 'UTF-8');
		$person["created"] = $row["created"];
		$person["birthday"] = $row["birthday"];
		$person["gender"] = $row["gender"];
		$person["picture"] = $row["picture"];

		array_push($response["person"], $person);
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