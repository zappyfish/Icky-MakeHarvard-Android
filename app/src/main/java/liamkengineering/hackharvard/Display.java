package liamkengineering.hackharvard;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

public class Display extends AppCompatActivity {

    String SENDER_ID = "756256496659";

    private static final String SHARED = "SHARED";
    private BroadcastReceiver statusReceiver;
    private IntentFilter mIntent;
    private static String SEND_URL = null;
    private static final String IMAGE = "/bitch";
    private static final String DENY = "/deny";
    private static final String ALLOW = "/open";
    private ImageView door;

    private Button allowButton;
    private Button denyButton;

    private static boolean RESUMED = false;

    RequestQueue queue = null;

    private TextView test;
    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);

        door = (ImageView)findViewById(R.id.door_image);

        queue = Volley.newRequestQueue(this);

        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter(SHARED));

        test = (TextView)findViewById(R.id.time);

        allowButton = (Button)findViewById(R.id.confirm);

        allowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendConfirmation(ALLOW);
            }
        });

        denyButton = (Button)findViewById(R.id.deny);

        denyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendConfirmation(DENY);
            }
        });

        mDatabase.child("url").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    SEND_URL = child.getValue(String.class);
                }
                if(RESUMED) getImage(SEND_URL);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    private void sendConfirmation(String decision) {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, SEND_URL + decision,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(Display.this, "got response", Toast.LENGTH_SHORT).show();
                    }}, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
// Add the request to the RequestQueue.
        queue.add(stringRequest);
        RESUMED = false;
        finish();
    }

    private void getImage(String url) {
    StringRequest stringRequest = new StringRequest(Request.Method.GET, SEND_URL + IMAGE,
        new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {
            // Log.d("base64", response);
            String encoded = response.substring(response.indexOf(",") + 1);
            byte[] decodedString = Base64.decode(encoded, Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            door.setImageBitmap(decodedByte);


        }}, new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {

        }
    });
// Add the request to the RequestQueue.
    queue.add(stringRequest);
    }


    @Override
    protected void onResume()
    {
        super.onResume();
        RESUMED = true;
    }

    @Override
    protected void onPause() {
        if(mIntent != null) {
            unregisterReceiver(statusReceiver);
            mIntent = null;
        }
        super.onPause();
    }
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            String message = intent.getStringExtra("url");
            test.setText(message);
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        }
    };
}
