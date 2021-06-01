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

public class NoticeEditActivity extends Activity {

    String userID, userName, nb_time, nb_title, nb_content;
    EditText tv_nb_id, tv_nb_name, tv_nb_time, tv_nb_title, tv_nb_content;
    Button nb_back;
    AlertDialog dialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notice_edit);

        Intent intent = getIntent();
        userID = intent.getStringExtra("nb_ID");
        userName = intent.getStringExtra("nb_Name");
        nb_time = intent.getStringExtra("nb_Time");
        nb_title = intent.getStringExtra("nb_Title");
        nb_content = intent.getStringExtra("nb_Content");

        tv_nb_id = (EditText) findViewById(R.id.nb_id);
        tv_nb_name = (EditText) findViewById(R.id.nb_name);
        tv_nb_time = (EditText) findViewById(R.id.nb_time);
        tv_nb_title = (EditText) findViewById(R.id.nb_title);
        tv_nb_content = (EditText) findViewById(R.id.nb_content);

        StringUtils stringUtils = new StringUtils();
        tv_nb_id.setText(userID.substring(0,3) + stringUtils.repeat("*", userID.substring(3).length()));
        tv_nb_name.setText(userName);
        tv_nb_time.setText(nb_time);
        tv_nb_title.setText(nb_title);
        tv_nb_content.setText(nb_content);

        nb_back = (Button) findViewById(R.id.nb_back);
        nb_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(NoticeEditActivity.this);
                builder.setMessage("글 편집을 취소하시겠습니까?\n(편집한 내용은 저장되지 않습니다.)");
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

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(NoticeEditActivity.this);
        builder.setMessage("글 편집을 취소하시겠습니까?\n(편집한 내용은 저장되지 않습니다.)");
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

    public void btn_write(View v) {
        if(tv_nb_title.getText().toString().equals("") || tv_nb_title.getText().toString() == null) {
            Toast.makeText(getApplicationContext(), "제목을 입력해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }
        else if (tv_nb_content.getText().toString().equals("") || tv_nb_content.getText().toString() == null) {
            Toast.makeText(getApplicationContext(), "내용을 입력해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }
        else {
            UpdateDB udb = new UpdateDB();
            udb.execute();
        }
    }

    public class UpdateDB extends AsyncTask<Void, Integer, Void> {

        String data = "";

        @Override
        protected Void doInBackground(Void... unused) {

            /* 인풋 파라메터값 생성 */
            String param = "time=" + tv_nb_time.getText().toString() + "&title=" + tv_nb_title.getText().toString()
                    + "&content=" + tv_nb_content.getText().toString() + "";
            try {
                /* 서버연결 */
                URL url = new URL("http://hong123.dothome.co.kr/NoticeUpdate.php");
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
            if(data.equals("update success")) {
                Toast.makeText(getApplicationContext(), "게시글이 편집되었습니다.", Toast.LENGTH_SHORT).show();
                Intent intent = getIntent();
                setResult(RESULT_OK, intent);
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
