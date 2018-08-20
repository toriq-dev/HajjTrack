package com.example.sucianalf.grouptracking.activity;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.example.sucianalf.grouptracking.adapter.PlaceAutocompleteAdapter;
import com.example.sucianalf.grouptracking.util.AppController;
import com.example.sucianalf.grouptracking.model.MarkerObject;
import com.example.sucianalf.grouptracking.model.PlaceInfo;
import com.example.sucianalf.grouptracking.R;
import com.example.sucianalf.grouptracking.api.Url;
import com.example.sucianalf.grouptracking.util.DirectionsJSONParser;
import com.example.sucianalf.grouptracking.util.SessionManager;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.sucianalf.grouptracking.util.AppController.getContext;

public class DisplayNavigation extends AppCompatActivity implements OnMapReadyCallback,
        GoogleApiClient.OnConnectionFailedListener, NavigationView.OnNavigationItemSelectedListener, View.OnClickListener, GoogleApiClient.ConnectionCallbacks, LocationListener {
    private static final String TAG = "DisplayNavigation";
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final float DEFAULT_ZOOM = 16f;
    private static final LatLngBounds LAT_LNG_BOUNDS = new LatLngBounds(
            new LatLng(-40, -168), new LatLng(71, 136));
    private AutoCompleteTextView mSearchText;
    private FloatingActionButton mGps;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private GoogleApiClient mGoogleApiClient;
    private GoogleMap mMap;
    private ImageView btnRemove;
    private ImageView btnSet;
    private PlaceAutocompleteAdapter mPlaceAutocompleteAdapter;
    private PlaceInfo mPlace;
    private SessionManager session;
    private LinearLayout drivingInfo, walkingInfo;
    private Boolean mLocationPermissionsGranted = false;
    private double deviceLat = 0;
    private double deviceLng = 0;
    private double searchLat = 0;
    private double searchLng = 0;
    private int groupID;
    private String requestURL, getStatus, getMessage, getPlace, setDestinasi;
    private String set_by, place_name, set_time, getToast, getCommand;

    private ArrayList<MarkerObject> getMemberMarker = new ArrayList<>();
    private MarkerOptions options;
    private HashMap<String, Marker> params = new HashMap<>();

    private Bundle bundle;
    private TextView txtAddress, txtWalkDistance, txtCarDistance;
    private Location mLastLocation;
    private LocationRequest mLocationRequest;
    private Polyline polyline;
    private Marker memberMarker;

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        setContentView(R.layout.activity_drawer);
        mSearchText = findViewById(R.id.input_search);
        btnSet = findViewById(R.id.setDestination);
        btnRemove = findViewById(R.id.removeDestination);
        mGps = findViewById(R.id.ic_gps);
        txtAddress = findViewById(R.id.address);
        txtCarDistance = findViewById(R.id.carDistance);
        txtWalkDistance = findViewById(R.id.walkDistance);

        walkingInfo = findViewById(R.id.walkingInfo);
        drivingInfo = findViewById(R.id.drivingInfo);

        walkingInfo.setOnClickListener(this);
        drivingInfo.setOnClickListener(this);

        getLocationPermission();
        initToobar();
        session = new SessionManager(getApplicationContext());
        Bundle extras = getIntent().getExtras();
        groupID = extras != null ? extras.getInt("groupID") : 0;
        LocalBroadcastManager.getInstance(DisplayNavigation.this).registerReceiver(
                mMessageReceiver, new IntentFilter("intentKey"));
    }

    private void initToobar() {
        Toolbar addToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(addToolbar);
        ActionBar ab = getSupportActionBar();
        ab.setTitle("NAVIGASI");

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, addToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setItemIconTintList(null);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Toast.makeText(this, "Map is Ready", Toast.LENGTH_SHORT).show();
        mMap = googleMap;

        if (mLocationPermissionsGranted) {
            getDeviceLocation();
            getMember();
            init();
            checkDestination();
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            }
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        mLocationPermissionsGranted = false;
        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    for (int grantResult : grantResults) {
                        if (grantResult != PackageManager.PERMISSION_GRANTED) {
                            mLocationPermissionsGranted = false;
                            return;
                        }
                    }
                    mLocationPermissionsGranted = true;
                    initMap();
                }
            }
        }
    }

    private void initMap() {
        Log.d(TAG, "initMap: initializing map");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(DisplayNavigation.this);
    }

    private void init() {
        Log.d(TAG, "init: initializing");
        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .enableAutoManage(this, this)
                .build();
        mGoogleApiClient.connect();

        mSearchText.setOnItemClickListener(mAutocompleteClickListener);
        mPlaceAutocompleteAdapter = new PlaceAutocompleteAdapter(this, mGoogleApiClient,
                LAT_LNG_BOUNDS, null);
        mSearchText.setAdapter(mPlaceAutocompleteAdapter);
        mSearchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH
                        || actionId == EditorInfo.IME_ACTION_DONE
                        || keyEvent.getAction() == KeyEvent.ACTION_DOWN
                        || keyEvent.getAction() == KeyEvent.KEYCODE_ENTER) {
                    geoLocate();
                }
                return false;
            }
        });

        mGps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: clicked gps icon");
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(deviceLat, deviceLng), DEFAULT_ZOOM));
            }
        });
        hideSoftKeyboard();
    }

    private void getLocationPermission() {
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};

        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                    COURSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mLocationPermissionsGranted = true;
                initMap();
            } else {
                ActivityCompat.requestPermissions(this,
                        permissions,
                        LOCATION_PERMISSION_REQUEST_CODE);
            }
        } else {
            ActivityCompat.requestPermissions(this,
                    permissions,
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    private void getDeviceLocation() {
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        try {
            if (mLocationPermissionsGranted) {
                final Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            Location currentLocation = (Location) task.getResult();
                            if (currentLocation == null)
                                Toast.makeText(DisplayNavigation.this, "Unable to get current location. " +
                                        "Make sure your device location is on and click location button.", Toast.LENGTH_SHORT).show();
                            else {
                                moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), DEFAULT_ZOOM, "You");
                                deviceLat = currentLocation.getLatitude();
                                deviceLng = currentLocation.getLongitude();
                            }
                        } else
                            Toast.makeText(DisplayNavigation.this, "unable to get current location", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        } catch (SecurityException e) {
            Log.e(TAG, "getDeviceLocation: SecurityException: " + e.getMessage());
        }
    }


    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String message = intent.getStringExtra("serviceTrack");
            if (message == null || message.equals("")) {
                Log.d(TAG, "onReceive: failed");
            }else if (!message.equals("")) {
                if(memberMarker!=null)
                    memberMarker.remove();
                getMember();
            }
        }
    };

    private void moveCamera(LatLng latLng, float zoom, String title) {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
        if(title.equals("You")){
            options = new MarkerOptions();
            options.position(new LatLng(deviceLat, deviceLng));
            options.title(title);
            Glide.with(getApplicationContext())
                    .load(session.getImage()).asBitmap().centerCrop()
                    .into(new SimpleTarget<Bitmap>(){
                        @Override
                        public void onResourceReady(Bitmap bitmap, GlideAnimation<? super Bitmap> glideAnimation) {
                            options.icon(BitmapDescriptorFactory.fromBitmap(getMarkerBitmapFromView(bitmap)));
                        }
                    });
            mMap.addMarker(options);
        } else{
            MarkerOptions options = new MarkerOptions()
                    .position(latLng)
                    .title(title)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
            mMap.addMarker(options);
        }

        hideSoftKeyboard();
    }

    private void moveCamera(LatLng latLng, float zoom, PlaceInfo placeInfo) {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));

        if (placeInfo != null) {
            try {
                String snippet = "Address: " + placeInfo.getAddress() + "\n" +
                        "Phone Number: " + placeInfo.getPhoneNumber() + "\n" +
                        "Website: " + placeInfo.getWebsiteUri() + "\n" +
                        "Price Rating: " + placeInfo.getRating() + "\n";

                MarkerOptions options = new MarkerOptions()
                        .position(latLng)
                        .title(placeInfo.getName())
                        .snippet(snippet)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                mMap.addMarker(options);
            } catch (NullPointerException e) {
                Log.e(TAG, "moveCamera: NullPointerException: " + e.getMessage());
            }
        } else {
            mMap.addMarker(new MarkerOptions().position(latLng));
        }
        hideSoftKeyboard();
    }

    private void hideSoftKeyboard() {
        InputMethodManager inputManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        View v = DisplayNavigation.this.getCurrentFocus();
        if (v != null) {
            DisplayNavigation.this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            assert inputManager != null;
            inputManager.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    private void geoLocate() {
        String searchString = mSearchText.getText().toString();
        Geocoder geocoder = new Geocoder(DisplayNavigation.this);
        List<Address> list = new ArrayList<>();
        try {
            list = geocoder.getFromLocationName(searchString, 1);
        } catch (IOException e) {
            Log.e(TAG, "geoLocate: IOException: " + e.getMessage());
        }

//        if (list.size() > 0) {
//            Address address = list.get(0);
//            moveCamera(new LatLng(address.getLatitude(), address.getLongitude()), DEFAULT_ZOOM,
//                    address.getAddressLine(0));
//        }
    }

    private Bitmap getMarkerBitmapFromView(Bitmap bitmap) {
        View customMarkerView = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.view_custom_mask, null);
        ImageView markerImageView = customMarkerView.findViewById(R.id.profile_image);
        markerImageView.setImageBitmap(bitmap);
        customMarkerView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        customMarkerView.layout(0, 0, customMarkerView.getMeasuredWidth(), customMarkerView.getMeasuredHeight());
        customMarkerView.buildDrawingCache();
        Bitmap returnedBitmap = Bitmap.createBitmap(customMarkerView.getMeasuredWidth(), customMarkerView.getMeasuredHeight(),
                Bitmap.Config.ARGB_4444);
        Canvas canvas = new Canvas(returnedBitmap);
        canvas.drawColor(Color.WHITE, PorterDuff.Mode.SRC_IN);
        Drawable drawable = customMarkerView.getBackground();
        if (drawable != null)
            drawable.draw(canvas);
        customMarkerView.draw(canvas);
        return returnedBitmap;
    }

    /*
        --------------------------- google places API autocomplete suggestions -----------------
     */

    private AdapterView.OnItemClickListener mAutocompleteClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            hideSoftKeyboard();
            final AutocompletePrediction item = mPlaceAutocompleteAdapter.getItem(i);
            assert item != null;
            final String placeId = item.getPlaceId();
            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                    .getPlaceById(mGoogleApiClient, placeId);
            placeResult.setResultCallback(mUpdatePlaceDetailsCallback);
        }
    };

    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(@NonNull PlaceBuffer places) {
            if (!places.getStatus().isSuccess()) {
                places.release();
                return;
            }
            final Place place = places.get(0);
            mPlace = new PlaceInfo();

            try {
                mPlace = new PlaceInfo(
                        place.getName().toString(),
                        place.getAddress().toString(),
                        place.getPhoneNumber().toString(),
                        place.getId(),
                        place.getWebsiteUri(),
                        place.getLatLng(),
                        place.getRating(),
                        ""
                );
                getPlace = place.getName().toString();

                searchLat = place.getLatLng().latitude;
                searchLng = place.getLatLng().longitude;

                // Getting URL to the Google Directions API
                txtAddress.setText(getPlace);
                getDistance(deviceLat, deviceLng, searchLat, searchLng, "driving");
                getDistance(deviceLat, deviceLng, searchLat, searchLng, "walking");

                String url = getDirectionsUrl(deviceLat, deviceLng, searchLat, searchLng, "driving");

                DownloadTask downloadTask = new DownloadTask();
                downloadTask.execute(url);

            } catch (NullPointerException e) {
                Log.e(TAG, "onResult: NullPointerException: " + e.getMessage());
            }

            moveCamera(new LatLng(searchLat,
                    searchLng), DEFAULT_ZOOM, mPlace);
            mPlace.setLatlng(place.getLatLng());
            places.release();
        }
    };
    private void getMember() {

        String urlJSON = Url.FunctionName.MEMBER_MARKER + groupID + "/username/" + session.getUsername();
        JsonObjectRequest jsonObjReqOrigin = new JsonObjectRequest(Request.Method.GET, urlJSON,
                null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    getStatus = response.getString("status");
                    getMessage = response.getString("message");

                    if (getStatus.trim().equals("Succes")) {
                        JSONArray member = response.getJSONArray("result");
                        for (int i = 0; i < member.length(); i++) {
                            final String user = member.getJSONObject(i).getString("username");
                            final String koordinat = member.getJSONObject(i).getString("koordinat");
                            final String time = member.getJSONObject(i).getString("timestamp");
                            String image = member.getJSONObject(i).getString("image_user");
                            getMemberMarker.add(new MarkerObject(user, koordinat, time, image));


                            Glide.with(getApplicationContext())
                                    .load(image).asBitmap().centerCrop()
                                    .into(new SimpleTarget<Bitmap>(){
                                        @Override
                                        public void onResourceReady(Bitmap bitmap, GlideAnimation<? super Bitmap> glideAnimation) {
                                            options = new MarkerOptions();
                                            String[] latlong = koordinat.split(",");
                                            options.position(new LatLng(Double.parseDouble(latlong[0]), Double.parseDouble(latlong[1])));
                                            options.title("" + user);
                                            options.snippet("Last Update: " + time);
                                            options.icon(BitmapDescriptorFactory.fromBitmap(getMarkerBitmapFromView(bitmap)));
                                            memberMarker = mMap.addMarker(options);
                                        }
                                    });

                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        AppController.getInstance().addToRequestQueue(jsonObjReqOrigin);
    }

    public void buttonClickSetDest(View v) {
        setDestination();
        startActivity(new Intent(DisplayNavigation.this, ListGroupActivity.class));
        finish();
    }

    public void buttonClickremoveDest(View v) {
        removeDestination();
        startActivity(new Intent(DisplayNavigation.this, ListGroupActivity.class));
        finish();
    }

    public void setDestination() {
        setDestinasi = searchLat + "," + searchLng;
        if (getPlace == null)
            Toast.makeText(getApplicationContext(), "Destinasi Tujuan tidak ditemukan. Harap lakukan pencarian.", Toast.LENGTH_SHORT).show();
        else {
            String tag_string_req = "set_destination";
            StringRequest strReq = new StringRequest(Request.Method.POST,
                    Url.FunctionName.SET_DEST, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject jObj = new JSONObject(response);
                        getStatus = jObj.getString("status").trim();
                        getMessage = jObj.getString("message");

                        if (getStatus.equals("Success")) {
                            Toast.makeText(getApplicationContext(), getMessage, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getApplicationContext(), getMessage, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    VolleyLog.d("TAG", "Error: " + error.getMessage());
                    String message;
                    if (error instanceof NetworkError) {
                        message = "Cannot connect to Internet...Please check your connection!";
                    } else if (error instanceof ServerError) {
                        message = "The server could not be found. Please try again after some time!!";
                    } else if (error instanceof AuthFailureError) {
                        message = "Cannot connect to Internet...Please check your connection!";
                    } else if (error instanceof ParseError) {
                        message = "Parsing error! Please try again after some time!!";
                    } else if (error instanceof TimeoutError) {
                        message = "Connection TimeOut! Please check your internet connection.";
                    }
                    else
                        message = error.getMessage();

                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                }
            }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("groupID", "" + groupID);
                    params.put("lokasi", setDestinasi);
                    params.put("username", session.getUsername());
                    params.put("place", getPlace);
                    return params;
                }
            };
            AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
        }
    }

    public void removeDestination() {
        String tag_string_req = "remove_destination";
        StringRequest strReq = new StringRequest(Request.Method.POST,
                Url.FunctionName.REMOVE_DEST, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jObj = new JSONObject(response);
                    getStatus = jObj.getString("status").trim();
                    getMessage = jObj.getString("message");

                    if (getStatus.equals("Success")) {
                        Toast.makeText(getApplicationContext(), getMessage, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), getMessage, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("TAG", "Error: " + error.getMessage());
                String message;
                if (error instanceof NetworkError) {
                    message = "Cannot connect to Internet...Please check your connection!";
                } else if (error instanceof ServerError) {
                    message = "The server could not be found. Please try again after some time!!";
                } else if (error instanceof AuthFailureError) {
                    message = "Cannot connect to Internet...Please check your connection!";
                } else if (error instanceof ParseError) {
                    message = "Parsing error! Please try again after some time!!";
                } else if (error instanceof TimeoutError) {
                    message = "Connection TimeOut! Please check your internet connection.";
                }
                else
                    message = error.getMessage();

                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("groupID", "" + groupID);
                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void checkDestination() {
        String urlJSON = Url.FunctionName.CHECK_DEST + groupID;
        Log.d(TAG, "getURL: " + urlJSON);
        JsonObjectRequest jsonObjReqOrigin = new JsonObjectRequest(Request.Method.GET, urlJSON,
                null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    getStatus = response.getString("status");
                    getMessage = response.getString("message");

                    if (getStatus.trim().equals("Success")) {
                        JSONArray member = response.getJSONArray("result");
                        set_by = member.getJSONObject(0).getString("set_by");
                        place_name = member.getJSONObject(0).getString("place_name");
                        set_time = member.getJSONObject(0).getString("set_time");

                        String[] latlong = member.getJSONObject(0).getString("LatLng").split(",");
                        searchLat = Double.parseDouble(latlong[0]);
                        searchLng = Double.parseDouble(latlong[1]);

                        mSearchText.setVisibility(View.GONE);
                        if (set_by.trim().equals(session.getUsername()))
                            btnRemove.setVisibility(View.VISIBLE);
                        else
                            btnRemove.setVisibility(View.GONE);

                        btnSet.setVisibility(View.GONE);
                        Toast.makeText(DisplayNavigation.this, getMessage, Toast.LENGTH_SHORT).show();
//                        DijkstraResult("" + checkDestLat, "" + checkDestLng, "checkDestination");

                        txtAddress.setText(place_name);
                        getDistance(deviceLat, deviceLng, searchLat, searchLng, "driving");
                        getDistance(deviceLat, deviceLng, searchLat, searchLng, "walking");

                        moveCamera(new LatLng(searchLat, searchLng), DEFAULT_ZOOM, place_name);

                        // Getting URL to the Google Directions API
                        String url = getDirectionsUrl(deviceLat, deviceLng, searchLat, searchLng, "driving");

                        DownloadTask downloadTask = new DownloadTask();

                        // Start downloading json data from Google Directions API
                        downloadTask.execute(url);

                        //requestLocationUpdates();
                        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                    } else {
                        mSearchText.setVisibility(View.VISIBLE);
                        btnRemove.setVisibility(View.GONE);
                        btnSet.setVisibility(View.VISIBLE);
                        Toast.makeText(DisplayNavigation.this, getMessage, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("TAG", "Error: " + error.getMessage());
                String message;
                if (error instanceof NetworkError) {
                    message = "Cannot connect to Internet...Please check your connection!";
                } else if (error instanceof ServerError) {
                    message = "The server could not be found. Please try again after some time!!";
                } else if (error instanceof AuthFailureError) {
                    message = "Cannot connect to Internet...Please check your connection!";
                } else if (error instanceof ParseError) {
                    message = "Parsing error! Please try again after some time!!";
                } else if (error instanceof TimeoutError) {
                    message = "Connection TimeOut! Please check your internet connection.";
                }
                else
                    message = error.getMessage();

                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
        AppController.getInstance().addToRequestQueue(jsonObjReqOrigin);
    }

    @Override
    public void onClick(View view) {
        if(view == drivingInfo){
            if(deviceLat!=0 && deviceLng!=0 && searchLat!=0 && searchLng!=0){
// Getting URL to the Google Directions API
                String url = getDirectionsUrl(deviceLat, deviceLng, searchLat, searchLng, "driving");

                DownloadTask downloadTask = new DownloadTask();

                // Start downloading json data from Google Directions API
                downloadTask.execute(url);
            }
            else{
                Toast.makeText(getApplicationContext(), "Address not found", Toast.LENGTH_SHORT).show();
            }
        }else if(view == walkingInfo){
            if(deviceLat!=0 && deviceLng!=0 && searchLat!=0 && searchLng!=0){
// Getting URL to the Google Directions API
                String url = getDirectionsUrl(deviceLat, deviceLng, searchLat, searchLng, "walking");

                DownloadTask downloadTask = new DownloadTask();

                // Start downloading json data from Google Directions API
                downloadTask.execute(url);
            }
            else{
                Toast.makeText(getApplicationContext(), "Address not found", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;


        //Place current location marker
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        options = new MarkerOptions();
        options.position(latLng);
        options.title("You");
        options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
        mMap.addMarker(options);

        //move map camera
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,11));
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    @Override
    public void onConnected(Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    private class DownloadTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... url) {

            String data = "";

            try {
                data = downloadUrl(url[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();


            parserTask.execute(result);

        }
    }

    /**
     * A class to parse the Google Places in JSON format
     */
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();

                routes = parser.parse(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            if(polyline!=null)
                polyline.remove();

            ArrayList<LatLng> points = new ArrayList<>();
            PolylineOptions lineOptions = new PolylineOptions();
            lineOptions.width(12);
            lineOptions.geodesic(true);
            lineOptions.color(Color.rgb(0,150,136));

            for (int i = 0; i < result.size(); i++) {


                List<HashMap<String, String>> path = result.get(i);

                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                lineOptions.addAll(points);

            }

// Drawing polyline in the Google Map for the i-th route
            polyline = mMap.addPolyline(lineOptions);
        }
    }

    private String getDirectionsUrl(double latFrom, double lngFrom, double latTo, double lngTo, final String modee) {

        // Origin of route
        String str_origin = "origin=" + latFrom + "," + lngFrom;

        // Destination of route
        String str_dest = "destination=" + latTo + "," + lngTo;

        // Sensor enabled
        String sensor = "sensor=false";
        String mode = "mode=" + modee;

        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor + "&" + mode;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;


        return url;
    }

    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.connect();

            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        } catch (Exception e) {
            Log.d("Exception", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        //calling the method displayselectedscreen and passing the id of selected menu
        //make this method blank
        Intent inten = new Intent();

        switch (item.getItemId()) {
            case R.id.nav_menu1:
                inten = new Intent(getApplicationContext(), ListGroupActivity.class);
                bundle = new Bundle();
                bundle.putString("username", session.getUsername());
                inten.putExtras(bundle);
                startActivity(inten);
                break;
            case R.id.nav_menu2:
                inten = new Intent(getApplicationContext(), ListGroupMemberActivity.class);
                bundle = new Bundle();
                bundle.putString("idGroup", String.valueOf(groupID));
                bundle.putString("namaGroup", getIntent().getExtras().getString("groupName"));
                inten.putExtras(bundle);
                startActivity(inten);
                break;
            case R.id.nav_menu3:
                Toast.makeText(getApplicationContext(), "You are in Navigation!", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_menu4:
                session.setLogin(false);
                Toast.makeText(getApplicationContext(), "Logout Successfully", Toast.LENGTH_SHORT).show();
                inten = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(inten);
                break;
        }
        return true;
    }

    private void getDistance(double latFrom, double lngFrom, double latTo, double lngTo, final String mode){
        String url = "https://maps.googleapis.com/maps/api/distancematrix/json?units=metrics&origins=" + latFrom + "," + lngFrom + "&destinations=" + latTo + "," + lngTo + "&mode=" + mode;
        Log.d(TAG, "getURL: " + url);
        JsonObjectRequest jsonObjReqOrigin = new JsonObjectRequest(Request.Method.GET, url,
                null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {

                    JSONArray array = response.getJSONArray("rows");

                    JSONObject routes = array.getJSONObject(0);

                    JSONArray legs = routes.getJSONArray("elements");

                    JSONObject steps = legs.getJSONObject(0);

                    JSONObject distance = steps.getJSONObject("distance");
                    JSONObject duration = steps.getJSONObject("duration");

                    if(mode.equals("walking")){
                        txtWalkDistance.setText(duration.getString("text"));
                    }else if(mode.equals("driving")){
                        txtCarDistance.setText(duration.getString("text"));
                    }


                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("TAG", "Error: " + error.getMessage());
                String message;
                if (error instanceof NetworkError) {
                    message = "Cannot connect to Internet...Please check your connection!";
                } else if (error instanceof ServerError) {
                    message = "The server could not be found. Please try again after some time!!";
                } else if (error instanceof AuthFailureError) {
                    message = "Cannot connect to Internet...Please check your connection!";
                } else if (error instanceof ParseError) {
                    message = "Parsing error! Please try again after some time!!";
                } else if (error instanceof TimeoutError) {
                    message = "Connection TimeOut! Please check your internet connection.";
                }
                else
                    message = error.getMessage();

                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
        AppController.getInstance().addToRequestQueue(jsonObjReqOrigin);
    }
}
