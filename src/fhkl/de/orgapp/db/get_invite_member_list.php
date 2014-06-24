<?php
 /*
  * Getting all members from one user's groups
  * */
$response = array ();

if(!isset($_GET['personId']) || !isset($_GET['groupId']))
{
	$response ["success"] = 0;
	echo json_encode($response);
}

require_once __DIR__ . '/db_connect.php';

$db = new DB_CONNECT ();

$personId = $_GET ['personId'];
$groupId = $_GET ['groupId'];

$result = mysql_query("SELECT DISTINCT priv.personId, pers.eMail, pers.firstName, pers.lastName
						FROM privilege priv JOIN person pers USING (personId)
						WHERE priv.groupId IN (SELECT groupId FROM groups WHERE personId = '$personId')
						AND priv.personId NOT LIKE '$personId' AND priv.groupId NOT LIKE '$groupId'") or die (mysql_error());

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