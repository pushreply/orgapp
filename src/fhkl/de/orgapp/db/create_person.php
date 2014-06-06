<?php

/*
 * Following code will create a new person row All person details are read from HTTP GET Request
 */

// array for JSON response
$response = array ();

// check for required fields
if (isset ( $_GET ['eMail'] ) && isset ( $_GET ['password'] ) &&
	isset ( $_GET ['firstName'] ) && isset ( $_GET ['lastName'])) {
	
	
	$eMail = $_GET ['eMail'];
	$password = $_GET ['password'];
	$firstName = $_GET ['firstName'];
	$lastName = $_GET ['lastName'];
	
	
	
	// include db connect class
	require_once __DIR__ .'/db_connect.php';
	
	// connecting to db
	$db = new DB_CONNECT ();
	
	// mysql inserting a new row
	$result = mysql_query (
			"INSERT INTO person(eMail, password, firstName, lastName) 
			VALUES('$eMail', '$password', '$firstName', '$lastName')" );
	
	// check if row inserted or not
	if ($result) {
		// successfully inserted into database
		$response ["success"] = 1;
		$response ["message"] = "Person successfully created.";
		
		// echoing JSON response
		echo json_encode ( $response );
	} else {
		// failed to insert row
		$response ["success"] = 0;
		$response ["message"] = "Oops! An error occurred.";
		
		// echoing JSON response
		echo json_encode ( $response );
	}
} else {
	// required field is missing
	$response ["success"] = 0;
	$response ["message"] = "Required field(s) is missing";
	
	// echoing JSON response
	echo json_encode ( $response );
}
?>