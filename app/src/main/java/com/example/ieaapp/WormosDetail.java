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

public class WormosDetail extends AppCompatActivity {
    Dialog contactDialog;
    AppCompatButton wormosContactBtn,wormosBackBtn;
    TextView wormosProductDetailTitleTv, wormosProductDetailDescTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wormos_detail);

        wormosContactBtn = findViewById(R.id.Wormos_detail_contact_btn);
        wormosProductDetailTitleTv = findViewById(R.id.Wormos_detail_title_tv);
        wormosProductDetailDescTv = findViewById(R.id.Wormos_detail_desc_tv);
        wormosBackBtn = findViewById(R.id.Wormos_detail_back_btn);
        contactDialog = new Dialog(this);

        wormosBackBtn.setOnClickListener(view -> finish());
        wormosContactBtn.setOnClickListener(view -> {
            openWhatsAppConvo();
        });
    }

    private void openWhatsAppConvo() {
        Uri uri = Uri.parse("https://wa.me/918700684656?text=Hi%20there,%20I%20just%20got%20your%20contact%20from%20IEA.");
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }
}