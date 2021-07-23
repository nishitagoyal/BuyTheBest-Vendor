package com.something.vendorapp.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.something.vendorapp.R;
import com.something.vendorapp.adapter.OrdersPlacedAdapter;
import com.something.vendorapp.model.OrderPlaced;

import java.util.ArrayList;
import java.util.List;


public class AllOrderFragment extends Fragment {

    RecyclerView orderPlacedRecyclerView;
    DatabaseReference orderPlaceRef;
    FirebaseDatabase rootnode;
    List<OrderPlaced> orderPlacedList;
    List<OrderPlaced> reverseOrderPlacedList;


    public AllOrderFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViews();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_today_order, container, false);
        orderPlacedRecyclerView = root.findViewById(R.id.pending_order_recycler);
        populateList();
        return root;
    }

    private void initViews() {
        rootnode = FirebaseDatabase.getInstance();
        orderPlaceRef = rootnode.getReference("orders");
        orderPlacedList = new ArrayList<>();
        reverseOrderPlacedList = new ArrayList<>();
    }

    private void populateList() {

        orderPlaceRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds: snapshot.getChildren())
                {
                    OrderPlaced orderPlaced = ds.getValue(OrderPlaced.class);
                    orderPlacedList.add(orderPlaced);
                }
                for(int i=orderPlacedList.size()-1; i>=0; i--)
                {
                    reverseOrderPlacedList.add(orderPlacedList.get(i));
                }
                orderPlacedRecyclerView.setAdapter(new OrdersPlacedAdapter(getContext(), reverseOrderPlacedList));
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
}