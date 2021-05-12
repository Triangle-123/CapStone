package com.example.introtest;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.opencsv.CSVReader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class MainActivity extends AppCompatActivity {

    TextView tv_logon;
    Button btn_pk_info, btn_favorite, btn_login, btn_signup, btn_logout;
    String ID, Name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn_pk_info = (Button) findViewById(R.id.btn_pk_info);
        btn_favorite = (Button) findViewById(R.id.btn_favorite);
        btn_login = (Button) findViewById(R.id.btn_login);
        btn_signup = (Button) findViewById(R.id.btn_signup);
        btn_logout = (Button) findViewById(R.id.btn_logout);
        tv_logon = (TextView) findViewById(R.id.tv_logon);

        btn_pk_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), pkmapActivity.class);
                intent.putExtra("userID", ID);
                intent.putExtra("userName", Name);
                startActivityForResult(intent, 0);
            }
        });

        btn_favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ID == null) {
                    Intent intent = new Intent(getApplicationContext(), loginActivity.class);
                    startActivityForResult(intent, 0);
                }
                else {
                    Intent intent = new Intent(getApplicationContext(), FavoritesActivity.class);
                    intent.putExtra("userID", ID);
                    startActivity(intent);
                }
            }
        });

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), loginActivity.class);
                startActivityForResult(intent, 0);
            }
        });

        btn_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
                startActivity(intent);
            }
        });

        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ID = null;
                Name = null;
                tv_logon.setVisibility(View.GONE);
                btn_logout.setVisibility(View.GONE);
                btn_login.setVisibility(View.VISIBLE);
                btn_signup.setVisibility(View.VISIBLE);
                Toast.makeText(getApplicationContext(), "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            if (resultCode != Activity.RESULT_OK) {
                return;
            }
            ID = data.getStringExtra("id");
            Name = data.getStringExtra("name");
            tv_logon.setText(Name + "님, 환영합니다.");
            tv_logon.setVisibility(View.VISIBLE);
            btn_logout.setVisibility(View.VISIBLE);
            btn_login.setVisibility(View.GONE);
            btn_signup.setVisibility(View.GONE);
        }
    }
}
