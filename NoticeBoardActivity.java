package com.example.introtest;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import androidx.annotation.Nullable;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class NoticeBoardActivity extends Activity {

    private static final String TAG_JSON="webnautes";
    private static final String TAG_NAME = "userName";
    private static final String TAG_TITLE = "nbTitle";
    private static final String TAG_TIME = "nbTime";

    ArrayList<HashMap<String, String>> mArrayList;
    ListAdapter adapter;
    ListView nblistView;
    FloatingActionButton fab;

    String mJsonString, id, userName, nbName, nbtitle, nbtime;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notice_board);

        nblistView = (ListView) findViewById(R.id.listview_nb);
        fab = (FloatingActionButton) findViewById(R.id.fab_write);

        mArrayList = new ArrayList<>();

        Intent intent = getIntent();
        id = intent.getStringExtra("userID");
        userName = intent.getStringExtra("userName");

        GetData task = new GetData();
        task.execute("http://hong123.dothome.co.kr/NoticeBoard.php");


        nblistView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                HashMap<String, String> takemap = (HashMap<String, String>)mArrayList.get(position);
                nbName = takemap.get(TAG_NAME);
                nbtitle = takemap.get(TAG_TITLE);
                nbtime = takemap.get(TAG_TIME);

                Intent intent = new Intent(getApplicationContext(), NoticeReadActivity.class);
                intent.putExtra("userID", id);
                intent.putExtra("nbTime", nbtime);
                startActivityForResult(intent, 1);
            }
        });


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), NoticeWriteActivity.class);
                intent.putExtra("userID", id);
                intent.putExtra("userName", userName);
                startActivityForResult(intent, 0);
            }
        });
        ImageButton btn_back = (ImageButton) findViewById(R.id.btn_nbback);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 0) {
            if(resultCode == RESULT_OK) {
                Intent intent = getIntent();
                finish();
                startActivity(intent);
            }
        }

        if(requestCode == 1) {
            if(resultCode == RESULT_OK) {
                Intent intent = getIntent();
                finish();
                startActivity(intent);
            }
        }
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

            for(int i=0;i<jsonArray.length();i++){

                JSONObject item = jsonArray.getJSONObject(i);
                String title = item.getString(TAG_TITLE);
                String name = item.getString(TAG_NAME);
                String time = item.getString(TAG_TIME);

                HashMap<String,String> hashMap = new HashMap<>();

                hashMap.put(TAG_TITLE, title);
                hashMap.put(TAG_NAME, name);
                hashMap.put(TAG_TIME, time);

                mArrayList.add(hashMap);
            }

            adapter = new SimpleAdapter(
                    NoticeBoardActivity.this, mArrayList, R.layout.item_list_nb,
                    new String[]{TAG_TITLE, TAG_NAME, TAG_TIME},
                    new int[]{ R.id.tv_list_nbtitle, R.id.tv_list_nbname, R.id.tv_list_nbtime}
            );

            nblistView.setAdapter(adapter);

        } catch (JSONException e) {

            e.printStackTrace();
        }

    }
}
