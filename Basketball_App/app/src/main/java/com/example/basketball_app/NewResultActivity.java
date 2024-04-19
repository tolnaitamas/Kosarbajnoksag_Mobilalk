package com.example.basketball_app;

import android.content.res.TypedArray;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class NewResultActivity extends AppCompatActivity {

    private MediaPlayer mp;

    EditText home;
    EditText away;
    EditText homepts;
    EditText awaypts;
    EditText date;

    CollectionReference item;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_new_result);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        home = findViewById(R.id.home);
        away = findViewById(R.id.away);
        homepts = findViewById(R.id.homepts);
        awaypts = findViewById(R.id.awaypts);
        date = findViewById(R.id.date);

        item = FirebaseFirestore.getInstance().collection("Results");

        mp = MediaPlayer.create(this, R.raw.music1);
        mp.setLooping(true);
    }

    public void cancel(View view) {
        finish();
    }
    public void createResult(View view) {

        int homeimg = 0;
        int awayimg = 0;

        List<String> team = new ArrayList<>();
        team.add("GSW");
        team.add("LAL");
        team.add("BOS");
        team.add("MIL");


        if (home.getText().toString().equals("") || away.getText().toString().equals("") || homepts.getText().toString().equals("") || awaypts.getText().toString().equals("") || date.getText().toString().equals("")){
            Toast.makeText(NewResultActivity.this, "Minden beviteli mezőt tölts ki!", Toast.LENGTH_LONG).show();
            return;
        }


        if (!(team.contains(home.getText().toString()) && team.contains(away.getText().toString()))){
            Toast.makeText(NewResultActivity.this, "Csak ezeket a csapatokat adhatod meg!\n(GSW, LAL, BOS, MIL)", Toast.LENGTH_LONG).show();
            return;
        }

        int a;
        int b;
        try {
            a = Integer.parseInt(homepts.getText().toString());
            b = Integer.parseInt(awaypts.getText().toString());
        }catch (Exception e){
            Toast.makeText(NewResultActivity.this, "Csak számot adhatsz meg pontnak!", Toast.LENGTH_LONG).show();
            return;
        }

        if (a > 300 || b > 300){
            Toast.makeText(NewResultActivity.this, "Irreálisan nagy szám lett megadva!", Toast.LENGTH_LONG).show();
            return;
        }

        if (date.getText().toString().length() > 5){
            Toast.makeText(NewResultActivity.this, "Rossz a dátumformátum!", Toast.LENGTH_LONG).show();
            return;
        }


        switch (home.getText().toString()) {
            case "GSW":
                homeimg = ResultListActivity.imgPath.get("GSW");
                break;
            case "BOS":
                homeimg = ResultListActivity.imgPath.get("BOS");
                break;
            case "LAL":
                homeimg = ResultListActivity.imgPath.get("LAL");
                break;
            case "MIL":
                homeimg = ResultListActivity.imgPath.get("MIL");
                break;
        }

        switch (away.getText().toString()) {
            case "GSW":
                awayimg = ResultListActivity.imgPath.get("GSW");
                break;
            case "BOS":
                awayimg = ResultListActivity.imgPath.get("BOS");
                break;
            case "LAL":
                awayimg = ResultListActivity.imgPath.get("LAL");
                break;
            case "MIL":
                awayimg = ResultListActivity.imgPath.get("MIL");
                break;
        }


        item.add(new MatchResult(home.getText().toString(), away.getText().toString(), homepts.getText().toString(),awaypts.getText().toString(),homeimg, awayimg, date.getText().toString()));
        finish();
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
