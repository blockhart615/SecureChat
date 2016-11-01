<?php
require_once 'Database/DBFunctions.php';

//instantiate DBFunctions object to perform queries and operations on Database.
$db = new DBFunctions();

if (isset($_POST['message-to-send']) && isset($_POST['recipient']) && isset($_POST['sender'])) {

	//get POST parameters and assign to variables
	$message = $_POST['message-to-send'];
	$recipient = $_POST['recipient'];
	$sender = $_POST['sender'];

	//if the user exists, and the message isn't null, then send a message
	if (db->userExists($recipient) && $message != NULL) {
		db->sendMessage($message, $recipient, $sender);
	}
	//if user is not found in database, output error message
	if (!db->userExists($recipient)) {
		echo 'That user does not exist in our database. Check the recpient\'s username.\n';
	}
	//if sender tries to send a blank message
	if ($message == NULL) {
		echo 'No message to send. Enter a message to send and try again.\n';
	}
}
?>
