/*------------------------------------------------------------
 * Returns all group events
* check the user input:
*/

if ($_GET['do']=="readGroupEvents" && isset($_GET['groupId']))
{
	$groupId = $_GET['groupId'];
	$response = array ();

	require_once $_SERVER['DOCUMENT_ROOT'] . '/' . $dbpath;

	try {

		$sql = 'SELECT eventId, personId, groupId, name, eventDate, eventTime, eventLocation
				FROM event
				WHERE groupId = :groupId and eventDate > (select CURDATE())
				ORDER BY eventDate asc, eventTime asc';

		$sth = $pdo->prepare($sql);
		$sth->bindValue(':groupId', $groupId, PDO::PARAM_INT);
		$confirm = $sth->execute();
		$result = $sth->fetchAll();

		if ($confirm==true) {
			/*
			 * need a container for json
			*/
			$response["event"] = array();

			foreach ($result as $row)
			{
				$event['eventId'] = $row['eventId'];
				$event['personId'] = $row['personId'];
				$event['groupId'] = $row['groupId'];
				$event['name'] = html_entity_decode($row['name'], ENT_QUOTES, 'UTF-8');
				$event['eventDate'] = $row['eventDate'];
				$event['eventTime'] = $row['eventTime'];
				$event['eventLocation'] = $row['eventLocation'];

				/*
				 * push each value to the data container
				*/
				array_push($response["event"], $event);
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