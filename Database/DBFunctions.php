<?php
	class DBFunctions {

	    private $conn;

	    // constructor
	    function __construct() {
	        require_once 'DBConnect.php';
	        // connect to database
	        $db = new DBConnect();
	        $this->conn = $db->connect();
	    }

	    // destructor
	    function __destruct() {

	    }

	    /**
	     * Stores user info into the database
	     * returns user details upon successful store
	     */
	    public function storeUser($username, $email, $password) {
			 //hash the password before storing
	        $hash = $this->hashSSHA($password);
	        $encrypted_password = $hash["encrypted"]; // encrypted password
	        $salt = $hash["salt"]; // salt

			  //prepare statements to protect against SQL injections.
	        $stmt = $this->conn->prepare("INSERT INTO users(username, email, encrypted_password, salt, created_at) VALUES(?, ?, ?, ?, NOW())");
	        $stmt->bind_param("ssss", $username, $email, $encrypted_password, $salt);
	        $result = $stmt->execute();
	        $stmt->close();

	        //if storage was successful, get user credentials to return.
	        if ($result) {
	            $stmt = $this->conn->prepare("SELECT * FROM users WHERE email = ?");
	            $stmt->bind_param("s", $email);
	            $stmt->execute();
					//get user credentials
	            $user = $stmt->get_result()->fetch_assoc();
	            $stmt->close();

	            return $user;
	        }
			  else { //user storage failed.
	            return false;
	        }
	    }

	    /**
	     * Get user by email and password
	     */
	    public function getUserByUsernameAndPassword($username, $password) {
			 //prepare statements to protect against SQL injections
	        $stmt = $this->conn->prepare("SELECT * FROM users WHERE username = ?");
	        $stmt->bind_param("s", $username);

			  //if statement executes successfully, assign data to user array.
	        if ($stmt->execute()) {
	            $user = $stmt->get_result()->fetch_assoc();
	            $stmt->close();

	            // verifying user password
	            $salt = $user['salt'];
	            $encrypted_password = $user['encrypted_password'];
	            $hash = $this->checkhashSSHA($salt, $password);
	            // checks if the pw in the DB matches the hash performed
	            if ($encrypted_password == $hash) {
	                // user authentication details are correct, returns user data
	                return $user;
	            }
	        }
			  else {
				  //return NULL if the user wasn't found
	            return NULL;
	        }
	    }

	    /**
	     * Checks whether or not the user exists
	     */
	    public function userExists($username) {
			 //prepare statements to protect against SQL injections
			  $stmt = $this->conn->prepare("SELECT username from users WHERE username = ?");
	        $stmt->bind_param("s", $username);
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
			 $salt = hash("sha256", mt_rand());
			 $salt = substr($salt, 0, 256);
			 $encryptedPW = base64_encode(hash("sha256", $password . $salt) . $salt);
			 $hash = array("salt" => $salt, "encrypted" => $encryptedPW);
	        return $hash;
	    }

	    /**
	     * Decrypting password
	     * @param salt, password
	     * returns hash string
	     */
	    public function checkhashSSHA($salt, $password) {
			 $hash = base64_encode(hash("sha256", $password . $salt) . $salt);
	        return $hash;
	    }

		 /*
	 	 * Sends a message to the database
	 	 */
	 	public function sendMessage($message, $receiver, $sender) {
	 		//prepare statements to protect against SQL injections.
	 		$stmt = $this->conn->prepare("INSERT INTO messages(sender, receiver, message, timeSent) VALUES(?, ?, ?, NOW())");
	 		$stmt->bind_param("sss", $sender, $receiver, $message);
	 		$result = $stmt->execute();
	 		$stmt->close();

			return $result;
	 	}

	 	/*
	 	 * Receives messages from the database
	 	 */
	 	public function receiveMessages($user) {

			return NULL;
	 	}
	}
?>
