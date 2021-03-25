<?php

include 'config.php';

// Create connection
$conn = new mysqli($servername, $username, $password, $dbname);

if ($_SERVER['REQUEST_METHOD'] == 'POST') {
    $DefaultId = 0;

    $ImageData = $_POST['image_data'];

    $ImageName = $_POST['image_tag'];

    $Cd_user = $_POST['cd_user'];

    $Direcao = $_POST['direcao'];

    $Gerencia = $_POST['gerencia'];

    $Equipe = $_POST['equipe'];

    $IdDesp = $_POST['id_desp'];

    $ValorDesp = $_POST['valor_desp'];

    $Km = $_POST['km'];

    $Obs = $_POST['obs'];

    $Data = $_POST['data'];

    $Status = $_POST['status'];

    $ObsRep = $_POST['obsRep'];

    $ImagePath = "uploads/$ImageName.jpg";

    $ServerURL = "uploadExample/$ImagePath";

    $InsertSQL = "INSERT INTO photos (image_path,image_name, cd_user, direcao, gerencia, equipe, id_desp, valor_desp, km, obs, data, status, obsRep) 
 values('$ServerURL','$ImageName','$ImageUser','$Cd_user', '$Direcao', '$Gerencia', '$Equipe', '$IdDesp', '$ValorDesp', '$Km', '$Obs', '$Data', '$Status', '$ObsRep')";

    if (mysqli_query($conn, $InsertSQL)) {

        file_put_contents($ImagePath, base64_decode($ImageData));

        echo "Sua imagem foi enviada!";
    }

    mysqli_close($conn);
} else {
    echo "Erro! Por favor, tente novamente.";
}
