<?php

$dbpath = 'PDO_DB_Connect.Inc.php';

/*------------------------------------------------------------
 * INSERT GROUP
* check the user input
*/

if ($_GET['do']=="createGroup"
		&& isset($_GET['personId'])
		&& isset($_GET['name'])
		&& isset($_GET['info']))
{
	/*
	 * pass the get values to some variables
	*/
	$personId = $_GET['personId'];
	$name = htmlspecialchars($_GET['name']); /*escape every '<tag>' (not only HTML) */
	$info = htmlspecialchars($_GET['info']);

	$response = array ();

	require_once $_SERVER['DOCUMENT_ROOT'] . '/' . $dbpath;

	try {
		$sql='INSERT INTO groups SET
				personId = :personId,
				name = :name,
				info = :info';

		$sth = $pdo->prepare($sql);

		/* bind the values, in the same order as the $sql statement. */
		$sth->bindValue(':personId', $personId, PDO::PARAM_INT);
		$sth->bindValue(':name', $name);
		$sth->bindValue(':info', $info);
		$confirm = $sth->execute();

		//check insertion status
		if ($confirm==true) {
			// successfully inserted into database
			$response ["success"] = 1;
			$response ["message"] = "Group is successfully created.";
			// echoing JSON response
			echo json_encode ($response);
		}
		else {
			$response ["success"] = 0;
			$response ["message"] = "Group insertion failed.";
			echo json_encode ($response);
		}
	}
	catch (PDOException $e) {
		$response ["success"] = 0;
		$response ["message"] = "Group insertion failed.";
		echo 'ERROR: ' . $e->getMessage();
		exit();
	}
}

/*------------------------------------------------------------
 * UPDATE GROUP
* check the user input
*/

if ($_GET['do']=="updateGroup"
		&& isset($_GET['groupId'])
		&& isset($_GET['name'])
		&& isset($_GET['info']))
{
	$groupId = $_GET['groupId'];
	$name = htmlspecialchars($_GET['name']);
	$info = htmlspecialchars($_GET['info']);

	$response = array ();

	require_once $_SERVER['DOCUMENT_ROOT'] . '/' . $dbpath;

	try {
		$sql='UPDATE groups SET
				name = :name
				info = :info
				WHERE groupId = :groupId';

		$sth = $pdo->prepare($sql);

		/* bind the values, in the same order as the $sql statement. */
		$sth->bindValue(':groupId', $groupId, PDO::PARAM_INT);
		$sth->bindValue(':name', $name);
		$sth->bindValue(':info', $info);
		$confirm = $sth->execute();

		if ($confirm==true) {
			$response ["success"] = 1;
			$response ["message"] = "Group is successfully updated.";
			echo json_encode ($response);
		}
		else {
			$response ["success"] = 0;
			$response ["message"] = "Group update failed.";
			echo json_encode ($response);
		}
	}
	catch (PDOException $e) {
		$response ["success"] = 0;
		$response ["message"] = "Group update failed.";
		echo 'ERROR: ' . $e->getMessage();
		exit();
	}
}

/*------------------------------------------------------------
 * DELETE 1 GROUP
* check the user input:
*/

if ($_GET['do']=="deleteGroup"
		&& isset($_GET['groupId']))
{

	$groupId = $_GET['groupId'];

	$response = array ();

	require_once $_SERVER['DOCUMENT_ROOT'] . '/' . $dbpath;

	try {

		$sql = 'DELETE FROM groups
				WHERE groupId = :groupId';

		$sth = $pdo->prepare($sql);
		$sth->bindValue(':groupId', $groupId, PDO::PARAM_INT); /* integer must have "PDO::PARAM_INT"; it's a PHP PDO bug :)*/
		$confirm = $sth->execute();

		if ($confirm==true) {
			$response ["success"] = 1;
			$response ["message"] = "Group is successfully deleted.";
			echo json_encode ($response);
		}
		else {

			$response ["message"] = "Group is not deleted.";
			echo json_encode ($response);
		}
	} catch (Exception $e) {
		$response ["success"] = 0;
		$response ["message"] = "Group delete failed.";
		echo 'ERROR: ' . $e->getMessage();
		exit();
	}
}

/*------------------------------------------------------------
 * Returns a specific group
* check the user input:
*/

if ($_GET['do']=="readGroup" && isset($_GET['groupId']))
{
	$groupId = $_GET['groupId'];
	$response = array ();

	require_once $_SERVER['DOCUMENT_ROOT'] . '/' . $dbpath;

	try {

		$sql = 'SELECT *
				FROM groups
				WHERE groupId = :groupId';

		$sth = $pdo->prepare($sql);
		$sth->bindValue(':groupId', $groupId, PDO::PARAM_INT);
		$confirm = $sth->execute();
		$result = $sth->fetchAll();

		if ($confirm==true) {
			/*
			 * need a container for json
			*/
			$response["groups"] = array();

			foreach ($result as $row)
			{
				$groups['groupId'] = $row['groupId'];
				$groups['personId'] = $row['personId'];
				$groups['name'] = html_entity_decode($row['name'], ENT_QUOTES, 'UTF-8');
				$groups['info'] = html_entity_decode($row['info'], ENT_QUOTES, 'UTF-8');

				/*
				 * push each value to the data container
				*/
				array_push($response["groups"], $groups);
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
 * Returns one user's groups
* check the user input:
*/

if ($_GET['do']=="readUserGroup" && isset($_GET['personId']))
{
	$personId = $_GET['personId'];
	$response = array ();

	require_once $_SERVER['DOCUMENT_ROOT'] . '/' . $dbpath;

	try {

		$sql = 'SELECT g.groupId, g.personId, g.name, g.info
				FROM groups g join privilege p using (groupId)
				WHERE p.personId = :personId';

		$sth = $pdo->prepare($sql);
		$sth->bindValue(':personId', $personId, PDO::PARAM_INT);
		$confirm = $sth->execute();
		$result = $sth->fetchAll();

		if ($confirm==true) {
			/*
			 * need a container for json
			*/
			$response["groups"] = array();

			foreach ($result as $row)
			{
				$groups['groupId'] = $row['groupId'];
				$groups['personId'] = $row['personId'];
				$groups['name'] = html_entity_decode($row['name'], ENT_QUOTES, 'UTF-8');
				$groups['info'] = html_entity_decode($row['info'], ENT_QUOTES, 'UTF-8');

				/*
				 * push each value to the data container
				*/
				array_push($response["groups"], $groups);
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
 * Returns a specific user in group
* check the user input:
*/

if ($_GET['do']=="readUserInGroup"
		&& isset($_GET['groupId'])
		&& isset($_GET['personId']))
{
	$groupId = $_GET['groupId'];
	$personId = $_GET['personId'];
	$response = array ();

	require_once $_SERVER['DOCUMENT_ROOT'] . '/' . $dbpath;

	try {

		$sql = 'SELECT * FROM privilege p, person pers WHERE p.groupId = :groupId
				AND p.personId = pers.personId and pers.personId = :personId';

		$sth = $pdo->prepare($sql);
		$sth->bindValue(':groupId', $groupId, PDO::PARAM_INT);
		$sth->bindValue(':personId', $personId, PDO::PARAM_INT);
		$confirm = $sth->execute();
		$result = $sth->fetchAll();

		if ($confirm==true) {
			/*
			 * need a container for json
			*/
			$response["member"] = array();

			foreach ($result as $row)
			{

				$member['privilegeManagement'] = $row ['privilegeManagement'];
				$member['memberInvitation'] = $row ['memberInvitation'];
				$member['memberlistEditing'] = $row ['memberlistEditing'];
				$member['eventCreating'] = $row ['eventCreating'];
				$member['eventEditing'] = $row ['eventEditing'];
				$member['eventDeleting'] = $row ['eventDeleting'];
				$member['commentEditing'] = $row ['commentEditing'];
				$member['commentDeleting'] = $row ['commentDeleting'];
				$member['memberSince'] = $row ['memberSince'];

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
 * Returns all user's in a group
* check the user input:
*/

if ($_GET['do']=="readAllUserInGroup"
		&& isset($_GET['groupId']))
{
	$groupId = $_GET['groupId'];
	$response = array ();

	require_once $_SERVER['DOCUMENT_ROOT'] . '/' . $dbpath;

	try {

		$sql = 'SELECT pers.personId, pers.eMail FROM privilege priv join person pers using(personId)
				WHERE priv.groupId = :groupId';

		$sth = $pdo->prepare($sql);
		$sth->bindValue(':groupId', $groupId, PDO::PARAM_INT);
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
 * Returns all user's in a group except a specific user
* check the user input:
*/

if ($_GET['do']=="readMemberList"
		&& isset($_GET['groupId'])
		&& isset($_GET['personId']))
{
	$groupId = $_GET['groupId'];
	$personId = $_GET['personId'];
	$response = array ();

	require_once $_SERVER['DOCUMENT_ROOT'] . '/' . $dbpath;

	try {

		$sql = 'SELECT pers.personId, pers.eMail, pers.firstName, pers.lastName FROM person pers JOIN privilege priv USING(personId)
				WHERE priv.groupId = :groupId AND priv.personId NOT LIKE :personId';

		$sth = $pdo->prepare($sql);
		$sth->bindValue(':groupId', $groupId, PDO::PARAM_INT);
		$sth->bindValue(':personId', $personId, PDO::PARAM_INT);
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
 * Returns all persons, who are in the user´s groups and not in the selected one
* check the user input:
*/

if ($_GET['do']=="readInviteMemberList"
		&& isset($_GET['groupId'])
		&& isset($_GET['personId']))
{
	$groupId = $_GET['groupId'];
	$personId = $_GET['personId'];
	$response = array ();

	require_once $_SERVER['DOCUMENT_ROOT'] . '/' . $dbpath;

	try {

		$sql = 'SELECT DISTINCT pers.personId, pers.eMail, pers.firstName, pers.lastName
				FROM person pers
				JOIN privilege priv
				USING(personId)
				WHERE pers.personId != :personId
				AND priv.groupId
				IN
				(
				SELECT groupId
				FROM privilege
				WHERE personId = :personId
				)
				AND pers.personId
				NOT IN
				(
				SELECT pers.personId
				FROM person pers
				JOIN privilege priv
				USING(personId)
				WHERE priv.groupId = :groupId AND pers.personId != :personId
				)';

		$sth = $pdo->prepare($sql);
		$sth->bindValue(':groupId', $groupId, PDO::PARAM_INT);
		$sth->bindValue(':personId', $personId, PDO::PARAM_INT);
		$confirm = $sth->execute();
		$result = $sth->fetchAll();

		if ($confirm==true) {
			/*
			 * need a container for json
			*/
			$response["memberList"] = array();

			foreach ($result as $row)
			{

				$memberList['personId'] = $row['personId'];
				$memberList['eMail'] = html_entity_decode($row['eMail'], ENT_QUOTES, 'UTF-8');
				$memberList['firstName'] = html_entity_decode($row['firstName'], ENT_QUOTES, 'UTF-8');
				$memberList['lastName'] = html_entity_decode($row['lastName'], ENT_QUOTES, 'UTF-8');

				/*
				 * push each value to the data container
				*/
				array_push($response["memberList"], $memberList);
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

?>