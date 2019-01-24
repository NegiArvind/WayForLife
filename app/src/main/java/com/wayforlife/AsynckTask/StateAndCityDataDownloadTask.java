package com.wayforlife.AsynckTask;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class StateAndCityDataDownloadTask extends AsyncTask<String, String, String> {

    public static HashMap<String,ArrayList<String>> stateAndCityHashMap=new HashMap<>();
    public static ArrayList<String> stateArrayList=new ArrayList<>();

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... strings) {

        String current = "";
        try {
            URL url;
            HttpURLConnection httpURLConnection = null;
            try{
                url = new URL(strings[0]);
                httpURLConnection = (HttpURLConnection) url.openConnection();

                InputStream inputStream= httpURLConnection.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);

                int data = inputStreamReader.read();
                while (data != -1) {
                    current += (char) data;
                    data = inputStreamReader.read();
                    Log.i("current data.. ",current);
                }

                // return the data to onPostExecute method
                return current;

            }catch (Exception e) {
                e.printStackTrace();
            }finally{
                if(httpURLConnection != null) {
                    httpURLConnection.disconnect();
                }
            }
        }catch (Exception e) {
            e.printStackTrace();
            return "Exception: " + e.getMessage();
        }
        return current;
    }


    @Override
    protected void onPostExecute(String data) {
//        super.onPostExecute(s);

        Log.d("data", data);
        // dismiss the progress dialog after receiving data from API

        try {

            // JSON Parsing of data
            JSONObject stateJsonObject=new JSONObject(data);
            JSONArray jsonArray = stateJsonObject.getJSONArray("states");

            for(int i=0;i<jsonArray.length();i++){
                JSONObject jsonObject=jsonArray.getJSONObject(i);
                JSONArray districtJsonArray=jsonObject.getJSONArray("districts");
                ArrayList<String> districtArrayList=new ArrayList<>();
                for(i=0;i<districtJsonArray.length();i++){
                    districtArrayList.add(districtJsonArray.get(i).toString());
                }
                stateArrayList.add(jsonObject.getString("state"));
                stateAndCityHashMap.put(jsonObject.getString("state"),districtArrayList);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
