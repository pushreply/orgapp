<?php

$dbpath = 'PDO_DB_Connect.Inc.php';

/*------------------------------------------------------------
 * CREATE NOTIFICATION
*/

if
(
	$_GET['do']=="create"
	&& isset($_GET['eMail'])
	&& isset($_GET['classification'])
	&& isset($_GET['message'])
)
{
	/*
	 * pass the get values to some variables
	*/
	$eMail = htmlspecialchars($_GET['eMail']); /*escape every '<tag>' (not only HTML) */
	$classification = $_GET['classification'];
	$message = htmlspecialchars($_GET['message']); /*escape every '<tag>' (not only HTML) */

	$response = array ();

	require_once $_SERVER['DOCUMENT_ROOT'] . '/' . $dbpath;

	try
	{
		$sql='INSERT INTO notifications (personId, classification, message)
				VALUES ((SELECT personId FROM person WHERE eMail = :eMail), :classification, :message)';

		$sth = $pdo->prepare($sql);

		/* bind the values, in the same order as the $sql statement. */
		$sth->bindValue(':eMail', $eMail);
		$sth->bindValue(':classification', $classification);
		$sth->bindValue(':message', $message);

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
 * READ NOTIFICATION
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

	$groupInvites = isset($_GET["groupInvites"]) ? $_GET["groupInvites"] : null;
	$groupEdited = isset($_GET["groupEdited"]) ? $_GET["groupEdited"] : null;
	$groupRemoved = isset($_GET["groupRemoved"]) ? $_GET["groupRemoved"] : null;
	$eventsAdded = isset($_GET["eventsAdded"]) ? $_GET["eventsAdded"] : null;
	$eventsEdited = isset($_GET["eventsEdited"]) ? $_GET["eventsEdited"] : null;
	$eventsRemoved = isset($_GET["eventsRemoved"]) ? $_GET["eventsRemoved"] : null;
	$commentsAdded = isset($_GET["commentsAdded"]) ? $_GET["commentsAdded"] : null;
	$commentsEdited = isset($_GET["commentsEdited"]) ? $_GET["commentsEdited"] : null;
	$commentsRemoved = isset($_GET["commentsRemoved"]) ? $_GET["commentsRemoved"] : null;
	$privilegeGiven = isset($_GET["privilegeGiven"]) ? $_GET["privilegeGiven"] : null;

	$response = array ();

	require_once $_SERVER['DOCUMENT_ROOT'] . '/' . $dbpath;

	try
	{
		$sql= 'SELECT * FROM notifications WHERE personId = :personId AND classification IN
			(:groupInvites, :groupEdited, :groupRemoved, :eventsAdded, :eventsEdited, :eventsRemoved,
			 :commentsAdded, :commentsEdited, :commentsRemoved, :privilegeGiven) ORDER BY notificationsId DESC';

		$sth = $pdo->prepare($sql);

		/* bind the values, in the same order as the $sql statement. */
		$sth->bindValue(':personId', $personId, PDO::PARAM_INT); /* every integer must have "PDO::PARAM_INT"; it's a PHP PDO bug :)*/

		isset($_GET['groupInvites']) ? $sth->bindValue(':groupInvites', $groupInvites, PDO::PARAM_INT) : $sth->bindValue(':groupInvites', $groupInvites, PDO::PARAM_NULL);
		isset($_GET['groupEdited']) ? $sth->bindValue(':groupEdited', $groupEdited, PDO::PARAM_INT) : $sth->bindValue(':groupEdited', $groupEdited, PDO::PARAM_NULL);
		isset($_GET['groupRemoved']) ? $sth->bindValue(':groupRemoved', $groupRemoved, PDO::PARAM_INT) : $sth->bindValue(':groupRemoved', $groupRemoved, PDO::PARAM_NULL);
		isset($_GET['eventsAdded']) ? $sth->bindValue(':eventsAdded', $eventsAdded, PDO::PARAM_INT) : $sth->bindValue(':eventsAdded', $eventsAdded, PDO::PARAM_NULL);
		isset($_GET['eventsEdited']) ? $sth->bindValue(':eventsEdited', $eventsEdited, PDO::PARAM_INT) : $sth->bindValue(':eventsEdited', $eventsEdited, PDO::PARAM_NULL);
		isset($_GET['eventsRemoved']) ? $sth->bindValue(':eventsRemoved', $eventsRemoved, PDO::PARAM_INT) : $sth->bindValue(':eventsRemoved', $eventsRemoved, PDO::PARAM_NULL);
		isset($_GET['commentsAdded']) ? $sth->bindValue(':commentsAdded', $commentsAdded, PDO::PARAM_INT) : $sth->bindValue(':commentsAdded', $commentsAdded, PDO::PARAM_NULL);
		isset($_GET['commentsEdited']) ? $sth->bindValue(':commentsEdited', $commentsEdited, PDO::PARAM_INT) : $sth->bindValue(':commentsEdited', $commentsEdited, PDO::PARAM_NULL);
		isset($_GET['commentsRemoved']) ? $sth->bindValue(':commentsRemoved', $commentsRemoved, PDO::PARAM_INT) : $sth->bindValue(':commentsRemoved', $commentsRemoved, PDO::PARAM_NULL);
		isset($_GET['privilegeGiven']) ? $sth->bindValue(':privilegeGiven', $privilegeGiven, PDO::PARAM_INT) : $sth->bindValue(':privilegeGiven', $privilegeGiven, PDO::PARAM_NULL);

		$sth->execute();
		$result = $sth->fetchAll();

		// if $result contains rows
		if(count($result) > 0)
		{
			/*
			 * need a container for json
			*/
			$response["notification"] = array();

			if(isset($_GET['shownEntries']))
			{
				$shownEntries = $_GET['shownEntries'];
				$i=0;

				foreach ($result as $row)
				{
					if($i == $shownEntries)
						break;

					$notification['notificationsId'] = $row['notificationsId'];
					$notification['personId'] = $row['personId'];
					$notification['classification'] = $row['classification'];
					$notification['message'] = html_entity_decode($row['message'], ENT_QUOTES, 'UTF-8');
					$notification['isRead'] = $row['isRead'];

					/*
					 * push each value to the data container
					*/
					array_push($response["notification"], $notification);

					$i++;
				}
			}
			else
			{
				foreach ($result as $row)
				{
					$notification['notificationsId'] = $row['notificationsId'];
					$notification['personId'] = $row['personId'];
					$notification['classification'] = $row['classification'];
					$notification['message'] = html_entity_decode($row['message'], ENT_QUOTES, 'UTF-8');
					$notification['isRead'] = $row['isRead'];

					/*
					 * push each value to the data container
					*/
					array_push($response["notification"], $notification);
				}
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
 * CHECK FOR UNREAD NOTIFICATION
*/

if
(
		$_GET['do']=="checkunread"
		&& isset($_GET['personId'])
)
{
	/*
	 * pass the get values to some variables
	*/
	$personId = $_GET['personId'];

	$groupInvites = isset($_GET["groupInvites"]) ? $_GET["groupInvites"] : null;
	$groupEdited = isset($_GET["groupEdited"]) ? $_GET["groupEdited"] : null;
	$groupRemoved = isset($_GET["groupRemoved"]) ? $_GET["groupRemoved"] : null;
	$eventsAdded = isset($_GET["eventsAdded"]) ? $_GET["eventsAdded"] : null;
	$eventsEdited = isset($_GET["eventsEdited"]) ? $_GET["eventsEdited"] : null;
	$eventsRemoved = isset($_GET["eventsRemoved"]) ? $_GET["eventsRemoved"] : null;
	$commentsAdded = isset($_GET["commentsAdded"]) ? $_GET["commentsAdded"] : null;
	$commentsEdited = isset($_GET["commentsEdited"]) ? $_GET["commentsEdited"] : null;
	$commentsRemoved = isset($_GET["commentsRemoved"]) ? $_GET["commentsRemoved"] : null;
	$privilegeGiven = isset($_GET["privilegeGiven"]) ? $_GET["privilegeGiven"] : null;

	$response = array ();

	require_once $_SERVER['DOCUMENT_ROOT'] . '/' . $dbpath;

	try
	{
		$sql= 'SELECT * FROM notifications WHERE personId = :personId AND isRead = 0 AND classification IN
			(:groupInvites, :groupEdited, :groupRemoved, :eventsAdded, :eventsEdited, :eventsRemoved,
			 :commentsAdded, :commentsEdited, :commentsRemoved, :privilegeGiven) ORDER BY notificationsId DESC';

		$sth = $pdo->prepare($sql);

		/* bind the values, in the same order as the $sql statement. */
		$sth->bindValue(':personId', $personId, PDO::PARAM_INT); /* every integer must have "PDO::PARAM_INT"; it's a PHP PDO bug :)*/

		isset($_GET['groupInvites']) ? $sth->bindValue(':groupInvites', $groupInvites, PDO::PARAM_INT) : $sth->bindValue(':groupInvites', $groupInvites, PDO::PARAM_NULL);
		isset($_GET['groupEdited']) ? $sth->bindValue(':groupEdited', $groupEdited, PDO::PARAM_INT) : $sth->bindValue(':groupEdited', $groupEdited, PDO::PARAM_NULL);
		isset($_GET['groupRemoved']) ? $sth->bindValue(':groupRemoved', $groupRemoved, PDO::PARAM_INT) : $sth->bindValue(':groupRemoved', $groupRemoved, PDO::PARAM_NULL);
		isset($_GET['eventsAdded']) ? $sth->bindValue(':eventsAdded', $eventsAdded, PDO::PARAM_INT) : $sth->bindValue(':eventsAdded', $eventsAdded, PDO::PARAM_NULL);
		isset($_GET['eventsEdited']) ? $sth->bindValue(':eventsEdited', $eventsEdited, PDO::PARAM_INT) : $sth->bindValue(':eventsEdited', $eventsEdited, PDO::PARAM_NULL);
		isset($_GET['eventsRemoved']) ? $sth->bindValue(':eventsRemoved', $eventsRemoved, PDO::PARAM_INT) : $sth->bindValue(':eventsRemoved', $eventsRemoved, PDO::PARAM_NULL);
		isset($_GET['commentsAdded']) ? $sth->bindValue(':commentsAdded', $commentsAdded, PDO::PARAM_INT) : $sth->bindValue(':commentsAdded', $commentsAdded, PDO::PARAM_NULL);
		isset($_GET['commentsEdited']) ? $sth->bindValue(':commentsEdited', $commentsEdited, PDO::PARAM_INT) : $sth->bindValue(':commentsEdited', $commentsEdited, PDO::PARAM_NULL);
		isset($_GET['commentsRemoved']) ? $sth->bindValue(':commentsRemoved', $commentsRemoved, PDO::PARAM_INT) : $sth->bindValue(':commentsRemoved', $commentsRemoved, PDO::PARAM_NULL);
		isset($_GET['privilegeGiven']) ? $sth->bindValue(':privilegeGiven', $privilegeGiven, PDO::PARAM_INT) : $sth->bindValue(':privilegeGiven', $privilegeGiven, PDO::PARAM_NULL);

		$sth->execute();
		$result = $sth->fetchAll();

		// if $result contains rows
		if(count($result) > 0)
			$response["hasNewNotifications"] = 1;
		else
			$response["hasNewNotifications"] = 0;

		$response["success"] = 1;
		echo json_encode($response);
	}
	catch (PDOException $e)
	{
		$response["success"] = 0;
		echo 'ERROR: ' . $e->getMessage();
		exit();
	}
}

/*------------------------------------------------------------
 * READ NUMBER NEW NOTIFICATION
*/

if
(
		$_GET['do']=="checknumberunread"
		&& isset($_GET['personId'])
)
{
	/*
	 * pass the get values to some variables
	*/
	$personId = $_GET['personId'];

	$groupInvites = isset($_GET["groupInvites"]) ? $_GET["groupInvites"] : null;
	$groupEdited = isset($_GET["groupEdited"]) ? $_GET["groupEdited"] : null;
	$groupRemoved = isset($_GET["groupRemoved"]) ? $_GET["groupRemoved"] : null;
	$eventsAdded = isset($_GET["eventsAdded"]) ? $_GET["eventsAdded"] : null;
	$eventsEdited = isset($_GET["eventsEdited"]) ? $_GET["eventsEdited"] : null;
	$eventsRemoved = isset($_GET["eventsRemoved"]) ? $_GET["eventsRemoved"] : null;
	$commentsAdded = isset($_GET["commentsAdded"]) ? $_GET["commentsAdded"] : null;
	$commentsEdited = isset($_GET["commentsEdited"]) ? $_GET["commentsEdited"] : null;
	$commentsRemoved = isset($_GET["commentsRemoved"]) ? $_GET["commentsRemoved"] : null;
	$privilegeGiven = isset($_GET["privilegeGiven"]) ? $_GET["privilegeGiven"] : null;

	$response = array ();

	require_once $_SERVER['DOCUMENT_ROOT'] . '/' . $dbpath;

	try
	{
		$sql= 'SELECT * FROM notifications WHERE personId = :personId AND isRead = 0 AND classification IN
			(:groupInvites, :groupEdited, :groupRemoved, :eventsAdded, :eventsEdited, :eventsRemoved,
			 :commentsAdded, :commentsEdited, :commentsRemoved, :privilegeGiven) ORDER BY notificationsId DESC';

		$sth = $pdo->prepare($sql);

		/* bind the values, in the same order as the $sql statement. */
		$sth->bindValue(':personId', $personId, PDO::PARAM_INT); /* every integer must have "PDO::PARAM_INT"; it's a PHP PDO bug :)*/

		isset($_GET['groupInvites']) ? $sth->bindValue(':groupInvites', $groupInvites, PDO::PARAM_INT) : $sth->bindValue(':groupInvites', $groupInvites, PDO::PARAM_NULL);
		isset($_GET['groupEdited']) ? $sth->bindValue(':groupEdited', $groupEdited, PDO::PARAM_INT) : $sth->bindValue(':groupEdited', $groupEdited, PDO::PARAM_NULL);
		isset($_GET['groupRemoved']) ? $sth->bindValue(':groupRemoved', $groupRemoved, PDO::PARAM_INT) : $sth->bindValue(':groupRemoved', $groupRemoved, PDO::PARAM_NULL);
		isset($_GET['eventsAdded']) ? $sth->bindValue(':eventsAdded', $eventsAdded, PDO::PARAM_INT) : $sth->bindValue(':eventsAdded', $eventsAdded, PDO::PARAM_NULL);
		isset($_GET['eventsEdited']) ? $sth->bindValue(':eventsEdited', $eventsEdited, PDO::PARAM_INT) : $sth->bindValue(':eventsEdited', $eventsEdited, PDO::PARAM_NULL);
		isset($_GET['eventsRemoved']) ? $sth->bindValue(':eventsRemoved', $eventsRemoved, PDO::PARAM_INT) : $sth->bindValue(':eventsRemoved', $eventsRemoved, PDO::PARAM_NULL);
		isset($_GET['commentsAdded']) ? $sth->bindValue(':commentsAdded', $commentsAdded, PDO::PARAM_INT) : $sth->bindValue(':commentsAdded', $commentsAdded, PDO::PARAM_NULL);
		isset($_GET['commentsEdited']) ? $sth->bindValue(':commentsEdited', $commentsEdited, PDO::PARAM_INT) : $sth->bindValue(':commentsEdited', $commentsEdited, PDO::PARAM_NULL);
		isset($_GET['commentsRemoved']) ? $sth->bindValue(':commentsRemoved', $commentsRemoved, PDO::PARAM_INT) : $sth->bindValue(':commentsRemoved', $commentsRemoved, PDO::PARAM_NULL);
		isset($_GET['privilegeGiven']) ? $sth->bindValue(':privilegeGiven', $privilegeGiven, PDO::PARAM_INT) : $sth->bindValue(':privilegeGiven', $privilegeGiven, PDO::PARAM_NULL);

		$sth->execute();
		$result = $sth->fetchAll();

		$response["numberNewNotifications"] = count($result);
		$response["success"] = 1;
		echo json_encode($response);
	}
	catch (PDOException $e)
	{
		$response["success"] = 0;
		echo 'ERROR: ' . $e->getMessage();
		exit();
	}
}

/*------------------------------------------------------------
 * UPDATE NOTIFICATION
*/

if
(
	$_GET['do']=="update"
	&& isset($_GET['notificationsId'])
)
{
	/*
	 * pass the get values to some variables
	*/
	$notificationsId = $_GET['notificationsId'];

	$response = array ();

	require_once $_SERVER['DOCUMENT_ROOT'] . '/' . $dbpath;

	try
	{
		$sql='UPDATE notifications SET
				isRead = 1
				WHERE notificationsId = :notificationsId';

		$sth = $pdo->prepare($sql);

		/* bind the values, in the same order as the $sql statement. */
		$sth->bindValue(':notificationsId', $notificationsId, PDO::PARAM_INT); /* every integer must have "PDO::PARAM_INT"; it's a PHP PDO bug :)*/

		$confirm = $sth->execute();

		//check update status
		if ($confirm==true) {
			// successfully updated into database
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

?>