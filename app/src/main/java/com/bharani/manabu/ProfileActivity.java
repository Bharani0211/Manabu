package com.bharani.manabu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    private String UserId="", UserImage="", UserName="", UserStatus="";
    private CircleImageView profileImage;
    private TextView profileUsername, profileStatus;
    private Button acceptBtn, declineBtn;
    private FirebaseAuth mAuth;
    private String senderUserid;
    private String currentState="new";
    private DatabaseReference friendRequestRef, contactsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        profileImage=findViewById(R.id.profile_image);
        profileUsername=findViewById(R.id.profile_username);
        profileStatus=findViewById(R.id.profile_status);
        acceptBtn=findViewById(R.id.add_friend);
        declineBtn=findViewById(R.id.decline_friend);

        mAuth=FirebaseAuth.getInstance();
        senderUserid=mAuth.getCurrentUser().getUid();

        friendRequestRef=FirebaseDatabase.getInstance().getReference().child("Friend Request");
        contactsRef=FirebaseDatabase.getInstance().getReference().child("Contacts");

        UserId=getIntent().getExtras().get("visit_user").toString();
        UserImage=getIntent().getExtras().get("profile_image").toString();
        UserName=getIntent().getExtras().get("user_name").toString();
        UserStatus=getIntent().getExtras().get("status").toString();

        Picasso.get().load(UserImage).into(profileImage);
        profileUsername.setText(UserName);
        profileStatus.setText(UserStatus);

        manageClickEvents();
    }

    private void manageClickEvents() {

        friendRequestRef.child(senderUserid).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild(UserId)){
                            String requestType = dataSnapshot.child(UserId).child("request_type").getValue().toString();
                            if(requestType.equals("sent")){
                                currentState = "request_sent";
                                acceptBtn.setText("Cancel Friend Request");
                            }else if(requestType.equals("received")){
                                currentState = "request_received";
                                acceptBtn.setText("Accept Friend Request");
                                declineBtn.setVisibility(View.VISIBLE);
                                declineBtn.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        declineBtn.setVisibility(View.GONE);
                                        cancelFriendRequest();
                                    }
                                });
                            }
                        }else{
                            contactsRef.child(senderUserid).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if(dataSnapshot.hasChild(UserId)){
                                        currentState = "friends";
                                        acceptBtn.setText("Unfriend");
                                        acceptBtn.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                unFriendRequest();
                                            }
                                        });
                                    }
                                    else {
                                        currentState = "new";
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                }
        );

        if(senderUserid.equals(UserId)){
            acceptBtn.setVisibility(View.GONE);
        }else{
            acceptBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(currentState.equals("new")){
                        acceptBtn.setText("Cancel Friend Request");
                        sendFriendRequest();
                    }
                    if(currentState.equals("request_sent")){
                        acceptBtn.setText("Add as friend");
                        cancelFriendRequest();
                    }
                    if(currentState.equals("request_received")){
                        acceptBtn.setText("Unfriend");
                        declineBtn.setVisibility(View.GONE);
                        acceptFriendRequest();
                    }
                    if(currentState.equals("request_sent")){
                        acceptBtn.setText("Add as friend");
                        cancelFriendRequest();
                    }
                }
            });
        }
    }

    private void unFriendRequest() {
        contactsRef.child(senderUserid).child(UserId)
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            contactsRef.child(UserId).child(senderUserid).removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){
                                                currentState = "new";
                                                acceptBtn.setText("Add as friend");
                                            }
                                        }
                                    });
                        }
                    }
                });
    }

    private void acceptFriendRequest() {
        contactsRef.child(senderUserid).child(UserId).child("Contact").setValue("Saved")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            contactsRef.child(UserId).child(senderUserid).child("Contact").setValue("Saved")
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                friendRequestRef.child(senderUserid).child(UserId)
                                                        .removeValue()
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if(task.isSuccessful()){
                                                                    friendRequestRef.child(UserId).child(senderUserid).removeValue()
                                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                                    if (task.isSuccessful()){
                                                                                        currentState = "friends";
                                                                                        acceptBtn.setText("Unfriend");
                                                                                        declineBtn.setVisibility(View.GONE);

                                                                                        acceptBtn.setOnClickListener(new View.OnClickListener() {
                                                                                            @Override
                                                                                            public void onClick(View v) {
                                                                                                unFriendRequest();
                                                                                            }
                                                                                        });
                                                                                    }
                                                                                }
                                                                            });
                                                                }
                                                            }
                                                        });
                                            }
                                        }
                                    });
                        }
                    }
                });
    }

    private void cancelFriendRequest() {
        friendRequestRef.child(senderUserid).child(UserId)
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            friendRequestRef.child(UserId).child(senderUserid).removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){
                                                currentState = "new";
                                                acceptBtn.setText("Add as friend");
                                            }
                                        }
                                    });
                        }
                    }
                });
    }

    private void sendFriendRequest() {
        friendRequestRef.child(senderUserid).child(UserId).child("request_type").setValue("sent")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            friendRequestRef.child(UserId).child(senderUserid).child("request_type").setValue("received")
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                currentState = "request_sent";
                                                acceptBtn.setText("Cancel Friend Request");
                                                Toast.makeText(ProfileActivity.this, "Request sent", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        }
                    }
                });
    }
}
