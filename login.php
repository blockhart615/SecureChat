<?php
require_once 'vendor/autoload.php';
require_once 'Database/DBFunctions.php';
use \Firebase\JWT\JWT;

//define constants for ALGORITHM and Secret Key
define('ALGORITHM', 'HS256');
define('SECRET_KEY','Your-Secret-Key');

//instantiate db to perform Database functions
$db = new DBFunctions();

//if username and password are provided
if (isset($_POST['username']) && isset($_POST['password'])) {

    // assign the post params to variables
    $username = $_POST['username'];
    $password = $_POST['password'];

    // get the user by username and password
    $user = $db->getUserByUsernameAndPassword($username, $password);

	 // If the user is found
    if ($user != NULL) {
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

    } else { //Username or Password were incorrect
		  echo "Username or password are incorrect. Please try again!";
    }
} else { //A parameter is missing
	echo "Required parameters username or password is missing!";
}
?>
