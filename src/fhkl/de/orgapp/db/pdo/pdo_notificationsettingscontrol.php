<?php

$dbpath = 'pdo_db_connect.inc.php';

/*------------------------------------------------------------
 * CREATE NOTIFICATION SETTINGS
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
	$personId = $_GET['personId'];
	
	$response = array ();
	
	require_once $_SERVER['DOCUMENT_ROOT'] . '/' . $dbpath;
	
	try
	{
		$sql='INSERT INTO notificationSettings (personId, groupInvites, groupEdited,
			groupRemoved, eventsAdded, eventsEdited, eventsRemoved, commentsAdded, commentsEdited, commentsRemoved, privilegeGiven)
			values(:personId, true, true, true, true, true, true, true, true, true, true)';
	
		$sth = $pdo->prepare($sql);
	
		/* bind the values, in the same order as the $sql statement. */
		$sth->bindValue(':personId', $personId, PDO::PARAM_INT); /* every integer must have "PDO::PARAM_INT"; it's a PHP PDO bug :)*/
		
		$confirm = $sth->execute();
	
		//check insertion status
		if ($confirm==true)
		{
			// successfully inserted into database
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
 * READ NOTIFICATION SETTINGS
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
	$personId = $_GET['personId'];
	
	$response = array();
	
	require_once $_SERVER['DOCUMENT_ROOT'] . '/' . $dbpath;
	
	try
	{
		$sql= 'SELECT * FROM notificationSettings WHERE personId = :personId';
		
		$sth = $pdo->prepare($sql);
		
		/* bind the values, in the same order as the $sql statement. */
		$sth->bindValue(':personId', $personId, PDO::PARAM_INT); /* every integer must have "PDO::PARAM_INT"; it's a PHP PDO bug :)*/
	
		$sth->execute();
		$result = $sth->fetchAll();
	
		// if $result contains rows
		if(count($result) > 0)
		{
			/*
			 * need a container for json
			*/
			$response["notificationSettings"] = array();
				
			foreach ($result as $row)
			{
				$notificationSettings["notificationSettingsId"] = $row["notificationSettingsId"];
				$notificationSettings["personId"] = $row["personId"];
				$notificationSettings["shownEntries"] = $row["shownEntries"];
				$notificationSettings["groupInvites"] = $row["groupInvites"];
				$notificationSettings["groupEdited"] = $row["groupEdited"];
				$notificationSettings["groupRemoved"] = $row["groupRemoved"];
				$notificationSettings["eventsAdded"] = $row["eventsAdded"];
				$notificationSettings["eventsEdited"] = $row["eventsEdited"];
				$notificationSettings["eventsRemoved"] = $row["eventsRemoved"];
				$notificationSettings["commentsAdded"] = $row["commentsAdded"];
				$notificationSettings["commentsEdited"] = $row["commentsEdited"];
				$notificationSettings["commentsRemoved"] = $row["commentsRemoved"];
				$notificationSettings["privilegeGiven"] = $row["privilegeGiven"];
				$notificationSettings["vibration"] = $row["vibration"];
				
				/*
				 * push each value to the data container
				*/
				array_push($response["notificationSettings"], $notificationSettings);
			}
			
			// successfully selected from database
			$response["success"] = 1;
			// echoing JSON response
			echo json_encode($response);
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
 * UPDATE NOTIFICATION SETTINGS
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
	$personId = $_GET['personId'];
	$groupInvites = $_GET["groupInvites"];
	$groupEdited = $_GET["groupEdited"];
	$groupRemoved = $_GET["groupRemoved"];
	$eventsAdded = $_GET["eventsAdded"];
	$eventsEdited = $_GET["eventsEdited"];
	$eventsRemoved = $_GET["eventsRemoved"];
	$commentsAdded = $_GET["commentsAdded"];
	$commentsEdited = $_GET["commentsEdited"];
	$commentsRemoved = $_GET["commentsRemoved"];
	$privilegeGiven = $_GET["privilegeGiven"];
	$vibration = $_GET["vibration"];
	
	$response = array();
	
	require_once $_SERVER['DOCUMENT_ROOT'] . '/' . $dbpath;
	
	try
	{
		// Parameter 'shownEntries' set
		if(isset($_GET['shownEntries']))
		{
			$shownEntries = $_GET['shownEntries'];
			
			$sql= 'UPDATE notificationSettings
					SET shownEntries = :shownEntries, groupInvites = :groupInvites, groupEdited = :groupEdited,
					groupRemoved = :groupRemoved, eventsAdded = :eventsAdded, eventsEdited = :eventsEdited,
					eventsRemoved = :eventsRemoved, commentsAdded = :commentsAdded, commentsEdited = :commentsEdited,
					commentsRemoved = :commentsRemoved, privilegeGiven = :privilegeGiven, vibration = :vibration
					WHERE personId = :personId';
			
			$sth = $pdo->prepare($sql);
			
			/* bind the values, in the same order as the $sql statement. */
			/* every integer must have "PDO::PARAM_INT"; it's a PHP PDO bug :)*/
			$sth->bindValue(':shownEntries', $shownEntries, PDO::PARAM_INT);
			$sth->bindValue(':groupInvites', $groupInvites, PDO::PARAM_INT);
			$sth->bindValue(':groupEdited', $groupEdited, PDO::PARAM_INT);
			$sth->bindValue(':groupRemoved', $groupRemoved, PDO::PARAM_INT);
			$sth->bindValue(':eventsAdded', $eventsAdded, PDO::PARAM_INT);
			$sth->bindValue(':eventsEdited', $eventsEdited, PDO::PARAM_INT);
			$sth->bindValue(':eventsRemoved', $eventsRemoved, PDO::PARAM_INT);
			$sth->bindValue(':commentsAdded', $commentsAdded, PDO::PARAM_INT);
			$sth->bindValue(':commentsEdited', $commentsEdited, PDO::PARAM_INT);
			$sth->bindValue(':commentsRemoved', $commentsRemoved, PDO::PARAM_INT);
			$sth->bindValue(':privilegeGiven', $privilegeGiven, PDO::PARAM_INT);
			$sth->bindValue(':vibration', $vibration, PDO::PARAM_INT);
			$sth->bindValue(':personId', $personId, PDO::PARAM_INT);
			
			$confirm = $sth->execute();
			
			//check update status
			if($confirm==true)
			{
				// successfully updated into database
				$response["success"] = 1;
				// echoing JSON response
				echo json_encode ($response);
			}
			else
			{
				$response["success"] = 0;
				echo json_encode ($response);
			}
		}
		// Parameter 'shownEntries' not set
		else
		{
			$sql= 'UPDATE notificationSettings
					SET shownEntries = null, groupInvites = :groupInvites, groupEdited = :groupEdited,
					groupRemoved = :groupRemoved, eventsAdded = :eventsAdded, eventsEdited = :eventsEdited,
					eventsRemoved = :eventsRemoved, commentsAdded = :commentsAdded, commentsEdited = :commentsEdited,
					commentsRemoved = :commentsRemoved, privilegeGiven = :privilegeGiven, vibration = :vibration
					WHERE personId = :personId';
				
			$sth = $pdo->prepare($sql);
				
			/* bind the values, in the same order as the $sql statement. */
			/* every integer must have "PDO::PARAM_INT"; it's a PHP PDO bug :)*/
			$sth->bindValue(':groupInvites', $groupInvites, PDO::PARAM_INT);
			$sth->bindValue(':groupEdited', $groupEdited, PDO::PARAM_INT);
			$sth->bindValue(':groupRemoved', $groupRemoved, PDO::PARAM_INT);
			$sth->bindValue(':eventsAdded', $eventsAdded, PDO::PARAM_INT);
			$sth->bindValue(':eventsEdited', $eventsEdited, PDO::PARAM_INT);
			$sth->bindValue(':eventsRemoved', $eventsRemoved, PDO::PARAM_INT);
			$sth->bindValue(':commentsAdded', $commentsAdded, PDO::PARAM_INT);
			$sth->bindValue(':commentsEdited', $commentsEdited, PDO::PARAM_INT);
			$sth->bindValue(':commentsRemoved', $commentsRemoved, PDO::PARAM_INT);
			$sth->bindValue(':privilegeGiven', $privilegeGiven, PDO::PARAM_INT);
			$sth->bindValue(':vibration', $vibration, PDO::PARAM_INT);
			$sth->bindValue(':personId', $personId, PDO::PARAM_INT);
				
			$confirm = $sth->execute();
				
			//check update status
			if($confirm==true)
			{
				// successfully updated into database
				$response["success"] = 1;
				// echoing JSON response
				echo json_encode ($response);
			}
			else
			{
				$response["success"] = 0;
				echo json_encode ($response);
			}
		}
	}
	catch (PDOException $e)
	{
		$response ["success"] = 0;
		echo 'ERROR: ' . $e->getMessage();
		exit();
	}
}

?>