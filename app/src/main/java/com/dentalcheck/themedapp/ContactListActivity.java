package com.dentalcheck.themedapp;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ContactListActivity extends Fragment {

    DatabaseReference databaseReference;
    RecyclerView allUserList;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ((HomeActivity) getActivity()).getSupportActionBar().setTitle("Contact List");
        return inflater.inflate(R.layout.activity_contacts, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        allUserList = view.findViewById(R.id.recycler_view_people);
        allUserList.setHasFixedSize(true);
        allUserList.setLayoutManager(new LinearLayoutManager(getActivity()));
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.keepSynced(true);
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<user, AlluserViewHolder> firebaseRecyclerAdapter
                = new FirebaseRecyclerAdapter<user, AlluserViewHolder>
                (
                        user.class,
                        R.layout.itemcontactlayout,
                        AlluserViewHolder.class,
                        databaseReference) {
            @Override
            protected void populateViewHolder(AlluserViewHolder viewHolder, user model, final int position) {
                viewHolder.setUser_image(getContext(),model.getImage());
                viewHolder.setUser_name(model.getUsername());
                viewHolder.setuser_status(model.getGender());

                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String ClickedUserid = getRef(position).getKey();
                        startActivity((new Intent(getContext(),OtherUserProfileActivity.class))
                                .putExtra("ClickedUserid",ClickedUserid));
                    }
                });
            }

        };
        allUserList.setAdapter(firebaseRecyclerAdapter);
    }


    @Override
    public void onResume() {
        super.onResume();
        ((HomeActivity) getActivity()).getSupportActionBar().setTitle("Contact List");
    }
}
