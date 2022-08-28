package com.example.ieaapp;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.cardview.widget.CardView;

public class explore_us extends AppCompatActivity {

    TextView join_now;
    AppCompatButton exploreUsBackButton;
    CardView exploreUsContactUsCard, exploreUsOpenWhatsappCv;
    Dialog exploreUsContactDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explore_us);

        join_now = findViewById(R.id.join_now);

        join_now.setOnClickListener(view -> startActivity(new Intent(explore_us.this, Events.class)));

        exploreUsBackButton = findViewById(R.id.exploreus_back_button);
        exploreUsContactUsCard  = findViewById(R.id.explore_us_contact_us_card);
        exploreUsOpenWhatsappCv = findViewById(R.id.explore_us_open_whatsapp_cv);

        exploreUsContactDialog = new Dialog(this);


        exploreUsBackButton.setOnClickListener(view -> {
            finish();
        });

        exploreUsContactUsCard.setOnClickListener(view -> {
            LayoutInflater inflater = getLayoutInflater();
            View exploreUsView = inflater.inflate(R.layout.support_contact_popup, null);

            exploreUsContactDialog.setContentView(exploreUsView);
            exploreUsContactDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            exploreUsContactDialog.show();
        });

        exploreUsOpenWhatsappCv.setOnClickListener(view -> openWhatsAppConvo());

    }

    private void openWhatsAppConvo() {
        Uri uri = Uri.parse("https://wa.me/919145114666?text=Hello,%20I%20have%20a%20query.");
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }
}