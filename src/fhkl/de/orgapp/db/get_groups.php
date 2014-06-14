<?php
$response = array();

require_once __DIR__ . '/db_connect.php';

$db = new DB_CONNECT();

$name = $_GET ['name'];
$result = mysql_query("SELECT * FROM groups where name = '$name'") or die(mysql_error());

if (mysql_num_rows($result) > 0) {

    $response["groups"] = array();
    while ($row = mysql_fetch_array($result)) {
        $groups = array();
        $groups["groupId"] = $row["groupId"];
        $groups["personId"] = $row["personId"];
        $groups["name"] = html_entity_decode($row["name"], ENT_QUOTES, 'UTF-8');
        $groups["info"] = html_entity_decode($row["info"], ENT_QUOTES, 'UTF-8');
        $groups["picture"] = $row["picture"];

        array_push($response["groups"], $groups);
    }
    $response["success"] = 1;

    echo json_encode($response);
} else {
    $response["success"] = 0;
    $response["message"] = "No groups found";

    echo json_encode($response);
}
?>