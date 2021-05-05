package com.example.introtest;

import android.app.AlertDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class SignupActivity extends AppCompatActivity {

    private ArrayAdapter<String> adapter;
    private Spinner spinner;
    EditText edt_id, edt_pw, edt_name, edt_birth, edt_tel;
    String userID, userPassword, userName, userBirthdate, userGender, userPhone;
    AlertDialog dialog;
    boolean validate = false;
    Button btn_vd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup);


        ImageButton btn_back2 = (ImageButton) findViewById(R.id.btn_back2);
        btn_back2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        String[] sex = {"성별", "남자", "여자"};

        spinner = (Spinner) findViewById(R.id.spinner_sex);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, sex);
        spinner.setAdapter(adapter);

        edt_id = (EditText) findViewById(R.id.sign_edt_id);
        edt_pw = (EditText) findViewById(R.id.sign_edt_pw);
        edt_name = (EditText) findViewById(R.id.sign_edt_name);
        edt_birth = (EditText) findViewById(R.id.sign_edt_birth);
        edt_tel = (EditText) findViewById(R.id.sign_edt_tel);
        btn_vd = (Button) findViewById(R.id.btn_vd);

    }

    public void btn_signup(View view) {
        userID = edt_id.getText().toString();
        userPassword = edt_pw.getText().toString();
        userName = edt_name.getText().toString();
        userBirthdate = edt_birth.getText().toString();
        userPhone = edt_tel.getText().toString();
        userGender = spinner.getSelectedItem().toString();

        if (!validate) {
            AlertDialog.Builder builder = new AlertDialog.Builder(SignupActivity.this);
            dialog = builder.setMessage("ID 중복확인을 해주세요.").setPositiveButton("확인", null).create();
            dialog.show();
            return;
        }

        if(userID.equals("") || userPassword.equals("") || userName.equals("") || userBirthdate.equals("")
                || userPhone.equals("")) {
            Toast.makeText(getApplicationContext(), "정보를 모두 입력해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }
        else if (userGender.equals("성별")) {
            Toast.makeText(getApplicationContext(), "성별을 선택해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }
        else {
            registDB rdb = new registDB();
            rdb.execute();
        }
    }

    public void btn_validate(View v)
    {
        try{
            userID = edt_id.getText().toString();
        }catch (NullPointerException e)
        {
            Log.e("err",e.getMessage());
        }

        if(userID.equals("")) {
            AlertDialog.Builder builder = new AlertDialog.Builder(SignupActivity.this);
            dialog = builder.setMessage("ID를 입력하십시오.").setPositiveButton("확인", null).create();
            dialog.show();
            return;
        }
        else {
            validateDB vDB = new validateDB();
            vDB.execute();
        }
    }


    public class registDB extends AsyncTask<Void, Integer, Void> {

        String data = "";

        @Override
        protected Void doInBackground(Void... unused) {

            /* 인풋 파라메터값 생성 */
            String param = "u_id=" + userID + "&u_pw=" + userPassword + "&u_name=" +
                            userName + "&u_bd=" + userBirthdate + "&u_gd=" + userGender + "&u_tel=" + userPhone;
            try {
                /* 서버연결 */
                URL url = new URL("http://hong123.dothome.co.kr/Regist.php");
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
                Toast.makeText(getApplicationContext(), "회원가입이 완료되었습니다.", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }


    public class validateDB extends AsyncTask<Void, Integer, Void> {

        String data = "";

        @Override
        protected Void doInBackground(Void... unused) {

            /* 인풋 파라메터값 생성 */
            String param = "u_id=" + userID + "";
            Log.e("POST",param);
            try {
                /* 서버연결 */
                URL url = new URL("http://hong123.dothome.co.kr/Validate.php");
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
                AlertDialog.Builder builder = new AlertDialog.Builder(SignupActivity.this);
                dialog = builder.setMessage("사용 가능한 ID입니다.").setPositiveButton("확인", null).create();
                dialog.show();
                edt_id.setEnabled(false); // 아이디값 변경불가
                validate = true; // 검증완료
                edt_id.setBackgroundColor(getResources().getColor(R.color.colorGray));
                btn_vd.setBackgroundColor(getResources().getColor(R.color.colorGray));
                btn_vd.setText("확인됨");
            }
            else if (data.equals("unavailable")){  // 사용할 수 없는 ID라면
                AlertDialog.Builder builder = new AlertDialog.Builder(SignupActivity.this);
                dialog = builder.setMessage("이미 사용중인 ID입니다.").setPositiveButton("확인", null).create();
                dialog.show();
            }

        }
    }
}





