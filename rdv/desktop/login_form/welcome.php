<?php
// Initialize the session
session_start();

// Check if the user is logged in, if not then redirect him to login page
if (!isset($_SESSION["loggedin"]) || $_SESSION["loggedin"] !== true) {
    header("location: login.php");
    exit;
}
?>

<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">
    <title>Welcome</title>
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.css">
    <style type="text/css">
        body {
            font: 14px sans-serif;
            text-align: center;
        }
    </style>
</head>

<body>
    <div class="page-header">
        <h3>Ola, <b><?php echo htmlspecialchars($_SESSION["username"]); ?></b>. Selecione uma das ações abaixo;</h1>
    </div>
    <p>
        <a href="../data_form/main.html" class="btn btn-success">Aprovação e Consulta de gastos</a>
        <br> <br> <a href="reset-password.php" class="btn btn-warning">Trocar senha</a>
        <br> <br> <a href="logout.php" class="btn btn-danger">Sair</a>
    </p>
</body>

</html>