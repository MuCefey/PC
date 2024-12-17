<?php
require 'db_connect.php'; // Убедитесь, что соединение установлено
$con = connect_to_db();
// Проверяем, что параметры переданы в запросе
if ($con->connect_error) {
die("Ошибка подключения: " . $con->connect_error);
}

// Получаем данные из запроса
$userId = isset($_POST['user_id']) ? intval($_POST['user_id']) : 0;
$productId = isset($_POST['product_id']) ? intval($_POST['product_id']) : 0;
$quantity = isset($_POST['quantity']) ? intval($_POST['quantity']) : 0;

// Выполняем запрос на вставку
$sql = "INSERT INTO orders (user_id, product_id, quantity) VALUES ('$userId', '$productId', '$quantity')";

if ($con->query($sql) === TRUE) {
echo "Заказ оформлен";
} else {
echo "Error: " . $sql . "<br>" . $con->error;
}

$con->close();
?>