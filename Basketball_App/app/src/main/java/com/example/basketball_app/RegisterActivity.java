package com.example.basketball_app;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity {

    private MediaPlayer mp;

    EditText userNameInput;
    EditText emailInput;
    EditText passwordInput;
    EditText passwordInputAgain;
    EditText phoneInput;

    private FirebaseAuth mAuth;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        userNameInput = findViewById(R.id.userNameEditText);
        emailInput = findViewById(R.id.userEmailEditText);
        passwordInput = findViewById(R.id.passwordEditText);
        passwordInputAgain = findViewById(R.id.passwordAgainEditText);
        phoneInput = findViewById(R.id.phoneEditText);

        mAuth = FirebaseAuth.getInstance();

        mp = MediaPlayer.create(this, R.raw.music3);
        mp.setLooping(true);
    }

    public void register(View view) {
        String userName = userNameInput.getText().toString();
        String email = emailInput.getText().toString();
        String password = passwordInput.getText().toString();
        String passwordConfirm = passwordInputAgain.getText().toString();
        String phone = phoneInput.getText().toString();

        if (!password.equals(passwordConfirm)) {
            Toast.makeText(RegisterActivity.this, "A jelszavak nem egyeznek!", Toast.LENGTH_LONG).show();
            return;
        }

        if (userName.equals("") || email.equals("") || password.equals("") || passwordConfirm.equals("") || phone.equals("")){
            Toast.makeText(RegisterActivity.this, "Minden beviteli mezőt tölts ki!", Toast.LENGTH_LONG).show();
            return;
        }



        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, task -> {
            if(task.isSuccessful()){
                resultList();
            } else {
                Toast.makeText(RegisterActivity.this, "User was't created successfully: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    public void cancel(View view) {
        finish();
    }

    private void resultList(){
        Intent intent = new Intent(this, ResultListActivity.class);
        startActivity(intent);
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