<?php
    //Configure Database Connection Variables
    define("DB_HOST", "localhost");
    define("DB_USER", "root");
    define("DB_PASSWORD", "liferuined478");
    define("DB_DATABASE", "SecureChatDB");

    //make connection to database
    $conn = new mysqli(DB_HOST, DB_USER, DB_PASSWORD, DB_DATABASE);
?>
