<?php

	$connection = mysqli_connect("localhost","","","") or die("Error " . mysqli_error($connection));

	$Id =$_GET['id'];

	$sql = "DELETE FROM photos WHERE id = '$Id'";
	$result = mysqli_query($connection, $sql);
	
	if($result){
		echo "Data Deleted";
	}
	else
	{
		echo "Failed";
	}
	
mysqli_close($connection);
?>