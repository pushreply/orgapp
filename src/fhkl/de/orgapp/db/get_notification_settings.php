<?php

/*
 * Querying all notification settings of a person
*/

$response = array();

require_once __DIR__ . '/db_connect.php';

$db = new DB_CONNECT();

$personId = $_GET ['personId'];
$result = mysql_query("SELECT * FROM notificationSettings where personId = '$personId'") or die(mysql_error());

if (mysql_num_rows($result) > 0) {

    $response["notificationSettings"] = array();
    while ($row = mysql_fetch_array($result)) {
        $notificationSettings = array();
        $notificationSettings["personId"] = $row["personId"];
        $notificationSettings["shownEntries"] = $row["shownEntries"];
        $notificationSettings["groupInvites"] = $row["groupInvites"];
        $notificationSettings["groupEdited"] = $row["groupEdited"];
        $notificationSettings["groupRemoved"] = $row["groupRemoved"];
        $notificationSettings["eventsAdded"] = $row["eventsAdded"];
        $notificationSettings["eventsEdited"] = $row["eventsEdited"];
        $notificationSettings["eventsRemoved"] = $row["eventsRemoved"];
        $notificationSettings["commentsAdded"] = $row["commentsAdded"];
        $notificationSettings["commentsEdited"] = $row["commentsEdited"];
        $notificationSettings["commentsRemoved"] = $row["commentsRemoved"];
        $notificationSettings["privilegeGiven"] = $row["privilegeGiven"];

        array_push($response["notificationSettings"], $notificationSettings);
    }
    $response["success"] = 1;

    echo json_encode($response);
} else {
    $response["success"] = 0;
    $response["message"] = "No notification settings found";

    echo json_encode($response);
}
?>