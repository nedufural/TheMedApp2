package com.dentalcheck.themedapp;


import android.Manifest.permission;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;


public class CameraActivity extends Activity {


    CameraAdapter cameraAdapter;
    ArrayList<getSetClass> getSets;
    ListView listView;
    Uri selectedImage;
    // Temp save listItem position
    int position;

    int imageCount;
    String imageTempName;
    String[] imageFor;
    ImageView imageView;

    //Send images via volley
    private ProgressDialog dialog = null;
    private Button sendForDiagnosis;
    private JSONObject jsonObject;
    ArrayList<Uri> imagesUriList;
    ArrayList<String> encodedImageList;
    String imageURI;
    String picturePath;

    //firebase
    FirebaseAuth mAuth;
    FirebaseUser user;
    DatabaseReference picRef;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkPermissions();
        setContentView(R.layout.activity_camera);


        imageView = findViewById(R.id.imgPrv);
        listView = findViewById(R.id.captureList);
        sendForDiagnosis = findViewById(R.id.btn_send_diagnosis);
        sendForDiagnosis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendRequests();
            }
        });
        dialog = new ProgressDialog(getApplicationContext());
        dialog.setMessage("Checking for Results...");
        dialog.setCancelable(false);
        getSets = new ArrayList<>();
        imageFor = getResources().getStringArray(R.array.imageFor);
        for (int i = 0; i < 5; i++) {

            getSetClass inflate = new getSetClass();
            // Global Values
            inflate.setUid(String.valueOf(i));

            inflate.setLabel("Image");
            inflate.setHaveImage(false);
            inflate.setSubtext(imageFor[i]);
            inflate.setStatus(true);

            getSets.add(inflate);
        }
        cameraAdapter = new CameraAdapter(getSets, this);
        listView.setAdapter(cameraAdapter);


    }


    // granting  permission to read and write


    private void checkPermissions() {
        if (Build.VERSION.SDK_INT < 23)
            return;

        if (ActivityCompat.checkSelfPermission(this, permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{
                    permission.READ_EXTERNAL_STORAGE,
                    permission.CAMERA,
                    permission.WRITE_EXTERNAL_STORAGE}, 0);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED
                && grantResults[2] == PackageManager.PERMISSION_GRANTED) {
        } else if (grantResults[0] == PackageManager.PERMISSION_DENIED || grantResults[1] == PackageManager.PERMISSION_DENIED
                || grantResults[2] == PackageManager.PERMISSION_DENIED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (shouldShowRequestPermissionRationale(permission.CAMERA) ||
                        shouldShowRequestPermissionRationale(permission.READ_EXTERNAL_STORAGE) ||
                        shouldShowRequestPermissionRationale(permission.WRITE_EXTERNAL_STORAGE)) {
                    //Show an explanation to the user *asynchronously*
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage("This permission is important for the app.")
                            .setTitle("Important permission required");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                requestPermissions(new
                                        String[]{
                                        permission.READ_EXTERNAL_STORAGE,
                                        permission.CAMERA,
                                        permission.WRITE_EXTERNAL_STORAGE}, 0);

                            }

                        }
                    });

                    requestPermissions(new String[]{permission.READ_EXTERNAL_STORAGE, permission.CAMERA, permission.WRITE_EXTERNAL_STORAGE}, 0);
                } else {
                    //Never ask again and handle your app without permission.
                }
            }
        }
    }

    /**
     * Choose Image and save into database
     */
    public void showImageChooser(int pos2, String imageName2) {
        position = pos2;
        imageTempName = imageName2;
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, 102);

    }

    /**
     * Capture Image and save into database
     */

    public void captureImage(int pos, String imageName) {
        position = pos;
        imageTempName = imageName;
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, 101);
    }


    /**
     * Set capture image to database and set to image preview
     *
     * @param data
     */
    private void onCaptureImageResult(Intent data) {

        Bundle extras = data.getExtras();
        Bitmap imageBitmap = (Bitmap) extras.get("data");
        // CALL THIS METHOD TO GET THE URI FROM THE BITMAP
        Uri tempUri = getImageUri(this, imageBitmap, imageTempName);
        picturePath = getRealPathFromURI(tempUri);
        System.out.println("Nedu's picture path " + picturePath);
        cameraAdapter.setImageInItem(position, imageBitmap, picturePath);

    }

    private void onChooseImageResult(Intent data) {
        selectedImage = data.getData();
        String paths = selectedImage.toString();
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImage);
            Uri tempUri = getImageUri(this, bitmap, imageTempName);
            picturePath = getRealPathFromURI(tempUri);
            System.out.println("Nedu's picture path 2" + picturePath);
            cameraAdapter.setImageInItem(position, bitmap, paths);
        } catch (Exception e) {
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 101 && resultCode != Activity.RESULT_CANCELED) {
            onCaptureImageResult(data);

        } else if (requestCode == 102) {
            onChooseImageResult(data);
            System.out.println("Nedu's picture path 3  " + picturePath);
        }


    }

    public Uri getImageUri(Context inContext, Bitmap inImage, String imageName) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, imageName, null);
        return Uri.parse(path);
    }

    public String getRealPathFromURI(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
    }

    public Bitmap convertSrcToBitmap(String imageSrc) {
        Bitmap myBitmap = null;
        File imgFile = new File(imageSrc);
        if (imgFile.exists()) {
            myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
        }
        return myBitmap;
    }

    public void SendRequests() {
        JSONArray jsonArray = new JSONArray();

        if (encodedImageList.isEmpty()) {
            Toast.makeText(this, "Please select some images first.", Toast.LENGTH_SHORT).show();
            return;
        }

        for (String encoded : encodedImageList) {
            jsonArray.put(encoded);
        }

        try {
            /*
        {
            "resourceType" : "InputImages",

                "identifier" : 412,
                "patient" : "p0005",
                "authoringTime" : "02/05/2018",

                "study" : {
            "imageType": 1,
                    "imagingStudy" : "c:/mnp.jpg",
                    "takingtime" : "02/05/2018"
        }
        }*/
            //jsonObject.put(Utils.imageName, picturePath.trim());
            jsonObject.put(Utils.imageList, jsonArray);
            jsonObject.put("resourceType","InputImages");
            jsonObject.put("patient", "p0005");
            jsonObject.put("authoringTime", "02/05/2018");
            jsonObject.put("imageType", "1");
            jsonObject.put("imagingStudy",  picturePath.trim());
            jsonObject.put("takingtime", "02/05/2018");

        } catch (JSONException e) {
            Log.e("JSONObject Here", e.toString());
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Utils.urlUpload, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        Log.e("Message from server", jsonObject.toString());
                        dialog.dismiss();
                        toastMessage("Images Uploaded Successfully");
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.e("Message from server", volleyError.toString());
                Toast.makeText(getApplication(), "Error Occurred", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(200 * 30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Volley.newRequestQueue(this).add(jsonObjectRequest);


    }

    private void toastMessage(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }


}



   /* final static int CHOOSE_IMAGE_CODE = 100;
    Uri selectedImage;
    ProgressDialog progressDialog;
    String profileImageUrl;
    ProgressBar progressBar;
    DatabaseReference mDataReference = FirebaseDatabase.getInstance().getReference("teethImages");
    Button sendToServer;

    String[] city= {
            "Front Teeth",
            "Upper Right Teeth",
            "Lower Right Teeth",
            "Upper Left Teeth",
            "Lower Left Teeth",
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        //send for diagnosis
        *//*sendToServer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
*//*
        // -- Display mode of the ListView

        ListView listview= getListView();
        //	listview.setChoiceMode(listview.CHOICE_MODE_NONE);
        //	listview.setChoiceMode(listview.CHOICE_MODE_SINGLE);
        listview.setChoiceMode(listview.CHOICE_MODE_MULTIPLE);

        //--	text filtering
        listview.setTextFilterEnabled(true);

        setListAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_checked,city));
    }

    public void onListItemClick(ListView parent, View v, int position, long id){
        CheckedTextView item = (CheckedTextView) v;
        if(item.isChecked()){
         showImageChooser();
        }
		*//*Toast.makeText(this, city[position] + " checked : " +
		item.isChecked(), Toast.LENGTH_SHORT).show();*//*
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CHOOSE_IMAGE_CODE && resultCode == RESULT_OK && null != data && data.getData() != null) {
            selectedImage = data.getData();
            uploadImage();
        }
    }




    public void showImageChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, CHOOSE_IMAGE_CODE);

    }

    public void uploadImage() {
        StorageReference profilePicsref = FirebaseStorage.
                getInstance().getReference("teethImages/" + System.currentTimeMillis() + ".jpg");
        if (selectedImage != null) {
             progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("upLoading....");
            progressDialog.setTitle("Uploading Image");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.show();
            profilePicsref.putFile(selectedImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    progressDialog.dismiss();
                    String ImageName = taskSnapshot.getMetadata().getName().toString();
                    profileImageUrl = taskSnapshot.getDownloadUrl().toString();
                    writeNewImageInfoToDB(ImageName,profileImageUrl);
                    toastMessage("Image Uploaded");
                }

            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            toastMessage(e.getMessage());
                        }
                    }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    // progress percentage
                    double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();

                    // percentage in progress dialog
                    progressDialog.setMessage("Uploaded " + ((int) progress) + "%...");
                }
            })
                    .addOnPausedListener(new OnPausedListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onPaused(UploadTask.TaskSnapshot taskSnapshot) {
                            System.out.println("Upload is paused!");
                        }
                    });

        }
    }

    private void writeNewImageInfoToDB(String name, String url) {
        UploadInfo info = new UploadInfo(name, url);
        String key = mDataReference.push().getKey();
        mDataReference.child(key).setValue(info);
    }
    private void toastMessage(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }
    }*/

