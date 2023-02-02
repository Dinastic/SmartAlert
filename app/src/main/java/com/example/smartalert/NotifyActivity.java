package com.example.smartalert;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.sql.Timestamp;
import java.util.List;
import java.util.Locale;

public class NotifyActivity extends AppCompatActivity implements LocationListener, AdapterView.OnItemSelectedListener {
    private FirebaseDatabase database;
    private DatabaseReference reference;
    private Button buttonLocation,confirmDanger;
    private TextView textViewLocation;
    private EditText  editTextComment;
    private LocationManager locationManager;
    private Spinner spinner;
    private ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notify);

        database = FirebaseDatabase.getInstance();
        reference = database.getReference("Alerts");

        textViewLocation = (TextView) findViewById(R.id.locationtext);
        buttonLocation = (Button) findViewById(R.id.locationButton);
        confirmDanger = (Button) findViewById(R.id.confirmDanger);

        editTextComment = (EditText) findViewById(R.id.comment);

        spinner = (Spinner) findViewById(R.id.chooseDanger);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.dangers, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},123);
        }
    }

    public void confirm(View view) {
                Toast.makeText(this, "hi", Toast.LENGTH_SHORT).show();
                notifyDanger();
    }

    @SuppressLint("MissingPermission")
    public void notifyDanger() {
        try {
            locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,5000,5,this);
            try {
                //Gets the location
                Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                List<Address> addresses = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);

                //Initializes variables for inserting into Database.
                String address = addresses.get(0).getAddressLine(0);
                String comment = editTextComment.getText().toString().trim();
                String dangerType = spinner.getSelectedItem().toString();
                Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                String time = timestamp.toString();

                if(comment.isEmpty()){
                    editTextComment.setError("Say something!");
                    editTextComment.requestFocus();
                    return;
                }
                progressBar.setVisibility(View.VISIBLE);
                Alert alert = new Alert(comment,time,address,dangerType);
                reference.setValue(alert).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(NotifyActivity.this,"Alert has been successfully submitted!",Toast.LENGTH_LONG).show();
                            progressBar.setVisibility(View.GONE);
                        }
                        else{
                            Toast.makeText(NotifyActivity.this, "Failed to submit! Try Again!", Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });

                textViewLocation.setText(address);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {

    }

    @Override
    public void onLocationChanged(@NonNull List<Location> locations) {
        LocationListener.super.onLocationChanged(locations);
    }

    @Override
    public void onFlushComplete(int requestCode) {
        LocationListener.super.onFlushComplete(requestCode);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        LocationListener.super.onStatusChanged(provider, status, extras);
    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {
        LocationListener.super.onProviderEnabled(provider);
    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {
        LocationListener.super.onProviderDisabled(provider);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}