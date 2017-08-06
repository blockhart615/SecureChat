<?php
//Configure Database Connection Variables
define("DB_HOST", "localhost");
define("DB_USER", "root");
define("DB_PASSWORD", "PASSWORD");
define("DB_DATABASE", "SecureChatDB");

class DBConnect {
      private $conn;

      public function connect() {
            //make connection to database
            $this->conn = new mysqli(DB_HOST, DB_USER, DB_PASSWORD, DB_DATABASE);

            return $this->conn;
      }
}
?>
