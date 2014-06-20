<?php

/*
 * Following code will return all users of a group
 */
$response = array ();

// include db connect class
require_once __DIR__ . '/db_connect.php';

// connecting to db
$db = new DB_CONNECT ();

$groupId = $_GET ['groupId'];
$result = mysql_query ( "SELECT pers.personId, pers.eMail FROM privilege priv join person pers using(personId)
		WHERE priv.groupId = '$groupId'") or die ( mysql_error () );

if (mysql_num_rows ( $result ) > 0) {

	$response ["member"] = array ();
	while ( $row = mysql_fetch_array ( $result ) ) {
		$member = array ();
		$member ["personId"] = $row ["personId"];
		$member ["eMail"] = html_entity_decode($row["eMail"], ENT_QUOTES, 'UTF-8');

		array_push ( $response ["member"], $member );
	}

	$response ["success"] = 1;

	echo json_encode ( $response );
} else {
	$response ["success"] = 0;
	echo json_encode ( $response );
}

?>