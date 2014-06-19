<?php

$response = array ();

if(!isset($_GET['personId']))
{
	$response["success"] = 0;
	echo json_encode($response);
}

// include db connect class
require_once __DIR__ .'/db_connect.php';

// connecting to db
$db = new DB_CONNECT ();

$personId = $_GET['personId'];
$currentDate = date("Y-m-d");

$result = mysql_query("SELECT * FROM event WHERE personId = '$personId' AND regularity = '0'") or die(mysql_error());

if(mysql_num_rows($result) > 0)
{
	$response["previousEvents"] = array();

	while ($row = mysql_fetch_array($result))
	{
		// if date of event is in the past
		if(strtotime($row["eventDate"]) < strtotime($currentDate))
		{
			$previousEvent = array();
			$previousEvent["name"] = $row["name"];
			$previousEvent["eventDate"] = $row["eventDate"];
			array_push($response["previousEvents"], $previousEvent);
		}
	}

	if(count($response["previousEvents"]) > 0)
		$response["success"] = 1;
	else
		$response["success"] = 0;

	echo json_encode($response);
}
else
{
	$response["success"] = 0;
	echo json_encode($response);
}

?>