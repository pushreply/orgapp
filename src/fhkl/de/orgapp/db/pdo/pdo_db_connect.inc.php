<?php

try 
{
    $pdo = new PDO('mysql:host=localhost;dbname=orgapp_orgappfhzwdb', 'orgapp_fhzwdb', 'DbTk55ggRe');
    $pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
    $pdo->exec('SET NAMES "utf8"');
} catch(PDOException $e) {
	echo 'DB: FAILED';
    exit();
}

?>