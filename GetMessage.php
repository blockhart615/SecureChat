<?php
require_once 'Database/DBFunctions.php';

//instantiate DBFunctions object to perform queries and operations on Database.
$db = new DBFunctions();

if (isset($_GET["receiver"])) {
	$messages = $db->getMessages();
}

echo $messages;
?>
