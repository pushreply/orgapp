<?php
$response = array();

require_once __DIR__ . '/db_connect.php';

$db = new DB_CONNECT();

$groupId = $_GET ['groupId'];
$result = mysql_query("SELECT pers.personId, pers.firstName, pers.lastName
from person pers join privilege priv using(personId) where priv.groupId = '$groupId'") or die(mysql_error());

if (mysql_num_rows($result) > 0) {

    $response["member"] = array();
    while ($row = mysql_fetch_array($result)) {
        $member = array();
        $member["personId"] = $row["personId"];
        $member["firstName"] = html_entity_decode($row["firstName"], ENT_QUOTES, 'UTF-8');
        $member["lastName"] = html_entity_decode($row["lastName"], ENT_QUOTES, 'UTF-8');

        array_push($response["member"], $member);
    }
    $response["success"] = 1;

    echo json_encode($response);
} else {
    $response["success"] = 0;
    $response["message"] = "No member found";

    echo json_encode($response);
}
?>