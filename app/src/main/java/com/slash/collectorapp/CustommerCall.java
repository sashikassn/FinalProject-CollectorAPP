package com.slash.collectorapp;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.SquareCap;
import com.slash.collectorapp.Common.Common;
import com.slash.collectorapp.Model.FCMResponse;
import com.slash.collectorapp.Model.Notification;
import com.slash.collectorapp.Model.Sender;
import com.slash.collectorapp.Model.Token;
import com.slash.collectorapp.Remote.IFCMService;
import com.slash.collectorapp.Remote.IGoogleAPI;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.slash.collectorapp.Common.Common.mLastLocation;

public class CustommerCall extends AppCompatActivity {

    TextView txtTime, txtAddress, txtDistance;
    MediaPlayer mediaPlayer;
    Button btnCancel,btnAccept;

    IGoogleAPI mService;
    IFCMService mFCMService;

    String customerId;

    double lat,lng;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custommer_call);

        mService = Common.getGoogleAPI();
        mFCMService = Common.getFCMService();

        txtTime = (TextView) findViewById(R.id.txtTime);
        txtAddress = (TextView) findViewById(R.id.txtAddress);
        txtDistance = (TextView) findViewById(R.id.txtDistance);

        btnAccept = (Button) findViewById(R.id.btnAccept);
        btnCancel = (Button) findViewById(R.id.btnDecline);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!TextUtils.isEmpty(customerId))
                    cacelBooking(customerId);
            }
        });

        btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CustommerCall.this,DriverTracking.class);
                intent.putExtra("lat",lat);
                intent.putExtra("lng",lng);
                intent.putExtra("customerId",customerId);
                startActivity(intent);
                finish();
            }
        });

        mediaPlayer = MediaPlayer.create(this,R.raw.ringtone);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();

        if(getIntent() != null){
            lat = getIntent().getDoubleExtra("lat",-1.0);
            lng = getIntent().getDoubleExtra("lng",-1.0);
            customerId = getIntent().getStringExtra("customer");

            getDirection(lat,lng);
        }
    }

    private void cacelBooking(String customerId) {
        Token token = new Token(customerId);

        Notification notification = new Notification("Cancel","Driver has cancelled your request");
        Sender sender = new Sender(token.getToken(),notification);

        mFCMService.sendMessage(sender)
                .enqueue(new Callback<FCMResponse>() {
                    @Override
                    public void onResponse(Call<FCMResponse> call, Response<FCMResponse> response) {
                        if(response.body().success == 1){
                            Toast.makeText(CustommerCall.this, "Cancelled", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }

                    @Override
                    public void onFailure(Call<FCMResponse> call, Throwable t) {

                    }
                });
    }

    private void getDirection(double lat, double lng) {
        String requestApi = null;
        try{
            requestApi = "https://maps.googleapis.com/maps/api/directions/json?"+
                    "mode=driving&"+
                    "transit_routing_preferences=less_driving&"+
                    "origin="+Common.mLastLocation.getLatitude()+","+Common.mLastLocation.getLongitude()+"&"+
                    "destination="+lat+","+lng+"&"+
                    "key="+getResources().getString(R.string.google_direction_api);
            Log.d("checkLog",requestApi);
            mService.getPath(requestApi)
                    .enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {
                            try {
                                JSONObject jsonObject = new JSONObject(response.body().toString());

                                JSONArray routes = jsonObject.getJSONArray("routes");
                                JSONObject object = routes.getJSONObject(0);
                                JSONArray legs = object.getJSONArray("legs");
                                JSONObject legsObject = legs.getJSONObject(0);
                                JSONObject distance = legsObject.getJSONObject("distance");
                                txtDistance.setText(distance.getString("text"));

                                JSONObject time = legsObject.getJSONObject("duration");
                                txtTime.setText(time.getString("text"));

                                String address = legsObject.getString("end_address");
                                txtAddress.setText(address);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFailure(Call<String> call, Throwable t) {
                            Toast.makeText(CustommerCall.this, ""+t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    protected void onStop() {
        mediaPlayer.release();
        super.onStop();
    }

    @Override
    protected void onPause() {
        mediaPlayer.pause();
        super.onPause();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        mediaPlayer.start();
    }
}
