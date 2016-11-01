<?php
require_once('vendor/autoload.php');
use \Firebase\JWT\JWT;
define('SECRET_KEY','_');  /// secret key can be a random string and keep in secret from anyone
define('ALGORITHM','HS512');   // Algorithm used to sign the token, see
                              https://tools.ietf.org/html/draft-ietf-jose-json-web-algorithms-40#section-3
//// Suppose you have submitted your form data here with userusername and password
$action = $_REQUEST['action'];
if ($userusername && $password && $action == 'login' ) {


                // if there is no error below code run
	$statement = $config->prepare("select * from login where username = :username" );
   $statement->execute(array(':username' => $_POST['userusername']));
	$row = $statement->fetchAll(PDO::FETCH_ASSOC);
   $hashAndSalt = password_hash($password, PASSWORD_BCRYPT);
	if(count($row)>0 && password_verify($row[0]['password'],$hashAndSalt)) {

    	$tokenId    = base64_encode(mcrypt_create_iv(32));
      $issuedAt   = time();
      $notBefore  = $issuedAt + 10;  //Adding 10 seconds
      $expire     = $notBefore + 60; //Token expires in 60 seconds
      $serverusername = 'https://www.toastabout.com/'; //domain username

      /*
      * Create the token as an array
      */
      $data = [
      	'iat'  => $issuedAt,         // Issued at: time when the token was generated
         'jti'  => $tokenId,          // Json Token Id: an unique identifier for the token
         'iss'  => $serverusername,       // Issuer
         'nbf'  => $notBefore,        // Not before
         'exp'  => $expire,           // Expire
         'data' => [                  // Data related to the logged user you can set your required data
				'id'   => $row[0]['id'], // id from the users table
			   'username' => $row[0]['username'], //  username
         ]
      ];

		$secretKey = base64_decode(SECRET_KEY);
      /// Here we will transform this array into JWT:
      $jwt = JWT::encode(
      	$data, //Data to be encoded in the JWT
         $secretKey, // The signing key
         ALGORITHM
      );

		$unencodedArray = ['jwt' => $jwt];
      echo  "{'status' : 'success','resp':".json_encode($unencodedArray)."}";
	}
	else {
		echo  "{'status' : 'error','msg':'Invalid email or passowrd'}";
	}
}
?>
