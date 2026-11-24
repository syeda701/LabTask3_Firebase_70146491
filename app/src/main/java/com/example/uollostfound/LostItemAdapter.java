package com.example.uollostfound;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class LostItemAdapter
        extends RecyclerView.Adapter<LostItemAdapter.ViewHolder> {

    // Item click callback (Lab Task 2)
    public interface OnItemClickListener {
        void onItemClick(LostItem item);
    }

    private final List<LostItem> items;
    private final OnItemClickListener listener;

    public LostItemAdapter(List<LostItem> items,
                           OnItemClickListener listener) {
        this.items = items;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                         int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_lost, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder,
                                 int position) {
        LostItem item = items.get(position);

        holder.tvItemName.setText(item.getItemName());
        holder.tvType.setText(item.getType().toUpperCase());
        holder.tvLocation.setText("Location: " + item.getLocation());
        holder.tvContact.setText("Contact: " + item.getContact());

        // Lost = red badge, Found = green badge
        if ("Found".equals(item.getType())) {
            holder.tvType.setTextColor(Color.parseColor("#2E7D32"));
        } else {
            holder.tvType.setTextColor(Color.parseColor("#C62828"));
        }

        // Returned cards look different (grey card)
        if ("Returned".equals(item.getStatus())) {
            holder.cardLost.setCardBackgroundColor(
                    Color.parseColor("#E0E0E0"));
            holder.tvStatus.setText("\u2713 Returned");
            holder.tvStatus.setTextColor(Color.parseColor("#2E7D32"));
        } else {
            holder.cardLost.setCardBackgroundColor(Color.WHITE);
            holder.tvStatus.setText("\u25CF Open");
            holder.tvStatus.setTextColor(Color.parseColor("#EF6C00"));
        }

        holder.itemView.setOnClickListener(v ->
                listener.onItemClick(item));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    // Static inner ViewHolder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardLost;
        TextView tvItemName, tvType, tvLocation, tvContact, tvStatus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardLost = itemView.findViewById(R.id.cardLost);
            tvItemName = itemView.findViewById(R.id.tvItemName);
            tvType = itemView.findViewById(R.id.tvType);
            tvLocation = itemView.findViewById(R.id.tvLocation);
            tvContact = itemView.findViewById(R.id.tvContact);
            tvStatus = itemView.findViewById(R.id.tvStatus);
        }
    }
}
