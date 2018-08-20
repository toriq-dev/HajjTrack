package com.example.sucianalf.grouptracking.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.sucianalf.grouptracking.R;
import com.example.sucianalf.grouptracking.util.SessionManager;

public class ProfileActivity extends AppCompatActivity {


    private String TAG = ProfileActivity.class.getSimpleName();
    private TextView user, email, alamat, no_telp;
    private ImageView avatar;
    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        initToobar();
        user = findViewById(R.id.user);
        email = findViewById(R.id.email);
        alamat = findViewById(R.id.alamat);
        no_telp = findViewById(R.id.tlp);
        avatar = findViewById(R.id.avatar);

        session = new SessionManager(getApplicationContext());
        user.setText(session.getUsername());
        email.setText(session.getEmail());
        alamat.setText(session.getAlamat());
        no_telp.setText(session.getNoTelp());
        Glide.with(this)
                .load(session.getImage())
                .placeholder(R.drawable.user).into(avatar);
        Log.d(TAG, "onCreate: New Profile");

    }

    private void initToobar() {
        Toolbar addToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(addToolbar);
        ActionBar ab = getSupportActionBar();
        ab.setTitle("Profile");
        ab.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_profile, menu);


        return  super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == android.R.id.home){
            ProfileActivity.this.finish();
        }else if(id == R.id.action_edit){
            startActivity(new Intent(ProfileActivity.this, EditProfileActivity.class));
        }
        return super.onOptionsItemSelected(item);
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

}
