<?php

$dbpath = 'pdo_db_connect.inc.php';

/*------------------------------------------------------------
 * UPDATE EVENT SETTINGS
 * check the user input
 */

if
	(
		$_GET['do']=="updateeventsettings"
		&& isset($_GET['personId'])
	)
{
	/*
	 * pass the get values to some variables
	 */
	$personid= $_GET['personId'];
	$shownEntries = isset($_GET['shownEntries']) ? $_GET['shownEntries'] : null;

	$response = array ();

	require_once $_SERVER['DOCUMENT_ROOT'] . '/' . $dbpath;

	try
	{
		$sql='UPDATE eventSettings SET
				shownEntries = :shownEntries
				WHERE personId = :personId';

		$sth = $pdo->prepare($sql);

		/* bind the values, in the same order as the $sql statement. */
		$sth->bindValue(':personId', $personid, PDO::PARAM_INT); /* every integer must have "PDO::PARAM_INT"; it's a PHP PDO bug :)*/
		
		if(isset($_GET['shownEntries']))
			$sth->bindValue(':shownEntries', $shownEntries, PDO::PARAM_INT);
		else
			$sth->bindValue(':shownEntries', $shownEntries, PDO::PARAM_NULL);
		
		$confirm = $sth->execute();
		
		//check insertion status
		if ($confirm==true) {
			// successfully inserted into database
			$response ["success"] = 1;
			$response ["message"] = "Update event settings was successful.";
			// echoing JSON response
			echo json_encode ($response);
		}
		else
		{
			$response ["success"] = 0;
			$response ["message"] = "Update event settings was not successful.";
			echo json_encode ($response);
		}
	}
	catch (PDOException $e)
	{
		$response ["success"] = 0;
		$response ["message"] = "Update event settings failed.";
	    echo 'ERROR: ' . $e->getMessage();
	    exit();
	}
}

?>