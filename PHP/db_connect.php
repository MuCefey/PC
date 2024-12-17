<?php

function connect_to_db() {
    require 'db_config.php';

    // Устанавливаем соединение с сервером базы данных
    $con = mysqli_connect(DB_SERVER, DB_USER, DB_PASSWORD);
    if (!$con) {
        die('Ошибка подключения: ' . mysqli_connect_error());
    }

    // Выбираем базу данных
    $db = mysqli_select_db($con, DB_DATABASE);
    if (!$db) {
        die('Ошибка выбора базы данных: ' . mysqli_error($con));
    }

    return $con;
}

function close_db_connection($con) {
    mysqli_close($con);
}


?>