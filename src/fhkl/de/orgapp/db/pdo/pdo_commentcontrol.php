<?php 

$dbpath = 'pdo_db_connect.inc.php';

/*------------------------------------------------------------
 * INSERT COMMENT
 * check the user input
 * if button add comment '+' clicked, set addcomment = 1
 */

if ($_GET['addcomment']==1 && isset($_GET['message'])) 
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
		$sql='INSERT INTO comment 
				SET 
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
 * DELETE COMMENT
 * check the user input:
 * if button delete comment clicked, set deletecomment = 1
 * 
 * Note: To delete a comment, which privileges does the user have
 * and how we control them?
 */

if ($_GET['deletecomment']==1 && isset($_GET['commentid'])) 
{
	$commentid = $_GET['commentid'];
	$eventid = $_GET['eventid'];
	$personid= $_GET['personid'];
	$classification = $_GET['classification'];
	
	$response = array ();
	
	require_once $_SERVER['DOCUMENT_ROOT'] . '/' . $dbpath;
	
	try {
		$sql = 'DELETE FROM comment WHERE commentid = :commentid';
		$sth = $pdo->prepare($sql);
		$sth->bindValue(':commentid', $commentid, PDO::PARAM_INT); /* integer must have "PDO::PARAM_INT"; it's a PHP PDO bug :)*/
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
	
	
?>