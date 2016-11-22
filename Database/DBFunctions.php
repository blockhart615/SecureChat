<?php
class DBFunctions
{

    private $conn;

    // constructor
    public function __construct()
    {
        require_once 'DBConnect.php';
        // connect to database
        $db         = new DBConnect();
        $this->conn = $db->connect();
    }

    // destructor
    public function __destruct()
    {

    }

    /**
     * Stores user info into the database
     * returns user details upon successful store
     */
    public function storeUser($username, $email, $password)
    {
        //hash the password before storing
        $hash               = $this->hashSSHA($password);
        $encrypted_password = $hash["encrypted"]; // encrypted password
        $salt               = $hash["salt"]; // salt

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
        } else {
            //user storage failed.
            return false;
        }
    }

    /**
     * Get user by email and password
     */
    public function getUserByUsernameAndPassword($username, $password)
    {
        //prepare statements to protect against SQL injections
        $stmt = $this->conn->prepare("SELECT * FROM users WHERE username = ?");
        $stmt->bind_param("s", $username);

        //if statement executes successfully, assign data to user array.
        if ($stmt->execute()) {
            $user = $stmt->get_result()->fetch_assoc();
            $stmt->close();

            // verifying user password
            $salt               = $user['salt'];
            $encrypted_password = $user['encrypted_password'];
            $hash               = $this->checkhashSSHA($salt, $password);
            // checks if the pw in the DB matches the hash performed
            if ($encrypted_password == $hash) {
                // user authentication details are correct, returns user data
                return $user;
            }
        } else {
            //return NULL if the user wasn't found
            return null;
        }
    }

    /**
     * Checks whether or not the user exists
     */
    public function userExists($username)
    {
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

    public function emailExists($email) {
                //prepare statements to protect against SQL injections
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
    public function hashSSHA($password)
    {
        $salt        = hash("sha256", mt_rand());
        $salt        = substr($salt, 0, 256);
        $encryptedPW = base64_encode(hash("sha256", $password . $salt) . $salt);
        $hash        = array("salt" => $salt, "encrypted" => $encryptedPW);
        return $hash;
    }

    /**
     * Decrypting password
     * @param salt, password
     * returns hash string
     */
    public function checkhashSSHA($salt, $password)
    {
        $hash = base64_encode(hash("sha256", $password . $salt) . $salt);
        return $hash;
    }

    /*
     * Sends a message to the database
     */
    public function sendMessage($sender, $receiver, $message)
    {
        //prepare statements to protect against SQL injections.
        $stmt = $this->conn->prepare("INSERT INTO messages(sender, receiver, message, time_sent) VALUES(?, ?, ?, NOW())");
        $stmt->bind_param("sss", $sender, $receiver, $message);
        $result = $stmt->execute();
        $stmt->close();

        if ($result) {
            return true;
        } else {
            return false;
        }
    }

    /*
     * Receives messages from the database
     */
    public function getMessages($username) {
        //prepare statements to protect against SQL injections
        $stmt = $this->conn->prepare("SELECT * FROM messages WHERE receiver = ? OR sender = ? ORDER BY time_sent");
        $stmt->bind_param("ss", $username, $username);

        //if statement executes successfully, assign data to user array.
        if ($stmt->execute()) {

            //ARRAY OF ASSOCIATIVE ARRAYS
            $conversations = null;
            $convo = "";

            $result = $stmt->get_result();
            if ($result->num_rows > 0) {

                while ($row = $result->fetch_assoc()) {
                    //get message contents into an associative array
                    $sender   = $row['sender'];
                    $message  = $row['message'];
                    $timeSent = $row['time_sent'];
                    $receiver = $row['receiver'];
                    $message  = array(
                        'sender'    => $sender,
                        'receiver'	=> $receiver,
                        'message'   => $message,
                        'time_sent' => $timeSent
                    );
                    $jwtMessage = json_encode($message);

                    //TO GET AN INDIVIDUAL CONVERSATION
                    if ($username != $sender) {
                        $convo = $sender;
                    } else if ($username == $sender && $username != $receiver) {
                    	$convo = $receiver;
                    }

                    //ADD NEW MESSAGE TO CONVERSATION
                    if(array_key_exists($convo, $conversations)) {
                    	array_push($conversations[$convo], $jwtMessage);

                    }
                    //CREATE NEW CONVERSATION
                    else {
                    	$conversations[$convo] = array($jwtMessage);
                    }            

                } //END WHILE LOOP

                $response["conversations"] = $conversations;
                $response["error"] = false;
                $response["error_msg"] = "No errors!";

            } //IF AT LEAST ONE ROW
            else {
                $response["error"] = true;
                $response["error_msg"] = "No messages to read.";
            }
        } 

        //Unknown Error
        else {
            $response["error"] = true;
            $response["error_msg"] = "Statement execution unsuccessful.";            
        }
        $stmt->close();

        
        return json_encode($response);
    }//END Function getMessages()
}
