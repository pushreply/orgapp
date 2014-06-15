<?php

/*
 * Following code will check, whether a person is in a group
*/

$response = array ();


// include db connect class
require_once __DIR__ .'/db_connect.php';

// connecting to db
$db = new DB_CONNECT ();

$groupId = $_GET ['groupId'];
$eMail = htmlentities($_GET ['eMail']);
$result = mysql_query("SELECT * FROM privilege p, person pers WHERE p.groupId = '$groupId'
		 and p.personId = pers.personId and pers.eMail = '$eMail'") or die(mysql_error());

if (mysql_num_rows($result) > 0) {

	$response ["success"] = 1;

	echo json_encode($response);
}
else
{
	$response ["success"] = 0;
	echo json_encode($response);
}

?>