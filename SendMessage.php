<?php
require_once 'vendor/autoload.php';
require_once 'Database/DBFunctions.php';
use \Firebase\JWT\JWT;

//define constants for ALGORITHM and Secret Key
define('ALGORITHM', 'HS256');
define('SECRET_KEY','Your-Secret-Key');


//instantiate DBFunctions object to perform queries and operations on Database.
$db = new DBFunctions();
$response["error"] = false;

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
		$response["error_msg"] = "Message sent successfully!";
		echo json_encode($response);
	}
	//if user is not found in database, output error message
	if (!($db->userExists($receiver))){
		$response["error"] = true;
		$response["error_msg"] = "That user does not exist in our database. Check the recpient's username.";
		echo json_encode($response);
	}
	//if sender tries to send a blank message
	if ($message == NULL) {
		$response["error"] = true;
		$response["error_msg"] = 'No message to send. Enter a message to send and try again.';
		echo json_encode($response);
	}
}
?>
