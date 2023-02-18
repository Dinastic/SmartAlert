package com.example.smartalert;

import static android.app.PendingIntent.getActivity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

    Context context;
    DatabaseReference reference;
    CheckActivity checkActivity;

    ArrayList<UserCounterAlerts> list;


    public MyAdapter(Context context, ArrayList<UserCounterAlerts> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item,parent,false);
        return  new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        reference = FirebaseDatabase.getInstance().getReference("AlertCounter");
        FirebaseMessaging.getInstance().subscribeToTopic("all");
        UserCounterAlerts alert = list.get(position);
        holder.dangerType.setText(alert.dangerType);
        holder.time.setText(alert.time);
        holder.address.setText(alert.city);
        reference.addValueEventListener(new ValueEventListener() {


            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String s;
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                        UserCounterAlerts alert = dataSnapshot1.getValue(UserCounterAlerts.class);
                        /*s = dataSnapshot.getValue().toString();*/
                        //list.add(alert);
                        /*Log.d("HELP", s);*/
                        holder.notifyButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                FcmNotificationsSender notificationsSender= new FcmNotificationsSender("/topics/all", alert.city,view.getContext(),checkActivity);
                                notificationsSender.SendNotifications();

                            }
                        });

                    }
                }
                notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        public Button notifyButton;
        TextView dangerType, time, address;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            notifyButton = (Button) itemView.findViewById(R.id.notifyButton);
            dangerType = itemView.findViewById(R.id.specificDanger);
            time = itemView.findViewById(R.id.specificTime);
            address = itemView.findViewById(R.id.specificAddress);


        }
    }

}