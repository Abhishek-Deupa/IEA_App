package com.example.ieaapp;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.cardview.widget.CardView;

public class explore_iea extends AppCompatActivity {

    CardView exploreIeaContactUsCardView, exploreIeaHelpCv;
    Dialog exploreIeaContactDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explore_iea);

        exploreIeaContactUsCardView = findViewById(R.id.explore_iea_contact_us_cardView);
        exploreIeaHelpCv = findViewById(R.id.explore_iea_help_cv);
        exploreIeaContactDialog = new Dialog(this);

        AppCompatButton exploreback_btn = findViewById(R.id.explore_back_button);
        exploreback_btn.setOnClickListener(view -> {
            finish();
        });

        exploreIeaContactUsCardView.setOnClickListener(view -> {
            LayoutInflater inflater = getLayoutInflater();
            View exploreUsView = inflater.inflate(R.layout.support_contact_popup, null);

            exploreIeaContactDialog.setContentView(exploreUsView);
            exploreIeaContactDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            exploreIeaContactDialog.show();
        });
        exploreIeaHelpCv.setOnClickListener(view -> openWhatsAppConvo());
    }

    private void openWhatsAppConvo() {
        Uri uri = Uri.parse("https://wa.me/919145114666?text=Hello,%20I%20have%20a%20query.");
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }
}