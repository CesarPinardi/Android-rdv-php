<?php

include 'config.php';

// Create connection
$conn = new mysqli($servername, $username, $password, $dbname);
 
 if($_SERVER['REQUEST_METHOD'] == 'POST')
 {
 $DefaultId = 0;
 
 $ImageData = $_POST['image_data'];
 
 $ImageName = $_POST['image_tag'];

 $ImageUser = $_POST['image_user'];

 $ImageDesp = $_POST['image_desp'];
 
 $ImagePath = "uploads/$ImageName.jpg";
 
 $ServerURL = "uploadExample/$ImagePath";
 
 $InsertSQL = "INSERT INTO photos (image_path,image_name, image_user, image_desp) 
 values('$ServerURL','$ImageName','$ImageUser','$ImageDesp')";
 
 if(mysqli_query($conn, $InsertSQL)){

 file_put_contents($ImagePath,base64_decode($ImageData));

 echo "Sua imagem foi enviada!.";
 }
 
 mysqli_close($conn);
 }else{
 echo "Por favor, tente novamente.";
 }
