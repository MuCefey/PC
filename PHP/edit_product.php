<?php
require 'db_connect.php';

$con = connect_to_db();
// Получаем данные
$id = $_POST['id'];
$name = $_POST['name'];
$description = $_POST['description'];
$price = $_POST['price'];
$image_url = $_POST['image_url'];

// Обновление записи
$sql = "UPDATE products SET name='$name', description='$description', price='$price', image_url='$image_url' WHERE id=$id";

if ($con->query($sql) === TRUE) {
    echo "Данные успешно обновлены";
} else {
    echo "Ошибка обновление данных: " . $con->error;
}

$con->close();
?>