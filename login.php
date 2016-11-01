<?php
require_once 'Database/DBFunctions.php';
$db = new DBFunctions();

// json response array
$response = array("error" => FALSE);

if (isset($_POST['username']) && isset($_POST['password'])) {

    // receiving the post params
    $username = $_POST['username'];
    $password = $_POST['password'];

    // get the user by username and password
    $user = $db->getUserByUsernameAndPassword($username, $password);

    if ($user != NULL) {
        // use is found
        $response["error"] = FALSE;
        $response["user"]["username"] = $user["username"];
        $response["user"]["email"] = $user["email"];
        $response["user"]["created_at"] = $user["created_at"];
        echo json_encode($response);
    } else {
        // user is not found with the credentials
        $response["error"] = TRUE;
        $response["error_msg"] = "Login credentials are wrong. Please try again!";
        echo json_encode($response);
    }
} else {
    // required post params is missing
    $response["error"] = TRUE;
    $response["error_msg"] = "Required parameters username or password is missing!";
    echo json_encode($response);
}
?>
