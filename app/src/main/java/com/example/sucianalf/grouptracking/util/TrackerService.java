package com.example.sucianalf.grouptracking.util;

import android.Manifest;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.sucianalf.grouptracking.R;
import com.example.sucianalf.grouptracking.api.Url;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import org.json.JSONException;
import org.json.JSONObject;

public class TrackerService extends Service {

    private static final String TAG = TrackerService.class.getSimpleName();
    SessionManager session;
    public String username;

    @Override
    public void onCreate() {
        super.onCreate();
        session = new SessionManager(getApplicationContext());
        buildNotification();
        requestLocationUpdates();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

//        Toast.makeText(this, "service starting", Toast.LENGTH_SHORT).show();
        return super.onStartCommand(intent,flags,startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // A client is binding to the service with bindService()
//        return mBinder;
        return null;
    }
    @Override
    public boolean onUnbind(Intent intent) {
        // All clients have unbound with unbindService()
//        return mAllowRebind;
        return false;
    }
    @Override
    public void onRebind(Intent intent) {
        // A client is binding to the service with bindService(),
        // after onUnbind() has already been called
    }
    @Override
    public void onDestroy() {

    }

    private void buildNotification() {
        String stop = "stop";
        registerReceiver(stopReceiver, new IntentFilter(stop));
        PendingIntent broadcastIntent = PendingIntent.getBroadcast(
                this, 0, new Intent(stop), PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(getString(R.string.notification_text))
                .setOngoing(true)
                .setContentIntent(broadcastIntent)
                .setSmallIcon(R.drawable.ic_gps);
        startForeground(1, builder.build());
    }

    protected BroadcastReceiver stopReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "received stop broadcast");
            unregisterReceiver(stopReceiver);
            stopSelf();
        }
    };

    private void requestLocationUpdates() {
        LocationRequest request = new LocationRequest();
        request.setInterval(30000);
        request.setFastestInterval(15000);
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        FusedLocationProviderClient client = LocationServices.getFusedLocationProviderClient(this);
        int permission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        if (permission == PackageManager.PERMISSION_GRANTED) {
            client.requestLocationUpdates(request, new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    Location location = locationResult.getLastLocation();
                    if (location != null) {
                        if (session.getUsername() == null || session.getUsername() == "")
                            username = "";
                        else
                        {
                            String userLocation = ""+location.getLatitude()+","+location.getLongitude();
                            username = session.getUsername();
                            String requestURL = Url.FunctionName.UPDATE_LOC + username+ "/lokasi/" + userLocation;
                            Log.d(TAG, "updateLoc: "+requestURL);

                            JsonObjectRequest jsonObjReqOrigin = new JsonObjectRequest(Request.Method.GET, requestURL,
                                    null, new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    try {
                                        String getStatus = response.getString("status").trim();
                                        String getMessages = response.getString("message").trim();

                                        if(getStatus.equals("Success"))
                                        {
                                            Intent intent = new Intent("intentKey");
                                            intent.putExtra("serviceTrack", "update");
                                            LocalBroadcastManager.getInstance(TrackerService.this).sendBroadcast(intent);
                                        }
                                        else if(getStatus.trim().equals("Failed"))
                                            Toast.makeText(TrackerService.this, getMessages, Toast.LENGTH_SHORT).show();
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    VolleyLog.d(TAG, "Error: " + error.getMessage());
                                    String message ="";
                                    if (error instanceof NetworkError) {
                                        message = "Cannot connect to Internet...Please check your connection!";
                                    } else if (error instanceof ServerError) {
                                        message = "The server could not be found. Please try again after some time!!";
                                    } else if (error instanceof AuthFailureError) {
                                        message = "Cannot connect to Internet...Please check your connection!";
                                    } else if (error instanceof ParseError) {
                                        message = "Parsing error! Please try again after some time!!";
                                    } else if (error instanceof NoConnectionError) {
                                        message = "Cannot connect to Internet...Please check your connection!";
                                    } else if (error instanceof TimeoutError) {
                                        message = "Connection TimeOut! Please check your internet connection.";
                                    }
                                    else
                                        message = error.getMessage();

                                    Intent intent = new Intent("intentKey");
                                    intent.putExtra("serviceTrack", "");
                                    LocalBroadcastManager.getInstance(TrackerService.this).sendBroadcast(intent);
                                }
                            });
                            AppController.getInstance().addToRequestQueue(jsonObjReqOrigin);
                        }
                    }
                }
            }, null);
        }
    }
}