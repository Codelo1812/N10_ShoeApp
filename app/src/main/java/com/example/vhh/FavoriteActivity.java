package com.example.vhh;

import android.os.Bundle;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class FavoriteActivity extends AppCompatActivity {

    private RecyclerView recyclerViewFavorites;
    private ProductAdapter adapter;
    private List<Product> favoriteProducts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_favorite);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        recyclerViewFavorites = findViewById(R.id.recyclerViewFavorites);
        recyclerViewFavorites.setLayoutManager(new LinearLayoutManager(this));

        // Lấy sản phẩm yêu thích (phương thức này cần được triển khai)
        favoriteProducts = fetchFavoriteProducts();

        adapter = new ProductAdapter(this, favoriteProducts);
        recyclerViewFavorites.setAdapter(adapter);
    }

    private List<Product> fetchFavoriteProducts() {
        // TODO: Triển khai phương thức này để lấy sản phẩm yêu thích từ bộ nhớ
        return new ArrayList<>(); // Thay thế bằng sản phẩm yêu thích thực tế
    }
}