package com.bharani.manabu;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private RecyclerView recyclerView;
    private FloatingActionButton findPeople;
    private DatabaseReference contactsRef, userRef;
    private FirebaseAuth mAuth;
    private String currentuserid, username, profile_Image="", status="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView=findViewById(R.id.contact_list);
        findPeople=findViewById(R.id.find_people);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));


        mAuth=FirebaseAuth.getInstance();
        currentuserid=mAuth.getCurrentUser().getUid();
        contactsRef= FirebaseDatabase.getInstance().getReference().child("Contacts");
        userRef=FirebaseDatabase.getInstance().getReference().child("Users");

        if(!isConnected())
        {
            new AlertDialog.Builder(this)
                    .setTitle("Internet connection Alert!")
                    .setMessage("Please check your internet connection")
                    .setPositiveButton("Close", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    }).show();
        }

        NavBarSetup();

        findPeople.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent findPeople = new Intent(MainActivity.this,FindPeopleActivity.class);
                startActivity(findPeople);
            }
        });
    }

    private void NavBarSetup() {
        bottomNavigationView = findViewById(R.id.bottonNav);
        bottomNavigationView.setSelectedItemId(R.id.bottom_nav_home);
        CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) bottomNavigationView.getLayoutParams();
        layoutParams.setBehavior(new BottomNavHelper());
        BottomNavHelper.switchActivities(MainActivity.this,bottomNavigationView);
    }

    private boolean isConnected(){
        ConnectivityManager connectivityManager= (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        assert connectivityManager != null;
        NetworkInfo networkInfo=connectivityManager.getActiveNetworkInfo();
        return networkInfo!=null && networkInfo.isConnected();
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<ContactsModel> options
                = new FirebaseRecyclerOptions.Builder<ContactsModel>()
                .setQuery(contactsRef.child(currentuserid), ContactsModel.class)
                .build();

        FirebaseRecyclerAdapter<ContactsModel, ContactsViewHolder> firebaseRecyclerAdapter
                = new FirebaseRecyclerAdapter<ContactsModel, ContactsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final ContactsViewHolder contactsViewHolder, int i, @NonNull final ContactsModel contactsModel) {
                final String listUserId = getRef(i).getKey();

                userRef.child(listUserId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()){
                            username = dataSnapshot.child("name").getValue().toString();
                            status = dataSnapshot.child("status").getValue().toString();
                            profile_Image = dataSnapshot.child("profile_img").getValue().toString();

                            contactsViewHolder.username.setText(username);
                            contactsViewHolder.status.setText(status);
                            Picasso.get().load(profile_Image).into(contactsViewHolder.profile);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            @NonNull
            @Override
            public ContactsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_item, parent, false);
                ContactsViewHolder viewHolder = new ContactsViewHolder(view);
                return viewHolder;
            }
        };
        recyclerView.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();
    }

    public static class ContactsViewHolder extends RecyclerView.ViewHolder {

        TextView username ,status;
        CircleImageView profile;
        CardView cardView;
        ImageView callBtn;

        public ContactsViewHolder(@NonNull View itemview) {
            super(itemview);
            username = itemview.findViewById(R.id.contact_username);
            status = itemview.findViewById(R.id.contact_status);
            profile=itemview.findViewById(R.id.contact_profile);
            cardView=itemview.findViewById(R.id.single_contact_cardView);
            callBtn = itemview.findViewById(R.id.videoCall_btn);
        }
    }


}
