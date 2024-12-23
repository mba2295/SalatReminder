package com.mubilal.salatreminder.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mubilal.salatreminder.R;
import com.mubilal.salatreminder.models.PrayerTime;

import java.util.List;

public class PrayerTimeAdapter extends RecyclerView.Adapter<PrayerTimeAdapter.PrayerTimeViewHolder> {

    private List<PrayerTime> prayerTimes;

    // Constructor
    public PrayerTimeAdapter(List<PrayerTime> prayerTimes) {
        this.prayerTimes = prayerTimes;
    }

    @NonNull
    @Override
    public PrayerTimeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_prayer, parent, false);
        return new PrayerTimeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PrayerTimeViewHolder holder, int position) {
        PrayerTime prayerTime = prayerTimes.get(position);
        holder.prayerName.setText(prayerTime.getPrayerName());
        holder.prayerTime.setText(prayerTime.getPrayerTime());
    }

    @Override
    public int getItemCount() {
        return prayerTimes.size();
    }

    public static class PrayerTimeViewHolder extends RecyclerView.ViewHolder {
        TextView prayerName;
        TextView prayerTime;

        public PrayerTimeViewHolder(View itemView) {
            super(itemView);
            prayerName = itemView.findViewById(R.id.tvPrayerName);
            prayerTime = itemView.findViewById(R.id.tvPrayerTime);
        }
    }

    public void updatePrayerTimes(List<PrayerTime> newPrayerTimes) {
        this.prayerTimes = newPrayerTimes;
        notifyDataSetChanged();
    }
}

