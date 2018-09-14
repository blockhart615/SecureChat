<?php
//Configure Database Connection Variables
define("DB_HOST", '"' . getenv('DB_HOST') . '"');
define("DB_USER", '"' . getenv('DB_LOGIN') . '"');
define("DB_PASSWORD", '"' . getenv('DB_PW') . '"');
define("DB_DATABASE", '"' . getenv('DB_NAME') . '"');

class DBConnect {
      private $conn;

      public function connect() {
            //make connection to database
            $this->conn = new mysqli(DB_HOST, DB_USER, DB_PASSWORD, DB_DATABASE);

            return $this->conn;
      }
}
?>
