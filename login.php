<?php
require_once('vendor/autoload.php');
require_once 'Database/DBFunctions.php';
use \Firebase\JWT\JWT;
define('ALGORITHM', 'HS256');
define('SECRET_KEY','Your-Secret-Key');


$db = new DBFunctions();

// json response array
// $response = array("error" => FALSE);

if (isset($_POST['username']) && isset($_POST['password'])) {

    // receiving the post params
    $username = $_POST['username'];
    $password = $_POST['password'];

    // get the user by username and password
    $user = $db->getUserByUsernameAndPassword($username, $password);

    if ($user != NULL) {
        // user is found

        //build JSON Web Token
        $tokenID = base64_encode(mcrypt_create_iv(32));
        $issuedAt = time();
        $notBefore = $issuedAt + 10; //add 10 seconds
        $expireTime = $notBefore + 180; //3 minutes after not before
        $serverName = 'https://www.toastabout.com';

        //create token as an array
        $data = [
            'iat' => $issuedAt,
            'jti' => $tokenID,
            'iss' => $serverName,
            'nbf' => $notBefore,
            'exp' => $expireTime,
            'data' => [
                'username' => $user['username'],
                'email' => $user['email'],
            ]
        ];

        // $secretKey = base64_decode(SECRET_KEY);

        //encode into a JWT
        $jwt = JWT::encode(
            $data, //data being encoded into the token
            SECRET_KEY, //key to sign the token
            ALGORITHM
        );

        echo $jwt;

        // $response["error"] = FALSE;
        // $response["user"]["username"] = $user["username"];
        // $response["user"]["email"] = $user["email"];
        // $response["user"]["created_at"] = $user["created_at"];
        // echo json_encode($response);
    } else {
        // user is not found with the credentials
      //   $response["error"] = TRUE;
      //   $response["error_msg"] = "Username or password are incorrect. Please try again!";
		  echo "Username or password are incorrect. Please try again!";
      //   echo json_encode($response);
    }
} else {
    // required post params is missing
   //  $response["error"] = TRUE;
   //  $response["error_msg"] = "Required parameters username or password is missing!";
   //  echo json_encode($response);
	echo "Required parameters username or password is missing!";
}
?>
