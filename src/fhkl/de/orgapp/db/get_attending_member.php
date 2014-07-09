<?php

 /*	returns all attending members of one event
  *  */

$response = array();

require_once __DIR__ . '/db_connect.php';

$db = new DB_CONNECT();

$personId = $_GET ['personId'];
$eventId = $_GET ['eventId'];
$result = mysql_query("SELECT pers.personId, pers.eMail, pers.firstName, pers.lastName
from person pers join eventPerson ep using(personId) where ep.eventId = '$eventId' and ep.personId not like '$personId'") or die(mysql_error());

if (mysql_num_rows($result) > 0) {

    $response["member"] = array();
    while ($row = mysql_fetch_array($result)) {
        $member = array();
        $member["personId"] = $row["personId"];
        $member["eMail"] = html_entity_decode($row["eMail"], ENT_QUOTES, 'UTF-8');
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