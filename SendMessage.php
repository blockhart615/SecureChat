<?php
require_once 'vendor/autoload.php';
require_once 'Database/DBFunctions.php';
use \Firebase\JWT\JWT;

//define constants for ALGORITHM and Secret Key
define('ALGORITHM', 'HS256');
define('SECRET_KEY','Your-Secret-Key');


//instantiate DBFunctions object to perform queries and operations on Database.
$db = new DBFunctions();

if (isset($_POST['message-to-send']) && isset($_POST['recipient'])) {

	//get HTTP headers and put them into an assoc array
	$headers = apache_request_headers();


	//get POST parameters and assign to variables
	$message = $_POST['message-to-send'];
	$receiver = $_POST['recipient'];
	$token = $headers['Authorization'];

	//Decode token and put in assoc array
	$decoded = JWT::decode($token, SECRET_KEY, array(ALGORITHM));
	$token_array = (array) $decoded;
	$sender = $token_array['data']->username;


	//if the user exists, and the message isn't null, then send a message
	if ($db->userExists($receiver) && $db->userExists($sender) && $message != NULL) {
		//sends the message to the database
		$db->sendMessage($sender, $receiver, $message);
		echo "Message sent successfully!\n";
	}
	//if user is not found in database, output error message
	if (!($db->userExists($receiver))){
		echo "That user does not exist in our database. Check the recpient's username.";
	}
	//if sender tries to send a blank message
	if ($message == NULL) {
		echo 'No message to send. Enter a message to send and try again.';
	}
}
?>
