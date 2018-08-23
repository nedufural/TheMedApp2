package com.dentalcheck.themedapp;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.os.Bundle;
import android.view.View;

import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class OtherUserProfileActivity extends Activity {

    CircleImageView imageView;
    Button sendrequest, delete;
    TextView username, DOB, gender;
    DatabaseReference usersProfileRef, friendRequestRef, FriendsRef
            ,notificationsRef;
    String receiverID, currentUserID;

    String FRIEND_STATUS;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_user_profile);
        receiverID = getIntent().getExtras().get("ClickedUserid").toString();

        currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        usersProfileRef = FirebaseDatabase.getInstance().getReference().child("Users").child(receiverID);
        usersProfileRef.keepSynced(true);
        friendRequestRef = FirebaseDatabase.getInstance().getReference().child("FriendRequest");
        friendRequestRef.keepSynced(true);
        FriendsRef = FirebaseDatabase.getInstance().getReference().child("Friends");
        FriendsRef.keepSynced(true);
        notificationsRef = FirebaseDatabase.getInstance().getReference().child("Notifications");
        notificationsRef.keepSynced(true);


        FRIEND_STATUS = "not_friends";

        sendrequest = findViewById(R.id.add_friend);
        delete = findViewById(R.id.delete_friend);
        username = findViewById(R.id.usernameTxt2);
        DOB = findViewById(R.id.DOBTxt2);
        gender = findViewById(R.id.genderTxt2);
        imageView = findViewById(R.id.imageView_profile_picture);

        usersProfileRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                showclickedUserData(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        //deable the decline button
        delete.setVisibility(View.INVISIBLE);
        delete.setEnabled(false);



    if(currentUserID.equals(receiverID)){
        sendrequest.setVisibility(View.INVISIBLE);
        delete.setVisibility(View.INVISIBLE);
    }
    else{
        sendrequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendrequest.setEnabled(false);
                if (FRIEND_STATUS.equals("not_friends")) {
                    sendfriendRequest();
                }
                if (FRIEND_STATUS.equals("request_sent")) {
                    cancelFriendRequest();
                }
                if (FRIEND_STATUS.equals("request_received")) {
                    acceptFriendRequest();
                }
                if (FRIEND_STATUS.equals("friends")) {
                    unfriendPerson();
                }
            }
        });
    }
    }

    private void declineFriendRequest() {
        friendRequestRef.child(currentUserID).child(receiverID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    friendRequestRef.child(receiverID).child(currentUserID)
                            .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                sendrequest.setEnabled(true);
                                FRIEND_STATUS = "not_friends";
                                sendrequest.setText("Send Friend Request");
                                delete.setVisibility(View.INVISIBLE);
                                delete.setEnabled(false);
                            }
                        }
                    });
                }
            }
        });
    }


    private void unfriendPerson() {
        FriendsRef.child(currentUserID).child(receiverID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    FriendsRef.child(receiverID).child(currentUserID)
                            .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                sendrequest.setEnabled(true);
                                FRIEND_STATUS = "not_friends";
                                sendrequest.setText("send friend request");
                                delete.setVisibility(View.INVISIBLE);
                                delete.setEnabled(false);
                            }
                        }
                    });
                }
            }
        });
    }

    private void acceptFriendRequest() {
        Calendar friendshipStartDate = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MMMM/yyyy");
        final String currentdate = dateFormat.format(friendshipStartDate.getTime());

        FriendsRef.child(currentUserID).child(receiverID).setValue(currentdate)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        FriendsRef.child(receiverID).child(currentUserID).setValue(currentdate)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        friendRequestRef.child(currentUserID).child(receiverID).removeValue()
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            friendRequestRef.child(receiverID).child(currentUserID)
                                                                    .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    if (task.isSuccessful()) {
                                                                        sendrequest.setEnabled(true);
                                                                        FRIEND_STATUS = "friends";
                                                                        sendrequest.setText("unfriend");

                                                                        delete.setVisibility(View.INVISIBLE);
                                                                        delete.setEnabled(false);
                                                                    }
                                                                }
                                                            });
                                                        }
                                                    }
                                                });
                                    }
                                });
                    }
                });
    }

    private void cancelFriendRequest() {
        friendRequestRef.child(currentUserID).child(receiverID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    friendRequestRef.child(receiverID).child(currentUserID)
                            .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                sendrequest.setEnabled(true);
                                FRIEND_STATUS = "not_friends";
                                sendrequest.setText("Send Friend Request");
                                delete.setVisibility(View.INVISIBLE);
                                delete.setEnabled(false);
                            }
                        }
                    });
                }
            }
        });
    }


    private void sendfriendRequest() {
        friendRequestRef.child(currentUserID).child(receiverID).child("request_type").setValue("sent")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            friendRequestRef.child(receiverID).child(currentUserID).child("request_type")
                                    .setValue("received").addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {


                                        //adding push notification
                                        HashMap<String,String> notificationInfo = new HashMap<>();
                                        notificationInfo.put("from",currentUserID);
                                        notificationInfo.put("type","request");
                                        notificationsRef.child(receiverID).push().setValue(notificationInfo).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful()){
                                                    sendrequest.setEnabled(true);
                                                    FRIEND_STATUS = "request_sent";
                                                    sendrequest.setText("Cancel Friend Request");
                                                    delete.setVisibility(View.INVISIBLE);
                                                    delete.setEnabled(false);
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


    private void showclickedUserData(DataSnapshot dataSnapshot) {

        user uInfo = dataSnapshot.getValue(user.class);


        String names = uInfo.getUsername();
        String genders = uInfo.getGender();
        String dobs = uInfo.getDOB();
        String images = uInfo.getImage();
        //display all the information
        username.setText(names);
        DOB.setText(genders);
        gender.setText(dobs);
       // Glide.with(this).load(images).into(imageView);
        Picasso.with(this).load(images).placeholder(R.drawable.ic_person_black_24dp).into(imageView);



        friendRequestRef.child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    if (dataSnapshot.hasChild(receiverID)) {
                        String req_type = dataSnapshot.child(receiverID).child("request_type").getValue().toString();

                        if (req_type.equals("sent")) {
                            FRIEND_STATUS = "request_sent";
                            sendrequest.setText("Cancel Friend Request");
                            delete.setVisibility(View.INVISIBLE);
                            delete.setEnabled(false);
                        } else if (req_type.equals("received")) {
                            FRIEND_STATUS = "request_received";
                            sendrequest.setText("Accept Friend Request");
                            delete.setVisibility(View.VISIBLE);
                            delete.setEnabled(true);
                            delete.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    declineFriendRequest();
                                }
                            });
                        }
                    }
                }
                else{
                    FriendsRef.child(currentUserID).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if(dataSnapshot.hasChild(receiverID)){
                                FRIEND_STATUS="friends";
                                sendrequest.setText("unfriend");
                                delete.setVisibility(View.INVISIBLE);
                                delete.setEnabled(false);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }


    public void toastMessages(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
    }
}
