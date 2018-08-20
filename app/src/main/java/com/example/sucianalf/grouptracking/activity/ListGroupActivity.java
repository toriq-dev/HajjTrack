package com.example.sucianalf.grouptracking.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
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
import com.android.volley.toolbox.StringRequest;
import com.example.sucianalf.grouptracking.adapter.GroupAdapter;
import com.example.sucianalf.grouptracking.util.AppController;
import com.example.sucianalf.grouptracking.model.ListGroup;
import com.example.sucianalf.grouptracking.R;
import com.example.sucianalf.grouptracking.util.TrackerService;
import com.example.sucianalf.grouptracking.api.Url;
import com.example.sucianalf.grouptracking.util.ProgressDialogUtil;
import com.example.sucianalf.grouptracking.util.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ListGroupActivity extends AppCompatActivity {
    private Bundle getBundle;
    private ListView list;
    private TextView txtKosong;
    private List<ListGroup> groupList = new ArrayList<>();
    private GroupAdapter groupAdapter;
    private EditText namaGroup;
    private String TAG = ListGroupActivity.class.getSimpleName();
    private String groupName = "";
    private SessionManager session;
    private static final int PERMISSIONS_REQUEST = 1;
    private Intent intent;
    public String getStatus, getMessage, urlRequest;
    private boolean doubleBackToExitPressedOnce = false;
    private ProgressDialogUtil progressDialogUtil;
    private FloatingActionButton floatingActionButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_group);
        getBundle = getIntent().getExtras();
        session = new SessionManager(getApplicationContext());
        // Check GPS is enabled
        LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);
        assert lm != null;
        if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Toast.makeText(this, "Please enable location services", Toast.LENGTH_SHORT).show();
        }

        int permission = ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION);
        if (permission == PackageManager.PERMISSION_GRANTED) {
            startTrackerService();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST);
        }

        groupAdapter = new GroupAdapter(ListGroupActivity.this, groupList);
        progressDialogUtil = new ProgressDialogUtil(this, ProgressDialog.STYLE_SPINNER, false);
        list = findViewById(R.id.list_group);
        txtKosong = findViewById(R.id.kosong);
        floatingActionButton = findViewById(R.id.add);
        groupList.clear();
        list.setAdapter(groupAdapter);
        groupAdapter.notifyDataSetChanged();
        getGroup(session.getUsername());
        initToobar();


        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog();
            }
        });
        list.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        final String idGroup = ((TextView) view.findViewById(R.id.idGroup)).getText().toString();
                        final String namaGroup = ((TextView) view.findViewById(R.id.groupName)).getText().toString();
                        Bundle bundle = new Bundle();
                        bundle.putString("idGroup", idGroup);
                        bundle.putString("namaGroup", namaGroup);
                        intent = new Intent(getApplicationContext(), ListGroupMemberActivity.class);
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }
                }
        );
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            finishAndRemoveTask();
        else
            this.finishAffinity();

        startService(new Intent(this, TrackerService.class));
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                finishAndRemoveTask();
            else
                this.finishAffinity();

            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Click BACK once again to exit", Toast.LENGTH_SHORT).show();
    }



    private void startTrackerService() {
        session.getUsername();
        startService(new Intent(this, TrackerService.class));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, @NonNull int[]
            grantResults) {
        if (requestCode == PERMISSIONS_REQUEST && grantResults.length == 1
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startTrackerService();
        }
    }

    private void initToobar() {
        Toolbar addToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(addToolbar);
        ActionBar ab = getSupportActionBar();
        ab.setTitle("LIST MUTHOWIF");
    }

    private void getGroup(final String username) {
        progressDialogUtil.show();
        urlRequest = Url.FunctionName.SELECT_RELATED_GROUP + "username/" + username;
        Log.i(TAG, "urlRequest: " + urlRequest);
        JsonObjectRequest prosesRequest = new JsonObjectRequest(urlRequest,null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    getStatus = response.getString("status").trim();
                    if(getStatus.equals("Success")){
                        progressDialogUtil.dismiss();
                        JSONArray data = response.getJSONArray("group_detail");
                        for (int i = 0; i < data.length(); i++) {
                            ListGroup historyData = new ListGroup();
                            JSONObject obj = data.getJSONObject(i);
                            historyData.setGroupID(obj.getString("id_grup"));
                            historyData.setNama(obj.getString("nama_grup"));
                            groupList.add(historyData);
                        }
                    } else {
                        progressDialogUtil.dismiss();
                        txtKosong.setText("Belum ada group");
                        txtKosong.setVisibility(View.VISIBLE);
                        list.setVisibility(View.GONE);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                // notifikasi adanya perubahan data pada adapter
                groupAdapter.notifyDataSetChanged();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialogUtil.dismiss();
                VolleyLog.d("TAG", "Error: " + error.getMessage());
                String message = "";
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

                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
        AppController.getInstance().addToRequestQueue(prosesRequest);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu_group, menu);
        return super.onCreateOptionsMenu(menu);
    }


    private void showDialog(){
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(this);
        View mView = layoutInflaterAndroid.inflate(R.layout.user_input_dialog, null);
        AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(this);
        alertDialogBuilderUserInput.setView(mView);
        Toolbar toolbar = mView.findViewById(R.id.toolbar);
        toolbar.setTitle("New Muthawif");
        final EditText userInputDialogEditText = mView.findViewById(R.id.userInputDialog);
        alertDialogBuilderUserInput
                .setCancelable(false)
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogBox, int id) {
                        // ToDo get user input here

                        if (session.getUsername().equalsIgnoreCase("") || session.getUsername() == null) {
                            Toast.makeText(ListGroupActivity.this, "username tidak tercatat di session ketika login", Toast.LENGTH_SHORT).show();
                        } else {
                            String grName = userInputDialogEditText.getText().toString().trim();
                            if (grName.equalsIgnoreCase("") || grName == null) {
                                Toast.makeText(ListGroupActivity.this, "nama group tidak boleh kosong", Toast.LENGTH_SHORT).show();
                            } else {
                                addNewGroup(grName);
                            }
                        }

                    }
                })

                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogBox, int id) {
                                dialogBox.cancel();
                            }
                        });

        AlertDialog alertDialogAndroid = alertDialogBuilderUserInput.create();
        alertDialogAndroid.show();
    }

    private void addNewGroup(final String groupName) {
        String tag_string_req = "insert_new_group";
        urlRequest = Url.FunctionName.INSERT_NEW_GROUP;
        StringRequest strReq = new StringRequest(Request.Method.POST, urlRequest, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jObj = new JSONObject(response);
                    getStatus = jObj.getString("status").toString().trim();
                    if(getStatus.equals("Success")){
                        String message = jObj.getString("message");
                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                        intent = new Intent(
                                getApplicationContext(),
                                ListGroupActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        getMessage = jObj.getString("message");
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
                String message = "";
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

                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("username", session.getUsername());
                params.put("namagrup", groupName);
                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {
            session.setLogin(false);
            Toast.makeText(getApplicationContext(), "Logout Successfully", Toast.LENGTH_SHORT).show();
            intent = new Intent(ListGroupActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.action_profile) {
            intent = new Intent(ListGroupActivity.this, ProfileActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
}
