package com.skyle.foodbuildings;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

public class FoodTruckListAdapter extends RecyclerView.Adapter<FoodTruckListAdapter.ViewHolder> {

    private Listener listener;

    interface Listener {
        void onClick(int pos);
    }

    public void setListener(Listener l) {
        listener = l;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LinearLayout v = (LinearLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.food_truck_view, parent, false);
        return new FoodTruckListAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int pos) {
        LinearLayout linearLayout = holder.v;
        TextView name = linearLayout.findViewById(R.id.foodtruck_name);
        TextView address = linearLayout.findViewById(R.id.foodtruck_address);
        TextView description = linearLayout.findViewById(R.id.foodtruck_description);
        name.setText(FoodTruckData.trucks[pos].name);
        address.setText(FoodTruckData.trucks[pos].address);
        description.setText(FoodTruckData.trucks[pos].description);
        linearLayout.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                listener.onClick(pos);
            }
        });
    }

    @Override
    public int getItemCount() {
        return FoodTruckData.trucks.length;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public LinearLayout v;
        public ViewHolder(LinearLayout itemView) {
            super(itemView);
            v = itemView;
        }
    }

}
