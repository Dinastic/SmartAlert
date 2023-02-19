package com.example.smartalert;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.cardview.widget.CardView;

import android.annotation.SuppressLint;
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

import java.time.LocalDate;
import java.util.ArrayList;

public class CheckActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    DatabaseReference reference;
    MyAdapter myAdapter;
    ArrayList<UserCounterAlerts> list;
    ArrayList<String> listCities,listDangers,listDates;

    EditText positionNot,positionDel;

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
        FirebaseMessaging.getInstance().subscribeToTopic("all");
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
                               Log.d("HELP2",listDates.get(notifyByPosition));
                               FcmNotificationsSender notificationsSender= new FcmNotificationsSender("/topics/all","DANGER","Be careful there is " + listDangers.get(notifyByPosition) +" near "+listCities.get(notifyByPosition),getApplicationContext(),CheckActivity.this);
                               notificationsSender.SendNotifications();

                           }
                       });

                       deleteButton.setOnClickListener(new View.OnClickListener() {
                           @Override
                           public void onClick(View view) {
                               deleteByPosition=Integer.parseInt(positionDel.getText().toString());
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
    }
    public void deleteMyItem(int deleteByPosition){
        list.remove(deleteByPosition);
    }
}




