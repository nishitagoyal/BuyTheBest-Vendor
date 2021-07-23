package com.something.vendorapp.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
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
    DatabaseReference orderPlaceRef, databaseReference;
    FirebaseDatabase rootnode;
    List<OrderPlaced> orderPlacedList;
    List<OrderPlaced> reverseOrderPlacedList;
    ProgressBar progressBar;
    SwipeRefreshLayout swipeRefreshLayout;

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
        databaseReference = rootnode.getReference("users");
        orderPlacedList = new ArrayList<>();
        reverseOrderPlacedList = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_today_order, container, false);
        orderPlacedRecyclerView = root.findViewById(R.id.pending_order_recycler);
        progressBar = root.findViewById(R.id.orders_progress);
        swipeRefreshLayout = root.findViewById(R.id.container);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                orderPlacedList.clear();
                reverseOrderPlacedList.clear();
                populateList();
                swipeRefreshLayout.setRefreshing(false);

            }
        });
        populateList();
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
                orderPlacedRecyclerView.setAdapter(new OrdersPlacedAdapter(getContext(), reverseOrderPlacedList, TodayOrderFragment.this));
                progressBar.setVisibility(View.GONE);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getContext(), "Please try again later", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void setStatus (String key, String status, String user_key, String order_key)
    {
        orderPlaceRef.child(key).child("order_status").setValue(status);
        databaseReference.child(user_key).child("orders_placed").child(order_key).child("order_status").setValue(status);
    }


}