package com.dentalcheck.themedapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;

public class profileActivity extends Fragment {
    private static final int CHOOSE_IMAGE_CODE = 100;
    CircleImageView img;
    Button save;
    Uri selectedImage;
    ProgressBar profile_progress;
    String profileImageUrl;


    //Firebase objects

    StorageReference thumbImageStoreRef;
    TextView username, Email, DOB, gender;
    ProgressBar progressBar;
    FirebaseAuth mAuth;
    FirebaseUser user;
    FirebaseAuth.AuthStateListener mAuthListener;
    DatabaseReference myRef;

    StorageReference imageStoreRef;
    String userID;


    // The onCreateView method is called when Fragment should create its View object hierarchy,
    // either dynamically or via XML layout inflation.
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        userID = user.getUid();
        myRef = FirebaseDatabase.getInstance().getReference().child("Users").child(userID);

        imageStoreRef = FirebaseStorage.getInstance().getReference().child("profileImages");
        thumbImageStoreRef = FirebaseStorage.getInstance().getReference().child("thumbnailImages");

        ((HomeActivity)getActivity()).getSupportActionBar().setTitle("Profile");
        return inflater.inflate(R.layout.profile, parent, false);

    }

    // This event is triggered soon after onCreateView().
    // Any view setup should occur here.  E.g., view lookups and attaching view listeners.
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        //declaring all authentications

        final String TAG = "ViewDatabase";


        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    toastMessage("Successfully signed in with: " + user.getEmail());
                } else {
                    // User is signed out6
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                    toastMessage("Successfully signed out.");
                }
                // ...
            }
        };

        // Setup any handles to view objects here
        // EditText etFoo = (EditText) view.findViewById(R.id.etFoo);
        progressBar = view.findViewById(R.id.profileProgress);
        Email = view.findViewById(R.id.useremailTxt);
        username = view.findViewById(R.id.usernameTxt);
        DOB = view.findViewById(R.id.DOBTxt);
        gender = view.findViewById(R.id.genderTxt);
        img = view.findViewById(R.id.profile_pics);
        profile_progress = view.findViewById(R.id.profileProgress);

        profile_progress.setVisibility(View.GONE);


       // loadUserInformation();
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImageChooser();
                save.setVisibility(View.VISIBLE);
            }
        });

        save = view.findViewById(R.id.saveImage);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SaveUserInfo();
                save.setVisibility(View.INVISIBLE);
            }

        });



        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                showData(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void showData(DataSnapshot dataSnapshot) {
        for (DataSnapshot ds : dataSnapshot.getChildren()) {
            user uInfo = dataSnapshot.getValue(user.class);
/*

                uInfo.setUsername(ds.child(userID).getValue(user.class).getUsername()); //set the name
                uInfo.setEmail(ds.child(userID).getValue(user.class).getEmail()); //set the email
                uInfo.setGender(ds.child(userID).getValue(user.class).getGender());//setthe gender
                uInfo.setDOB(ds.child(userID).getValue(user.class).getDOB());//set the dob
                uInfo.setImage(ds.child(userID).getValue(user.class).getImage());//get Image Url link}
*/
            String special = uInfo.getSpeciality();
            String names = uInfo.getUsername();
            String emails = uInfo.getEmail();
            String genders =  uInfo.getGender();
            String dobs =  uInfo.getDOB();
            final String images =  uInfo.getImage();


                //System.out.print

                //display all the information
                username.setText(names);
                Email.setText(emails);
                DOB.setText(genders);
                gender.setText(dobs);

            Picasso.with(getContext()).load(images).placeholder(R.drawable.ic_person_black_24dp).into(img, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError() {
                    Picasso.with(getContext()).load(images).placeholder(R.drawable.ic_person_black_24dp).into(img);

                }
            });


        }

    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CHOOSE_IMAGE_CODE && resultCode == RESULT_OK && null != data && data.getData() != null) {
            selectedImage = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), selectedImage);
                img.setImageBitmap(bitmap);
                uploadImage();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void SaveUserInfo() {
        if (user != null && profileImageUrl != null) {
            UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder()
                    .setPhotoUri(Uri.parse(profileImageUrl))
                    .build();

            user.updateProfile(profileChangeRequest).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        toastMessage("profile Updated");
                    }
                }
            });

        }
    }

    /*public void loadUserInformation() {
        final FirebaseUser user = mAuth.getCurrentUser();

        if (user != null) {
            if (user.getPhotoUrl() != null) {
                Glide.with(this)
                        .load(user.getPhotoUrl().toString())
                        .into(img);
            }
            else{
                toastMessage("photo url is empty");
            }
        }
    }*/

    public void showImageChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, CHOOSE_IMAGE_CODE);

    }

    public void uploadImage() {
        String user_id = mAuth.getCurrentUser().getUid();
        StorageReference profilePicsref = imageStoreRef.child(user_id + ".jpg");

        if (selectedImage != null) {
            profile_progress.setVisibility(View.VISIBLE);
            profilePicsref.putFile(selectedImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    profile_progress.setVisibility(View.GONE);
                    profileImageUrl = taskSnapshot.getDownloadUrl().toString();
                    writeNewImageInfoToDB(profileImageUrl);
                }

            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            profile_progress.setVisibility(View.GONE);
                            toastMessage(e.getMessage());
                        }
                    });

        }
    }

    private void writeNewImageInfoToDB( String url) {
        myRef.child("image").setValue(url).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                toastMessage("Image Updated to DB Successfully");
            }
        });
    }


    private void toastMessage(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }
}