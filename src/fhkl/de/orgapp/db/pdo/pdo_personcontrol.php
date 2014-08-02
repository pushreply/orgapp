<?php

$dbpath = 'pdo_db_connect.inc.php';

/*------------------------------------------------------------
 * CREATE PERSON
*/

if
(
	$_GET['do']=="create"
	&& isset($_GET['eMail'])
	&& isset($_GET['password'])
	&& isset($_GET['firstName'])
	&& isset($_GET['lastName'])
	&& isset($_GET['created'])
)
{
	/*
	 * pass the get values to some variables
	*/
	$eMail = htmlspecialchars($_GET['eMail']); /*escape every '<tag>' (not only HTML) */
	$password = htmlspecialchars($_GET['password']); /*escape every '<tag>' (not only HTML) */
	$firstName = htmlspecialchars($_GET['firstName']); /*escape every '<tag>' (not only HTML) */
	$lastName = htmlspecialchars($_GET['lastName']); /*escape every '<tag>' (not only HTML) */
	$created = $_GET['created'];
	
	$response = array ();
	
	require_once $_SERVER['DOCUMENT_ROOT'] . '/' . $dbpath;
	
	try
	{
		$sql='INSERT INTO person(eMail, password, firstName, lastName, created)
				VALUES(:eMail, :password, :firstName, :lastName, :created)';
	
		$sth = $pdo->prepare($sql);
	
		/* bind the values, in the same order as the $sql statement. */
		$sth->bindValue(':eMail', $eMail);
		$sth->bindValue(':password', $password);
		$sth->bindValue(':firstName', $firstName);
		$sth->bindValue(':lastName', $lastName);
		$sth->bindValue(':created', $created);
		
		$confirm = $sth->execute();
	
		//check insertion status
		if ($confirm==true)
		{
			// successfully inserted into database
			$response ["success"] = 1;
			// echoing JSON response
			echo json_encode ($response);
		}
		else {
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
 * READ PERSON
*/

if
(
	$_GET['do']=="read"
	&& isset($_GET['eMail'])
)
{
	/*
	 * pass the get values to some variables
	*/
	$eMail = $_GET['eMail'];
	
	$response = array ();
	
	require_once $_SERVER['DOCUMENT_ROOT'] . '/' . $dbpath;
	
	try
	{
		$sql= 'SELECT * FROM person WHERE eMail = :eMail';
	
		$sth = $pdo->prepare($sql);
	
		/* bind the values, in the same order as the $sql statement. */
		$sth->bindValue(':eMail', $eMail);
		
		$sth->execute();
		$result = $sth->fetchAll();
		
		// if $result contains rows
		if(count($result) > 0)
		{
			/*
			 * need a container for json
			*/
			$response["person"] = array();
				
			foreach ($result as $row)
			{
				$person['personId'] = $row['personId'];
				$person['eMail'] = html_entity_decode($row['eMail'], ENT_QUOTES, 'UTF-8');
				$person['password'] = $row['password'];
				$person['firstName'] = html_entity_decode($row['firstName'], ENT_QUOTES, 'UTF-8');
				$person['lastName'] = html_entity_decode($row['lastName'], ENT_QUOTES, 'UTF-8');
				$person['created'] = $row['created'];
				$person['birthday'] = $row['birthday'];
				$person['gender'] = $row['gender'];
					
				/*
				 * push each value to the data container
				*/
				array_push($response["person"], $person);
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
 * UPDATE PERSON
*/

if
(
	$_GET['do']=="update"
	&& isset($_GET['personId'])
	&& isset($_GET['eMail'])
	&& isset($_GET['firstName'])
	&& isset($_GET['lastName'])
	&& isset($_GET['birthday'])
	&& isset($_GET['gender'])
)
{
	/*
	 * pass the get values to some variables
	*/
	$personid= $_GET['personId'];
	$eMail = htmlspecialchars($_GET['eMail']); /*escape every '<tag>' (not only HTML) */
	$firstName = htmlspecialchars($_GET['firstName']); /*escape every '<tag>' (not only HTML) */
	$lastName = htmlspecialchars($_GET['lastName']); /*escape every '<tag>' (not only HTML) */
	$birthday = $_GET['birthday'];
	$gender = $_GET['gender'];
	
	$response = array ();
	
	require_once $_SERVER['DOCUMENT_ROOT'] . '/' . $dbpath;
	
	try
	{
		$sql='UPDATE person SET
				eMail = :eMail,
				firstName = :firstName,
				lastName = :lastName,
				birthday = :birthday,
				gender = :gender
				WHERE personId = :personId';
	
		$sth = $pdo->prepare($sql);
	
		/* bind the values, in the same order as the $sql statement. */
		$sth->bindValue(':eMail', $eMail);
		$sth->bindValue(':firstName', $firstName);
		$sth->bindValue(':lastName', $lastName);
		$sth->bindValue(':birthday', $birthday);
		$sth->bindValue(':gender', $gender);
		$sth->bindValue(':personId', $personid, PDO::PARAM_INT); /* every integer must have "PDO::PARAM_INT"; it's a PHP PDO bug :)*/
	
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