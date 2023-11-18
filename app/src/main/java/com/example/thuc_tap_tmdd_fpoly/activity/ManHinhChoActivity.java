package com.example.thuc_tap_tmdd_fpoly.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.example.thuc_tap_tmdd_fpoly.MainActivity;
import com.example.thuc_tap_tmdd_fpoly.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class ManHinhChoActivity extends AppCompatActivity {
    private FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_man_hinh_cho);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                nextActivity();
            }
        }, 2000);
    }
    private void nextActivity() {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser == null){
            startActivity(new Intent(ManHinhChoActivity.this, LoginActivity.class));
        } else {
            startActivity(new Intent(ManHinhChoActivity.this, MainActivity.class));
        }
    }
}