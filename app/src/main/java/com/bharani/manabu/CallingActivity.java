package com.bharani.manabu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class CallingActivity extends AppCompatActivity {

    private TextView username;
    private ImageView profileImg;
    private FloatingActionButton makeCallBtn, cancelCallBtn;
    private DatabaseReference userRef;
    private String receiverUsersId="", receiverUsersName="", receiverUsersImage="";
    private String senderUsersId="", senderUsersName="", senderUsersImage="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calling);

        username=findViewById(R.id.calling_username);
        profileImg=findViewById(R.id.calling_profile);
        makeCallBtn=findViewById(R.id.make_call);
        cancelCallBtn=findViewById(R.id.cancel_call);

        senderUsersId= FirebaseAuth.getInstance().getCurrentUser().getUid();
        receiverUsersId=getIntent().getExtras().get("clicked_user_id").toString();
        userRef= FirebaseDatabase.getInstance().getReference().child("Users");

        setReceiverInfo();

    }

    private void setReceiverInfo() {
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(receiverUsersId).exists()){
                    receiverUsersImage=dataSnapshot.child(receiverUsersId).child("profile_img").getValue().toString();
                    receiverUsersName=dataSnapshot.child(receiverUsersId).child("name").getValue().toString();

                    username.setText(receiverUsersName);
                    Picasso.get().load(receiverUsersImage).placeholder(R.drawable.ic_person).into(profileImg);
                }
                if(dataSnapshot.child(senderUsersId).exists()){
                    senderUsersImage=dataSnapshot.child(senderUsersId).child("profile_img").getValue().toString();
                    senderUsersName=dataSnapshot.child(senderUsersId).child("name").getValue().toString();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
