package com.example.sucianalf.grouptracking.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.sucianalf.grouptracking.adapter.MemberGroupAdapter;
import com.example.sucianalf.grouptracking.util.AppController;
import com.example.sucianalf.grouptracking.model.DataGroupMember;
import com.example.sucianalf.grouptracking.R;
import com.example.sucianalf.grouptracking.api.Url;
import com.example.sucianalf.grouptracking.util.ProgressDialogUtil;
import com.example.sucianalf.grouptracking.util.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ListGroupMemberActivity extends AppCompatActivity {
    public Bundle getBundle;
    private ListView list;
    private TextView txtKosong;
    public ImageView imgUser;
    private SessionManager sessionManager;
    private List<DataGroupMember> groupMemberList = new ArrayList<>();
    private MemberGroupAdapter memberGroupAdapter;
    public String namaGroup,idGroup,memberName ="";
    private Intent intent;
    public String getStatus, getMessage, urlRequest;
    private FloatingActionButton floatingActionButton;
    private ProgressDialogUtil progressDialogUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_group_member);
        getBundle = getIntent().getExtras();
        sessionManager = new SessionManager(getApplicationContext());
        idGroup= getBundle.getString("idGroup");
        namaGroup= getBundle.getString("namaGroup");
        memberGroupAdapter = new MemberGroupAdapter (ListGroupMemberActivity.this,groupMemberList);
        list = findViewById(R.id.list_group_member);
        txtKosong= findViewById(R.id.kosong);
        imgUser = findViewById(R.id.icon);
        groupMemberList.clear();
        list.setAdapter(memberGroupAdapter);
        progressDialogUtil = new ProgressDialogUtil(this, ProgressDialog.STYLE_SPINNER, false);
        floatingActionButton = findViewById(R.id.add);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog();
            }
        });
        memberGroupAdapter.notifyDataSetChanged();
        initToobar();
        getMembers(idGroup);
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

    private void initToobar(){
        Toolbar addToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(addToolbar);
        ActionBar ab = getSupportActionBar();
        ab.setTitle(namaGroup);
        ab.setDisplayHomeAsUpEnabled(true);
    }

    private void getMembers(final String idGroup){
        progressDialogUtil.show();
        urlRequest = Url.FunctionName.SELECT_RELATED_GROUP_MEMBER+idGroup+"/username/"+sessionManager.getUsername();
        Log.i("TAG", "urlRequest: "+urlRequest);
        JsonObjectRequest prosesRequest = new JsonObjectRequest(urlRequest,null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    getStatus = response.getString("status").trim();
                    if(getStatus.equals("Success")){
                        progressDialogUtil.dismiss();
                        JSONArray data = response.getJSONArray("member_detail");
                        for(int i=0; i<data.length(); i++){
                            DataGroupMember dataGroupMember= new DataGroupMember();
                            JSONObject obj = data.getJSONObject(i);
                            dataGroupMember.setMemberID(obj.getString("id_anggota"));
                            dataGroupMember.setMemberName(obj.getString("id_user"));
                            dataGroupMember.setMemberPhone(obj.getString("phone"));
                            dataGroupMember.setMemberPhoto(obj.getString("image_user"));
                            groupMemberList.add(dataGroupMember);
                        }
                    }else{
                        progressDialogUtil.dismiss();
                        txtKosong.setText("Tidak ada member");
                        txtKosong.setVisibility(View.VISIBLE);
                        list.setVisibility(View.GONE);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                // notifikasi adanya perubahan data pada adapter
                memberGroupAdapter.notifyDataSetChanged();
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
        inflater.inflate(R.menu.main_menu_member_group, menu);
        return  super.onCreateOptionsMenu(menu);
    }

    private void showDialog(){
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(this);
        View mView = layoutInflaterAndroid.inflate(R.layout.user_input_dialog, null);
        AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(this);
        alertDialogBuilderUserInput.setView(mView);
        Toolbar toolbar = mView.findViewById(R.id.toolbar);
        toolbar.setTitle("Add Jamaah");
        final EditText userInputDialogEditText = mView.findViewById(R.id.userInputDialog);

        alertDialogBuilderUserInput
                .setCancelable(false)
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogBox, int id) {
                        // ToDo get user input here
                        String membrName = userInputDialogEditText.getText().toString().trim();
                        if(membrName.equalsIgnoreCase("")||membrName==null){
                            Toast.makeText(getApplicationContext(), "nama group tidak boleh kosong", Toast.LENGTH_SHORT).show();
                        }else {
                            addNewGroupMember(membrName);
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

    private void deleteGroup(String user_id, String id_group){
        urlRequest = Url.FunctionName.DELETE_GROUP_MEMBER+id_group+"/username/"+user_id;
        Log.i("TAG", "deleteGroup: "+urlRequest);
        JsonObjectRequest prosesRequest = new JsonObjectRequest(urlRequest,null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    getStatus = response.getString("status").trim();
                    getMessage = response.getString("message").trim();

                    if(getStatus.equals("Success")){
                        Toast.makeText(ListGroupMemberActivity.this, ""+getMessage, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(ListGroupMemberActivity.this, ""+getMessage, Toast.LENGTH_SHORT).show();
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
        });
        AppController.getInstance().addToRequestQueue(prosesRequest);
    }

    private void addNewGroupMember(final String memberName )
    {
        String tag_string_req = "req_register";
        urlRequest = Url.FunctionName.INSERT_NEW_GROUP_MEMBER+idGroup+"/user/"+memberName;
        JsonObjectRequest prosesRequest = new JsonObjectRequest(urlRequest,null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    getStatus = response.getString("status").trim();
                    getMessage = response.getString("message");
                    if(getStatus.equals("Success")){
                        Toast.makeText(getApplicationContext(), ""+ getMessage, Toast.LENGTH_SHORT).show();
                        intent = new Intent(
                                getApplicationContext(),
                                ListGroupActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(getApplicationContext(), getMessage, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();

            }
        });
        AppController.getInstance().addToRequestQueue(prosesRequest, tag_string_req);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id==R.id.action_map){
            intent = new Intent(ListGroupMemberActivity.this,DisplayNavigation.class);
            intent.putExtra("groupID", Integer.parseInt(idGroup));
            intent.putExtra("groupName", namaGroup);
            startActivity(intent);
        }
        else if(id == R.id.action_delete) {
            deleteGroup(sessionManager.getUsername(), idGroup);
            intent = new Intent(ListGroupMemberActivity.this, ListGroupActivity.class);
            startActivity(intent);
            finish();
        }
        else if(id == android.R.id.home){
            ListGroupMemberActivity.this.finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
