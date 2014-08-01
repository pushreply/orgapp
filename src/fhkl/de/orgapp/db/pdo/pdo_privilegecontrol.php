<?php

$dbpath = 'pdo_db_connect.inc.php';

/*------------------------------------------------------------
 * INSERT PRIVILEGE for admins
* check the user input
*/

if ($_GET['do']=="createPrivilegeAdmin"
		&& isset($_GET['groupId'])
		&& isset($_GET['personId'])
		&& isset($_GET['memberSince']))
{
	/*
	 * pass the get values to some variables
	*/
	$groupId = $_GET['groupId'];
	$personId = $_GET['personId'];
	$memberSince = $_GET['memberSince'];

	$response = array ();

	require_once $_SERVER['DOCUMENT_ROOT'] . '/' . $dbpath;

	try {
		$sql='INSERT INTO privilege SET
				groupId = :groupId,
				personId = :personId,
				memberSince = :membersince,
				privilegeManagement = true,
				memberInvitation = true,
				memberlistEditing = true,
				eventCreating = true,
				eventEditing = true,
				eventDeleting = true,
				commentEditing = true,
				commentDeleting = true';

		$sth = $pdo->prepare($sql);

		/* bind the values, in the same order as the $sql statement. */
		$sth->bindValue(':groupId', $groupId, PDO::PARAM_INT);
		$sth->bindValue(':personId', $personid, PDO::PARAM_INT);
		$sth->bindValue(':memberSince', $memberSince);

		$confirm = $sth->execute();

		//check insertion status
		if ($confirm==true) {
			// successfully inserted into database
			$response ["success"] = 1;
			$response ["message"] = "Privilege is successfully created.";
			// echoing JSON response
			echo json_encode ($response);
		}
		else {
			$response ["success"] = 0;
			$response ["message"] = "Privilege insertion failed.";
			echo json_encode ($response);
		}
	}
	catch (PDOException $e) {
		$response ["success"] = 0;
		$response ["message"] = "Privilege insertion failed.";
		echo 'ERROR: ' . $e->getMessage();
		exit();
	}
}

/*------------------------------------------------------------
 * INSERT PRIVILEGE for member
* check the user input
*/

if ($_GET['do']=="createPrivilegeMember"
		&& isset($_GET['groupId'])
		&& isset($_GET['personId'])
		&& isset($_GET['memberSince']))
{
	/*
	 * pass the get values to some variables
	*/
	$groupId = $_GET['groupId'];
	$personId = $_GET['personId'];
	$memberSince = $_GET['memberSince'];

	$response = array ();

	require_once $_SERVER['DOCUMENT_ROOT'] . '/' . $dbpath;

	try {
		$sql='INSERT INTO privilege SET
				groupId = :groupId,
				personId = :personId,
				memberSince = :membersince,
				privilegeManagement = false,
				memberInvitation = false,
				memberlistEditing = false,
				eventCreating = false,
				eventEditing = false,
				eventDeleting = false,
				commentEditing = false,
				commentDeleting = false';

		$sth = $pdo->prepare($sql);

		/* bind the values, in the same order as the $sql statement. */
		$sth->bindValue(':groupId', $groupId, PDO::PARAM_INT);
		$sth->bindValue(':personId', $personId, PDO::PARAM_INT);
		$sth->bindValue(':memberSince', $memberSince);

		$confirm = $sth->execute();

		//check insertion status
		if ($confirm==true) {
			// successfully inserted into database
			$response ["success"] = 1;
			$response ["message"] = "Privilege is successfully created.";
			// echoing JSON response
			echo json_encode ($response);
		}
		else {
			$response ["success"] = 0;
			$response ["message"] = "Privilege insertion failed.";
			echo json_encode ($response);
		}
	}
	catch (PDOException $e) {
		$response ["success"] = 0;
		$response ["message"] = "Privilege insertion failed.";
		echo 'ERROR: ' . $e->getMessage();
		exit();
	}
}

/*------------------------------------------------------------
 * UPDATE PRIVILEGE
* check the user input
*/

if ($_GET['do']=="updatePrivilege"
		&& isset($_GET['personId'])
		&& isset($_GET['groupId'])
		&& isset($_GET['memberInvitation'])
		&& isset($_GET['memberlistEditing'])
		&& isset($_GET['eventCreating'])
		&& isset($_GET['eventEditing'])
		&& isset($_GET['eventDeleting'])
		&& isset($_GET['commentEditing'])
		&& isset($_GET['commentDeleting'])
		&& isset($_GET['privilegeManagement']))
{
	$personId = $_GET ['personId'];
	$groupId = $_GET ['groupId'];
	$memberInvitation = $_GET ['memberInvitation'];
	$memberlistEditing = $_GET ['memberlistEditing'];
	$eventCreating = $_GET ['eventCreating'];
	$eventEditing = $_GET ['eventEditing'];
	$eventDeleting = $_GET ['eventDeleting'];
	$commentEditing = $_GET ['commentEditing'];
	$commentDeleting = $_GET ['commentDeleting'];
	$privilegeManagement = $_GET ['privilegeManagement'];

	$response = array ();

	require_once $_SERVER['DOCUMENT_ROOT'] . '/' . $dbpath;

	try {
		$sql='UPDATE privilege SET
				memberInvitation = :memberInvitation,
				memberlistEditing = :memberlistEditing,
				eventCreating = :eventCreating,
				eventEditing = :eventEditing,
				eventDeleting = :eventDeleting,
				commentEditing = :commentEditing,
				commentDeleting = :commentDeleting,
				privilegeManagement = :privilegeManagement
				WHERE personId = :personId and groupId = :groupId';

		$sth = $pdo->prepare($sql);

		/* bind the values, in the same order as the $sql statement. */
		$sth->bindValue(':personId', $personId, PDO::PARAM_INT);
		$sth->bindValue(':groupId', $groupId, PDO::PARAM_INT);
		$sth->bindValue(':memberInvitation', $memberInvitation);
		$sth->bindValue(':memberlistEditing', $memberlistEditing);
		$sth->bindValue(':eventCreating', $eventCreating);
		$sth->bindValue(':eventEditing', $eventEditing);
		$sth->bindValue(':eventDeleting', $eventDeleting);
		$sth->bindValue(':commentEditing', $commentEditing);
		$sth->bindValue(':commentDeleting', $commentDeleting);
		$sth->bindValue(':privilegeManagement', $privilegeManagement);
		$confirm = $sth->execute();

		if ($confirm==true) {
			$response ["success"] = 1;
			$response ["message"] = "Privilege is successfully updated.";
			echo json_encode ($response);
		}
		else {
			$response ["success"] = 0;
			$response ["message"] = "Privilege update failed.";
			echo json_encode ($response);
		}
	}
	catch (PDOException $e) {
		$response ["success"] = 0;
		$response ["message"] = "Privilege update failed.";
		echo 'ERROR: ' . $e->getMessage();
		exit();
	}
}

/*------------------------------------------------------------
 * DELETE 1 PRIVILEGE
* check the user input:
*/

if ($_GET['do']=="deletePrivilege"
		&& isset($_GET['personId'])
		&& isset($_GET['groupId']))
{
	$personId = $_GET['personId'];
	$groupId = $_GET['groupId'];

	$response = array ();

	require_once $_SERVER['DOCUMENT_ROOT'] . '/' . $dbpath;

	try {

		$sql = 'DELETE FROM privilege
				WHERE personId = :personId and groupId = :groupId';

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

?>