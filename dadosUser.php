<?php
    
    $conn = mysqli_connect("localhost", "root", "");
    mysqli_select_db($conn, "rdv");

    if($_GET['cd_user'])
    {
        $ia =$_GET['cd_user'];

        $qry = "SELECT * FROM `users` WHERE `cd_user` = '$ia'";

        $raw = mysqli_query($conn, $qry);

        while ($res = mysqli_fetch_assoc($raw)) {
            $data[] = $res;
        }
		
		print(json_encode($data));
				        
    }
	else{
		echo "Erro!";
	}
?>