package org.meicode.connhonay;




import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.UUID;

public class UserInfo extends AppCompatActivity {
    DatabaseReference mData;
    EditText nameUser;
    TextView Email;
    EditText Birthday;
    EditText PhoneNumber;
    ImageView profileImage;
    RadioGroup rdtG;
    RadioButton radioButtonnam;
    RadioButton radioButtonnu;
    RadioButton radioButtonkhac;
    String userid;
    FirebaseUser user;
    StorageReference storageReference;
    private Uri filePath;
    final Calendar myCalendar = Calendar.getInstance();
    private final int PICK_IMAGE_REQUEST = 71;
    Button btnSave, changeProfileImage, btnUpload;

    @Override
    protected void onCreate(Bundle savedInstanceState) {



        super.onCreate(savedInstanceState);
        setContentView(R.layout.user);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        mData = FirebaseDatabase.getInstance().getReference();

        Email = (TextView) findViewById(R.id.edittextGmail);
        Birthday = (EditText) findViewById(R.id.editTextNgaySinh);
        btnSave = (Button) findViewById(R.id.buttonSave);
        btnUpload = (Button) findViewById(R.id.btnUpload);
        nameUser = (EditText) findViewById(R.id.nameuser);
        radioButtonkhac = (RadioButton) findViewById(R.id.radio_khac);
        radioButtonnam = (RadioButton) findViewById(R.id.radio_nam);
        radioButtonnu = (RadioButton) findViewById(R.id.radio_nu);
        rdtG =(RadioGroup) findViewById(R.id.radioGroup);
        changeProfileImage = (Button) findViewById(R.id.buttonAvatar);
        profileImage = (ImageView) findViewById(R.id.profileImage);
        final SharedPreferences sharedPreferences = getSharedPreferences("USERID", MODE_PRIVATE);

        userid = sharedPreferences.getString("UID",null);

//        String imageId = binding.etimageId.getText().toString();
//

        storageReference = FirebaseStorage.getInstance().getReference().child(userid).child("avatar.jpg");
        try {
            final File localFile = File.createTempFile("avatar","jpg");
            storageReference.getFile(localFile)
                    .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                            ((ImageView) findViewById(R.id.profileImage)).setImageBitmap(bitmap);
                        }
                    }) .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(UserInfo.this, "Load avatar fail "+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        }



        Birthday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(UserInfo.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        changeProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
            }
        });

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserInfo.this,input_time.class);
                startActivity(intent);

            }
        });


        showUserData();
        updateUser();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null )
        {
            filePath = data.getData();
            try {

                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                profileImage.setImageBitmap(bitmap);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }
    private void uploadImage() {

        if(filePath != null)
        {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            StorageReference storageRef = FirebaseStorage.getInstance().getReference();

            // Create a reference to the file to delete
            StorageReference desertRef = storageRef.child(userid).child("avatar.jpg");

            // Delete the file
            desertRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(UserInfo.this, "delete thành công", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Uh-oh, an error occurred!
                }
            });
            progressDialog.setTitle("Uploading...");
            progressDialog.show();
            StorageReference ref = storageReference;//child("images/"+ UUID.randomUUID().toString());
            ref.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Toast.makeText(UserInfo.this, "Uploaded", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(UserInfo.this, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage("Uploaded "+(int)progress+"%");
                        }
                    });
        }
    }

    DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
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

        Birthday.setText(sdf.format(myCalendar.getTime()));
    }
    private void updateUser(){
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                {
                    isBirthdayChanged();
                    isNameChanged();
                    isSexChanged();
                    uploadImage();
                    Toast.makeText(UserInfo.this,"Lưu thành công",Toast.LENGTH_SHORT).show();
                }

            }

        });
    }
    private void showUserData(){

        mData.child("Users").child(userid).child("UserInfo").child("Name").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                nameUser.setText(snapshot.getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        mData.child("Users").child(userid).child("UserInfo").child("Email").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Email.setText(snapshot.getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        mData.child("Users").child(userid).child("UserInfo").child("Birthday").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Birthday.setText(snapshot.getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        mData.child("Users").child(userid).child("UserInfo").child("Sex").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                switch (snapshot.getValue().toString()) {
                    case "Nam":
                        rdtG.check(R.id.radio_nam);
                        break;
                    case "Nữ":
                        rdtG.check(R.id.radio_nu);
                        break;
                    case "Khác":
                        rdtG.check(R.id.radio_khac);
                        break;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private void isNameChanged(){
        if(!nameUser.equals(nameUser.getText().toString())){
            mData.child("Users").child(userid).child("UserInfo").child("Name").setValue(nameUser.getText().toString());
        }
    }
    private void isBirthdayChanged(){
        if(!Birthday.equals(Birthday.getText().toString())){
            mData.child("Users").child(userid).child("UserInfo").child("Birthday").setValue(Birthday.getText().toString());
        }
    }
    private void isSexChanged() {
        switch (rdtG.getCheckedRadioButtonId()) {
            case R.id.radio_nam:
                mData.child("Users").child(userid).child("UserInfo").child("Sex").setValue("Nam");
                break;
            case R.id.radio_nu:
                mData.child("Users").child(userid).child("UserInfo").child("Sex").setValue("Nữ");
                break;
            case R.id.radio_khac:
                mData.child("Users").child(userid).child("UserInfo").child("Sex").setValue("Khác");
                break;
        }
    }



}
