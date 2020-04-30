package com.bharani.manabu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class FindPeopleActivity extends AppCompatActivity {

    private RecyclerView findFriendsList;
    private EditText search;
    private String string="";
    private DatabaseReference userRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_people);

        Toolbar findToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(findToolbar);
        ActionBar ab=getSupportActionBar();
        assert ab != null;
        ab.setDisplayHomeAsUpEnabled(true);

        search=findViewById(R.id.contact_search);
        findFriendsList=findViewById(R.id.search_contact_list);
        findFriendsList.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        userRef = FirebaseDatabase.getInstance().getReference().child("Users");

        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(search.getText().toString().isEmpty()){

                }else{
                    string = s.toString();
                    onStart();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<ContactsModel> options = null;
        if(string.equals("")){
            options = new FirebaseRecyclerOptions.Builder<ContactsModel>().setQuery(userRef, ContactsModel.class).build();
        }else{
            options = new FirebaseRecyclerOptions.Builder<ContactsModel>()
                    .setQuery(userRef.orderByChild("name").startAt(string).endAt(string+"\uf8ff"),
                    ContactsModel.class).build();
        }

        FirebaseRecyclerAdapter<ContactsModel, FriendsViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<ContactsModel, FriendsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final FriendsViewHolder friendsViewHolder, final int position, @NonNull final ContactsModel contactsModel) {
                friendsViewHolder.username.setText(contactsModel.getName());
                friendsViewHolder.status.setText(contactsModel.getStatus());
                Picasso.get().load(contactsModel.getProfile_img()).into(friendsViewHolder.profile_image);

                friendsViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String visit_user=getRef(position).getKey();

                        Intent intent = new Intent(FindPeopleActivity.this, ProfileActivity.class);
                        intent.putExtra("visit_user",visit_user);
                        intent.putExtra("profile_image", contactsModel.getProfile_img());
                        intent.putExtra("user_name",contactsModel.getName());
                        intent.putExtra("status", contactsModel.getStatus());

                        startActivity(intent);
                    }
                });
            }

            @NonNull
            @Override
            public FriendsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_item, parent, false);
                FriendsViewHolder viewHolder = new FriendsViewHolder(view);
                return viewHolder;
            }
        };

        findFriendsList.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();

    }

    public static class FriendsViewHolder extends RecyclerView.ViewHolder {

        TextView username, status;
        ImageView videoCallBtn;
        CircleImageView profile_image;
        CardView cardView;

        public FriendsViewHolder(@NonNull View itemview) {
            super(itemview);
            username = itemview.findViewById(R.id.contact_username);
            videoCallBtn=itemview.findViewById(R.id.videoCall_btn);
            profile_image=itemview.findViewById(R.id.contact_profile);
            cardView=itemview.findViewById(R.id.single_contact_cardView);
            status = itemview.findViewById(R.id.contact_status);

            videoCallBtn.setVisibility(View.GONE);
        }
    }

}
