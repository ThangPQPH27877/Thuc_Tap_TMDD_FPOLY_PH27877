package com.example.thuc_tap_tmdd_fpoly.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.thuc_tap_tmdd_fpoly.databinding.SliderItemContainerBinding;


public class SliderImageAdapter extends RecyclerView.Adapter<SliderImageAdapter.ViewHolder> {

    Context context;
    public SliderImageAdapter(Context context) {

        this.context = context;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(SliderItemContainerBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 5;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        SliderItemContainerBinding binding;
        public ViewHolder(SliderItemContainerBinding itemView) {
            super(itemView.getRoot());
            this.binding = itemView;
        }

    }

}
