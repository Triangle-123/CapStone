package com.example.introtest;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
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
import java.util.ArrayList;
import java.util.HashMap;

public class FavoritesActivity extends Activity {

    private static final String TAG_JSON="webnautes";
    private static final String TAG_NAME = "pkName";
    private static final String TAG_ADDRESS ="pkAddr";

    ArrayList<HashMap<String, String>> mArrayList;
    ListAdapter adapter;
    ListView fvlistView;
    String mJsonString, id, userName, pkname, pkaddr;
    AlertDialog dialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        fvlistView = (ListView) findViewById(R.id.listview_fv);
        mArrayList = new ArrayList<>();

        Intent intent = getIntent();
        id = intent.getStringExtra("userID");
        userName = intent.getStringExtra("userName");

        GetData task = new GetData();
        task.execute("http://hong123.dothome.co.kr/Favoritesquery.php?u_id=" + id);


        fvlistView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                HashMap<String, String> takemap = (HashMap<String, String>)mArrayList.get(position);
                pkname = takemap.get(TAG_NAME);
                pkaddr = takemap.get(TAG_ADDRESS);

                Intent intent = new Intent(getApplicationContext(), PkInfoActivity.class);
                intent.putExtra("itemName", pkname);
                intent.putExtra("addr", pkaddr);
                intent.putExtra("userID", id);
                intent.putExtra("userName", userName);
                startActivityForResult(intent, 0);
            }
        });

        fvlistView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int position, long l) {
                HashMap<String, String> takemap = (HashMap<String, String>)mArrayList.get(position);
                pkname = takemap.get(TAG_NAME);
                pkaddr = takemap.get(TAG_ADDRESS);
                AlertDialog.Builder builder = new AlertDialog.Builder(FavoritesActivity.this);
                builder.setMessage("주차장명 : " + pkname + "\n주소 : " + pkaddr + "\n\n이 즐겨찾기를 삭제하시겠습니까?");
                builder.setPositiveButton("아니오", null);
                builder.setNegativeButton("예", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        DeleteDB dldb = new DeleteDB();
                        dldb.execute(id, pkname, pkaddr);
                    }
                });
                dialog = builder.create();
                dialog.show();
                return false;
            }
        });

        ImageButton btn_back = (ImageButton) findViewById(R.id.btn_fvback);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
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
                String name = item.getString(TAG_NAME);
                String address = item.getString(TAG_ADDRESS);

                HashMap<String,String> hashMap = new HashMap<>();

                hashMap.put(TAG_NAME, name);
                hashMap.put(TAG_ADDRESS, address);

                mArrayList.add(hashMap);
            }

            adapter = new SimpleAdapter(
                    FavoritesActivity.this, mArrayList, R.layout.item_list,
                    new String[]{TAG_NAME, TAG_ADDRESS},
                    new int[]{ R.id.tv_list_pkname, R.id.tv_list_pkaddr}
            );

            fvlistView.setAdapter(adapter);

        } catch (JSONException e) {

            e.printStackTrace();
        }

    }


    public class DeleteDB extends AsyncTask<String, Void, String> {

        String data = "";

        @Override
        protected String doInBackground(String... params) {

            /* 인풋 파라메터값 생성 */
            String param = "u_id=" + params[0] + "&pk_name=" + params[1] + "&pk_addr=" + params[2] +
                    "";
            try {
                /* 서버연결 */
                URL url = new URL("http://hong123.dothome.co.kr/Favoritesdelete.php");
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
                Toast.makeText(getApplicationContext(), "즐겨찾기가 삭제되었습니다.", Toast.LENGTH_SHORT).show();
                Intent intent = getIntent();
                finish();
                startActivity(intent);
            }
        }
    }
}
