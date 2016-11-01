<?php
/**
 * @author Ravi Tamada
 * @link http://www.androidhive.info/2012/01/android-login-and-registration-with-php-mysql-and-sqlite/ Complete tutorial
 */

	class DBFunctions {

	    private $conn;

	    // constructor
	    function __construct() {
	        require_once 'DBConnect.php';
	        // connecting to database
	        $db = new DBConnect();
	        $this->conn = $db->connect();
	    }

	    // destructor
	    function __destruct() {

	    }

	    /**
	     * Storing new user
	     * returns user details
	     */
	    public function storeUser($username, $email, $password) {
	      //$uuid = uniqid('', true);
	        $hash = $this->hashSSHA($password);
	        $encrypted_password = $hash["encrypted"]; // encrypted password
	        $salt = $hash["salt"]; // salt

	        $stmt = $this->conn->prepare("INSERT INTO users(username, email, encrypted_password, salt, created_at) VALUES(?, ?, ?, ?, NOW())");
	        $stmt->bind_param("ssss", $username, $email, $encrypted_password, $salt);
	        $result = $stmt->execute();
	        $stmt->close();

	        // check for successful store
	        if ($result) {
	            $stmt = $this->conn->prepare("SELECT * FROM users WHERE email = ?");
	            $stmt->bind_param("s", $email);
	            $stmt->execute();
	            $user = $stmt->get_result()->fetch_assoc();
	            $stmt->close();

	            return $user;
	        } else {
	            return false;
	        }
	    }

	    /**
	     * Get user by email and password
	     */
	    public function getUserByUsernameAndPassword($username, $password) {

	        $stmt = $this->conn->prepare("SELECT * FROM users WHERE username = ?");
	        $stmt->bind_param("s", $username);

	        if ($stmt->execute()) {
	            $user = $stmt->get_result()->fetch_assoc();
	            $stmt->close();

	            // verifying user password
	            $salt = $user['salt'];
	            $encrypted_password = $user['encrypted_password'];
	            $hash = $this->checkhashSSHA($salt, $password);
	            // check for password equality
	            if ($encrypted_password == $hash) {
	                // user authentication details are correct
	                return $user;
	            }
	        } else {
	            return NULL;
	        }
	    }

	    /**
	     * Checks whether or not the user exists
	     */
	    public function userExists($email) {
	        $stmt = $this->conn->prepare("SELECT email from users WHERE email = ?");
	        $stmt->bind_param("s", $email);
	        $stmt->execute();
	        $stmt->store_result();

			  //if there is more than 0 rows, then there is an existing user
	        if ($stmt->num_rows > 0) {
	            $stmt->close();
	            return true;
	        } else {
	            // user doesn't exist
	            $stmt->close();
	            return false;
	        }
	    }

	    /**
	     * Encrypting password
	     * @param password
	     * returns salt and encrypted password
	     */
	    public function hashSSHA($password) {
	        $salt = sha1(mt_rand());
	        $salt = substr($salt, 0, 128);
	        $encryptedPW = base64_encode(sha1($password . $salt, true) . $salt);
	        $hash = array("salt" => $salt, "encrypted" => $encryptedPW);
	        return $hash;
	    }

	    /**
	     * Decrypting password
	     * @param salt, password
	     * returns hash string
	     */
	    public function checkhashSSHA($salt, $password) {
	        $hash = base64_encode(sha1($password . $salt, true) . $salt);
	        return $hash;
	    }
	}
?>
