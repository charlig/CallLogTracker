package app.crud.com.calllogtracker;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    public Button callLog;
    public Button send;


    public void init(){
        callLog = (Button) findViewById(R.id.button_show);
        send = (Button) findViewById(R.id.button_send);
        callLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent showLog = new Intent(MainActivity.this,ShowCallLog.class);
                startActivity(showLog);
            }
        });
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this,SendCallLog.class));
            }
        });
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }
}
