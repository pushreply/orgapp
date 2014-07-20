<?php 

$dbpath = 'pdo_db_connect.inc.php';

/*------------------------------------------------------------
 * INSERT COMMENT
 * check the user input
 * if button add comment '+' clicked, set addcomment = 1
 */

if ($_GET['addcomment']==1 
		&& isset($_GET['eventid']) 
		&& isset($_GET['personid'])
		&& isset($_GET['message'])) 
{
	/*
	 * pass the get values to some variables
	 */
	$eventid = $_GET['eventid'];
	$personid= $_GET['personid'];
	$message = htmlspecialchars($_GET['message']); /*escape every '<tag>' (not only HTML) */
	$classification = $_GET['classification'];
	
	$response = array ();
	
	require_once $_SERVER['DOCUMENT_ROOT'] . '/' . $dbpath;
	
	try {
		$sql='INSERT INTO comment SET 
				eventid = :eventid, 
				personid = :personid, 
				message = :message, 
				classification = :classification';
		
		$sth = $pdo->prepare($sql);
		
		/* bind the values, in the same order as the $sql statement. */
		$sth->bindValue(':eventid', $eventid, PDO::PARAM_INT); /* every integer must have "PDO::PARAM_INT"; it's a PHP PDO bug :)*/
		$sth->bindValue(':personid', $personid, PDO::PARAM_INT); 
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

if ($_GET['updatecomment']==1 
		&& isset($_GET['commentid'])
		&& isset($_GET['eventid']) 
		&& isset($_GET['personid'])
		&& isset($_GET['message']))
{
	$commentid = $_GET['commentid'];
	$eventid = $_GET['eventid'];
	$personid= $_GET['personid'];
	$message = htmlspecialchars($_GET['message']); /*escape every '<tag>' (not only HTML) */
	$classification = $_GET['classification'];

	$response = array ();

	require_once $_SERVER['DOCUMENT_ROOT'] . '/' . $dbpath;

	try {
		$sql='UPDATE comment SET
				message = :message,
				classification = :classification
				WHERE commentid = :commentid
				AND eventid = :eventid
				AND personid = :personid';

		$sth = $pdo->prepare($sql);

		/* bind the values, in the same order as the $sql statement. */
		$sth->bindValue(':commentid', $commentid, PDO::PARAM_INT);
		$sth->bindValue(':eventid', $eventid, PDO::PARAM_INT); /* every integer must have "PDO::PARAM_INT"; it's a PHP PDO bug :)*/
		$sth->bindValue(':personid', $personid, PDO::PARAM_INT);
		$sth->bindValue(':message', $message);
		$sth->bindValue(':classification', $classification, PDO::PARAM_INT);
		$confirm = $sth->execute();

		if ($confirm==true) {
			$response ["success"] = 1;
			$response ["message"] = "Message is successfully updated.";
			echo json_encode ($response);
		}
		else {
			$response ["success"] = 0;
			$response ["message"] = "Message update failed.";
			echo json_encode ($response);
		}
	}
	catch (PDOException $e) {
		$response ["success"] = 0;
		$response ["message"] = "Message update failed.";
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

if ($_GET['deletecomment']==1 && isset($_GET['commentid']) && isset($_GET['eventid']) && isset($_GET['personid'])) 
{
	
	$commentid = $_GET['commentid'];
	$eventid = $_GET['eventid'];
	$personid= $_GET['personid'];
	$classification = $_GET['classification'];
	$groupid = $_GET['groupid'];
	
	$response = array ();
	
	require_once $_SERVER['DOCUMENT_ROOT'] . '/' . $dbpath;
	
	try {
		
		$sql = 'DELETE FROM comment WHERE commentid = :commentid AND eventid = :eventid AND personid = :personid';
		$sth = $pdo->prepare($sql);
		$sth->bindValue(':commentid', $commentid, PDO::PARAM_INT); /* integer must have "PDO::PARAM_INT"; it's a PHP PDO bug :)*/
		$sth->bindValue(':eventid', $eventid, PDO::PARAM_INT);
		$sth->bindValue(':personid', $groupid, PDO::PARAM_INT);
		$confirm = $sth->execute();
		
		//check deletion status
		if ($confirm==true) {
			$response ["success"] = 1;
			$response ["message"] = "Message is successfully deleted.";
			echo json_encode ($response);
		}
		else {
			$response ["success"] = 0;
			$response ["message"] = "Message is not deleted.";
			echo json_encode ($response);
		}
	} catch (Exception $e) {
		echo 'ERROR: delete message failed.';
		exit();
	}
}
	
/*------------------------------------------------------------
 * SHOW COMMENTS
* check the user input:
* show comments if an event activity opened, set showcomment = 1
* 
*/

if ($_GET['showcomment']==1 && isset($_GET['eventid']))
{
	$eventid = $_GET['eventid'];
	$response = array ();

	require_once $_SERVER['DOCUMENT_ROOT'] . '/' . $dbpath;

	try {

		$sql = 'SELECT c.commentid, c.commentDateTime, c.personid, p.firstName, p.lastName, c.message 
				FROM comment c, person p 
				WHERE c.personid = p.personid
				AND c.eventid = :eventid';
		
		$sth = $pdo->prepare($sql);
		$sth->bindValue(':eventid', $eventid, PDO::PARAM_INT);
		$confirm = $sth->execute();
		$result = $sth->fetchAll();
		
		if ($confirm==true) {
			/*
			 * need a container for json
			 */
			$response["comment"] = array();
			
			/*
			 * must be placed outside foreach loop, otherwise create a weird array index.
			 */
			$comment[] = array();
			
			foreach ($result as $row)
			{
				$comment['commentid'] = $row['commentid'];
				$comment['commentdatetime'] = $row['commentDateTime'];
				$comment['personid'] = $row['personid'];
				$comment['firstname'] = html_entity_decode($row['firstName'], ENT_QUOTES, 'UTF-8');
				$comment['lastname'] = html_entity_decode($row['lastName'], ENT_QUOTES, 'UTF-8');
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