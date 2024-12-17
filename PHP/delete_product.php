<?php
require 'db_connect.php';

$con = connect_to_db();
if ($con->connect_error) {
    die("Connection failed: " . $con->connect_error);
}

// Получаем ID товара из запроса
if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $product_id = intval($_POST['id']);

    // Проверка, что ID действителен
    if ($product_id > 0) {
        // SQL-запрос на удаление товара
        $sql = "DELETE FROM products WHERE id = ?";
        
        // Подготовка и выполнение
        if ($stmt = $con->prepare($sql)) {
            $stmt->bind_param("i", $product_id);
            if ($stmt->execute()) {
                echo json_encode(["status" => "success", "message" => "Товар успешно удален."]);
            } else {
                echo json_encode(["status" => "error", "message" => "Ошибка при удалении товара."]);
            }
            $stmt->close();
        } else {
            echo json_encode(["status" => "error", "message" => "Ошибка подготовки запроса."]);
        }
    } else {
        echo json_encode(["status" => "error", "message" => "Некорректный ID товара."]);
    }
} else {
    // Если запрос не является методом POST
    echo json_encode(["status" => "error", "message" => "Неверный метод запроса. Используйте POST."]);
}

$con->close();
?>