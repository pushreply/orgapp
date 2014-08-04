<?php
 /*
  * Get all persons, who are in the user´s groups and not in the selected one
  * */
$response = array ();

if(!isset($_GET['groupId']) || !isset($_GET['personId']))
{
	$response ["success"] = 0;
	echo json_encode($response);
}

require_once __DIR__ . '/db_connect.php';

$db = new DB_CONNECT ();

$groupId = $_GET ['groupId'];
$personId = $_GET ['personId'];

$result = mysql_query(
						"SELECT DISTINCT pers.personId, pers.eMail, pers.firstName, pers.lastName
						FROM person pers
						JOIN privilege priv
						USING(personId)
						WHERE pers.personId != '$personId'
						AND priv.groupId
						IN
						(
							SELECT groupId
							FROM privilege
							WHERE personId = '$personId'
						)
						AND pers.personId
						NOT IN
						(
							SELECT pers.personId
							FROM person pers
							JOIN privilege priv
							USING(personId)
							WHERE priv.groupId = '$groupId' AND pers.personId != '$personId'
						)"
) or die (mysql_error());

if(mysql_num_rows ( $result ) > 0)
{
	$response ["memberList"] = array();
	
	while($row = mysql_fetch_array($result))
	{
		$memberlist = array();
		
		$memberlist ["personId"] = $row ["personId"];
		$memberlist ["eMail"] = html_entity_decode ( $row ["eMail"], ENT_QUOTES, 'UTF-8' );
		$memberlist ["firstName"] = html_entity_decode ( $row ["firstName"], ENT_QUOTES, 'UTF-8' );
		$memberlist ["lastName"] = html_entity_decode ( $row ["lastName"], ENT_QUOTES, 'UTF-8' );


		array_push($response["memberList"], $memberlist);
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