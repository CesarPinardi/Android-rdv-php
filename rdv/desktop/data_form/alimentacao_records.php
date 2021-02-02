<html>

<head>
  <link rel="stylesheet" type="text/css" href="tables_style.css" />
  <link rel="preconnect" href="https://fonts.gstatic.com">
  <link href="https://fonts.googleapis.com/css2?family=Bitter&family=Open+Sans:wght@300&display=swap" rel="stylesheet">
</head>

<body>
  <h2>Alimentação</h2>
  <br>

  <form>

    <table border="2">
      <tr>
        <th>Nome</th>
        <th>Valor gasto</th>
        <th>Cidade</th>
        <th>Identificação</th>
        <th>Ação </th>
        <th>Comentários </th>
      </tr>

      <?php

      include "dbConn.php"; // Using database connection file here

      $records = mysqli_query($db, "select * from alimentacao_data"); // fetch data from database

      while ($data = mysqli_fetch_array($records)) {
      ?>
        <tr>
          <td><?php echo $data['loginA']; ?></td>
          <td><?php echo $data['dataA']; ?></td>
          <td>R$<?php echo $data['valorRefeicao']; ?></td>
          <td><?php echo $data['cidadeAlimentacao']; ?></td>
          <td><?php echo $data['idAlimentacao']; ?></td>
          <td>
            <button onclick="aprova()">Aprovar</button> &nbsp;
            <button onclick="vetar()">Vetar</button>
          </td>

          <td>
            <textarea id="comments" rows="3" cols="18">

            </textarea>
          </td>
            <!-- aqui precisa fazer a verificacao de aprovacao ou nao-
            <td><a href="edit.php?id=<?php echo $data['id']; ?>">Aprovar</a></td> -->
        </tr>
      <?php
      }
      ?>
      <tr>
        <td id = "a" ></td>
        <td id = "a"></td>
        <td id = "a"></td>
        <td id = "a"></td>
        <td id = "a"></td>
        <td id = "a"></td>
      </tr>
      <tr>
        <td></td>
        <td></td>
        <td><a id = "req" href="main.html"> Voltar </a></td>
        <td><button id = "req" onclick="enviar()"> Enviar </button></td>
        <td></td>
        <td></td>
      </tr>
    </table>


  </form>

</body>
<script src="validar.js"></script>

</html>