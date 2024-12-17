<?php
require 'db_connect.php';

$con = connect_to_db();


// Получение данных из запроса
$name = $_POST['name'];
$description = $_POST['description'];
$price = $_POST['price'];
$image_url = $_POST['image_url'];

// SQL запрос на добавление товара
$sql = "INSERT INTO products (name, description, price, image_url) VALUES ('$name', '$description', '$price', '$image_url')";

if ($con->query($sql) === TRUE) {
    echo "Товар добавлен";
} else {
    echo "Ошибка: " . $sql . "<br>" . $con->error;
}

$con->close();
?>