package app.crud.com.calllogtracker;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.provider.CallLog;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ShowCallLog extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_call_log);

        if (ContextCompat.checkSelfPermission(ShowCallLog.this,
                Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(ShowCallLog.this, Manifest.permission.READ_CALL_LOG)) {
                ActivityCompat.requestPermissions(ShowCallLog.this, new String[]{Manifest.permission.READ_CALL_LOG}, 1);
            } else {
                ActivityCompat.requestPermissions(ShowCallLog.this, new String[]{Manifest.permission.READ_CALL_LOG}, 1);
            }
        } else {

            TextView textView = (TextView) findViewById(R.id.textView);
            textView.setText(getCallDetails());
        }
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(ShowCallLog.this,
                            Manifest.permission.READ_CALL_LOG) == PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(this, "Permision granted", Toast.LENGTH_LONG).show();
                        TextView textView = (TextView) findViewById(R.id.textView);
                        textView.setText(getCallDetails());
                    }
                } else {
                    Toast.makeText(this, "Permision denied", Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }

    private String getCallDetails() {
        StringBuffer logBuffer = new StringBuffer();
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
        Cursor managedCursor = getContentResolver().query(CallLog.Calls.CONTENT_URI, null, null, null, null);
        int number = managedCursor.getColumnIndex(CallLog.Calls.NUMBER);
        int type = managedCursor.getColumnIndex(CallLog.Calls.TYPE);
        int date = managedCursor.getColumnIndex(CallLog.Calls.DATE);
        int duration = managedCursor.getColumnIndex(CallLog.Calls.DURATION);
        logBuffer.append("Call Details: \n\n");
        while (managedCursor.moveToNext()){
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
            logBuffer.append("\nPhone Number : "+phnNumber+" \nCall Type : "+dir+"\nCall Date : "+dateString+
                    "\nCall Duration: "+callDuration);
            logBuffer.append("\n---------------------------------");
        }
        managedCursor.close();
        return logBuffer.toString();
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
}
