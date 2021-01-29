<?php
require "conn_rdv.php";
$id_func = $_POST["id_func"];
$id_desp = $_POST["id_desp"];
$valor_desp = $_POST["valor_desp"];
$valor_km = $_POST["valor_km"];
$obs = $_POST["obs"];
$dataM = $_POST["dataM"];

//LETRAS MAIUSCULAS NA QUERY
$mysql_qry = "INSERT INTO movimento (id_func, id_desp, valor_desp, valor_km, obs, dataM) VALUES 
('$id_func','$id_desp','$valor_desp','$valor_km', '$obs', '$dataM')";

if($conn_rdv->query($mysql_qry) === TRUE){
    echo "Dados inseridos com sucesso";

}
else{
    echo(mysqli_error($conn_rdv));
}

$conn_rdv->close();
