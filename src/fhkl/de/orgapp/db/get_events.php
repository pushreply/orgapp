<?php
$response = array();
 
require_once __DIR__ . '/db_connect.php';
 
$db = new DB_CONNECT();
 
$result = mysql_query("SELECT * FROM event") or die(mysql_error());
 
if (mysql_num_rows($result) > 0) {

    $response["event"] = array(); 
    while ($row = mysql_fetch_array($result)) {
        $event = array();
        $event["eventId"] = $row["eventId"];
        $event["personId"] = $row["personId"];
        $event["groupId"] = $row["groupId"];
        $event["name"] = $row["name"];
        $event["eventDate"] = $row["eventDate"];
        $event["eventTime"] = $row["eventTime"];
        $event["regularity"] = $row["regularity"];
 
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