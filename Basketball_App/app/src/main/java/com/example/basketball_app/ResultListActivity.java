package com.example.basketball_app;

import android.content.Intent;
import android.content.res.TypedArray;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.MenuItemCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class ResultListActivity extends AppCompatActivity {

    private MediaPlayer mp;

    private FirebaseUser user;
    private FirebaseFirestore mFirestore;
    private CollectionReference mItems;

    Spinner spinnerLimit;
    Spinner spinnerRendezes;

    private RecyclerView mRecyclerView;
    private ArrayList<MatchResult> mItemsData;
    private ResultItemAdapter mAdapter;

    private int gridNumber = 1;

    private NotificationHelper mNotificationHelper;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_result_list);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        spinnerLimit = findViewById(R.id.limitInput);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.limit, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerLimit.setAdapter(adapter);

        int defaultPosition = adapter.getPosition("Össz.");
        spinnerLimit.setSelection(defaultPosition);

        spinnerRendezes = findViewById(R.id.rendezesInput);
        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(this,
                R.array.rendezes, android.R.layout.simple_spinner_item);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRendezes.setAdapter(adapter1);

        int defaultPosition1 = adapter.getPosition("Növ.");
        spinnerRendezes.setSelection(defaultPosition1);

        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null){
            finish();
        }

        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, gridNumber));
        mItemsData = new ArrayList<>();

        mAdapter = new ResultItemAdapter(this, mItemsData);

        mFirestore = FirebaseFirestore.getInstance();
        mItems = mFirestore.collection("Results");

        mRecyclerView.setAdapter(mAdapter);

        queryData();

        //initializeData();

        mp = MediaPlayer.create(this, R.raw.music2);
        mp.setLooping(true);

        mNotificationHelper = new NotificationHelper(this);
    }

    public void queryData() {

        mItemsData.clear();

        //mItems.whereEqualTo()....

        mItems.orderBy("date", Query.Direction.DESCENDING).get().addOnSuccessListener(queryDocumentSnapshots -> {
            for ( QueryDocumentSnapshot document :queryDocumentSnapshots){
                MatchResult item = document.toObject(MatchResult.class);
                item.setId(document.getId());
                mItemsData.add(item);
            }

            if (mItemsData.size() == 0){
                initializeData();
                queryData();
            }

            mAdapter.notifyDataSetChanged();

        });

    }

    private void initializeData() {
        String[] home = getResources().getStringArray(R.array.homeName);
        String[] away = getResources().getStringArray(R.array.awayName);
        String[] homePts = getResources().getStringArray(R.array.homePts);
        String[] awayPts = getResources().getStringArray(R.array.awayPts);
        String[] date = getResources().getStringArray(R.array.date);

        TypedArray homeImg = getResources().obtainTypedArray(R.array.homeImg);
        TypedArray awayImg = getResources().obtainTypedArray(R.array.awayImg);

        //mItemsData.clear();

        for (int i = 0; i < home.length; i++) {
            mItems.add(new MatchResult(
                    home[i],
                    away[i],
                    homePts[i],
                    awayPts[i],
                    homeImg.getResourceId(i, 0),
                    awayImg.getResourceId(i, 0),
                    date[i]
                    ));
        }

        homeImg.recycle();
        awayImg.recycle();

        //mAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu, menu);

        MenuItem menuItem = menu.findItem(R.id.search_bar);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menuItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mAdapter.getFilter().filter(newText);
                return false;
            }
        });
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.logout){
            FirebaseAuth.getInstance().signOut();
            finish();
            return true;
        }
        if (item.getItemId() == R.id.newresult){
            Intent intent = new Intent(this, NewResultActivity.class);
            startActivity(intent);
            return true;
        }

        return false;
    }


    public void deleteItem(MatchResult item) {
        DocumentReference ref = mItems.document(item._getId());
        ref.delete().addOnSuccessListener(success -> {});

        queryData();
        mNotificationHelper.send("Törölted az eredményt!");

    }



    public void search(View view) {
        mItemsData.clear();

        //mItems.whereEqualTo()....

        String limit = spinnerLimit.getSelectedItem().toString();
        String rendezes = spinnerRendezes.getSelectedItem().toString();

        if (limit.equals("Össz.")){
            limit = "999";
        }

        if (rendezes.equals("Növ.")){
            mItems.orderBy("date", Query.Direction.ASCENDING).limit(Integer.parseInt(limit)).get().addOnSuccessListener(queryDocumentSnapshots -> {
                for ( QueryDocumentSnapshot document :queryDocumentSnapshots){
                    MatchResult item = document.toObject(MatchResult.class);
                    mItemsData.add(item);
                }

                if (mItemsData.size() == 0){
                    initializeData();
                    queryData();
                }

                mAdapter.notifyDataSetChanged();

            });
        }
        if(rendezes.equals("Csök.")){
            mItems.orderBy("date", Query.Direction.DESCENDING).limit(Integer.parseInt(limit)).get().addOnSuccessListener(queryDocumentSnapshots -> {
                for ( QueryDocumentSnapshot document :queryDocumentSnapshots){
                    MatchResult item = document.toObject(MatchResult.class);
                    mItemsData.add(item);
                }

                if (mItemsData.size() == 0){
                    initializeData();
                    queryData();
                }

                mAdapter.notifyDataSetChanged();

            });
        }


    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mp.isPlaying()) {
            mp.pause();
        }else{
            mp.start();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mp.stop();
        mp.release();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!mp.isPlaying()) {
            mp.start();

        }else{
            mp.pause();
        }
    }
}