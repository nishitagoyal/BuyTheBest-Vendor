package com.something.vendorapp.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.something.vendorapp.R;
import com.something.vendorapp.adapter.ItemsAdapter;
import com.something.vendorapp.model.ItemHelper;
import com.something.vendorapp.model.Shared;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ItemActivity extends AppCompatActivity {

    List<ItemHelper> groceryItems;
    ItemsAdapter itemsAdapter;
    RecyclerView itemRecyclerView;
    FirebaseDatabase rootnode;
    StorageReference mStorage;
    DatabaseReference reference, itemReference, addToCartReference;
    String categoryName,categoryKey;
    Shared shared;
    TextView txt_item;
    DisplayMetrics metrics;
    int width, height;
    ImageView noItemsIV;
    ProgressBar progressBar;
    SearchView searchView;
    CardView searchCardView;
    private ProgressDialog editItemDialog;
    public static final int PICK_IMAGE_REQUEST = 1001;
    ImageView itemImage;
    String itemImageFileName = new String("null");
    Uri contentUri;
    String name, price, Qty,itemImageUri = "null";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item);
        Intent intent = getIntent();
        categoryName = intent.getStringExtra("categoryName");
        categoryKey = intent.getStringExtra("categoryKey");
        txt_item = findViewById(R.id.txt_heading);
        txt_item.setText(categoryName);
        initViews();
    }

    private void initViews() {
        mStorage = FirebaseStorage.getInstance().getReference();
        editItemDialog = new ProgressDialog(ItemActivity.this, R.style.AlertDialogStyle);
        editItemDialog.setTitle("Item Updating");
        editItemDialog.setCancelable(false);
        editItemDialog.setProgress(5);
        itemRecyclerView = findViewById(R.id.itemListRecyclerView);
        groceryItems = new ArrayList<>();
        itemsAdapter = new ItemsAdapter(ItemActivity.this,groceryItems, ItemActivity.this);
        itemRecyclerView.setAdapter(itemsAdapter);
        rootnode = FirebaseDatabase.getInstance();
        reference = rootnode.getReference("categories");
        addToCartReference = rootnode.getReference("users");
        shared = new Shared(ItemActivity.this);
        metrics = getResources().getDisplayMetrics();
        width = metrics.widthPixels;
        height = metrics.heightPixels;
        progressBar = findViewById(R.id.items_progress);
        noItemsIV = findViewById(R.id.empty_item);
        searchView = findViewById(R.id.search_view);
        searchCardView = findViewById(R.id.search_cardView);
        populateList();

    }

    private void populateList() {

        groceryItems.clear();
        itemReference = reference.child(categoryKey).child("items");
        itemReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds: snapshot.getChildren())
                {
                    ItemHelper groceryItem = ds.getValue(ItemHelper.class);
                    groceryItems.add(groceryItem);
                }

                if(groceryItems.isEmpty()) {
                    noItemsIV.setVisibility(View.VISIBLE);
                    searchCardView.setVisibility(View.GONE);
                }

                itemRecyclerView.setAdapter(new ItemsAdapter(ItemActivity.this, groceryItems,ItemActivity.this));
                progressBar.setVisibility(View.GONE);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public void itemAvailabilityStatus(String key, String status)
    {
        itemReference = reference.child(categoryKey).child("items");
        itemReference.child(key).child("itemAvailability").setValue(status);
        populateList();
    }

    public void editDetails(String key, String itemName, String itemPrice, String itemQty, String itemImageLink)
    {
        final Dialog dialog = new Dialog(ItemActivity.this);
        dialog.setContentView(R.layout.edit_item_dialog);
        dialog.setTitle("Edit Details");
        dialog.getWindow().setLayout((6 * width)/7, LinearLayout.LayoutParams.WRAP_CONTENT);
        itemReference = reference.child(categoryKey).child("items");
        itemImage = dialog.findViewById(R.id.edit_item_image);
        Uri itemUri = Uri.parse(itemImageLink);
        Picasso.get().load(itemUri).into(itemImage);
        FloatingActionButton selectImageFab = dialog.findViewById(R.id.edit_itemImage_fab);
        Button button = (Button) dialog.findViewById(R.id.editDetailsButton);
        TextInputEditText editName = dialog.findViewById(R.id.name_editText);
        TextInputEditText editPrice = dialog.findViewById(R.id.phoneNum_editText);
        TextInputEditText editQty = dialog.findViewById(R.id.address_editText);
        editName.setText(itemName);
        editPrice.setText(itemPrice);
        editQty.setText(itemQty);
        selectImageFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser(); }
        });
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                if(!editName.getText().toString().isEmpty() && !editPrice.getText().toString().isEmpty() && !editQty.getText().toString().isEmpty() && !itemImageFileName.equals("null"))
                {
                    name = editName.getText().toString();
                    price = editPrice.getText().toString();
                    Qty = editQty.getText().toString();
                    editItemDialog.show();
                    uploadImageToFirebase(itemImageFileName, contentUri, key);
//                    itemReference.child(key).child("itemName").setValue(name);
//                    itemReference.child(key).child("itemPrice").setValue(price);
//                    itemReference.child(key).child("itemQty").setValue(Qty);
//                    Toast.makeText(ItemActivity.this,"Item edited successfully!",Toast.LENGTH_SHORT).show();
//                    populateList();
                    dialog.dismiss();

                }
                if(editName.getText().toString().isEmpty())
                {
                    editName.setError(getString(R.string.field_required));
                }
                if(editPrice.getText().toString().isEmpty())
                {
                    editPrice.setError(getString(R.string.field_required));
                }
                if(editQty.getText().toString().isEmpty())
                {
                    editQty.setError(getString(R.string.field_required));
                }
                if(itemImageFileName.equals("null"))
                { Toast.makeText(ItemActivity.this,"Image is mandatory",Toast.LENGTH_LONG).show(); }
            }
        });
        dialog.show();
    }

    private void uploadImageToFirebase(String name, Uri contentUri, String key) {

        StorageReference Profile_Image =mStorage.child("items/" + name);
        Profile_Image.putFile(contentUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Profile_Image.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        itemImageUri = uri.toString();
                        editItemToFirebase(key);
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                editItemDialog.dismiss();
                Toast.makeText(ItemActivity.this, "Failed to upload. Please try again later.", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void editItemToFirebase(String key) {
        itemReference.child(key).child("itemImage").setValue(itemImageUri);
        itemReference.child(key).child("itemName").setValue(name);
        itemReference.child(key).child("itemPrice").setValue(price);
        itemReference.child(key).child("itemQty").setValue(Qty);
        Toast.makeText(ItemActivity.this,"Item edited successfully!",Toast.LENGTH_SHORT).show();
        populateList();
        editItemDialog.dismiss();
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
    public void onStart() {
        super.onStart();
        if(searchView!=null)
        {
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    search(newText);
                    return false;
                }
            });
        }
    }

    private void search(String str) {
        List<ItemHelper> itemSearchList = new ArrayList<>();
        for(ItemHelper itemHelperObject: groceryItems)
        {
            if(itemHelperObject.getItemName().toLowerCase().contains(str.toLowerCase()))
                itemSearchList.add(itemHelperObject);
        }
        itemRecyclerView.setAdapter(new ItemsAdapter(ItemActivity.this, itemSearchList,ItemActivity.this));


    }

}