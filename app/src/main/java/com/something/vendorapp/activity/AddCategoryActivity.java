package com.something.vendorapp.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.something.vendorapp.R;
import com.something.vendorapp.model.CategoryHelper;

import java.text.SimpleDateFormat;
import java.util.Date;

public class AddCategoryActivity extends AppCompatActivity {

    public static final int PICK_IMAGE_REQUEST = 1001;
    StorageReference mStorage;
    FirebaseDatabase rootnode;
    DatabaseReference reference;
    TextInputEditText addCategoryET;
    Button addCategoryButton;
    ImageView categoryImage;
    FloatingActionButton selectImageFab;
    Uri contentUri;
    private ProgressDialog addCategoryDialog;
    String categoryName, categoryImageUri = "null";
    String categoryImageFileName = new String("null");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_category);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Add Category");
        initView();

        //push category data to firebase
        addCategoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fieldValidation()) {
                    categoryName = addCategoryET.getText().toString();
                    addCategoryButton.setEnabled(false);
                    addCategoryDialog.show();
                    uploadImageToFirebase(categoryImageFileName, contentUri);
                }
            }
        });

        selectImageFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });

    }

    private void addCategoryToFirebase() {
        String key = reference.push().getKey();
        CategoryHelper categoryHelper = new CategoryHelper(categoryName, categoryImageUri, key);
        reference.child(key).setValue(categoryHelper).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()) {
                    addCategoryDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Category Added Successfully", Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    addCategoryButton.setEnabled(true);
                    Toast.makeText(AddCategoryActivity.this, "Failed to upload. Please try again later.", Toast.LENGTH_LONG).show();

                }
            }
        });

    }

    private boolean fieldValidation() {

        boolean validate = false;

        if (!categoryImageFileName.equals("null") && !addCategoryET.getText().toString().isEmpty())
        {
            return true;
        }
        if(categoryImageFileName.equals("null") && addCategoryET.getText().toString().isEmpty())
        {
            Toast.makeText(AddCategoryActivity.this, "All fields are required", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (addCategoryET.getText().toString().isEmpty()) {
            addCategoryET.setError(getString(R.string.field_required));
            validate = false;
        }
        if (categoryImageFileName.equals("null")) {
            Toast.makeText(AddCategoryActivity.this, "Select Category Image", Toast.LENGTH_SHORT).show();
            validate = false;
        }
        return validate;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void openFileChooser() {

        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(gallery, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
                contentUri = data.getData();
                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                categoryImageFileName = "JPEG_" + timeStamp + "." + getFileExtension(contentUri);
                categoryImage.setImageURI(contentUri);
            }
        }

    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(cR.getType(uri));
    }

    private void initView() {
        addCategoryDialog = new ProgressDialog(AddCategoryActivity.this, R.style.AlertDialogStyle);
        addCategoryDialog.setTitle("Adding Category");
        addCategoryDialog.setCancelable(false);
        addCategoryDialog.setProgress(5);
        mStorage = FirebaseStorage.getInstance().getReference();
        rootnode = FirebaseDatabase.getInstance();
        reference = rootnode.getReference("categories");
        addCategoryET = findViewById(R.id.category_name_et);
        addCategoryButton = findViewById(R.id.add_category_button);
        selectImageFab = findViewById(R.id.add_categoryImage_fab);
        categoryImage = findViewById(R.id.category_image);
    }

    private void uploadImageToFirebase(String name, Uri contentUri) {

        StorageReference category_Image = mStorage.child("category/" + name);
        category_Image.putFile(contentUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                category_Image.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        categoryImageUri = uri.toString();
                        addCategoryToFirebase();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                addCategoryDialog.dismiss();
                addCategoryButton.setEnabled(true);
                Toast.makeText(AddCategoryActivity.this, "Failed to upload. Please try again later.", Toast.LENGTH_LONG).show();
            }
        });
    }
}