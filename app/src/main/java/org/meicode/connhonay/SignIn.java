package org.meicode.connhonay;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignIn extends AppCompatActivity {
    private FirebaseAuth mAuth;
    Button btn;
    EditText edtPass,edtEmail;
    ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_in);
        TextView tvFotget = (TextView) findViewById(R.id.textView4);
        TextView tvSignUp = (TextView) findViewById(R.id.textView3);
        edtEmail = (EditText) findViewById(R.id.edtEmail);
        edtPass = (EditText) findViewById(R.id.edtPass);
        progressBar=(ProgressBar) findViewById(R.id.progressBar);
        btn=(Button) findViewById(R.id.button2);
        tvFotget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignIn.this,ForgetPass.class);
                startActivity(intent);
            }
        });
        tvSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignIn.this,SignUp.class);
                startActivity(intent);
            }
        });
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginUser();
            }
        });
        mAuth = FirebaseAuth.getInstance();
    }
    private void loginUser() {
        String email = edtEmail.getText().toString().trim();
        String password = edtPass.getText().toString().trim();

        if(email.isEmpty()){
            edtEmail.setError("Email is required!");
            edtEmail.requestFocus();
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            edtEmail.setError("Please provide valid email!");
            edtEmail.requestFocus();
            return;
        }

        if(password.isEmpty()){
            edtPass.setError("Password is required!");
            edtPass.requestFocus();
            return;
        }

//        if(password.length()<6){
//            edtPass.setError("Min password length should be 6 characters!");
//            edtPass.requestFocus();
//            return;
//        }

        progressBar.setVisibility(View.VISIBLE);
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    final SharedPreferences sharedPreferences = getSharedPreferences("USERID", MODE_PRIVATE);
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(SignIn.this, "User logged in  successfully", Toast.LENGTH_LONG).show();
                            progressBar.setVisibility(View.GONE);
                            Intent intent = new Intent(SignIn.this, input_time.class);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("UID",mAuth.getUid());
                            editor.commit();
                            startActivity(intent);
                        } else {
                            Toast.makeText(SignIn.this, "Log in Error", Toast.LENGTH_LONG).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });
    }
//    @Override
//    public void onStart() {
//        super.onStart();
//        // Check if user is signed in (non-null) and update UI accordingly.
//        FirebaseUser currentUser = mAuth.getCurrentUser();
//        if(currentUser != null){
//            currentUser.reload();
//        }
//    }
}