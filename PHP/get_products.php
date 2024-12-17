<?php
require 'db_connect.php';

$con = connect_to_db();
if ($con->connect_error) {
    die("Connection failed: " . $con->connect_error);
}

$sql = "SELECT id, name, description, price, image_url FROM products";
$result = $con->query($sql);

$products = array();
if ($result && $result->num_rows > 0) {
    while ($row = $result->fetch_assoc()) {
        $products[] = $row;
    }
} else {
    // Если запрос вернул пустое значение, выводим пустой массив
    echo json_encode([]);
    $con->close();
    exit();
}

echo json_encode($products);
$con->close();
?>