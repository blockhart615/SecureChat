<?php

require_once 'Database/DBFunctions.php';
$db = new DBFunctions();

// json response array
// $response = array("error" => FALSE);

if (isset($_POST['username']) && isset($_POST['email']) && isset($_POST['password'])) {

    // receiving the post params
    $username = $_POST['username'];
    $email = $_POST['email'];
    $password = $_POST['password'];

    // check if user is already existed with the same email
    if ($db->userExists($username)) {
        // user already existed
      //   $response["error"] = TRUE;
      //   $response["error_msg"] = "Username " . $username . " already exists. Please pick another username.";
      //   echo json_encode($response);
		echo "Username " . $username . " already exists. Please pick another username.";
    } else {
        // create a new user
        $user = $db->storeUser($username, $email, $password);
        if ($user) {
            // user stored successfully
            // $response["error"] = FALSE;
				echo "User Account Creation Successful! <br/>";
				echo "Username: " . $user["username"] . "<br/>";
            echo "Email: " . $user["email"] . "<br/>";
            echo "Date Created: " . $user["created_at"] . "<br/>";
            // echo json_encode($response);
        } else {
            // user failed to store
            // $response["error"] = TRUE;
            // $response["error_msg"] = "Unknown error occurred in registration!";
            // echo json_encode($response);
				echo "Unknown error occurred during registration.";
        }
    }
} else {
   //  $response["error"] = TRUE;
   //  $response["error_msg"] = "Required parameters (username, email or password) missing.";
   //  echo json_encode($response);
	echo "Required parameters (username, email or password) missing.";
}
?>
