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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class NotifyActivity extends AppCompatActivity implements LocationListener, AdapterView.OnItemSelectedListener {
    private FirebaseDatabase database;
    private DatabaseReference reference,reference2;
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
        reference2 = database.getReference("AlertCounter");

        textViewLocation = (TextView) findViewById(R.id.locationtext);
        //buttonLocation = (Button) findViewById(R.id.locationButton);
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
                String address = addresses.get(0).getAddressLine(0).replace("."," ");
                String city = addresses.get(0).getLocality();
                String comment = editTextComment.getText().toString().trim();
                String dangerType = spinner.getSelectedItem().toString();
                Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                String time = timestamp.toString();
                System.out.println(city);

                if(comment.isEmpty()){
                    editTextComment.setError("Say something!");
                    editTextComment.requestFocus();
                    return;
                }
                progressBar.setVisibility(View.VISIBLE);
                Alert alert = new Alert(comment,time,address,dangerType);
                reference.child(alert.address).setValue(alert).addOnCompleteListener(new OnCompleteListener<Void>() {
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

                UserCounterAlerts userCounterAlerts = new UserCounterAlerts(city,1,alert.time,alert.dangerType);
                //An DEN iparxei afto to alert ston pinaka AlertCounter tote ftiaxnei kainourio
                reference2.child(city).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(!snapshot.exists()) {
                            reference2.child(city).setValue(userCounterAlerts).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Toast.makeText(NotifyActivity.this,"Counter completed!",Toast.LENGTH_LONG).show();
                                        progressBar.setVisibility(View.GONE);
                                    }
                                    else{
                                        Toast.makeText(NotifyActivity.this, "Failed to count! Try Again!", Toast.LENGTH_SHORT).show();
                                        progressBar.setVisibility(View.GONE);
                                    }
                                }
                            });
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                //An iparxei tote prepei na ginei update
                reference2.child(city).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String count = "";
                        snapshot.getChildren();
                        HashMap counter = new HashMap<>();

                        if (!snapshot.exists()){
                            counter.put("count",1);
                            counter.put("dangerType",userCounterAlerts.dangerType);
                            counter.put("time",userCounterAlerts.time);

                        }
                        else{
                            count = snapshot.child("count").getValue().toString();
                            counter.put("count",Integer.valueOf(count) + 1);
                            counter.put("dangerType",userCounterAlerts.dangerType);
                            counter.put("time",userCounterAlerts.time);
                        }
                        reference2.child(city).updateChildren(counter).addOnCompleteListener(new OnCompleteListener() {
                            @Override
                            public void onComplete(@NonNull Task task) {
                                if(task.isSuccessful()){
                                    Toast.makeText(NotifyActivity.this,"Update completed!",Toast.LENGTH_LONG).show();
                                    progressBar.setVisibility(View.GONE);
                                }
                                else{
                                    Toast.makeText(NotifyActivity.this, "Failed to update! Try Again!", Toast.LENGTH_SHORT).show();
                                    progressBar.setVisibility(View.GONE);
                                }
                            }
                        });
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(NotifyActivity.this,"Failed1",Toast.LENGTH_LONG).show();
            }
        }catch(Exception e){
            e.printStackTrace();
            Toast.makeText(NotifyActivity.this,"Failed2",Toast.LENGTH_LONG).show();
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