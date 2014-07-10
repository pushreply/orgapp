<?php
$response = array();

require_once __DIR__ . '/db_connect.php';

$db = new DB_CONNECT();

$groupId = $_GET ['groupId'];
$result = mysql_query("SELECT ev.eventId, ev.personId, ev.groupId, ev.name, ev.eventDate, ev.eventTime, ev.eventLocation
		FROM event ev
		WHERE ev.groupId = '$groupId' and ev.eventDate > (select CURDATE())") or die(mysql_error());

if (mysql_num_rows($result) > 0) {

    $response["event"] = array();
    while ($row = mysql_fetch_array($result)) {
        $event = array();
        $event["eventId"] = $row["eventId"];
        $event["personId"] = $row["personId"];
        $event["groupId"] = $row["groupId"];
        $event["name"] = html_entity_decode($row["name"], ENT_QUOTES, 'UTF-8');
        $event["eventDate"] = $row["eventDate"];
        $event["eventTime"] = $row["eventTime"];
        $event["eventLocation"] = $row["eventLocation"];

        array_push($response["event"], $event);
    }
    $response["success"] = 1;

    echo json_encode($response);
} else {
    $response["success"] = 0;
    $response["message"] = "No events found";

    echo json_encode($response);
}
?>