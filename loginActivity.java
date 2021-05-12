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
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;


import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

public class loginActivity extends Activity {

    EditText edt_id, edt_pw;
    String userID, userPassword;
    AlertDialog dialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        ImageButton btn_back = (ImageButton) findViewById(R.id.btn_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        edt_id = (EditText) findViewById(R.id.edt_id);
        edt_pw = (EditText) findViewById(R.id.edt_pw);

        LinearLayout ly_signup = (LinearLayout) findViewById(R.id.ly_btnsignup);
        ly_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
                startActivity(intent);
            }
        });
    }

    public void btn_login(View v) {

        try{
            userID = edt_id.getText().toString();
            userPassword = edt_pw.getText().toString();
        }catch (NullPointerException e)
        {
            Log.e("err",e.getMessage());
        }

        if(edt_id.getText().toString().equals("") || edt_pw.getText().toString().equals("")) {
            Toast.makeText(getApplicationContext(), "아이디와 비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show();
        }
        else {
            loginDB lDB = new loginDB();
            lDB.execute();
        }
    }

    public class loginDB extends AsyncTask<Void, Integer, Void> {

        String data = "";

        @Override
        protected Void doInBackground(Void... unused) {

            /* 인풋 파라메터값 생성 */
            String param = "u_id=" + userID + "&u_pw=" + userPassword + "";
            Log.e("POST",param);
            try {
                /* 서버연결 */
                URL url = new URL("http://hong123.dothome.co.kr/Login.php");
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

            /* 서버에서 응답 */
            Log.e("RECV DATA",data);

            if(data.equals("1")) // 로그인 성공
            {
                Toast.makeText(getApplicationContext(), "로그인 되었습니다.", Toast.LENGTH_SHORT).show();
                LogonDBquery query = new LogonDBquery();
                query.execute();
            }
            else if(data.equals("0"))
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(loginActivity.this);
                dialog = builder.setMessage("비밀번호가 일치하지 않습니다.").setPositiveButton("확인", null).create();
                dialog.show();
            }
            else if(data.equals("Can not find ID"))
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(loginActivity.this);
                dialog = builder.setMessage("ID가 일치하지 않습니다.").setPositiveButton("확인", null).create();
                dialog.show();
            }
        }
    }

    private class LogonDBquery extends AsyncTask<String,Void,String> {

        @Override
        protected String doInBackground(String... arg0) {

            try {
                String id = userID;

                String link = "http://hong123.dothome.co.kr/Logonquery.php?ID=" + id;
                URL url = new URL(link);
                HttpClient client = new DefaultHttpClient();
                HttpGet request = new HttpGet();
                request.setURI(new URI(link));
                HttpResponse response = client.execute(request);
                BufferedReader in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

                StringBuffer sb = new StringBuffer("");
                String line = "";

                while ((line = in.readLine()) != null) {
                    sb.append(line);
                    break;
                }

                in.close();
                return sb.toString();
            } catch (Exception e) {
                return new String("Exception: " + e.getMessage());
            }

        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Intent data = new Intent();
            data.putExtra("id", userID);
            data.putExtra("name", result);
            setResult(Activity.RESULT_OK, data);
            finish();
        }
    }
}