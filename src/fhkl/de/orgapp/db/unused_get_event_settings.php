<?php

/*
 * Querying all notification settings of a person
*/

$response = array();

require_once __DIR__ . '/db_connect.php';

$db = new DB_CONNECT();

$personId = $_GET ['personId'];
$result = mysql_query("SELECT * FROM eventSettings where personId = '$personId'") or die(mysql_error());

if (mysql_num_rows($result) > 0) {

    $response["eventSettings"] = array();
    while ($row = mysql_fetch_array($result)) {
        $EventSettings = array();
        $EventSettings["eventSettingsId"] = $row["eventSettingsId"];
        $EventSettings["shownEntries"] = $row["shownEntries"];

        array_push($response["eventSettings"], $EventSettings);
    }
    $response["success"] = 1;

    echo json_encode($response);
} else {
    $response["success"] = 0;
    $response["message"] = "No event settings found";

    echo json_encode($response);
}
?>