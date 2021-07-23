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

public class TodayOrderFragment extends Fragment {


    RecyclerView orderPlacedRecyclerView;
    DatabaseReference orderPlaceRef;
    FirebaseDatabase rootnode;
    List<OrderPlaced> orderPlacedList;
    List<OrderPlaced> reverseOrderPlacedList;

    public TodayOrderFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViews();
    }

    private void initViews() {
        rootnode = FirebaseDatabase.getInstance();
        orderPlaceRef = rootnode.getReference("orders");
        orderPlacedList = new ArrayList<>();
        reverseOrderPlacedList = new ArrayList<>();
        populateList();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_today_order, container, false);
        orderPlacedRecyclerView = root.findViewById(R.id.pending_order_recycler);
        return root;
    }

    private void populateList() {

        orderPlaceRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds: snapshot.getChildren())
                {
                    OrderPlaced orderPlaced = ds.getValue(OrderPlaced.class);
                    if(orderPlaced.getOrder_status().equalsIgnoreCase("PENDING"))
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