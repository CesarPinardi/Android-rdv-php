<?php 
 
	$conn = mysqli_connect("localhost", "", "");
	mysqli_select_db($conn, "");
	
	$raw = mysqli_query($conn, $qry);
	
	while ($res = mysqli_fetch_array($raw))
	{
		$data[] = $res;
	}
	print(json_enncode($data));
 
?> 