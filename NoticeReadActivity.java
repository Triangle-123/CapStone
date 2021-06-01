package com.example.introtest;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

public class NoticeReadActivity extends Activity {

    private static final String TAG_JSON="webnautes";
    private static final String TAG_NAME = "userName";
    private static final String TAG_TITLE = "nbTitle";
    private static final String TAG_ID = "userID";
    private static final String TAG_CONTENT = "nbContent";

    TextView tv_nbID, tv_nbtitle, tv_nbname, tv_nbtime, tv_nbcontent;
    String mJsonString, userID, nb_ID, nbTitle, nbName, nbTime, nbContent;
    ImageButton btn_edit, btn_remove;
    AlertDialog dialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notice_read);

        Intent intent = getIntent();
        userID = intent.getStringExtra("userID");
        nbTime = intent.getStringExtra("nbTime");

        tv_nbID = (TextView) findViewById(R.id.nb_id);
        tv_nbname = (TextView) findViewById(R.id.nb_name);
        tv_nbtime = (TextView) findViewById(R.id.nb_time);
        tv_nbtitle = (TextView) findViewById(R.id.nb_title);
        tv_nbcontent = (TextView) findViewById(R.id.nb_content);

        btn_edit = (ImageButton) findViewById(R.id.btn_edit);
        btn_remove = (ImageButton) findViewById(R.id.btn_remove);

        GetData task = new GetData();
        task.execute("http://hong123.dothome.co.kr/NoticeRead.php?nb_time=" + nbTime);

        btn_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), NoticeEditActivity.class);
                intent.putExtra("nb_ID", nb_ID);
                intent.putExtra("nb_Name", nbName);
                intent.putExtra("nb_Time", nbTime);
                intent.putExtra("nb_Title", nbTitle);
                intent.putExtra("nb_Content", nbContent);
                startActivityForResult(intent, 2);
            }
        });

        btn_remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(NoticeReadActivity.this);
                builder.setMessage("이 게시물을 삭제하시겠습니까?");
                builder.setPositiveButton("아니오", null);
                builder.setNegativeButton("예", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        DeleteDB dldb = new DeleteDB();
                        dldb.execute(nbTime);
                    }
                });
                dialog = builder.create();
                dialog.show();
            }
        });

        ImageButton btn_back = (ImageButton) findViewById(R.id.btn_nbback);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = getIntent();
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = getIntent();
        setResult(RESULT_OK, intent);
        finish();
    }

    private class GetData extends AsyncTask<String, Void, String> {
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            mJsonString = result;
            showResult();

        }

        @Override
        protected String doInBackground(String... params) {

            String serverURL = params[0];


            try {

                URL url = new URL(serverURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

                httpURLConnection.connect();


                int responseStatusCode = httpURLConnection.getResponseCode();

                InputStream inputStream;
                if(responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = httpURLConnection.getInputStream();
                }
                else{
                    inputStream = httpURLConnection.getErrorStream();
                }


                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line;

                while((line = bufferedReader.readLine()) != null){
                    sb.append(line);
                }


                bufferedReader.close();


                return sb.toString().trim();


            } catch (Exception e) {

                e.printStackTrace();
                return null;
            }

        }
    }


    private void showResult(){
        try {
            JSONObject jsonObject = new JSONObject(mJsonString);
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

            JSONObject item = jsonArray.getJSONObject(0);
            nb_ID = item.getString(TAG_ID);
            nbName = item.getString(TAG_NAME);
            nbTitle = item.getString(TAG_TITLE);
            nbContent = item.getString(TAG_CONTENT);

            StringUtils stringUtils = new StringUtils();
            tv_nbID.setText(nb_ID.substring(0,3) + stringUtils.repeat("*", nb_ID.substring(3).length()));
            tv_nbname.setText(nbName);
            tv_nbtime.setText(nbTime);
            tv_nbtitle.setText(nbTitle);
            tv_nbcontent.setText(nbContent);

            if (userID.equals(nb_ID)) {
                btn_edit.setVisibility(View.VISIBLE);
                btn_remove.setVisibility(View.VISIBLE);
            }


        } catch (JSONException e) {

            e.printStackTrace();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 2) {
            if(resultCode == RESULT_OK) {
                Intent intent = getIntent();
                finish();
                startActivity(intent);
            }
        }
    }

    public class DeleteDB extends AsyncTask<String, Void, String> {

        String data = "";

        @Override
        protected String doInBackground(String... params) {

            /* 인풋 파라메터값 생성 */
            String param = "nb_time=" + params[0] + "";
            try {
                /* 서버연결 */
                URL url = new URL("http://hong123.dothome.co.kr/NoticeDelete.php");
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
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            Log.e("RECV DATA",data);
            if(data.equals("delete success")) {
                Toast.makeText(getApplicationContext(), "게시물이 삭제되었습니다.", Toast.LENGTH_SHORT).show();
                Intent intent = getIntent();
                setResult(RESULT_OK ,intent);
                finish();
            }
        }
    }
}
