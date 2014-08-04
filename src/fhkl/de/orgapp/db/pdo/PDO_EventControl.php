<?php

$dbpath = 'PDO_DB_Connect.Inc.php';

/*------------------------------------------------------------
 * INSERT EVENT
* check the user input
*/

if ($_GET['do']=="createEvent"
		&& isset($_GET['personId'])
		&& isset($_GET['groupId'])
		&& isset($_GET['name'])
		&& isset($_GET['eventDate'])
		&& isset($_GET['eventTime'])
		&& isset($_GET['eventLocation']))
{
	/*
	 * pass the get values to some variables
	*/
	$personId = $_GET['personId'];
	$groupId = $_GET['groupId'];
	$name = htmlspecialchars($_GET['name']); /*escape every '<tag>' (not only HTML) */
	$eventDate = $_GET['eventDate'];
	$eventTime = $_GET['eventTime'];
	$eventLocation = $_GET['eventLocation'];

	$response = array ();

	require_once $_SERVER['DOCUMENT_ROOT'] . '/' . $dbpath;

	try {
		$sql='INSERT INTO event SET
				personId = :personId,
				groupId = :groupId,
				name = :name,
				eventDate = :eventDate
				eventTime = :eventTime
				eventLocation = :eventLocation';

		$sth = $pdo->prepare($sql);

		/* bind the values, in the same order as the $sql statement. */
		$sth->bindValue(':personId', $personId, PDO::PARAM_INT);
		$sth->bindValue(':groupId', $groupId, PDO::PARAM_INT);
		$sth->bindValue(':name', $name);
		$sth->bindValue(':eventDate', $eventDate);
		$sth->bindValue(':eventTime', $eventTime);
		$sth->bindValue(':eventLocation', $eventLocation);

		$confirm = $sth->execute();

		//check insertion status
		if ($confirm==true) {
			// successfully inserted into database
			$response ["success"] = 1;
			$response ["message"] = "Event is successfully created.";
			// echoing JSON response
			echo json_encode ($response);
		}
		else {
			$response ["success"] = 0;
			$response ["message"] = "Event insertion failed.";
			echo json_encode ($response);
		}
	}
	catch (PDOException $e) {
		$response ["success"] = 0;
		$response ["message"] = "Event insertion failed.";
		echo 'ERROR: ' . $e->getMessage();
		exit();
	}
}

/*------------------------------------------------------------
 * UPDATE EVENT
* check the user input
*/

if ($_GET['do']=="updateEvent"
		&& isset($_GET['eventId'])
		&& isset($_GET['name'])
		&& isset($_GET['eventDate'])
		&& isset($_GET['eventTime'])
		&& isset($_GET['eventLocation']))
{
	$eventId = $_GET['eventId'];
	$name = htmlspecialchars($_GET['name']);
	$eventDate = $_GET['eventDate'];
	$eventTime = $_GET['eventTime'];
	$eventLocation = htmlspecialchars($_GET['eventLocation']);

	$response = array ();

	require_once $_SERVER['DOCUMENT_ROOT'] . '/' . $dbpath;

	try {
		$sql='UPDATE event  SET
				name = :name
				eventDate = :eventDate
				eventTime = :eventTime
				eventLocation = :eventLocation
				WHERE eventId = :eventId';

		$sth = $pdo->prepare($sql);

		/* bind the values, in the same order as the $sql statement. */
		$sth->bindValue(':eventId', $eventId, PDO::PARAM_INT);
		$sth->bindValue(':name', $name);
		$sth->bindValue(':eventDate', $eventDate);
		$sth->bindValue(':eventTime', $eventTime);
		$sth->bindValue(':eventLocation', $eventLocation);
		$confirm = $sth->execute();

		if ($confirm==true) {
			$response ["success"] = 1;
			$response ["message"] = "Event is successfully updated.";
			echo json_encode ($response);
		}
		else {
			$response ["success"] = 0;
			$response ["message"] = "Event update failed.";
			echo json_encode ($response);
		}
	}
	catch (PDOException $e) {
		$response ["success"] = 0;
		$response ["message"] = "Event update failed.";
		echo 'ERROR: ' . $e->getMessage();
		exit();
	}
}

/*------------------------------------------------------------
 * DELETE 1 EVENT
* check the user input:
*/

if ($_GET['do']=="deleteEvent"
		&& isset($_GET['eventId']))
{

	$eventId = $_GET['eventId'];

	$response = array ();

	require_once $_SERVER['DOCUMENT_ROOT'] . '/' . $dbpath;

	try {

		$sql = 'DELETE FROM event
				WHERE eventId = :eventId';

		$sth = $pdo->prepare($sql);
		$sth->bindValue(':eventId', $eventId, PDO::PARAM_INT); /* integer must have "PDO::PARAM_INT"; it's a PHP PDO bug :)*/
		$confirm = $sth->execute();

		if ($confirm==true) {
			$response ["success"] = 1;
			$response ["message"] = "Event is successfully deleted.";
			echo json_encode ($response);
		}
		else {

			$response ["message"] = "Event is not deleted.";
			echo json_encode ($response);
		}
	} catch (Exception $e) {
		$response ["success"] = 0;
		$response ["message"] = "Event delete failed.";
		echo 'ERROR: ' . $e->getMessage();
		exit();
	}
}

/*------------------------------------------------------------
 * Returns specific event
* check the user input:
*/

if ($_GET['do']=="readEvent" && isset($_GET['eventId']))
{
	$eventId = $_GET['eventId'];
	$response = array ();

	require_once $_SERVER['DOCUMENT_ROOT'] . '/' . $dbpath;

	try {

		$sql = 'SELECT *
				FROM event
				WHERE eventId = :eventId';

		$sth = $pdo->prepare($sql);
		$sth->bindValue(':eventId', $eventId, PDO::PARAM_INT);
		$confirm = $sth->execute();
		$result = $sth->fetchAll();

		if ($confirm==true) {
			/*
			 * need a container for json
			*/
			$response["event"] = array();

			foreach ($result as $row)
			{
				$event['eventId'] = $row['eventId'];
				$event['personId'] = $row['personId'];
				$event['groupId'] = $row['groupId'];
				$event['name'] = html_entity_decode($row['name'], ENT_QUOTES, 'UTF-8');
				$event['eventDate'] = $row['eventDate'];
				$event['eventTime'] = $row['eventTime'];
				$event['eventLocation'] = html_entity_decode($row['eventLocation'], ENT_QUOTES, 'UTF-8');

				/*
				 * push each value to the data container
				*/
				array_push($response["event"], $event);
			}
			$response ["success"] = 1;
			echo json_encode ($response);
		}
		else {
			$response ["success"] = 0;
			echo json_encode ($response);
		}
	} catch (Exception $e) {
		echo 'ERROR: DB.';
		exit();
	}
}

/*------------------------------------------------------------
 * Returns all group events
* check the user input:
*/

if ($_GET['do']=="readGroupEvents" && isset($_GET['groupId']))
{
	$groupId = $_GET['groupId'];
	$response = array ();

	require_once $_SERVER['DOCUMENT_ROOT'] . '/' . $dbpath;

	try {

		$sql = 'SELECT eventId, personId, groupId, name, eventDate, eventTime, eventLocation
				FROM event
				WHERE groupId = :groupId and eventDate > (select CURDATE())
				ORDER BY eventDate asc, eventTime asc';

		$sth = $pdo->prepare($sql);
		$sth->bindValue(':groupId', $groupId, PDO::PARAM_INT);
		$confirm = $sth->execute();
		$result = $sth->fetchAll();

		if ($confirm==true) {
			/*
			 * need a container for json
			*/
			$response["event"] = array();

			foreach ($result as $row)
			{
				$event['eventId'] = $row['eventId'];
				$event['personId'] = $row['personId'];
				$event['groupId'] = $row['groupId'];
				$event['name'] = html_entity_decode($row['name'], ENT_QUOTES, 'UTF-8');
				$event['eventDate'] = $row['eventDate'];
				$event['eventTime'] = $row['eventTime'];
				$event['eventLocation'] = html_entity_decode($row['eventLocation'], ENT_QUOTES, 'UTF-8');

				/*
				 * push each value to the data container
				*/
				array_push($response["event"], $event);
			}
			$response ["success"] = 1;
			echo json_encode ($response);
		}
		else {
			$response ["success"] = 0;
			echo json_encode ($response);
		}
	} catch (Exception $e) {
		echo 'ERROR: DB.';
		exit();
	}
}

/*------------------------------------------------------------
 * Returns all user events
* check the user input:
*/

if ($_GET['do']=="readUserEvents" && isset($_GET['personId']))
{
	$personId = $_GET['personId'];
	$response = array ();

	require_once $_SERVER['DOCUMENT_ROOT'] . '/' . $dbpath;

	try {

		$sql = 'SELECT ev.eventId, ev.personId, ev.groupId, ev.name, ev.eventDate, ev.eventTime, ev.eventLocation
				FROM event ev join eventPerson ep using (eventId)
				WHERE ep.personId = :personId and ev.eventDate >= (select CURDATE())
				ORDER BY eventDate asc, eventTime asc';

		$sth = $pdo->prepare($sql);
		$sth->bindValue(':personId', $personId, PDO::PARAM_INT);
		$confirm = $sth->execute();
		$result = $sth->fetchAll();

		if ($confirm==true) {
			/*
			 * need a container for json
			*/
			$response["event"] = array();

			if(isset($_GET['shownEventEntries']))
			{
				$shownEventEntries = $_GET ['shownEventEntries'];

				for($i = 0; $i < $shownEventEntries & $result; $i ++) {

					$event['eventId'] = $result['eventId'];
					$event['personId'] = $result['personId'];
					$event['groupId'] = $result['groupId'];
					$event['name'] = html_entity_decode($result['name'], ENT_QUOTES, 'UTF-8');
					$event['eventDate'] = $result['eventDate'];
					$event['eventTime'] = $result['eventTime'];
					$event['eventLocation'] = html_entity_decode($result['eventLocation'], ENT_QUOTES, 'UTF-8');
				}
			} else {
				foreach ($result as $row)
				{
					$event['eventId'] = $row['eventId'];
					$event['personId'] = $row['personId'];
					$event['groupId'] = $row['groupId'];
					$event['name'] = html_entity_decode($row['name'], ENT_QUOTES, 'UTF-8');
					$event['eventDate'] = $row['eventDate'];
					$event['eventTime'] = $row['eventTime'];
					$event['eventLocation'] = html_entity_decode($row['eventLocation'], ENT_QUOTES, 'UTF-8');
				}
				/*
				 * push each value to the data container
				*/
				array_push($response["event"], $event);
			}
			$response ["success"] = 1;
			echo json_encode ($response);
		}
		else {
			$response ["success"] = 0;
			echo json_encode ($response);
		}
	} catch (Exception $e) {
		echo 'ERROR: DB.';
		exit();
	}
}

/*------------------------------------------------------------
 * Returns all old events
* check the user input:
*/

if ($_GET['do']=="readOldEvents" && isset($_GET['personId']))
{
	$personId = $_GET['personId'];
	$response = array ();

	require_once $_SERVER['DOCUMENT_ROOT'] . '/' . $dbpath;

	try {

		$sql = 'SELECT * FROM event WHERE personId = :$personId and eventDate < (select CURDATE())';

		$sth = $pdo->prepare($sql);
		$sth->bindValue(':personId', $personId, PDO::PARAM_INT);
		$confirm = $sth->execute();
		$result = $sth->fetchAll();

		if ($confirm==true) {
			/*
			 * need a container for json
			*/
			$response["previousEvents"] = array();

			$previousEvents["eventId"] = $row["eventId"];
			$previousEvents["name"] = html_entity_decode($row["name"], ENT_QUOTES, 'UTF-8');
			$previousEvents["eventDate"] = $row["eventDate"];

			/*
			 * push each value to the data container
			*/
			array_push($response["previousEvents"], $previousEvents);

			$response ["success"] = 1;
			echo json_encode ($response);
		}
		else {
			$response ["success"] = 0;
			echo json_encode ($response);
		}
	} catch (Exception $e) {
		echo 'ERROR: DB.';
		exit();
	}
}


?>