<?php

$db = mysqli_connect("localhost","root","","rdv");

if(!$db)
{
    die("Connection failed: " . mysqli_connect_error());
}

?>