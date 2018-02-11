package liamkengineering.hackharvard;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

public class MainActivity extends AppCompatActivity {

    private static final boolean USING_SPLASHSCREEN = false;

    Runnable run = new Runnable() {
        @Override
        public void run() {
            Intent i = new Intent(MainActivity.this, Display.class);
            startActivity(i);

        }
    };

    Handler h = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String token = FirebaseInstanceId.getInstance().getToken();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("reg_id");
        myRef.setValue(token);

        // change 1500 -> if no splashscreen
        h.postDelayed(run, USING_SPLASHSCREEN ? 1500 : 0);
    }


}
