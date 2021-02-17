package com.km.ao3reader;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class WorkListAdapter extends RecyclerView.Adapter<WorkListAdapter.WorkViewHolder> {
    //TODO: add a progress bar for how far you are in the PDF or smth
    private final String[] works;
    private final LayoutInflater inflater;
    private OnWorkListener onWorkListener;

    public WorkListAdapter(Context context, String[] works, OnWorkListener onWorkListener) {
        inflater = LayoutInflater.from(context);
        this.works = works;
        this.onWorkListener = onWorkListener;
    }

    @NonNull
    @Override
    public WorkViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemview = inflater.inflate(R.layout.worklist_item, parent, false);
        return new WorkViewHolder(itemview, this, onWorkListener);
    }

    @Override
    public void onBindViewHolder(@NonNull WorkViewHolder holder, int position) {
        String current = works[position];
        holder.titleView.setText(current);

    }

    @Override
    public int getItemCount() {
        return works.length;
    }

    class WorkViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final TextView titleView;
        final WorkListAdapter adapter;
        private OnWorkListener onWorkListener;

        public WorkViewHolder(View itemView, WorkListAdapter adapter, OnWorkListener onWorkListener) {
            super(itemView);
            titleView = itemView.findViewById(R.id.work_title);
            this.adapter = adapter;
            this.onWorkListener = onWorkListener;
            itemView.setOnClickListener(this);
        }


        @Override
        public void onClick(View v) {
            int position = getLayoutPosition();
            String work = works[position];
            onWorkListener.onWorkClick(work);
            //line below is for if we mutate the list. Don't think we will
            adapter.notifyDataSetChanged();

        }

    }
    public interface OnWorkListener{
        void onWorkClick(String work);
    }
}
