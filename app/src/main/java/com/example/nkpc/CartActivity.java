package com.example.nkpc;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CartActivity extends AppCompatActivity {
    private ListView listView;
    private ArrayList<Product> cartItems;
    private Button buttonCheckout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        listView = findViewById(R.id.cart_list_view);
        cartItems = (ArrayList<Product>) getIntent().getSerializableExtra("cartItems");
        Button checkoutButton = findViewById(R.id.buttonCheckout); // Добавьте кнопку в вашем XML
        checkoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                placeOrder();
            }
        });
        displayCartItems();
    }
    public void addProductToCart(Product product) {
        cartItems.add(product); // Добавляем продукт в корзину
        Toast.makeText(this, product.getName() + " добавлен в корзину", Toast.LENGTH_SHORT).show(); // Уведомление
    }

    private void displayCartItems() {
        ArrayList<String> itemDetails = new ArrayList<>();
        for (Product product : cartItems) {
            itemDetails.add(product.getName() + " - " + product.getPrice() + "₽");
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.list_item, itemDetails) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView textView = view.findViewById(R.id.item_text);
                textView.setText(itemDetails.get(position));
                return view;
            }
        };
        listView.setOnItemClickListener((parent, view, position, id) -> {
            // Удаляем товар из списка cartItems
            Product productToRemove = cartItems.get(position);
            cartItems.remove(position);

            // Обновляем отображение списка
            displayCartItems();

            // Если необходимо, можно добавить уведомление о том, что товар удалён
            Toast.makeText(CartActivity.this, productToRemove.getName() + " был удалён из корзины", Toast.LENGTH_SHORT).show();
        });
        listView.setAdapter(adapter);
    }
    private void placeOrder() {
// Предполагается, что вы знаете id пользователя (userId)
        int userId = 1; // Получите ID текущего пользователя, как вам нужно

        for (Product product : cartItems) {
// Отправляем данные на сервер используя Volley
            String url = "http://10.0.2.2/place_order.php"; // URL вашего PHP скрипта

            StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Toast.makeText(CartActivity.this, response, Toast.LENGTH_SHORT).show();
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(CartActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("user_id", String.valueOf(userId));
                    params.put("product_id", String.valueOf(product.getId()));
                    params.put("quantity", "1"); // Или любое другое количество
                    return params;
                }
            };

            Volley.newRequestQueue(this).add(stringRequest);
        }
    }
}

