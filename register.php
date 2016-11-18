<?php
require_once 'Database/DBFunctions.php';

//instantiate DBFunctions object to perform queries and operations on Database.
$db = new DBFunctions();
$response = array("error" => false);

if (isset($_POST['username']) && isset($_POST['email']) && isset($_POST['password'])) {

    // Assign POST parameters to variables.
    $username = $_POST['username'];
    $email = $_POST['email'];
    $password = $_POST['password'];

    // check if the user already exists
    if ($db->userExists($username)) {
      // If user already exists, display error message.
        $response["error"] = true;
        $response["error_msg"] = "Username " . $username . " already exists. Please pick another username.";
        echo json_encode($resposne);
    }

	else {
        // create a new user
        $user = $db->storeUser($username, $email, $password);

		//display user information to confirm successful creation
        if ($user) {
            $response["error"] = false;
            $response["username"] = $user['username'];
            $response["email"] = $user['email'];
            $response["created_at"] = $user['created_at'];
            echo json_encode($response);
        }//IF USER CREATION SUCCESSFUL

 	  		//something unknown went wrong
		  else {
				$response["error"] = true;
                $response['error_msg'] = "Unknown error occurred during registration.";
                echo json_encode($response);
        }//ELSE
    }//ELSE 
}//IF ISSET

//parameters were missing in the POST array
else {
	$response["error"] = true;
    $response["error_msg"] = "Required parameters (username, email or password) missing.";
    echo json_encode($resposne);
}
?>
