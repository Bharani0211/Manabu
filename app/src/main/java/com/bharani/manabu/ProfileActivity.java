package com.bharani.manabu;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    private String UserId="", UserImage="", UserName="", UserStatus="";
    private CircleImageView profileImage;
    private TextView profileUsername, profileStatus;
    private Button acceptBtn, declineBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        profileImage=findViewById(R.id.profile_image);
        profileUsername=findViewById(R.id.profile_username);
        profileStatus=findViewById(R.id.profile_status);
        acceptBtn=findViewById(R.id.add_friend);
        declineBtn=findViewById(R.id.decline_friend);

        UserId=getIntent().getExtras().get("visit_user").toString();
        UserImage=getIntent().getExtras().get("profile_image").toString();
        UserName=getIntent().getExtras().get("user_name").toString();
        UserStatus=getIntent().getExtras().get("status").toString();

        Picasso.get().load(UserImage).into(profileImage);
        profileUsername.setText(UserName);
        profileStatus.setText(UserStatus);
    }
}
