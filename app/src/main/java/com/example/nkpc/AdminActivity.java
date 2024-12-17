    package com.example.nkpc;

    import androidx.appcompat.app.AppCompatActivity;
    import androidx.recyclerview.widget.LinearLayoutManager;
    import androidx.recyclerview.widget.RecyclerView;

    import android.os.AsyncTask;
    import android.os.Bundle;
    import android.view.View;
    import android.widget.Button;
    import android.widget.EditText;
    import android.widget.Toast;

    import org.json.JSONArray;
    import org.json.JSONObject;

    import java.io.BufferedReader;
    import java.io.InputStream;
    import java.io.InputStreamReader;
    import java.io.OutputStream;
    import java.net.HttpURLConnection;
    import java.net.URL;
    import java.net.URLEncoder;
    import java.util.ArrayList;
    import java.util.List;

    public class AdminActivity extends AppCompatActivity {

        private EditText editTextProductName;
        private EditText editTextProductDescription;
        private EditText editTextProductPrice;
        private EditText editTextProductImageUrl;
        private Button buttonAddProduct;
        private RecyclerView recyclerViewProducts;
        private AdminProductAdapter productAdapter;
        private List<Product> productList;
        private EditText editTextProductId;
        private Button buttonDeleteProduct;
        private EditText editTextEditProductName;
        private EditText editTextEditProductId;
        private EditText editTextEditProductDescription;
        private EditText editTextEditProductPrice;
        private EditText editTextEditProductImageUrl;
        private Button buttonEditProduct;




        private static final String ADD_PRODUCT_URL = "http://10.0.2.2/add_product.php";
        private static final String GET_PRODUCTS_URL = "http://10.0.2.2/get_products_admin.php";

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_admin);

            editTextProductName = findViewById(R.id.editTextProductName);
            editTextProductDescription = findViewById(R.id.editTextProductDescription);
            editTextProductPrice = findViewById(R.id.editTextProductPrice);
            editTextProductImageUrl = findViewById(R.id.editTextProductImageUrl);
            buttonAddProduct = findViewById(R.id.buttonAddProduct);
            recyclerViewProducts = findViewById(R.id.recyclerViewProducts);
            editTextProductId = findViewById(R.id.editTextProductId);
            buttonDeleteProduct = findViewById(R.id.buttonDeleteProduct);
            editTextEditProductId = findViewById(R.id.editTextEditProductId);
            editTextEditProductName = findViewById(R.id.editTextEditProductName);
            editTextEditProductDescription = findViewById(R.id.editTextEditProductDescription);
            editTextEditProductPrice = findViewById(R.id.editTextEditProductPrice);
            editTextEditProductImageUrl = findViewById(R.id.editTextEditProductImageUrl);
            buttonEditProduct = findViewById(R.id.buttonEditProduct);


            productList = new ArrayList<>();
            productAdapter = new AdminProductAdapter(this, productList);
            recyclerViewProducts.setLayoutManager(new LinearLayoutManager(this));
            recyclerViewProducts.setAdapter(productAdapter);

            buttonAddProduct.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String name = editTextProductName.getText().toString();
                    String description = editTextProductDescription.getText().toString();
                    String price = editTextProductPrice.getText().toString();
                    String imageUrl = editTextProductImageUrl.getText().toString();

                    // Проверка на пустые поля
                    if (name.isEmpty() || description.isEmpty() || price.isEmpty() || imageUrl.isEmpty()) {
                        Toast.makeText(AdminActivity.this, "Пожалуйста, заполните все поля", Toast.LENGTH_SHORT).show();
                    } else {
                        new AddProductTask().execute(name, description, price, imageUrl);
                    }
                }
            });
            buttonDeleteProduct.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String productId = editTextProductId.getText().toString();
                    if (productId.isEmpty()) {
                        Toast.makeText(AdminActivity.this, "Пожалуйста, введите ID товара", Toast.LENGTH_SHORT).show();
                    } else {
                        new DeleteProductTask().execute(productId);
                    }
                }
            });
            buttonEditProduct.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String productId = editTextEditProductId.getText().toString();
                    String newName = editTextEditProductName.getText().toString();
                    String newDescription = editTextEditProductDescription.getText().toString();
                    String newPrice = editTextEditProductPrice.getText().toString();
                    String newImageUrl = editTextEditProductImageUrl.getText().toString();

                    if (productId.isEmpty()) {
                        Toast.makeText(AdminActivity.this, "Пожалуйста, введите ID товара", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Проверка на пустые поля для редактирования
                    if (newName.isEmpty() || newDescription.isEmpty() || newPrice.isEmpty() || newImageUrl.isEmpty()) {
                        Toast.makeText(AdminActivity.this, "Пожалуйста, заполните все поля", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    new EditProductTask().execute(productId, newName, newDescription, newPrice, newImageUrl);
                }
            });


            getProducts();
        }

        private void getProducts() {
            new GetProductsTask().execute();
        }

        private class GetProductsTask extends AsyncTask<Void, Void, List<Product>> {
            @Override
            protected List<Product> doInBackground(Void... voids) {
                List<Product> products = new ArrayList<>();
                try {
                    URL url = new URL(GET_PRODUCTS_URL);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");

                    InputStream inputStream = connection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();

                    JSONArray jsonArray = new JSONArray(response.toString());
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        int id = jsonObject.getInt("id");
                        String name = jsonObject.getString("name");
                        String description = jsonObject.getString("description");
                        double price = jsonObject.getDouble("price");
                        String imageUrl = jsonObject.getString("image_url");

                        products.add(new Product(id, name, description, price, imageUrl));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return products;
            }

            @Override
            protected void onPostExecute(List<Product> products) {
                super.onPostExecute(products);
                // Обновляем адаптер с новыми данными
                productList.clear(); // Очищаем существующий список
                productList.addAll(products); // Добавляем новые продукты
                productAdapter.notifyDataSetChanged(); // Уведомляем адаптер об изменениях
            }
        }
        private class AddProductTask extends AsyncTask<String, Void, String> {
            @Override
            protected String doInBackground(String... params) {
                String name = params[0];
                String description = params[1];
                String price = params[2];
                String imageUrl = params[3];

                try {
                    URL url = new URL(ADD_PRODUCT_URL);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("POST");
                    connection.setDoOutput(true);

                    String postData = "name=" + URLEncoder.encode(name, "UTF-8") +
                            "&description=" + URLEncoder.encode(description, "UTF-8") +
                            "&price=" + URLEncoder.encode(price, "UTF-8") +
                            "&image_url=" + URLEncoder.encode(imageUrl, "UTF-8");

                    OutputStream os = connection.getOutputStream();
                    os.write(postData.getBytes());
                    os.flush();
                    os.close();

                    return String.valueOf(connection.getResponseCode());

                } catch (Exception e) {
                    e.printStackTrace();
                    return "Ошибка: " + e.getMessage();
                }
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                if (result.equals("200")) {
                    Toast.makeText(AdminActivity.this, "Товар добавлен успешно", Toast.LENGTH_SHORT).show();
                    // Очистка полей ввода
                    editTextProductName.setText("");
                    editTextProductDescription.setText("");
                    editTextProductPrice.setText("");
                    editTextProductImageUrl.setText("");
                    // Обновляем список товаров после добавления
                    getProducts();
                } else {
                    Toast.makeText(AdminActivity.this, "Ошибка при добавлении товара", Toast.LENGTH_SHORT).show();
                }
            }
        }

        private class DeleteProductTask extends AsyncTask<String, Void, String> {
            @Override
            protected String doInBackground(String... params) {
                String productId = params[0];
                try {
                    URL url = new URL("http://10.0.2.2/delete_product.php"); // Замените на ваш URL для удаления
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("POST");
                    connection.setDoOutput(true);

                    String postData = "id=" + URLEncoder.encode(productId, "UTF-8");
                    OutputStream os = connection.getOutputStream();
                    os.write(postData.getBytes());
                    os.flush();
                    os.close();

                    return String.valueOf(connection.getResponseCode());
                } catch (Exception e) {
                    e.printStackTrace();
                    return "Ошибка: " + e.getMessage();
                }
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                if (result.equals("200")) {
                    Toast.makeText(AdminActivity.this, "Товар удалён успешно", Toast.LENGTH_SHORT).show();
                    // Обновляем список товаров после удаления
                    getProducts();
                } else {
                    Toast.makeText(AdminActivity.this, "Ошибка при удалении товара", Toast.LENGTH_SHORT).show();
                }
            }
        }

        private class EditProductTask extends AsyncTask<String, Void, String> {
            @Override
            protected String doInBackground(String... params) {
                String productId = params[0];
                String newName = params[1];
                String newDescription = params[2];
                String newPrice = params[3];
                String newImageUrl = params[4];

                try {
                    URL url = new URL("http://10.0.2.2/edit_product.php"); // Замените на ваш URL для редактирования
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("POST");
                    connection.setDoOutput(true);

                    // Обновляем данные, которые отправляем на сервер
                    String postData = "id=" + URLEncoder.encode(productId, "UTF-8") +
                            "&name=" + URLEncoder.encode(newName, "UTF-8") +
                            "&description=" + URLEncoder.encode(newDescription, "UTF-8") +
                            "&price=" + URLEncoder.encode(newPrice, "UTF-8") +
                            "&image_url=" + URLEncoder.encode(newImageUrl, "UTF-8");

                    OutputStream os = connection.getOutputStream();
                    os.write(postData.getBytes());
                    os.flush();
                    os.close();

                    return String.valueOf(connection.getResponseCode());
                } catch (Exception e) {
                    e.printStackTrace();
                    return "Ошибка: " + e.getMessage();
                }
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                if (result.equals("200")) {
                    Toast.makeText(AdminActivity.this, "Товар успешно отредактирован", Toast.LENGTH_SHORT).show();
                    // Очищаем поля ввода
                    editTextEditProductId.setText("");
                    editTextEditProductName.setText("");
                    editTextProductDescription.setText("");
                    editTextProductPrice.setText("");
                    editTextProductImageUrl.setText("");
                    // Обновляем список товаров после редактирования
                    getProducts();
                } else {
                    Toast.makeText(AdminActivity.this, "Ошибка при редактировании товара", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }