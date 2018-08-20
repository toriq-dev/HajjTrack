package com.example.sucianalf.grouptracking.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.example.sucianalf.grouptracking.util.AppController;
import com.example.sucianalf.grouptracking.R;
import com.example.sucianalf.grouptracking.api.Url;
import com.example.sucianalf.grouptracking.util.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfileActivity extends AppCompatActivity {
    private EditText edtUserName, edtEmail, edtTlp, edtLocation, edtPassword, edtConfirmPassword;
    private SessionManager session;
    private Button btnEditProfil;
    private CircleImageView profilePictureImageView;
    private final int PICK_IMAGE = 500;
    private Bitmap bitmapProfilePicture;
    private String encodedProfilePicture;
    private boolean isImageChanged = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        edtUserName = (EditText) findViewById(R.id.fullName);
        edtEmail = (EditText) findViewById(R.id.userEmailId);
        edtTlp = (EditText) findViewById(R.id.mobileNumber);
        edtLocation = (EditText) findViewById(R.id.location);
        edtPassword = (EditText) findViewById(R.id.password);
        btnEditProfil = findViewById(R.id.editProfilBtn);
        profilePictureImageView = findViewById(R.id.profilePicture);

        session = new SessionManager(getApplicationContext());
        edtUserName.setText(session.getUsername());
        edtEmail.setText(session.getEmail());
        edtLocation.setText(session.getAlamat());
        edtTlp.setText(session.getNoTelp());
        edtPassword.setText(session.getPassword());
        Log.d("TAG", "getPassword: " + session.getPassword());
//        Glide.with(this).load(session.getImage()).placeholder(R.drawable.user).into(avatar);

        btnEditProfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editProfil();
            }
        });

        getProfilePicture();

        profilePictureImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveToMedia();
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

    private void editProfil() {

        final String username = edtUserName.getText().toString();
        final String email = edtEmail.getText().toString();
        final String tlp = edtTlp.getText().toString();
        final String location = edtLocation.getText().toString();
        final String password = edtPassword.getText().toString();

        String tag_string_req = "req_register";
        StringRequest strReq = new StringRequest(Request.Method.POST,
                Url.FunctionName.EDIT_PROFIL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("TAG", "Edit user Response >>>>>>>>>" + response.toString());
                try {
                    JSONObject jObj = new JSONObject(response);
                    String status = jObj.getString("status").toString().trim();
                    if (status.equals("Success")) {
                        Toast.makeText(getApplicationContext(), "Profile successfully Updated. Please re-login!", Toast.LENGTH_LONG).show();
                        Log.d("masuk ke get value", " >>>>>>>>> OK!");
                        session.setLogin(false);
//                        if (Build.VERSION_CODES.KITKAT <= Build.VERSION.SDK_INT) {
//                            ((ActivityManager)EditProfileActivity.this.getSystemService(ACTIVITY_SERVICE))
//                                    .clearApplicationUserData(); // note: it has a return value!
//                            Log.d("TAG", "onSksesProfileEdit: data clear");
//                        }
                        Intent intent = new Intent(
                                getApplicationContext(),
                                MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        String errorMsg = jObj.getString("message");
                        Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("TAG", "Registration New Contact Error >>>>>>>>> " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                error.printStackTrace();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                params.put("username", username);
                params.put("email", email);
                params.put("no_telp", tlp);
                params.put("password", password);
                params.put("alamat", location);
                if(isImageChanged == true){
                    params.put("image_user", encodedProfilePicture);
                }
                else {
                    params.put("image_user", "default");
                }

                Log.d("param response >>>>>>", params.toString());
                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void getProfilePicture() {
        Glide.with(this)
                .load(session.getImage())
                .dontAnimate()
                .placeholder(R.mipmap.ic_launcher_round)
                .into(profilePictureImageView);

    }

    private void moveToMedia() {

        Intent chooseImage = new Intent();
        chooseImage.setType("image/*");
        chooseImage.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(chooseImage, "Select Picture"), PICK_IMAGE);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK) {
            Uri image = data.getData();

            try {

                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), image);
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                byte[] byteArray = byteArrayOutputStream.toByteArray();
                encodedProfilePicture = Base64.encodeToString(byteArray, Base64.DEFAULT);
                profilePictureImageView.setImageBitmap(bitmap);

                isImageChanged = true;

            } catch (IOException e) {
                e.printStackTrace();
            }

        } else if (requestCode == PICK_IMAGE && resultCode == RESULT_CANCELED) {
            Toast.makeText(this, "No File Chosen", Toast.LENGTH_SHORT).show();
            isImageChanged = false;
        }
    }
}