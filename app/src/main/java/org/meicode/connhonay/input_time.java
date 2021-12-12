package org.meicode.connhonay;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class input_time extends AppCompatActivity {
    EditText edt1,edt2,edt3;
    Button btn;
    Intent intent;
    FirebaseDatabase database;
    DatabaseReference reference;
    FirebaseAuth mAuth;
    String t1,t2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_time);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        edt1=(EditText) findViewById(R.id.edt1);
        edt2=(EditText) findViewById(R.id.edt2);
        edt3=(EditText) findViewById(R.id.edt3);
        btn=(Button) findViewById(R.id.btn);
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance("https://auth-4a763-default-rtdb.firebaseio.com/");
        intent = new Intent(input_time.this, TimeCoutDown.class);

        edt2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                TimePickerDialog timePickerDialog = new TimePickerDialog(input_time.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int i, int i1) {
                        edt2.setText(i + ":" + i1);
                        intent.putExtra("time",i*60+i1);
                        t1=i+" giờ "+i1+" phút";
                    }
                }, 0, 0, false);
                timePickerDialog.show();
            }
        });

        edt3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                TimePickerDialog timePickerDialog = new TimePickerDialog(input_time.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int i, int i1) {

                        edt3.setText(i + ":" + i1);
                        intent.putExtra("time1",i*60+i1);
                        t2=i+" giờ "+i1+" phút";
                    }
                }, 0, 0, false);
                timePickerDialog.show();

            }
        });

        btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                String nametask =edt1.getText().toString();
                String time1task =edt2.getText().toString();
                String time2task =edt3.getText().toString();
                if(nametask.isEmpty())
                {
                    edt1.setError("Name is required!");
                    edt1.requestFocus();
                    return;
                }
                if(time1task.isEmpty())
                {
                    edt2.setError("Time is required!");
                    edt2.requestFocus();
                    return;
                }
                if(time2task.isEmpty())
                {
                    edt3.setError("Time is required!");
                    edt3.requestFocus();
                    return;
                }
                reference = database.getReference("Users");
                String uid = mAuth.getUid();
                reference.child(uid).child("FocusTask").child(nametask).child("Tổng thời gian").setValue(t1);
                reference.child(uid).child("FocusTask").child(nametask).child("Nghỉ sau").setValue(t2);
                intent.putExtra("name",edt1.getText().toString());
                startActivity(intent);
            }
        });
    }

}
