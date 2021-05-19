package com.example.introtest;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import java.text.SimpleDateFormat;
import java.util.Date;

public class NoticeWriteActivity extends Activity {

    String userID, userName;
    EditText nb_id, nb_name, nb_time, nb_title, nb_content;
    Button nb_back;
    AlertDialog dialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notice_write);

        Intent intent = getIntent();
        userID = intent.getStringExtra("userID");
        userName = intent.getStringExtra("userName");

        nb_id = (EditText) findViewById(R.id.nb_id);
        nb_name = (EditText) findViewById(R.id.nb_name);
        nb_time = (EditText) findViewById(R.id.nb_time);
        nb_title = (EditText) findViewById(R.id.nb_title);
        nb_content = (EditText) findViewById(R.id.nb_content);

        StringUtils stringUtils = new StringUtils();
        nb_id.setText(userID.substring(0,3) + stringUtils.repeat("*", userID.substring(3).length()));
        nb_name.setText(userName);

        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String getTime = sdf.format(date);
        nb_time.setText(getTime);

        nb_back = (Button) findViewById(R.id.nb_back);
        nb_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(NoticeWriteActivity.this);
                builder.setMessage("글 작성을 취소하시겠습니까?");
                builder.setPositiveButton("아니오", null);
                builder.setNegativeButton("예", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                });
                dialog = builder.create();
                dialog.show();
            }
        });

    }


    public void btn_write(View v) {
        if(nb_title.getText().toString().equals("") || nb_title.getText().toString() == null) {
            Toast.makeText(getApplicationContext(), "제목을 입력해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }
        else if (nb_content.getText().toString().equals("") || nb_content.getText().toString() == null) {
            Toast.makeText(getApplicationContext(), "내용을 입력해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }
        else {
            WriteDB wdb = new WriteDB();
            wdb.execute();
        }
    }


    public class WriteDB extends AsyncTask<Void, Integer, Void> {

        String data = "";

        @Override
        protected Void doInBackground(Void... unused) {

            /* 인풋 파라메터값 생성 */
            String param = "u_id=" + userID + "&u_name=" + userName + "&time=" + nb_time.getText().toString() +
                    "&title=" + nb_title.getText().toString() + "&content=" + nb_content.getText().toString() + "";
            try {
                /* 서버연결 */
                URL url = new URL("http://hong123.dothome.co.kr/NoticeWrite.php");
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
                Toast.makeText(getApplicationContext(), "게시글이 작성되었습니다.", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    public class StringUtils{
        public String repeat(String val, int count){
            StringBuilder buf = new StringBuilder(val.length() * count);
            while (count-- > 0) {
                buf.append(val);
            }
            return buf.toString();
        }
    }
}
