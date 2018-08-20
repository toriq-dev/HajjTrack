package com.example.sucianalf.grouptracking.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import com.android.volley.toolbox.StringRequest;
import com.example.sucianalf.grouptracking.util.AppController;
import com.example.sucianalf.grouptracking.R;
import com.example.sucianalf.grouptracking.api.Url;
import com.example.sucianalf.grouptracking.util.ProgressDialogUtil;
import com.onesignal.OneSignal;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity {
    private EditText edtUserName,edtEmail,edtTlp,edtLocation,edtPassword,edtConfirmPassword;
    private Button btnSignUp;
    private TextView txtAlready;
    private ProgressDialogUtil progressDialogUtil;
    private Intent intent;
    private String getStatus, getMessage, urlRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        progressDialogUtil = new ProgressDialogUtil(this, ProgressDialog.STYLE_SPINNER, false);
        initComponent();
        txtAlready.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(
                        getApplicationContext(),
                        MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OneSignal.idsAvailable(new OneSignal.IdsAvailableHandler() {
                    @Override
                    public void idsAvailable(String userId, String registrationId) {
                        if (registrationId != null){
                            registerUser(userId);
                        }else{
                            Toast.makeText(getApplicationContext(), "ID ONE SIGNAL KOSONG, RESTART APLIKASI SEKARANG", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
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


    private void initComponent(){
        edtUserName = findViewById(R.id.fullName);
        edtEmail = findViewById(R.id.userEmailId);
        edtTlp = findViewById(R.id.mobileNumber);
        edtLocation = findViewById(R.id.location);
        edtPassword = findViewById(R.id.password);
        edtConfirmPassword = findViewById(R.id.confirmPassword);
        btnSignUp = findViewById(R.id.signUpBtn);
        txtAlready = findViewById(R.id.already_user);
    }

    private void registerUser(final String oneSignalID){
        progressDialogUtil.setMessage("Please wait...");
        progressDialogUtil.show();
        final String username = edtUserName.getText().toString();
        final String email = edtEmail.getText().toString();
        final String tlp = edtTlp.getText().toString();
        final String location = edtLocation.getText().toString();
        final String passowrd = edtPassword.getText().toString();
        final String confirmPassowrd = edtConfirmPassword.getText().toString();

        String tag_string_req = "req_register";
        urlRequest = Url.FunctionName.REGISTER_NEW_USER;
        StringRequest strReq = new StringRequest(Request.Method.POST,
                urlRequest, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jObj = new JSONObject(response);
                    getStatus = jObj.getString("status").trim();
                    getMessage = jObj.getString("message").trim();
                    if(getStatus.equals("Success")){
                        progressDialogUtil.dismiss();
                        Toast.makeText(getApplicationContext(), getMessage, Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(
                                getApplicationContext(),
                                MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        progressDialogUtil.dismiss();
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
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("username", username);
                params.put("email", email);
                params.put("alamat", location);
                params.put("tlp", tlp);
                params.put("password", passowrd);
                params.put("confirmPassword", confirmPassowrd);
                params.put("oneSignalID", oneSignalID);
                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }
}
