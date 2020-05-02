package com.bharani.manabu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class CallingActivity extends AppCompatActivity {

    private TextView username;
    private ImageView profileImg;
    private FloatingActionButton acceptCallBtn, cancelCallBtn;
    private DatabaseReference userRef;
    private String receiverUsersId="", receiverUsersName="", receiverUsersImage="";
    private String senderUsersId="", senderUsersName="", senderUsersImage="", checker="";
    private String callingID="", ringingID="";
    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calling);

        username=findViewById(R.id.calling_username);
        profileImg=findViewById(R.id.calling_profile);
        acceptCallBtn=findViewById(R.id.make_call);
        cancelCallBtn=findViewById(R.id.cancel_call);

        mediaPlayer = MediaPlayer.create(this, R.raw.ring);

        senderUsersId= FirebaseAuth.getInstance().getCurrentUser().getUid();
        receiverUsersId=getIntent().getExtras().get("clicked_user_id").toString();
        userRef= FirebaseDatabase.getInstance().getReference().child("Users");

        setReceiverInfo();

        cancelCallBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.stop();
                checker="clicked";
                cancelCallingUser();
            }
        });

        acceptCallBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mediaPlayer.stop();
                final HashMap<String ,Object> callingPickUpMap = new HashMap<>();
                callingPickUpMap.put("picked", "picked");

                userRef.child(senderUsersId).child("Ringing")
                        .updateChildren(callingPickUpMap)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isComplete()){
                                    Intent i = new Intent(CallingActivity.this, VideoChatActivity.class);
                                    startActivity(i);
                                }
                            }
                        });
            }
        });

    }

    private void cancelCallingUser() {
        //removing data for called person in database

        userRef.child(senderUsersId).child("Calling").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.hasChild("calling")){
                    callingID=dataSnapshot.child("calling").getValue().toString();
                    userRef.child(callingID).child("Ringing").removeValue()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        userRef.child(senderUsersId).child("Calling").removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                startActivity(new Intent(CallingActivity.this, RegisterActivity.class));
                                                finish();
                                            }
                                        });
                                    }
                                }
                            });
                }else{
                    startActivity(new Intent(CallingActivity.this, RegisterActivity.class));
                    finish();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //removing data for receiving person in database

        userRef.child(senderUsersId).child("Ringing").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.hasChild("ringing")){
                    ringingID=dataSnapshot.child("ringing").getValue().toString();
                    userRef.child(ringingID).child("Calling").removeValue()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        userRef.child(senderUsersId).child("Ringing").removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                startActivity(new Intent(CallingActivity.this, RegisterActivity.class));
                                                finish();
                                            }
                                        });
                                    }
                                }
                            });
                }else{
                    startActivity(new Intent(CallingActivity.this, RegisterActivity.class));
                    finish();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
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

    @Override
    protected void onStart() {
        super.onStart();
        mediaPlayer.start();

        userRef.child(receiverUsersId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if( !checker.equals("clicked") && !dataSnapshot.hasChild("Calling") && !dataSnapshot.hasChild("Ringing")){


                    final HashMap<String,Object> callingInfo = new HashMap<>();
                    callingInfo.put("calling", receiverUsersId);

                    userRef.child(senderUsersId).child("Calling").updateChildren(callingInfo)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        final HashMap<String,Object> ringingInfo = new HashMap<>();
                                        ringingInfo.put("ringing", senderUsersId);

                                        userRef.child(receiverUsersId).child("Ringing").updateChildren(ringingInfo);
                                    }
                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(senderUsersId).hasChild("Ringing") &&
                        !dataSnapshot.child(senderUsersId).hasChild("Calling")){
                    acceptCallBtn.setVisibility(View.VISIBLE);
                }

                if (dataSnapshot.child(receiverUsersId).child("Ringing ").hasChild("picked")){
                    mediaPlayer.stop();
                    Intent i = new Intent(CallingActivity.this, VideoChatActivity.class);
                    startActivity(i);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
