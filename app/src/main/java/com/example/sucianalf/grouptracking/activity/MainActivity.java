package com.example.sucianalf.grouptracking.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
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
import com.example.sucianalf.grouptracking.util.AppController;
import com.example.sucianalf.grouptracking.R;
import com.example.sucianalf.grouptracking.api.Url;
import com.example.sucianalf.grouptracking.util.ProgressDialogUtil;
import com.example.sucianalf.grouptracking.util.SessionManager;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    EditText username, password;
    TextView txtCreateAccount;
    private static final String TAG = "MyActivity";
    private Bundle bundle;
    private SessionManager session;
    Intent intent;
    public String getStatus, getMessage, urlRequest;
    boolean doubleBackToExitPressedOnce = false;
    private ProgressDialogUtil progressDialogUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        txtCreateAccount=findViewById(R.id.createAccount);
        username = findViewById(R.id.login_emailid);
        password = findViewById(R.id.login_password);
        session = new SessionManager(getApplicationContext());
        progressDialogUtil = new ProgressDialogUtil(this, ProgressDialog.STYLE_SPINNER, false);
        if(session.isLoggedIn()){
            intent = new Intent(
                    getApplicationContext(),
                    ListGroupActivity.class);
            startActivity(intent);
            finish();
        }

        txtCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(
                        getApplicationContext(),
                        SignUpActivity.class);
                startActivity(intent);
            }
        });
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

    public void buttonClickFunction(View v)
    {
        requestLogin();
    }

    public void requestLogin()
    {
        progressDialogUtil.setMessage("Logged in...");
        progressDialogUtil.show();
        final String uname = username.getText().toString();
        final String pass = password.getText().toString();
        urlRequest = Url.FunctionName.LOGIN+"username/"+uname+"/password/"+pass;
        Log.i(TAG, "urlRequest: "+urlRequest);

        JsonObjectRequest prosesRequest = new JsonObjectRequest(urlRequest,null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    getStatus = response.getString("status").trim();
                    if(getStatus.equals("Success")){
                        progressDialogUtil.dismiss();
                        JSONArray data = response.getJSONArray("userDetail");
                        JSONObject obj = data.getJSONObject(0);
                        session.setAlamat(obj.getString("alamat"));
                        session.setUsername(obj.getString("username"));
                        session.setEmail(obj.getString("email"));
                        session.setNoTelp(obj.getString("no_telp"));
                        session.setImage(obj.getString("image_user"));
                        session.setLogin(true);

                        Toast.makeText(getApplicationContext(), response.getString("message"), Toast.LENGTH_SHORT).show();
                        intent = new Intent(getApplicationContext(), ListGroupActivity.class);
                        bundle = new Bundle();
                        bundle.putString("username", uname);
                        intent.putExtras(bundle);
                        startActivity(intent);
                        finish();
                    }else{
                        progressDialogUtil.dismiss();
                        getMessage = response.getString("message");
                        Toast.makeText(getApplicationContext(), getMessage, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
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
}
