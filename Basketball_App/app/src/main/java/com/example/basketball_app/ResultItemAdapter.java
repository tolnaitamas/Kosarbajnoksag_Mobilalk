package com.example.basketball_app;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class ResultItemAdapter extends RecyclerView.Adapter<ResultItemAdapter.ViewHolder> implements Filterable{
    private ArrayList<MatchResult> mMatchResultsData;
    private ArrayList<MatchResult> mMatchResultsDataAll;
    private Context mContext;
    private int lastPosition = -1;

    public ResultItemAdapter(Context context, ArrayList<MatchResult> itemsData) {
        this.mMatchResultsData = itemsData;
        this.mMatchResultsDataAll = itemsData;
        this.mContext = context;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ResultItemAdapter.ViewHolder holder, int position) {
        MatchResult currentItem = mMatchResultsData.get(position);
        
        holder.bindTo(currentItem);
    }

    @Override
    public int getItemCount() {
        return mMatchResultsData.size();
    }

    @Override
    public Filter getFilter() {
        return matchFilter;
    }

    private Filter matchFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            ArrayList<MatchResult> filterdList = new ArrayList<>();
            FilterResults results = new FilterResults();

            if (charSequence == null || charSequence.length() == 0){
                results.count = mMatchResultsDataAll.size();
                results.values = mMatchResultsDataAll;
            }else {
                String fiterPattern = charSequence.toString().toLowerCase().trim();

                for (MatchResult item : mMatchResultsDataAll){
                    if (item.getHome().toLowerCase().contains(fiterPattern) || item.getAway().toLowerCase().contains(fiterPattern)){
                        filterdList.add(item);
                    }
                }
                results.count = filterdList.size();
                results.values = filterdList;
            }

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            mMatchResultsData = (ArrayList) results.values;
            notifyDataSetChanged();

        }
    };

    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView homeText;
        private TextView awayText;
        private TextView homePts;
        private TextView awayPts;
        private TextView date;
        private ImageView homeImg;
        private ImageView awayImg;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            homeText = itemView.findViewById(R.id.homeName);
            awayText = itemView.findViewById(R.id.awayName);
            date = itemView.findViewById(R.id.date);
            homePts = itemView.findViewById(R.id.homePts);
            awayPts = itemView.findViewById(R.id.awayPts);
            homeImg = itemView.findViewById(R.id.homeImage);
            awayImg = itemView.findViewById(R.id.awayImage);





        }

        public void bindTo(MatchResult currentItem) {

            homeText.setText(currentItem.getHome());
            awayText.setText(currentItem.getAway());
            date.setText(currentItem.getDate());
            homePts.setText(currentItem.getHomePts());
            awayPts.setText(currentItem.getAwayPts());


            Glide.with(mContext).load(currentItem.getHomeImg()).into(homeImg);
            Glide.with(mContext).load(currentItem.getAwayImg()).into(awayImg);

            itemView.findViewById(R.id.delete).setOnClickListener(view -> ((ResultListActivity)mContext).deleteItem(currentItem));

        }
    };
}

