<?php
require 'db_connect.php'; // Подключаем файл с функцией для соединения с базой данных

// Устанавливаем соединение с базой данных
$con = connect_to_db();
if ($con->connect_error) {
    // Если не удалось установить соединение, выводим сообщение об ошибке
    die("Connection failed: " . $con->connect_error);
}

// SQL-запрос для извлечения списка продуктов
$sql = "SELECT id, name, description, price, image_url FROM products";
$result = $con->query($sql);

// Создаем массив для хранения продуктов
$products = array();

// Проверяем, есть ли результаты запроса
if ($result && $result->num_rows > 0) {
    // Извлекаем данные из результата запроса
    while ($row = $result->fetch_assoc()) {
        $products[] = $row; // Добавляем каждую строку в массив
    }
} else {
    // Если запрос вернул пустое значение, выводим пустой массив
    echo json_encode([]);
    $con->close();
    exit();
}

// Кодируем массив $products в формат JSON и выводим его
echo json_encode($products);
$con->close(); // Закрываем соединение с базой данных
?>