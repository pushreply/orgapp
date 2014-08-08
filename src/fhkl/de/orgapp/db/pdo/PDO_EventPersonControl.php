<?php

$dbpath = 'PDO_DB_Connect.Inc.php';

/*------------------------------------------------------------
 * INSERT Attending member
* check the user input
*/

if ($_GET['do']=="createPersonInEvent"
		&& isset($_GET['personId'])
		&& isset($_GET['eventId']))
{
	/*
	 * pass the get values to some variables
	*/
	$personId = $_GET['personId'];
	$eventId = $_GET['eventId'];

	$response = array ();

	require_once $_SERVER['DOCUMENT_ROOT'] . '/' . $dbpath;

	try {
		$sql='INSERT INTO eventPerson SET
				personId = :personId,
				eventId = :eventId';

		$sth = $pdo->prepare($sql);

		/* bind the values, in the same order as the $sql statement. */
		$sth->bindValue(':personId', $personId, PDO::PARAM_INT);
		$sth->bindValue(':eventId', $eventId, PDO::PARAM_INT);
		$confirm = $sth->execute();

		//check insertion status
		if ($confirm==true) {
			// successfully inserted into database
			$response ["success"] = 1;
			$response ["message"] = "Attending member is successfully created.";
			// echoing JSON response
			echo json_encode ($response);
		}
		else {
			$response ["success"] = 0;
			$response ["message"] = "Attending member insertion failed.";
			echo json_encode ($response);
		}
	}
	catch (PDOException $e) {
		$response ["success"] = 0;
		$response ["message"] = "Attending member insertion failed.";
		echo 'ERROR: ' . $e->getMessage();
		exit();
	}
}

/*------------------------------------------------------------
 * DELETE 1 Attending member
* check the user input:
*/

if ($_GET['do']=="deletePersonInEvent"
		&& isset($_GET['personId'])
		&& isset($_GET['eventId']))
{

	$personId = $_GET['personId'];
	$eventId = $_GET['eventId'];

	$response = array ();

	require_once $_SERVER['DOCUMENT_ROOT'] . '/' . $dbpath;

	try {

		$sql = 'DELETE FROM eventPerson
				WHERE personId = :personId AND eventId = :eventId';

		$sth = $pdo->prepare($sql);
		$sth->bindValue(':personId', $personId, PDO::PARAM_INT); /* integer must have "PDO::PARAM_INT"; it's a PHP PDO bug :)*/
		$sth->bindValue(':eventId', $eventId, PDO::PARAM_INT); /* integer must have "PDO::PARAM_INT"; it's a PHP PDO bug :)*/
		$confirm = $sth->execute();

		if ($confirm==true) {
			$response ["success"] = 1;
			$response ["message"] = "Attending member is successfully deleted.";
			echo json_encode ($response);
		}
		else {

			$response ["message"] = "Attending member is not deleted.";
			echo json_encode ($response);
		}
	} catch (Exception $e) {
		$response ["success"] = 0;
		$response ["message"] = "Attending member delete failed.";
		echo 'ERROR: ' . $e->getMessage();
		exit();
	}
}

/*------------------------------------------------------------
 * DELETE all Attending member from specific event
* check the user input:
*/

if ($_GET['do']=="deleteAllPersonsInEvent"
		&& isset($_GET['eventId']))
{

	$eventId = $_GET['eventId'];

	$response = array ();

	require_once $_SERVER['DOCUMENT_ROOT'] . '/' . $dbpath;

	try {

		$sql = 'DELETE FROM eventPerson
				WHERE eventId = :eventId';

		$sth = $pdo->prepare($sql);
		$sth->bindValue(':eventId', $eventId, PDO::PARAM_INT); /* integer must have "PDO::PARAM_INT"; it's a PHP PDO bug :)*/
		$confirm = $sth->execute();

		if ($confirm==true) {
			$response ["success"] = 1;
			$response ["message"] = "Attending members are successfully deleted.";
			echo json_encode ($response);
		}
		else {

			$response ["message"] = "Attending members are not deleted.";
			echo json_encode ($response);
		}
	} catch (Exception $e) {
		$response ["success"] = 0;
		$response ["message"] = "Attending members delete failed.";
		echo 'ERROR: ' . $e->getMessage();
		exit();
	}
}

/*------------------------------------------------------------
 * Returns a specific attending member
* check the user input:
*/

if ($_GET['do']=="readAttendingMember"
		&& isset($_GET['personId'])
		&& isset($_GET['eventId']))
{
	$personId = $_GET['personId'];
	$eventId = $_GET['eventId'];
	$response = array ();

	require_once $_SERVER['DOCUMENT_ROOT'] . '/' . $dbpath;

	try {

		$sql = 'SELECT pers.personId, pers.eMail, pers.firstName, pers.lastName FROM person pers JOIN eventPerson ep USING (personId)
				WHERE ep.eventId = :eventId AND ep.personId not like :personId';

		$sth = $pdo->prepare($sql);
		$sth->bindValue(':personId', $personId, PDO::PARAM_INT); /* integer must have "PDO::PARAM_INT"; it's a PHP PDO bug :)*/
		$sth->bindValue(':eventId', $eventId, PDO::PARAM_INT); /* integer must have "PDO::PARAM_INT"; it's a PHP PDO bug :)*/
		$confirm = $sth->execute();
		$result = $sth->fetchAll();

		if ($confirm==true) {
			/*
			 * need a container for json
			*/
			$response["member"] = array();

			foreach ($result as $row)
			{
				$member['personId'] = $row['personId'];
				$member['eMail'] = html_entity_decode($row['eMail'], ENT_QUOTES, 'UTF-8');
				$member['firstName'] = html_entity_decode($row['firstName'], ENT_QUOTES, 'UTF-8');
				$member['lastName'] = html_entity_decode($row['lastName'], ENT_QUOTES, 'UTF-8');

				/*
				 * push each value to the data container
				*/
				array_push($response["member"], $member);
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
 * Returns all attending members
* check the user input:
*/

if ($_GET['do']=="readAllAttendingMember"
		&& isset($_GET['eventId']))
{
	$eventId = $_GET['eventId'];
	$response = array ();

	require_once $_SERVER['DOCUMENT_ROOT'] . '/' . $dbpath;

	try {

		$sql = 'SELECT pers.personId, pers.eMail, pers.firstName, pers.lastName from person pers join eventPerson ep using(personId)
				WHERE ep.eventId = :eventId';

		$sth = $pdo->prepare($sql);
		$sth->bindValue(':eventId', $eventId, PDO::PARAM_INT); /* integer must have "PDO::PARAM_INT"; it's a PHP PDO bug :)*/
		$confirm = $sth->execute();
		$result = $sth->fetchAll();

		if ($confirm==true) {
			/*
			 * need a container for json
			*/
			$response["member"] = array();

			foreach ($result as $row)
			{
				$member['personId'] = $row['personId'];
				$member['eMail'] = html_entity_decode($row['eMail'], ENT_QUOTES, 'UTF-8');
				$member['firstName'] = html_entity_decode($row['firstName'], ENT_QUOTES, 'UTF-8');
				$member['lastName'] = html_entity_decode($row['lastName'], ENT_QUOTES, 'UTF-8');

				/*
				 * push each value to the data container
				*/
				array_push($response["member"], $member);
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
 * Check for user joined in event
* check the user input:
*/

if
(
	$_GET['do']=="checkuserjoinedevent"
	&& isset($_GET['personId'])
	&& isset($_GET['eventId'])
)
{
	/*
	 * pass the get values to some variables
	*/
	$eventId = $_GET['eventId'];
	$personId = $_GET['personId'];

	require_once $_SERVER['DOCUMENT_ROOT'] . '/' . $dbpath;

	try
	{
		$sql= 'SELECT * FROM eventPerson WHERE eventId = :eventId AND personId = :personId';

		$sth = $pdo->prepare($sql);

		/* bind the values, in the same order as the $sql statement. */
		$sth->bindValue(':eventId', $eventId, PDO::PARAM_INT); /* every integer must have "PDO::PARAM_INT"; it's a PHP PDO bug :)*/
		$sth->bindValue(':personId', $personId, PDO::PARAM_INT); /* every integer must have "PDO::PARAM_INT"; it's a PHP PDO bug :)*/

		$sth->execute();
		$result = $sth->fetchAll();

		// if $result contains a rows
		if(count($result) > 0)
			$response["success"] = 1;
		else
			$response["success"] = 0;

		echo json_encode($response);
	}
	catch (PDOException $e)
	{
		$response["success"] = 0;
		echo 'ERROR: ' . $e->getMessage();
		exit();
	}
}

?>