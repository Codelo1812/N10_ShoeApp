package com.example.vhh;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    private EditText searchEditText;
    private RecyclerView recyclerView;
    private ProductAdapter productAdapter;
    private List<Product> productList; // Danh sách sản phẩm gốc
    private List<Product> filteredList; // Danh sách sản phẩm sau khi lọc

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        searchEditText = findViewById(R.id.search_edit_text);
        recyclerView = findViewById(R.id.recycler_view);

        // Khởi tạo danh sách sản phẩm gốc
        productList = new ArrayList<>();
        // Thêm các sản phẩm vào productList
        productList.add(new Product("Nike Air Max", "Running", "A comfortable running shoe with air cushioning.", 199.99, R.drawable.giay1, true,1,false));
        productList.add(new Product("Adidas Ultra Boost", "Running", "A high-performance running shoe with boost technology.", 179.99, R.drawable.giay2, false,1,false));
        productList.add(new Product("giày đen thời trang", "Fashion", "A stylish black shoe suitable for different occasions.", 200, R.drawable.giay3, true,1,false));
        productList.add(new Product("xanh lá hủy hoại của thiên nhiên", "Fashion", "A green shoe that stands out and makes a statement.", 87.79, R.drawable.giay4, true,1,false));
        productList.add(new Product("giày hồng đem tới cuộc sống đẹp", "Fashion", "A pink shoe that brings beauty to life.", 203, R.drawable.giay5, false,1,false));
        productList.add(new Product("addidas xanh đại dương", "Running", "A blue Adidas shoe reminiscent of the ocean.", 123.5, R.drawable.giay6, true,1,false));
        productList.add(new Product("giày xanh tơ lụa vải mềm", "Fashion", "A soft green silk shoe for a comfortable fit.", 231, R.drawable.giay7, true,1,false));
        productList.add(new Product("trắng tinh khôi", "Fashion", "A pure white shoe that is timeless and elegant.", 266.5, R.drawable.giay8, false,1,false));
        productList.add(new Product("xanh 3 lá", "Fashion", "A green shoe with a three-leaf clover design.", 124.32, R.drawable.giay9, true,1,false));
        productList.add(new Product("niken khúc 3", "Running", "A shoe with a unique Nike design.", 156.223, R.drawable.giay10, false,1,false));
        productList.add(new Product("giày thể thao nhẹ", "Sports", "A lightweight sports shoe for active individuals.", 145.25, R.drawable.giay11, true,1,false));
        productList.add(new Product("giày bay bổng", "Fashion", "A shoe that gives you a feeling of flying.", 135.02, R.drawable.giay12, true,1,false));
        productList.add(new Product("giày quý tộc", "Fashion", "A luxurious shoe for the elite.", 500.05, R.drawable.giay13, false,1,false));
        productList.add(new Product("boot đen tạo", "Fashion", "A black boot with a rugged design.", 10000.054, R.drawable.giay14, true,1,false));
        productList.add(new Product("giày hoạt động nhẹ", "Sports", "A shoe designed for light activities.", 154.562, R.drawable.giay15, true,1,false));
        productList.add(new Product("hồng lá chuối", "Fashion", "A pink shoe with a banana leaf design.", 23.014, R.drawable.giay16, false,1,false));
        productList.add(new Product("cao gót", "Fashion", "A high-heeled shoe for special occasions.", 1245, R.drawable.giay17, true,1,false));
        productList.add(new Product("giày cao cổ", "Fashion", "A high-top shoe for added ankle support.", 120.23, R.drawable.giay18, false,1,false));
        productList.add(new Product("giày chúa hề lmao", "Fashion", "A clown shoe for fun and entertainment.", 25.124, R.drawable.giay19, true,1,false));
        productList.add(new Product("giày trẻ em", "children", "A children's shoe for everyday wear.", 124.25, R.drawable.giay20, false,1,false));
        // Khởi tạo danh sách sản phẩm sau khi lọc (ban đầu là danh sách trống)
        filteredList = new ArrayList<>();

        // Khởi tạo adapter và RecyclerView
        productAdapter = new ProductAdapter(this, filteredList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(productAdapter);

        // Thiết lập chức năng tìm kiếm
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    // Phương thức lọc sản phẩm theo từ khóa tìm kiếm
    private void filter(String query) {
        filteredList.clear();
        if (!query.isEmpty()) {
            for (Product product : productList) {
                if (product.getName().toLowerCase().contains(query.toLowerCase()) ||
                        product.getDescription().toLowerCase().contains(query.toLowerCase())) {
                    filteredList.add(product);
                }
            }
        }
        productAdapter.updateData(filteredList); // Cập nhật dữ liệu trong adapter
    }
}