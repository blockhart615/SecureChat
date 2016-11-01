<?php
require_once 'Database/DBFunctions.php';

//instantiate DBFunctions object to perform queries and operations on Database.
$db = new DBFunctions();

if (isset($_POST['username']) && isset($_POST['email']) && isset($_POST['password'])) {

    // Assign POST parameters to variables.
    $username = $_POST['username'];
    $email = $_POST['email'];
    $password = $_POST['password'];

    // check if the user already exists
    if ($db->userExists($username)) {
      // If user already exists, display error message.
		echo "Username " . $username . " already exists. Please pick another username.";
    }
	 else {
        // create a new user
        $user = $db->storeUser($username, $email, $password);

		  //display user information to confirm successful creation
        if ($user) {
				echo "User Account Creation Successful! <br/>";
				echo "Username: " . $user["username"] . "<br/>";
            echo "Email: " . $user["email"] . "<br/>";
            echo "Date Created: " . $user["created_at"] . "<br/>";
        }
 	  		//something unknown went wrong
		  else {
				echo "Unknown error occurred during registration.";
        }
    }
}
//parameters were missing in the POST array
else {
	echo "Required parameters (username, email or password) missing.";
}
?>
