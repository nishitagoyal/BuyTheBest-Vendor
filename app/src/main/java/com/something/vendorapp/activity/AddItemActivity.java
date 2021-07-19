package com.something.vendorapp.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.something.vendorapp.R;
import com.something.vendorapp.model.CategoryHelper;
import com.something.vendorapp.model.ItemHelper;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AddItemActivity extends AppCompatActivity {

    public static final int PICK_IMAGE_REQUEST = 1001;
    StorageReference mStorage;
    FirebaseDatabase rootnode;
    DatabaseReference reference, itemReference;
    Spinner selectCategory, selectAvailability;
    TextInputEditText  itemNameET, itemQtyET, itemPriceET;
    Button addItemButton;
    ImageView itemImage;
    FloatingActionButton selectImageFab;
    Uri contentUri;
    private ProgressDialog addItemDialog;
    String itemImageFileName = new String("null");
    String itemName, itemPrice, itemQty, itemAvailability, itemType, itemImageUri = "null";
    String[] availability = {"Select Availability", "In Stock", "Out of Stock"};
    List<CategoryHelper> categoryHelpers;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Add Items");
        initView();

        //to populate availability spinner
        ArrayAdapter arrayAdapter = new ArrayAdapter(this,android.R.layout.simple_spinner_dropdown_item,availability);
        selectAvailability.setAdapter(arrayAdapter);

        //provide functionality to the add image fab button
        selectImageFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser(); }
        });

        //provide functionality to the add item button: will push data to the firebase server
        addItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(fieldValidation())
                {
                    itemPrice = "Rs. " + itemPriceET.getText().toString();
                    itemName = itemNameET.getText().toString();
                    itemQty = itemQtyET.getText().toString();
                    itemAvailability = selectAvailability.getSelectedItem().toString();
                    itemType = selectCategory.getSelectedItem().toString();
                    addItemButton.setEnabled(false);
                    addItemDialog.show();
                    uploadImageToFirebase(itemImageFileName, contentUri);
                }
            }
        });

        //used to populate category spinner
        reference.orderByChild("categoryName").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<String> categoryNameList = new ArrayList<String>();
                categoryNameList.add("Select Category");
                for(DataSnapshot ds: snapshot.getChildren())
                {
                    CategoryHelper categoryHelper = ds.getValue(CategoryHelper.class);
                    categoryHelpers.add(categoryHelper);
                }
                for(int i =0; i<categoryHelpers.size();i++)
                {
                    String category_name = categoryHelpers.get(i).getCategoryName();
                    categoryNameList.add(category_name);
                }
                ArrayAdapter<String> addressAdapter = new ArrayAdapter<String>(AddItemActivity.this, android.R.layout.simple_spinner_dropdown_item, categoryNameList);
                selectCategory.setAdapter(addressAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private boolean fieldValidation() {
        if(!itemNameET.getText().toString().isEmpty() && !itemPriceET.getText().toString().isEmpty() && !itemQtyET.getText().toString().isEmpty() && selectAvailability.getSelectedItem()!="Select Availability" && !itemImageFileName.equals("null") && (selectCategory.getSelectedItem()!= "Select Category"))
        { return true; }
        else
        { Toast.makeText(AddItemActivity.this,"All fields are mandatory",Toast.LENGTH_LONG).show();
          return false; }
    }

    private void initView() {
        addItemDialog = new ProgressDialog(AddItemActivity.this, R.style.AlertDialogStyle);
        addItemDialog.setTitle("Item Adding");
        addItemDialog.setCancelable(false);
        addItemDialog.setProgress(5);
        categoryHelpers = new ArrayList<>();
        mStorage = FirebaseStorage.getInstance().getReference();
        rootnode = FirebaseDatabase.getInstance();
        reference = rootnode.getReference("categories");
        selectCategory = findViewById(R.id.category_Spinner);
        selectAvailability = findViewById(R.id.item_availability_Spinner);
        itemNameET = findViewById(R.id.item_name_et);
        itemQtyET = findViewById(R.id.item_qty_et);
        itemPriceET = findViewById(R.id.item_price_et);
        addItemButton = findViewById(R.id.add_item_button);
        selectImageFab = findViewById(R.id.add_itemImage_fab);
        itemImage = findViewById(R.id.item_image);
    }

    private void openFileChooser() {

        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(gallery,PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST) {
            if(resultCode == Activity.RESULT_OK){
                contentUri = data.getData();
                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                itemImageFileName = "JPEG_" + timeStamp + "." + getFileExtension(contentUri);
                itemImage.setImageURI(contentUri);
            }
        }

    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(cR.getType(uri));
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

    private void uploadImageToFirebase(String name, Uri contentUri) {

        StorageReference Profile_Image =mStorage.child("items/" + name);
        Profile_Image.putFile(contentUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Profile_Image.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        itemImageUri = uri.toString();
                        addItemToFirebase();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                addItemDialog.dismiss();
                addItemButton.setEnabled(true);
                Toast.makeText(AddItemActivity.this, "Failed to upload. Please try again later.", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void addItemToFirebase() {
        // this function queries the database for the selected category key and then push the item corresponding to that particular key.
        Query query = reference.orderByChild("categoryName").equalTo(itemType);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                String category_key = "null";
                for(DataSnapshot data: snapshot.getChildren())
                {
                    category_key = data.child("categoryKey").getValue().toString();
                }
                itemReference = reference.child(category_key).child("items");
                String key = itemReference.push().getKey();
                ItemHelper itemHelper = new ItemHelper(itemName,itemPrice,itemQty,itemImageUri,itemAvailability,itemType,key);
                itemReference.child(key).setValue(itemHelper).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            addItemDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "Item Added Successfully", Toast.LENGTH_LONG).show();
                            finish();
                        } else {
                            addItemButton.setEnabled(true);
                            Toast.makeText(AddItemActivity.this, "Failed to upload. Please try again later.", Toast.LENGTH_LONG).show();

                        }
                    }
                });
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                addItemButton.setEnabled(true);
                Toast.makeText(AddItemActivity.this, "Failed to upload. Please try again later.", Toast.LENGTH_LONG).show();
            }
        });
    }
}