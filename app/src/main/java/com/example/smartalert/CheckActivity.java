package com.example.smartalert;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.cardview.widget.CardView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CheckActivity extends AppCompatActivity implements LocationListener {

    RecyclerView recyclerView;
    DatabaseReference reference;
    MyAdapter myAdapter;
    ArrayList<UserCounterAlerts> list;
    ArrayList<String> listCities,listDangers,listDates;

    EditText positionNot,positionDel;
    private LocationManager locationManager;

    Button notifyButton,deleteButton;

    int notifyByPosition,deleteByPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check);

        recyclerView = findViewById(R.id.myList);
        positionNot = findViewById(R.id.notifyText);
        positionDel = findViewById(R.id.deleteText);
        notifyButton = (Button)findViewById(R.id.notifyButton);
        deleteButton = (Button)findViewById(R.id.deleteButton);

        reference = FirebaseDatabase.getInstance().getReference("AlertCounter");
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        list = new ArrayList<>();
        listCities = new ArrayList<>();
        listDangers = new ArrayList<>();
        listDates = new ArrayList<>();
        myAdapter = new MyAdapter(this,list);
        recyclerView.setAdapter(myAdapter);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},123);
        }
            locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5, this);

            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        try {
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            String city = addresses.get(0).getLocality();
            LocalDate localDate = LocalDate.now();
            String date = localDate.toString();
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                            UserCounterAlerts alert = dataSnapshot1.getValue(UserCounterAlerts.class);
                            if(date.equals(alert.time.substring(0, 10)) && alert.count > 3){
                                list.add(alert);
                                listCities.add(alert.city);
                                listDangers.add(alert.dangerType);
                                listDates.add(alert.time.substring(0,10));
                            }

                            notifyButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    notifyByPosition=Integer.parseInt(positionNot.getText().toString());
                                    if(city.equals(listCities.get(notifyByPosition))){
                                    FcmNotificationsSender notificationsSender= new FcmNotificationsSender("/topics/all","DANGER","Be careful there is " + listDangers.get(notifyByPosition) +" near "+listCities.get(notifyByPosition),getApplicationContext(),CheckActivity.this);
                                    notificationsSender.SendNotifications();
                                    }
                                }
                            });

                            deleteButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    deleteByPosition=Integer.parseInt(positionDel.getText().toString());
                                    reference.child(listCities.get(deleteByPosition)).child(listDates.get(deleteByPosition)).removeValue();
                                    deleteMyItem(deleteByPosition);
                                    myAdapter.notifyItemRemoved(deleteByPosition);
                                }
                            });
                        }
                    }
                    myAdapter.notifyDataSetChanged();
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void deleteMyItem(int deleteByPosition){
        list.remove(deleteByPosition);
    }
    @Override
    public void onLocationChanged(@NonNull Location location) {

    }
}




