<?php
 /*
  * Getting all members from one user's groups
  * */
$response = array ();

require_once __DIR__ . '/db_connect.php';

$db = new DB_CONNECT ();

$personId = $_GET ['personId'];
$groupId = $_GET ['groupId'];
$result = mysql_query ( "select distinct priv.personId, pers.eMail, pers.firstName, pers.lastName
		from privilege priv join person pers using (personId)
		where priv.groupId in (select groupId from groups where personId = '$personId')
		and priv.personId not like '$personId' and priv.groupId not like '$groupId'") or die ( mysql_error () );

if (mysql_num_rows ( $result ) > 0) {

	$response ["memberlist"] = array ();
	while ( $row = mysql_fetch_array ( $result ) ) {
		$memberlist = array ();
		$memberlist ["personId"] = $row ["personId"];
		$memberlist ["eMail"] = html_entity_decode ( $row ["eMail"], ENT_QUOTES, 'UTF-8' );
		$memberlist ["firstName"] = html_entity_decode ( $row ["firstName"], ENT_QUOTES, 'UTF-8' );
		$memberlist ["lastName"] = html_entity_decode ( $row ["lastName"], ENT_QUOTES, 'UTF-8' );


		array_push ( $response ["memberlist"], $memberlist );
	}
	$response ["success"] = 1;

	echo json_encode ( $response );
} else {
	$response ["success"] = 0;
	$response ["message"] = "No memberlist found";

	echo json_encode ( $response );
}
?>