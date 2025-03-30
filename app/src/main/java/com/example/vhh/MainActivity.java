package com.example.vhh;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdOptions;
import com.google.android.gms.ads.nativead.NativeAdView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MainActivity extends AppCompatActivity implements ProductAdapter.ItemClickListener {
    private RecyclerView recyclerView;
    private ProductAdapter adapter;
    private List<Product> productList;
    private List<Product> filteredProductList; // List to hold filtered products
    private AdView adView;
    private InterstitialAd interstitialAd;
    private NativeAd nativeAd;
    private Spinner categorySpinner;
    private CheckBox availabilityCheckbox;
    private EditText discountCodeEditText; // Trường nhập mã giảm giá
    private Button btnSaveDiscountCode; // Nút lưu mã giảm giá

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set up the toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Initialize AdMob
        MobileAds.initialize(this, initializationStatus -> {});

        // Set up the AdView
        adView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

        // Load Interstitial Ad
        InterstitialAd.load(this, "ca-app-pub-3940256099942544/1033173712", adRequest, new InterstitialAdLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull InterstitialAd ad) {
                interstitialAd = ad;
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError adError) {
                interstitialAd = null;
            }
        });

        // Load Native Ad
        AdLoader adLoader = new AdLoader.Builder(this, "ca-app-pub-3940256099942544/2247696110")
                .forNativeAd(nativeAd -> {
                    // Show the ad.
                    if (this.nativeAd != null) {
                        this.nativeAd.destroy();
                    }
                    this.nativeAd = nativeAd;
                    NativeAdView adView = (NativeAdView) getLayoutInflater().inflate(R.layout.native_ad_layout, null);
                    populateNativeAdView(nativeAd, adView);
                    // Ensure that the ad is displayed in the correct location in your layout.
                    // Add the adView to your layout.
                })
                .withAdListener(new com.google.android.gms.ads.AdListener() {
                    @Override
                    public void onAdFailedToLoad(LoadAdError adError) {
                        // Handle the failure by logging, altering the UI, and so on.
                    }
                })
                .withNativeAdOptions(new NativeAdOptions.Builder()
                        // Customize the options if needed.
                        .build())
                .build();
        adLoader.loadAd(new AdRequest.Builder().build());

        // Initialize product list
        productList = new ArrayList<>();
        filteredProductList = new ArrayList<>();
        initProductList();
        filteredProductList.addAll(productList);

        // Set up the RecyclerView
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2)); // 2 columns in grid
        adapter = new ProductAdapter(this, filteredProductList);
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);

        // Set up the filter section
        categorySpinner = findViewById(R.id.category_spinner);
        availabilityCheckbox = findViewById(R.id.availability_checkbox);
        discountCodeEditText = findViewById(R.id.discountCodeEditText); // Tìm trường nhập mã giảm giá
        btnSaveDiscountCode = findViewById(R.id.btnSaveDiscountCode); // Tìm nút lưu mã giảm giá

        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                applyFilters();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        availabilityCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> applyFilters());

        // Lắng nghe sự kiện khi nhấn nút "Lưu mã giảm giá"
        btnSaveDiscountCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String discountCode = discountCodeEditText.getText().toString().trim();
                if (!discountCode.isEmpty()) {
                    saveDiscountCode(discountCode);
                    Toast.makeText(MainActivity.this, "Mã giảm giá đã được lưu", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "Vui lòng nhập mã giảm giá", Toast.LENGTH_SHORT).show();
                }
            }
        });
        BottomNavigationView bottomNavigationView = findViewById(R.id.navMenu);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener(){
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item){
                int itemId = item.getItemId();
                if(itemId == R.id.mnHome){
                    return true;
                }
                else if (itemId == R.id.mnSearch){
                    startActivity(new Intent(MainActivity.this, SearchActivity.class));
                }
                else if (itemId == R.id.mnFavorite){
                    startActivity(new Intent(MainActivity.this, FavoriteActivity.class));
                }
                else if (itemId == R.id.mnBag){
                    startActivity(new Intent(MainActivity.this, CartActivity.class));
                }
                else if (itemId == R.id.mnProfile){
                    startActivity(new Intent(MainActivity.this, ProfileActivity.class));
                }
                return false;
            }
        });
    }

    private void applyFilters() {
        String selectedCategory = categorySpinner.getSelectedItem().toString();
        boolean availableOnly = availabilityCheckbox.isChecked();

        filteredProductList.clear();

        // Filter products based on selected category and availability
        filteredProductList.addAll(productList.stream()
                .filter(product -> (selectedCategory.equals("All") || product.getCategory().equals(selectedCategory))
                        && (!availableOnly || product.isAvailable()))
                .collect(Collectors.toList()));

        adapter.notifyDataSetChanged();
    }

    private void initProductList() {
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
    }

    private void saveDiscountCode(String discountCode) {
        SharedPreferences sharedPreferences = getSharedPreferences("DiscountPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("discount_code", discountCode);
        editor.apply();
    }

    private void showInterstitialAd() {
        if (interstitialAd != null) {
            interstitialAd.show(this);
        }
    }

    @Override
    public void onItemClick(View view, int position) {
        showInterstitialAd();

        Product product = adapter.getItem(position);
        Toast.makeText(this, "You clicked " + product.getName(), Toast.LENGTH_SHORT).show();

        // Tạo Intent để chuyển sang ProductDetailActivity
        Intent intent = new Intent(this, ProductDetailActivity.class);
        intent.putExtra("product_name", product.getName());
        intent.putExtra("product_description", product.getDescription()); // Truyền mô tả sản phẩm
        intent.putExtra("product_image_res_id", product.getImageResId()); // Truyền id ảnh sản phẩm
        intent.putExtra("product_price", product.getPrice());
        startActivity(intent);
    }

    private void populateNativeAdView(NativeAd nativeAd, NativeAdView adView) {
        // Set the media view.
        // adView.setMediaView(adView.findViewById(R.id.ad_media));

        // Set other ad assets.
        adView.setHeadlineView(adView.findViewById(R.id.native_ad_headline));
        adView.setBodyView(adView.findViewById(R.id.native_ad_body));
        adView.setCallToActionView(adView.findViewById(R.id.native_ad_call_to_action));
        adView.setIconView(adView.findViewById(R.id.native_ad_icon));

        // The headline is guaranteed to be in every NativeAd.
        ((TextView) adView.getHeadlineView()).setText(nativeAd.getHeadline());

        // These assets aren't guaranteed to be in every NativeAd, so it's important to
        // check before trying to display them.
        if (nativeAd.getBody() == null) {
            adView.getBodyView().setVisibility(View.INVISIBLE);
        } else {
            adView.getBodyView().setVisibility(View.VISIBLE);
            ((TextView) adView.getBodyView()).setText(nativeAd.getBody());
        }

        if (nativeAd.getCallToAction() == null) {
            adView.getCallToActionView().setVisibility(View.INVISIBLE);
        } else {
            adView.getCallToActionView().setVisibility(View.VISIBLE);
            ((Button) adView.getCallToActionView()).setText(nativeAd.getCallToAction());
        }

        if (nativeAd.getIcon() == null) {
            adView.getIconView().setVisibility(View.GONE);
        } else {
            ((ImageView) adView.getIconView()).setImageDrawable(nativeAd.getIcon().getDrawable());
            adView.getIconView().setVisibility(View.VISIBLE);
        }

        // Assign native ad object to the native view.
        adView.setNativeAd(nativeAd);
    }
}