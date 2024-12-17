<?php
require 'db_connect.php';

$con = connect_to_db();
// Получаем данные
$id = $_POST['id'];
$description = $_POST['description'];

// Обновление записи
$sql = "UPDATE products SET description='$description' WHERE id=$id";

if ($con->query($sql) === TRUE) {
    echo "Record updated successfully";
} else {
    echo "Error updating record: " . $con->error;
}

$con->close();
?>