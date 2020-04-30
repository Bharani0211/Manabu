package com.bharani.manabu;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {

    private CircleImageView settingsProfileImg;
    private EditText username, bio;
    private Button saveBtn;
    private Uri profileUri;
    private String downloadUri;

    private StorageReference profileImgReference;
    private DatabaseReference userRef;

    private ProgressDialog saveProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        NavBarSetup();

        settingsProfileImg=findViewById(R.id.settings_profileView);
        username=findViewById(R.id.settings_username);
        bio=findViewById(R.id.settings_bio);
        saveBtn=findViewById(R.id.settings_save_btn);

        saveProgress=new ProgressDialog(this);

        profileImgReference= FirebaseStorage.getInstance().getReference().child("Profile Images");
        userRef = FirebaseDatabase.getInstance().getReference().child("Users");

        settingsProfileImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onChoooseFile(v);
            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveAndStoreData();
            }
        });

        retrieveData();

    }

    private void saveAndStoreData() {
        final String usernameText = username.getText().toString();
        final String bioText = bio.getText().toString();

        if(profileUri == null){
            userRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).hasChild("profile_img")){
                            saveInfoWithoutImg();
                    }else{
                        Toast.makeText(SettingsActivity.this, "Select the profile pic", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }else if(usernameText.isEmpty()){
            Toast.makeText(this, "Please enter the username", Toast.LENGTH_SHORT).show();
        }else if(bioText.isEmpty()){
            Toast.makeText(this, "Please enter the status", Toast.LENGTH_SHORT).show();
        }else{
            saveProgress.setTitle("Setting profile");
            saveProgress.setMessage("Please wait while we set the profile");
            saveProgress.show();

            final StorageReference path = profileImgReference.child(FirebaseAuth.getInstance().getCurrentUser().getUid());
            final UploadTask uploadTask = path.putFile(profileUri);
            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if(!task.isSuccessful()){
                        throw task.getException();
                    }
                    downloadUri=path.getDownloadUrl().toString();
                    return path.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if(task.isSuccessful()){
                        downloadUri=task.getResult().toString();
                        HashMap<String,Object> profileMap = new HashMap<>();
                        profileMap.put("uid",FirebaseAuth.getInstance().getCurrentUser().getUid());
                        profileMap.put("name",usernameText);
                        profileMap.put("status",bioText);
                        profileMap.put("profile_img",downloadUri);

                        userRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .updateChildren(profileMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    saveProgress.dismiss();
                                    Toast.makeText(SettingsActivity.this, "Profile Updated", Toast.LENGTH_SHORT).show();

                                    Intent mainIntent = new Intent(SettingsActivity.this, MainActivity.class);
                                    startActivity(mainIntent);
                                    finish();
                                }else{
                                    Toast.makeText(SettingsActivity.this, "Error! Try again", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                }
            });
        }
    }

    public void onChoooseFile(View v){
        CropImage.activity().start(SettingsActivity.this);
    }

    private void saveInfoWithoutImg() {
        final String usernameText = username.getText().toString();
        final String bioText = bio.getText().toString();

        if(usernameText.isEmpty()){
            Toast.makeText(this, "Please enter the username", Toast.LENGTH_SHORT).show();
        }else if(bioText.isEmpty()){
            Toast.makeText(this, "Please enter the status", Toast.LENGTH_SHORT).show();
        }else{
            saveProgress.setTitle("Setting profile");
            saveProgress.setMessage("Please wait while we set the profile");
            saveProgress.show();

            HashMap<String,Object> profileMap = new HashMap<>();
            profileMap.put("uid",FirebaseAuth.getInstance().getCurrentUser().getUid());
            profileMap.put("name",usernameText);
            profileMap.put("status",bioText);

            userRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .updateChildren(profileMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        saveProgress.dismiss();
                        Toast.makeText(SettingsActivity.this, "Profile Updated", Toast.LENGTH_SHORT).show();

                        Intent mainIntent = new Intent(SettingsActivity.this, MainActivity.class);
                        startActivity(mainIntent);
                        finish();


                    }else{
                        Toast.makeText(SettingsActivity.this, "Error! Try again", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if(resultCode==RESULT_OK){
                profileUri = result.getUri();
                settingsProfileImg.setImageURI(profileUri);
            }else if (resultCode==CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE){
                Exception e = result.getError();
                Toast.makeText(this, "Error"+e, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void retrieveData(){
        userRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){

                    String image = dataSnapshot.child("profile_img").getValue().toString();
                    String name =  dataSnapshot.child("name").getValue().toString();
                    String status = dataSnapshot.child("status").getValue().toString();

                    username.setText(name);
                    bio.setText(status);
                    Picasso.get().load(image).placeholder(R.drawable.ic_person).into(settingsProfileImg);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void NavBarSetup() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottonNav);
        bottomNavigationView.setSelectedItemId(R.id.bottom_nav_settings);
        BottomNavHelper.switchActivities(SettingsActivity.this,bottomNavigationView);
    }
}
