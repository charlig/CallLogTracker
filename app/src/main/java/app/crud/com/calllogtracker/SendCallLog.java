package app.crud.com.calllogtracker;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.CallLog;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;


public class SendCallLog extends AppCompatActivity {
    private TextView mResult;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_call_log);

        mResult = (TextView) findViewById(R.id.tv_result);
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();

//        make get request
//        new GetDataTask().execute("http://192.168.1.100:1000/api/status");
//        make post request
//        new PostDataTask().execute("http://192.168.1.100:1000/api/status");
//        make put request
//        new PutDataTask().execute("http://192.168.1.100:1000/api/status/5929138d52d866176407c354");
//        new PutDataTask().execute("http://192.168.1.100:1000/api/status/");
//        make delete request
//        new DeleteDataTask().execute("http://192.168.1.100:1000/api/status/5929138d52d866176407c354");


        if (ContextCompat.checkSelfPermission(SendCallLog.this,
                Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(SendCallLog.this, Manifest.permission.READ_CALL_LOG)) {
                ActivityCompat.requestPermissions(SendCallLog.this, new String[]{Manifest.permission.READ_CALL_LOG}, 1);
            } else {
                ActivityCompat.requestPermissions(SendCallLog.this, new String[]{Manifest.permission.READ_CALL_LOG}, 1);
            }
        } else {

//            TextView textView = (TextView) findViewById(R.id.textView);
//            textView.setText(getCallDetails());
//            new GetDataTask().execute("http://192.168.1.100:1000/log/mobile");
            new DeleteDataTask().execute("http://192.168.1.100:1000/log/mobile");
        }




    }

//    calllog
public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    switch (requestCode) {
        case 1: {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ContextCompat.checkSelfPermission(SendCallLog.this,
                        Manifest.permission.READ_CALL_LOG) == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Permision granted", Toast.LENGTH_LONG).show();
//                    TextView textView = (TextView) findViewById(R.id.textView);
//                    textView.setText(getCallDetails());
                }
            } else {
                Toast.makeText(this, "Permision denied", Toast.LENGTH_LONG).show();
            }
            return;
        }
    }
}

    private Set<Datamodel> getCallDetails() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return null;
        }
        Log.e("get call","------------get call logs-------------");
        Cursor managedCursor = getContentResolver().query(CallLog.Calls.CONTENT_URI, null, null, null, null);
        int number = managedCursor.getColumnIndex(CallLog.Calls.NUMBER);
        int type = managedCursor.getColumnIndex(CallLog.Calls.TYPE);
        int date = managedCursor.getColumnIndex(CallLog.Calls.DATE);
        int duration = managedCursor.getColumnIndex(CallLog.Calls.DURATION);
//        logBuffer.append("Call Details: \n\n");
        Set<Datamodel>  log = new HashSet<Datamodel>();
        while (managedCursor.moveToNext()){
            Datamodel model = new Datamodel();
            String phnNumber = managedCursor.getString(number);
            String callType = managedCursor.getString(type);
            String callDate = managedCursor.getString(date);
            String callDuration = convertTime(managedCursor.getString(duration));
            Date callDateTime = new Date(Long.valueOf(callDate));
            SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MM-yy HH:mm");
            String dateString = dateFormatter.format(callDateTime);

            String dir = null;
            int dircode = Integer.parseInt(callType);
            switch (dircode){
                case CallLog.Calls.OUTGOING_TYPE :
                    dir = "OUTGOING";
                    break;
                case CallLog.Calls.INCOMING_TYPE:
                    dir="INCOMMING";
                    break;
                case CallLog.Calls.MISSED_TYPE:
                    dir="MISSED";
                    break;
            }

            model.setPhone(phnNumber);
            model.setCallType(dir);
            model.setCallDate(dateString);
            model.setCallDuration(callDuration);
            log.add(model);

        }
        managedCursor.close();
        return log;
    }

    private String convertTime(String sduration){
        long duration = Long.parseLong(sduration);
        String hours="0";
        String minu="0";
        String second="0";
        if(duration>=3600 && duration!=0){
            hours=Long.toString(duration/3600);
            duration = duration%3600;
        }
        if(duration>=60&& duration!=0){
            minu=Long.toString(duration/60);
            duration = duration%60;
        }
        if(duration<60&& duration!=0){
            second=Long.toString(duration);
        }

        return hours+" hours "+minu+" min "+second+" secs";

    }

//    asyntask
    class GetDataTask extends AsyncTask<String, Void, String> {

        ProgressDialog progressDialog;
        Set<String> ids = new HashSet<String>();
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(SendCallLog.this);
            progressDialog.setMessage("Loading data......");
            progressDialog.show();
            Log.e("get Data","------------getdata pre-------------");

        }

        @Override
        protected String doInBackground(String... params) {
            try {
                return getData(params[0]);
            } catch (IOException e) {
                e.printStackTrace();
                return "Network error !";
            }
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Log.e("get Data","------------getdata post-------------");
            if(ids.size()>0) {
                for (String id : ids) {
                    new DeleteDataTask().execute("http://192.168.1.100:1000/log/mobile/" + id);
                }
            }else{
                new PostDataTask().execute("http://192.168.1.100:1000/log/mobile");
            }
//            set data response to textviwe
            mResult.setText(result);
//            cansel progress dialog
            if (progressDialog != null) {
                progressDialog.dismiss();
            }

        }

        private String getData(String urlPath) throws IOException {
            StringBuilder result = new StringBuilder();
            BufferedReader bufferedReader = null;

//            initialize the config request, then connect to server
            try {
                URL url = new URL(urlPath);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setReadTimeout(10000);
                urlConnection.setConnectTimeout(10000);
                urlConnection.setRequestMethod("GET");
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.connect();

//                read data response form server
                InputStream inputStream = urlConnection.getInputStream();
                bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = bufferedReader.readLine()) != null) {
//                    JSONObject object =  new JSONObject(line);
                    Log.e("line", "line"+line+" -------------------");
//                    result.append(object.getString("_id")).append("\n");
//                    result.append(line).append("\n");
                    if(!line.equals("[]")) {
                        for (String obj : line.replace("[", "").replace("[", "").split("(\\}\\,)")) {
                            Log.e("obj", "delete id : " + obj + " -------------------");

                            JSONObject object = new JSONObject(obj.concat("}"));
//                        result.append(obj.concat("}")).append("\n");
                            result.append(object.getString("_id")).append("\n");
                            Log.e("delete", "delete id :" + object.getString("_id"));
                            ids.add(object.getString("_id"));

//                        new DeleteDataTask().execute("http://192.168.1.100:1000/log/mobile/"+object.getString("_id"));

                        }
                    }else{
                        return "table was empty";
                    }

                }

            } catch (IOException ex) {
                return "Network error !";
            }catch(Exception ex){
                ex.printStackTrace();
                return "object error";
            } finally {
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
            }
            return result.toString();
        }
    }

    class PostDataTask extends AsyncTask<String, Void, String> {

        ProgressDialog progressDialog;
        Set<Datamodel> datamodels = new HashSet<Datamodel>();



        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.e("post Data","------------postdata pre-------------");
            datamodels.addAll(getCallDetails());
            progressDialog = new ProgressDialog(SendCallLog.this);
            progressDialog.setMessage("Inserting data......");
            progressDialog.show();

        }

        @Override
        protected String doInBackground(String... params) {
            try {
                return postData(params[0]);
            } catch (IOException ex) {
                ex.printStackTrace();
                return "Network error";
            } catch (JSONException ex) {
                return "Data Invalid !";

            }
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            mResult.setText(result);
            if (progressDialog != null) {
                progressDialog.dismiss();
            }
        }

        private String postData(String urlPath) throws IOException, JSONException {
            BufferedReader bufferedReader = null;
            BufferedWriter bufferedWriter = null;
            //                create data to server

            StringBuilder result = new StringBuilder();
            try {

//            initialize and config request , then connect to server
                URL url = new URL(urlPath);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setReadTimeout(10000);
                urlConnection.setConnectTimeout(10000);
                urlConnection.setRequestMethod("POST");
                urlConnection.setDoOutput(true);
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.connect();
                OutputStream outputStream = urlConnection.getOutputStream();
                int count =0;
//            write data into server
                JSONArray array = new JSONArray();
                for(Datamodel model : datamodels) {
                    JSONObject dataToSend = new JSONObject();
                    Log.e("phone", model.getPhone());
                    Log.e("callType", model.getCallType());
                    Log.e("callDate",model.getCallDate());
                    Log.e("callDuration",model.getCallDuration());

                    dataToSend.put("phone", model.getPhone());
                    dataToSend.put("callType", model.getCallType());
                    dataToSend.put("callDate",model.getCallDate());
                    dataToSend.put("callDuration",model.getCallDuration());

                    Log.e("Object",dataToSend.toString());
                    array.put(dataToSend);

//                    if(count==0) {
//                        bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream));
//                        bufferedWriter.write(dataToSend.toString().trim());
//                    }else{
//                        bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream));
//                        bufferedWriter.write(","+dataToSend.toString());
//                    }
//                    bufferedWriter.flush();
//                    bufferedWriter.close();
//                    count++;
                }
                Log.e("--------Array---------",array.toString());
                bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream));
                bufferedWriter.write(array.toString());
                bufferedWriter.flush();

//          read data response from server
                InputStream inputStream = urlConnection.getInputStream();
                bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    result.append(line).append("\n");
                }
            } catch (IOException ex) {
                return "Network error !";
            } finally {
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
                if (bufferedWriter != null) {
                    bufferedWriter.close();
                }
            }
            return result.toString();

        }
    }

    class PutDataTask extends AsyncTask<String, Void, String> {

        ProgressDialog progressDialog;

        @Override
        protected void onPostExecute(String result) {

            Log.e("post Data","------------post data post-------------");
            mResult.setText(result);
            if (progressDialog != null) {
                progressDialog.dismiss();
            }
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                return putData(params[0]);
            } catch (IOException ex) {
                ex.printStackTrace();
                return "Network error";
            } catch (JSONException ex) {
                return "Data Invalid !";
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(SendCallLog.this);
            progressDialog.setMessage("Update data......");
            progressDialog.show();
        }

        private String putData(String urlPath) throws IOException, JSONException {
            BufferedWriter bufferedWriter = null;
            String result = null;
            try {
//            create data to update
                JSONObject dataToSend = new JSONObject();
                dataToSend.put("fbname", "Think twice code once ! Hi");
                dataToSend.put("content", "feel good - UPDATE");
                dataToSend.put("likes", 999);
                dataToSend.put("comments", 999);

//            initialize and config request , then connect to server
                URL url = new URL(urlPath);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setReadTimeout(10000);
                urlConnection.setConnectTimeout(10000);
                urlConnection.setRequestMethod("PUT");
                urlConnection.setDoOutput(true);
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.connect();

                //            write data into server
                OutputStream outputStream = urlConnection.getOutputStream();
                bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream));
                bufferedWriter.write(dataToSend.toString());
                bufferedWriter.flush();

//                check update successful or not
                if (urlConnection.getResponseCode() == 200) {
                    return "Update Successfully !";
                } else {
                    return "Update Fail !";
                }


            } catch (IOException ex) {
                return "Network error !";
            } finally {
                if (bufferedWriter != null) {
                    bufferedWriter.close();
                }
            }

        }
    }

    class DeleteDataTask extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(SendCallLog.this);
            progressDialog.setMessage("Update data......");
            progressDialog.show();
            Log.e("delete Data","------------gdeletedata pre-------------");
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                return deleteData(params[0]);
            } catch (IOException ex) {
                ex.printStackTrace();
                return "Network error";
            }
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Log.e("delete Data","------------deletedata post-------------");
            new PostDataTask().execute("http://192.168.1.100:1000/log/mobile");
            mResult.setText(result);
            if (progressDialog != null) {
                progressDialog.dismiss();
            }
        }

        private String deleteData(String urlPath) throws IOException {

            try {
                //            initialize and config request , then connect to server
                URL url = new URL(urlPath);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setReadTimeout(10000);
                urlConnection.setConnectTimeout(10000);
                urlConnection.setRequestMethod("DELETE");
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.connect();

                //                check update successful or not
                if (urlConnection.getResponseCode() == 204) {
                    Log.e("delete Data","------------delete data-------------");
                    return "Delete Successfully !";

                } else {
                    return "Delete Fail !";
                }
            }catch (IOException ex){
                return "Delete fail";

            }
        }
    }
}
