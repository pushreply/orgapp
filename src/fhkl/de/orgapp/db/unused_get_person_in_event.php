<?php

 /*	checks if person is attending an event
  *  */

$response = array();

require_once __DIR__ . '/db_connect.php';

$db = new DB_CONNECT();

$personId = $_GET ['personId'];
$eventId = $_GET ['eventId'];
$result = mysql_query("SELECT * from eventPerson WHERE eventId = '$eventId' and personId = '$personId'") or die(mysql_error());

if (mysql_num_rows($result) > 0) {
    $response["success"] = 1;

    echo json_encode($response);
} else {
    $response["success"] = 0;
    $response["message"] = "No member found";

    echo json_encode($response);
}
?>