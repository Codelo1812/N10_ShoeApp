package com.example.vhh;

import android.os.Bundle;
import android.widget.ImageButton;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class EventActivity extends AppCompatActivity {
    ImageButton imbBackevent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_event);
        imbBackevent = findViewById(R.id.imbBackevent);
        imbBackevent.setOnClickListener(v -> {
            finish();
        });
    }
}