package com.ford.ocarpen4.hacakathoncontrolapp;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import com.openxc.VehicleManager;

public class MainActivity extends Activity {
    private VehicleManager mVehicleManager;
    private static final String TAG = "StarterActivity";
    Firebase mFirebaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Initialize Firebase
        Firebase.setAndroidContext(this);
        mFirebaseRef = new Firebase("https://fordeyespi.firebaseio.com/EyeSPI");

        //Set up message listener
        mFirebaseRef.child("message").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if(dataSnapshot != null){
                    try {
                        //New Message, Check if it's for me
                        Log.i("message", "new message");
                        int id = Integer.parseInt(dataSnapshot.child("id").getValue().toString());
                        if(id != 0){
                            //Message not for me, ignore
                            Log.i("message", "message not for me");
                            return;
                        }else{
                            //Message is for me, parse it
                            Log.i("message", "message is for me");
                            int fl = Integer.parseInt(dataSnapshot.child("FL").getValue().toString());
                            int fr = Integer.parseInt(dataSnapshot.child("FR").getValue().toString());
                            int rl = Integer.parseInt(dataSnapshot.child("RL").getValue().toString());
                            int rc = Integer.parseInt(dataSnapshot.child("RC").getValue().toString());
                            int rr = Integer.parseInt(dataSnapshot.child("RR").getValue().toString());
                            //display dat(a for now
                            Log.i("messages", "" + fl + fr + rl + rc + rr);
                            //Pass data to Jon's function(s)

                            //Delete Message
                            mFirebaseRef.child("message").child(dataSnapshot.getKey()).removeValue();
                        }
                    }catch(Exception e){
                        Log.i("message", e.toString());
                    }
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    private ServiceConnection mConnection = new ServiceConnection() {
        // Called when the connection with the VehicleManager service is
        // established, i.e. bound.
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            Log.i(TAG, "Bound to VehicleManager");
            // When the VehicleManager starts up, we store a reference to it
            // here in "mVehicleManager" so we can call functions on it
            // elsewhere in our code.
            mVehicleManager = ((VehicleManager.VehicleBinder) service)
                    .getService();
        }

        // Called when the connection with the service disconnects unexpectedly
        public void onServiceDisconnected(ComponentName className) {
            Log.w(TAG, "VehicleManager Service  disconnected unexpectedly");
            mVehicleManager = null;
        }
    };


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onResume() {
        super.onResume();
        // When the activity starts up or returns from the background,
        // re-connect to the VehicleManager so we can receive updates.
        if(mVehicleManager == null) {
            Intent intent = new Intent(this, VehicleManager.class);
            bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        }
    }

    public void startFacialDetection(){
        FirebaseRequest request = new FirebaseRequest(3, "", 0, 0, 0, 0, 0);
        mFirebaseRef.child("message").push().setValue(request);
    }
}
