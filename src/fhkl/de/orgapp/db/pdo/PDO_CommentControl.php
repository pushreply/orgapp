<?php

$dbpath = 'pdo_db_connect.inc.php';

/*------------------------------------------------------------
 * INSERT COMMENT
 * check the user input
 * if button add comment '+' clicked, set addcomment = 1
 */

if ($_GET['do']=="addcomment"
		&& isset($_GET['eventId'])
		&& isset($_GET['personId'])
		&& isset($_GET['message'])
		&& htmlspecialchars($_GET['message']) != null)
{
	/*
	 * pass the get values to some variables
	 */
	$eventid = $_GET['eventId'];
	$personid= $_GET['personId'];
	$message = htmlspecialchars($_GET['message']); /*escape every '<tag>' (not only HTML) */
	$classification = $_GET['classification'];

	$response = array ();

	require_once $_SERVER['DOCUMENT_ROOT'] . '/' . $dbpath;

	try {
		$sql='INSERT INTO comment SET
				eventId = :eventId,
				personId = :personId,
				message = :message,
				classification = :classification';

		$sth = $pdo->prepare($sql);

		/* bind the values, in the same order as the $sql statement. */
		$sth->bindValue(':eventId', $eventid, PDO::PARAM_INT); /* every integer must have "PDO::PARAM_INT"; it's a PHP PDO bug :)*/
		$sth->bindValue(':personId', $personid, PDO::PARAM_INT);
		$sth->bindValue(':message', $message);
		$sth->bindValue(':classification', $classification, PDO::PARAM_INT);
		$confirm = $sth->execute();

		//check insertion status
		if ($confirm==true) {
			// successfully inserted into database
			$response ["success"] = 1;
			$response ["message"] = "Message is successfully created.";
			// echoing JSON response
			echo json_encode ($response);
		}
		else {
			$response ["success"] = 0;
			$response ["message"] = "Message insertion failed.";
			echo json_encode ($response);
		}
	}
	catch (PDOException $e) {
		$response ["success"] = 0;
		$response ["message"] = "Message insertion failed.";
	    echo 'ERROR: ' . $e->getMessage();
	    exit();
	}
}

/*------------------------------------------------------------
 * UPDATE COMMENT
* check the user input
* if button edit comment clicked, set updatecomment = 1
*/

if ($_GET['do']=="updatecomment"
		&& isset($_GET['commentId'])
		&& isset($_GET['message']))
{
	$commentid = $_GET['commentId'];
	$message = htmlspecialchars($_GET['message']); /*escape every '<tag>' (not only HTML) */

	$response = array ();

	require_once $_SERVER['DOCUMENT_ROOT'] . '/' . $dbpath;

	try {
		$sql='UPDATE comment SET
				message = :message
				WHERE commentId = :commentId';

		$sth = $pdo->prepare($sql);

		/* bind the values, in the same order as the $sql statement. */
		$sth->bindValue(':commentId', $commentid, PDO::PARAM_INT);
		$sth->bindValue(':message', $message);
		$confirm = $sth->execute();

		if ($confirm==true) {
			$response ["success"] = 1;
			$response ["message"] = "Comment is successfully updated.";
			echo json_encode ($response);
		}
		else {
			$response ["success"] = 0;
			$response ["message"] = "Comment update failed.";
			echo json_encode ($response);
		}
	}
	catch (PDOException $e) {
		$response ["success"] = 0;
		$response ["message"] = "Comment update failed.";
		echo 'ERROR: ' . $e->getMessage();
		exit();
	}
}


/*------------------------------------------------------------
 * DELETE 1 COMMENT
 * check the user input:
 * if button delete comment clicked, set deletecomment = 1
 *
 * Note: To delete a comment, which privileges does the user have
 * and how we control them?
 */

if ($_GET['do']=="deletecomment"
		&& isset($_GET['commentId']))
{

	$commentid = $_GET['commentId'];

	$response = array ();

	require_once $_SERVER['DOCUMENT_ROOT'] . '/' . $dbpath;

	try {

		$sql = 'DELETE FROM comment
				WHERE commentId = :commentId';

		$sth = $pdo->prepare($sql);
		$sth->bindValue(':commentId', $commentid, PDO::PARAM_INT); /* integer must have "PDO::PARAM_INT"; it's a PHP PDO bug :)*/
		$confirm = $sth->execute();

		if ($confirm==true) {
			$response ["success"] = 1;
			$response ["message"] = "Comment is successfully deleted.";
			echo json_encode ($response);
		}
		else {

			$response ["message"] = "Comment is not deleted.";
			echo json_encode ($response);
		}
	} catch (Exception $e) {
		$response ["success"] = 0;
		$response ["message"] = "Comment delete failed.";
		echo 'ERROR: ' . $e->getMessage();
		exit();
	}
}

/*------------------------------------------------------------
 * SHOW COMMENTS
* check the user input:
* show comments if an event activity opened, set showcomment = 1
*
*/

if ($_GET['do']=="showcomment" && isset($_GET['eventId']))
{
	$eventid = $_GET['eventId'];
	$response = array ();

	require_once $_SERVER['DOCUMENT_ROOT'] . '/' . $dbpath;

	try {

		$sql = 'SELECT c.commentId, c.commentDateTime, c.personId, p.firstName, p.lastName, c.message
				FROM comment c, person p
				WHERE c.personId = p.personId
				AND c.eventId = :eventId';

		$sth = $pdo->prepare($sql);
		$sth->bindValue(':eventId', $eventid, PDO::PARAM_INT);
		$confirm = $sth->execute();
		$result = $sth->fetchAll();

		if ($confirm==true) {
			/*
			 * need a container for json
			 */
			$response["comment"] = array();

			foreach ($result as $row)
			{
				$comment['commentId'] = $row['commentId'];
				$comment['commentDateTime'] = $row['commentDateTime'];
				$comment['personId'] = $row['personId'];
				$comment['firstName'] = html_entity_decode($row['firstName'], ENT_QUOTES, 'UTF-8');
				$comment['lastName'] = html_entity_decode($row['lastName'], ENT_QUOTES, 'UTF-8');
				$comment['message'] = html_entity_decode($row['message'], ENT_QUOTES, 'UTF-8');

				/*
				 * push each value to the data container
				 */
				array_push($response["comment"], $comment);
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