package com.example.nkpc;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CatalogActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ProductAdapter productAdapter;
    private List<Product> productList;
    private ImageButton viewCartButton;
    private ArrayList<Product> cartItems = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        productList = new ArrayList<>();
        productAdapter = new ProductAdapter(productList, this);
        recyclerView.setAdapter(productAdapter);

        loadProducts();

        viewCartButton = findViewById(R.id.cart_button);
        viewCartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CatalogActivity.this, CartActivity.class);
                intent.putExtra("cartItems", cartItems); // Передача списка корзины
                startActivity(intent);
            }
        });
    }

    private void loadProducts() {
        String url = "http://10.0.2.2/get_products.php";

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        for (int i = 0; i < response.length(); i++) {
                            try {
                                JSONObject productObject = response.getJSONObject(i);
                                int id = productObject.getInt("id");
                                String name = productObject.getString("name");
                                String description = productObject.getString("description");
                                double price = productObject.getDouble("price");
                                String imageUrl = productObject.getString("image_url");

                                Product product = new Product(id, name, description, price, imageUrl);
                                productList.add(product);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        productAdapter.notifyDataSetChanged();
                    }
                }, error -> Log.e("CatalogActivity", "Error fetching products", error));

        Volley.newRequestQueue(this).add(jsonArrayRequest);
    }

    // Новый метод для добавления продуктов в корзину
    public void addProductToCart(Product product) {
        cartItems.add(product);
        Toast.makeText(this, product.getName() + " добавлен в корзину", Toast.LENGTH_SHORT).show();
    }
}