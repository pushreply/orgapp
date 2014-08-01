<?php

$dbpath = 'pdo_db_connect.inc.php';

/*------------------------------------------------------------
 * CREATE EVENT SETTINGS
*/

if
(
	$_GET['do']=="create"
	&& isset($_GET['personId'])
)
{
	/*
	 * pass the get values to some variables
	*/
	$personid= $_GET['personId'];
	
	$response = array ();
	
	require_once $_SERVER['DOCUMENT_ROOT'] . '/' . $dbpath;
	
	try
	{
		$sql= 'INSERT INTO eventSettings (personId) VALUES (:personId)';
	
		$sth = $pdo->prepare($sql);
	
		/* bind the values, in the same order as the $sql statement. */
		$sth->bindValue(':personId', $personid, PDO::PARAM_INT); /* every integer must have "PDO::PARAM_INT"; it's a PHP PDO bug :)*/
	
		$confirm = $sth->execute();
	
		//check insertion status
		if ($confirm==true) {
			// successfully insert into database
			$response ["success"] = 1;
			$response ["message"] = "Create event settings was successful.";
			// echoing JSON response
			echo json_encode ($response);
		}
		else
		{
			$response ["success"] = 0;
			$response ["message"] = "Create event settings was not successful.";
			echo json_encode ($response);
		}
	}
	catch (PDOException $e)
	{
		$response ["success"] = 0;
		$response ["message"] = "Create event settings failed.";
		echo 'ERROR: ' . $e->getMessage();
		exit();
	}
}

/*------------------------------------------------------------
 * READ EVENT SETTINGS
*/

if
(
	$_GET['do']=="read"
	&& isset($_GET['personId'])
)
{
	/*
	 * pass the get values to some variables
	*/
	$personid= $_GET['personId'];
	
	$response = array ();
	
	require_once $_SERVER['DOCUMENT_ROOT'] . '/' . $dbpath;
	
	try
	{
		$sql= 'SELECT * FROM eventSettings WHERE personId = :personId';
	
		$sth = $pdo->prepare($sql);
	
		/* bind the values, in the same order as the $sql statement. */
		$sth->bindValue(':personId', $personid, PDO::PARAM_INT); /* every integer must have "PDO::PARAM_INT"; it's a PHP PDO bug :)*/
	
		$confirm = $sth->execute();
		$result = $sth->fetchAll();
		
		//check select status
		if ($confirm==true)
		{
			/*
			 * need a container for json
			*/
			$response["eventSettings"] = array();
			
			foreach ($result as $row)
			{
				$eventSetting['eventSettingsId'] = $row['eventSettingsId'];
				$eventSetting['shownEntries'] = $row['shownEntries'];
			
				/*
				 * push each value to the data container
				*/
				array_push($response["eventSettings"], $eventSetting);
			}
			
			// successfully selected from database
			$response ["success"] = 1;
			// echoing JSON response
			echo json_encode ($response);
		}
		else
		{
			$response ["success"] = 0;
			echo json_encode ($response);
		}
	}
	catch (PDOException $e)
	{
		$response ["success"] = 0;
		echo 'ERROR: ' . $e->getMessage();
		exit();
	}
}
	
/*------------------------------------------------------------
 * UPDATE EVENT SETTINGS
 */

if
(
	$_GET['do']=="update"
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
		
		//check update status
		if ($confirm==true) {
			// successfully updated into database
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