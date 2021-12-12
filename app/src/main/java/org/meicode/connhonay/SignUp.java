package org.meicode.connhonay;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class SignUp extends AppCompatActivity {
    final Calendar myCalendar = Calendar.getInstance();
    FirebaseAuth mAuth;
    Button btn_regis;
    EditText fullname;
    EditText age;
    EditText email;
    EditText password;
    FirebaseDatabase database;
    DatabaseReference reference;
    TextView tv_signin;
    boolean showpassword;
    private ProgressBar progressBar;
    RadioGroup radioGroup;
    ImageView btn_showpass;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        fullname = (EditText)findViewById(R.id.edtName);
        age = (EditText)findViewById(R.id.edtAge);
        tv_signin = (TextView)findViewById(R.id.textView3);
        btn_regis = (Button) findViewById(R.id.button2);
        email = (EditText)findViewById(R.id.edtEmail);
        password = (EditText)findViewById(R.id.edtPass);
        showpassword = false;
        btn_showpass = (ImageView)findViewById(R.id.view_eye);
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance("https://auth-4a763-default-rtdb.firebaseio.com/");
        progressBar=(ProgressBar) findViewById(R.id.progressBar);
        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        onClick();
        age.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(SignUp.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
    }
    DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            // TODO Auto-generated method stub
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabel();
        }
    };
    private void updateLabel() {
        String myFormat = "dd/MM/yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        age.setText(sdf.format(myCalendar.getTime()));
    }
    private void onClick(){
        tv_signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignUp.this, SignIn.class);
                startActivity(intent);
                finish();
            }
        });
        btn_regis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createUser();
            }
        });
        btn_showpass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(showpassword) {
                    password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    showpassword=false;
                }
                else {
                    password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    showpassword = true;
                }
            }
        });
    }
    private void createUser(){
        String emaill = email.getText().toString();
        String passwordd = password.getText().toString();
        String fullName = fullname.getText().toString().trim();
        String agee = age.getText().toString().trim();
        if(fullName.isEmpty()){
            fullname.setError("Full name is required!");
            fullname.requestFocus();
            return;
        }

        if(agee.isEmpty()){
            age.setError("Age is required!");
            age.requestFocus();
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(emaill).matches()){
            email.setError("Please provide valid email!");
            email.requestFocus();
            return;
        }

        if(passwordd.isEmpty()){
            password.setError("Password is required!");
            password.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(emaill)){
            email.setError("Email cannot be empty");
            email.requestFocus();
        }else{
            progressBar.setVisibility(View.VISIBLE);
            mAuth.createUserWithEmailAndPassword(emaill,passwordd).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        Toast.makeText(getApplicationContext(), "User registered successfully", Toast.LENGTH_SHORT).show();
                        reference = database.getReference("Users");
                        String uid = mAuth.getUid();
                        reference.child(uid).child("UserInfo").child("Email").setValue(email.getText().toString());
                        reference.child(uid).child("UserInfo").child("Birthday").setValue(age.getText().toString());
                        reference.child(uid).child("UserInfo").child("Name").setValue(fullname.getText().toString());
                        switch (radioGroup.getCheckedRadioButtonId()){
                            case R.id.rdtNam:
                                reference.child(uid).child("UserInfo").child("Sex").setValue("Nam");
                                break;
                            case R.id.rdtNu:
                                reference.child(uid).child("UserInfo").child("Sex").setValue("Nữ");
                                break;
                            case R.id.rdtKhac:
                                reference.child(uid).child("UserInfo").child("Sex").setValue("Khác");
                                break;
                        }
                        Toast.makeText(SignUp.this, "User has been registered successfull", Toast.LENGTH_LONG).show();
                        progressBar.setVisibility(View.GONE);
                        startActivity(new Intent(SignUp.this,SignIn.class));
                        finish();
                    } else {
                        Toast.makeText(SignUp.this, "Failed to registered! Try again", Toast.LENGTH_LONG).show();
                        progressBar.setVisibility(View.GONE);
                    }
                }
            });
        }
    }
}