package com.example.introtest;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class PkInfoActivity extends Activity {

    String pkname, pkaddr, userID, userName;
    AlertDialog dialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parking_info);
        TextView tv_paking = (TextView) findViewById(R.id.parking_Lot);
        TextView tv_available = (TextView) findViewById(R.id.available_Lot);
        TextView tv_using = (TextView) findViewById(R.id.using_Lot);
        TextView tv_pkname = (TextView) findViewById(R.id.tv_pkname);
        TextView tv_pkaddr = (TextView) findViewById(R.id.tv_pkaddr);

        Intent intent = getIntent();
        pkname = intent.getStringExtra("itemName");
        pkaddr = intent.getStringExtra("addr");
        Bundle extras = intent.getExtras();
        if(extras != null) {
            userID = extras.getString("userID");
            userName = extras.getString("userName");
        }
        tv_pkname.setText(pkname);
        tv_pkaddr.setText(pkaddr);

        SpannableStringBuilder ssb1 = new SpannableStringBuilder(tv_paking.getText());
        ssb1.setSpan(new ForegroundColorSpan(Color.parseColor("#0EF116")), 0, 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        tv_paking.setText(ssb1);

        SpannableStringBuilder ssb2 = new SpannableStringBuilder(tv_available.getText());
        ssb2.setSpan(new ForegroundColorSpan(Color.parseColor("#0027FF")), 0, 5, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        tv_available.setText(ssb2);

        SpannableStringBuilder ssb3 = new SpannableStringBuilder(tv_using.getText());
        ssb3.setSpan(new ForegroundColorSpan(Color.parseColor("#FF0F00")), 0, 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        tv_using.setText(ssb3);

        ImageButton btn_back = (ImageButton) findViewById(R.id.btn_map_back2);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    public void tv_favorite(View view) {
        if(userID == null) {
            Intent intent = new Intent(getApplicationContext(), loginActivity.class);
            startActivityForResult(intent, 0);
        }
        else {
            FvalidateDB fdb = new FvalidateDB();
            fdb.execute();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            if (resultCode != Activity.RESULT_OK) {
                return;
            }
            userID = data.getStringExtra("id");
            userName = data.getStringExtra("name");

            Intent intent = new Intent();
            intent.putExtra("id", userID);
            intent.putExtra("name", userName);
            setResult(Activity.RESULT_OK, intent);
        }
    }

    public class FregistDB extends AsyncTask<Void, Integer, Void> {

        String data = "";

        @Override
        protected Void doInBackground(Void... unused) {

            /* 인풋 파라메터값 생성 */
            String param = "u_id=" + userID + "&pk_name=" + pkname + "&pk_addr=" + pkaddr + "";
            try {
                /* 서버연결 */
                URL url = new URL("http://hong123.dothome.co.kr/Favorites.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.connect();

                /* 안드로이드 -> 서버 파라메터값 전달 */
                OutputStream outs = conn.getOutputStream();
                outs.write(param.getBytes("UTF-8"));
                outs.flush();
                outs.close();

                /* 서버 -> 안드로이드 파라메터값 전달 */
                InputStream is = null;
                BufferedReader in = null;

                is = conn.getInputStream();
                in = new BufferedReader(new InputStreamReader(is), 8 * 1024);
                String line = null;
                StringBuffer buff = new StringBuffer();
                while ( ( line = in.readLine() ) != null )
                {
                    buff.append(line + "\n");
                }
                data = buff.toString().trim();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            Log.e("RECV DATA",data);
            if(data.equals("insert success")) {
                AlertDialog.Builder builder = new AlertDialog.Builder(PkInfoActivity.this);
                dialog = builder.setMessage("즐겨찾기에 등록되었습니다.").setPositiveButton("확인", null).create();
                dialog.show();
            }
        }
    }


    public class FvalidateDB extends AsyncTask<Void, Integer, Void> {

        String data = "";

        @Override
        protected Void doInBackground(Void... unused) {

            /* 인풋 파라메터값 생성 */
            String param = "u_id=" + userID + "&pk_name=" + pkname + "";
            Log.e("POST",param);
            try {
                /* 서버연결 */
                URL url = new URL("http://hong123.dothome.co.kr/FavoritesValidate.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.connect();

                /* 안드로이드 -> 서버 파라메터값 전달 */
                OutputStream outs = conn.getOutputStream();
                outs.write(param.getBytes("UTF-8"));
                outs.flush();
                outs.close();

                /* 서버 -> 안드로이드 파라메터값 전달 */
                InputStream is = null;
                BufferedReader in = null;

                is = conn.getInputStream();
                in = new BufferedReader(new InputStreamReader(is), 8 * 1024);
                String line = null;
                StringBuffer buff = new StringBuffer();
                while ( ( line = in.readLine() ) != null )
                {
                    buff.append(line + "\n");
                }
                data = buff.toString().trim();

                /* 서버에서 응답 */
                Log.e("RECV DATA",data);

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            Log.e("RECV DATA", data);

            if (data.equals("available")) {
                FregistDB fregistDB = new FregistDB();
                fregistDB.execute();
            }
            else if (data.equals("unavailable")){  // 사용할 수 없는 ID라면
                AlertDialog.Builder builder = new AlertDialog.Builder(PkInfoActivity.this);
                dialog = builder.setMessage("이미 즐겨찾기에 등록되어 있습니다.").setPositiveButton("확인", null).create();
                dialog.show();
            }

        }
    }
}
