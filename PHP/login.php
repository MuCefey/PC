
<?php
require 'db_connect.php';

$con = connect_to_db(); // Используем функцию подключения

// Проверяем, были ли переданы параметры
if (!isset($_GET['login']) || !isset($_GET['password'])) {
    echo json_encode(["success" => false, "message" => "Не все поля заполнены."]);
    exit();
}

$login = $_GET['login'];
$password = $_GET['password'];

$sql = "SELECT password FROM users WHERE login = ?";
$stmt = $con->prepare($sql);
if (!$stmt) {
    echo json_encode(["success" => false, "message" => "Ошибка в запросе к базе данных."]);
    exit();
}

$stmt->bind_param("s", $login);
$stmt->execute();
$stmt->store_result();

if ($stmt->num_rows > 0) {
    $stmt->bind_result($stored_password);
    $stmt->fetch();
    
    // Сравниваем введённый пароль с сохранённым в базе. Рекомендуется использовать password_verify
    if ($password === $stored_password) {
        echo json_encode(["success" => true, "message" => "Вход успешен."]);
    } else {
        echo json_encode(["success" => false, "message" => "Неверный пароль."]);
    }
} else {
    echo json_encode(["success" => false, "message" => "Пользователь не найден."]);
}

$stmt->close();
$con->close(); // Закрываем соединение
?>