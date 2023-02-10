package com.example.smartalert;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

    Context context;

    ArrayList<Alert> list;


    public MyAdapter(Context context, ArrayList<Alert> list) {
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

        Alert alert = list.get(position);
        holder.dangerType.setText(alert.getDangerType());
        holder.time.setText(alert.getTime());
        holder.address.setText(alert.getAddress());


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView dangerType, time, address;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            dangerType = itemView.findViewById(R.id.specificDanger);
            time = itemView.findViewById(R.id.specificTime);
            address = itemView.findViewById(R.id.specificAddress);

        }
    }

}