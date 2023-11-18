package com.example.thuc_tap_tmdd_fpoly.fragment;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

;
import com.example.thuc_tap_tmdd_fpoly.R;
import com.example.thuc_tap_tmdd_fpoly.activity.InforOrderActivity;
import com.example.thuc_tap_tmdd_fpoly.adapter.OrderAdapter;
import com.example.thuc_tap_tmdd_fpoly.model.Order;
import com.example.thuc_tap_tmdd_fpoly.model.Product;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ConfirmForShopFragment extends Fragment implements OrderAdapter.Callback{
    private RecyclerView recyclerView;
    private OrderAdapter oderAdapter;
    private ArrayList<Order> list = new ArrayList<>();
    private FirebaseUser firebaseUser;
    private TextView noResultsTextView;

    public ConfirmForShopFragment() {
        // Required empty public constructor
    }

    public static ConfirmForShopFragment newInstance() {
        ConfirmForShopFragment fragment = new ConfirmForShopFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_confirm_for_shop, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.rec_confirmedforshop);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        oderAdapter = new OrderAdapter(getContext(), list, this);
        noResultsTextView = view.findViewById(R.id.noResultsTextView);
        recyclerView.setAdapter(oderAdapter);
        GetDataConfirmListForBuyer();
    }
    private void GetDataConfirmListForBuyer() {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        String id_user = firebaseUser.getUid();
        DatabaseReference myReference = firebaseDatabase.getReference("list_order");

        myReference.orderByChild("status").equalTo("confirmed").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (list != null) {
                    list.clear();
                }
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Order order = dataSnapshot.getValue(Order.class);
                    if (order.getIdSeller().equals(id_user)) {
                        list.add(order);
                    }
                }
                if (list.isEmpty()){
                    recyclerView.setVisibility(View.GONE);
                    noResultsTextView.setVisibility(View.VISIBLE);

                }else {
                    recyclerView.setVisibility(View.VISIBLE);
                    noResultsTextView.setVisibility(View.GONE);
                }
                oderAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Get list order failed", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void dialogForUser(Order order) {
        Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.dialog_menu_order);
        dialog.getWindow().setBackgroundDrawable(getActivity().getDrawable(R.drawable.bg_dialog_order));
        Window window = dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        WindowManager.LayoutParams windowAttributes = window.getAttributes();
        window.setAttributes(windowAttributes);
        windowAttributes.gravity = Gravity.BOTTOM;
        Button btnCancel = dialog.findViewById(R.id.btn1);
        Button btnExit = dialog.findViewById(R.id.btn2);
        btnExit.setOnClickListener(view -> {
            dialog.cancel();
        });
        btnCancel.setText("Tiến hành giao");
        btnCancel.setOnClickListener(view -> {
            order.setStatus("deliver");
            UpdateStatus(order);
            AddSoldMinusQuantity(order.getId());
            dialog.dismiss();
        });
        Button tt = dialog.findViewById(R.id.btn_propety);
        tt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), InforOrderActivity.class);
                intent.putExtra("idOrder",order.getId());
                startActivity(intent);
            }
        });

        dialog.show();
    }

    @Override
    public void logic(Order order) {
        dialogForUser(order);
    }
    private void UpdateStatus(Order order) {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference myRef = firebaseDatabase.getReference("list_order");
        String id = order.getId();
        myRef.child(id).setValue(order, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                if (error == null) {
                    Toast.makeText(getContext(), "Update status", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Update fall", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void AddSoldMinusQuantity(String id) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("list_order");
        reference.orderByChild("id").equalTo(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot orderSnapshot : snapshot.getChildren()) {
                    String idOrder = orderSnapshot.child("id").getValue(String.class);
                    if (idOrder != null && idOrder.equals(id)) {
                        int quantity = orderSnapshot.child("quantity").getValue(Integer.class);
                        String idProduct = orderSnapshot.child("idProduct").getValue(String.class);
                        DatabaseReference productRef =FirebaseDatabase.getInstance().getReference("products");
                        productRef.orderByChild("id").equalTo(idProduct).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot proSnapshot) {
                                if (proSnapshot.exists()){
                                   for (DataSnapshot productSnapshot :proSnapshot.getChildren()){
                                       DatabaseReference productRef1 =FirebaseDatabase.getInstance().getReference("products").child(idProduct);
                                       Product product = productSnapshot.getValue(Product.class);
                                       if (product!=null){
                                           int sold = product.getSold();
                                           int quantityProduct = product.getQuantity();

                                           product.setSold(sold+quantity);
                                           product.setQuantity(quantityProduct-quantity);
                                           productRef1.child("sold").setValue(product.getSold());
                                           productRef1.child("quantity").setValue(product.getQuantity());
                                       }
                                   }
                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    } else {
                        Toast.makeText(getContext(), "NULLLL", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("===", "onCancelled: Error retrieving order data", error.toException());
            }
        });
    }
}