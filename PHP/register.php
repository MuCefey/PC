<?php
require 'db_connect.php';

$con = connect_to_db(); // Используем функцию подключения

if ($_SERVER["REQUEST_METHOD"] == "GET") {
    // Проверка наличия данных в GET запросе
    if (isset($_GET['login']) && isset($_GET['password'])) {
        // Получение данных из GET запроса
        $login = $_GET['login'];
        $password = $_GET['password'];

        // Хранение пароля без хеширования (НЕ БЕЗОПАСНО!)
        $stmt = $con->prepare("INSERT INTO users (login, password) VALUES (?, ?)");
        $stmt->bind_param("ss", $login, $password);

        // Выполнение запроса
        if ($stmt->execute()) {
            echo "Регистрация успешна!";
        } else {
            echo "Ошибка: " . $stmt->error;
        }

        // Закрытие подготовленного выражения
        $stmt->close();
    } else {
        echo "Пожалуйста, заполните все поля.";
    }
} else {
    echo "Недопустимый запрос.";
}

close_db_connection($con); // Закрываем соединение
?>