package com.example.thuc_tap_tmdd_fpoly.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.thuc_tap_tmdd_fpoly.R;
import com.example.thuc_tap_tmdd_fpoly.adapter.CartAdapter;
import com.example.thuc_tap_tmdd_fpoly.model.AddProductToCart;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class CartActivity extends AppCompatActivity implements CartAdapter.Callback {
    private RecyclerView recyclerViewCart;
    private TextView totalPriceItem,totalPriceCart;
    private CartAdapter cartAdapter;
    private AddProductToCart product;
    private List<AddProductToCart> list = new ArrayList<>();
    private FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        recyclerViewCart = findViewById(R.id.rcv_cart);
        totalPriceItem = findViewById(R.id.total_price_item);
        totalPriceCart =findViewById(R.id.total_price_cart);
        recyclerViewCart.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        getListProductAddCart();
        cartAdapter =new CartAdapter(CartActivity.this,list,this);
        recyclerViewCart.setAdapter(cartAdapter);
        sumPriceProduct();
    }
    private void getListProductAddCart() {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String id_user = firebaseUser.getUid();
        DatabaseReference myReference = firebaseDatabase.getReference("cart").child(id_user);
        myReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (list != null) {
                    list.clear();
                }
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    product = dataSnapshot.getValue(AddProductToCart.class);
                    list.add(product);
                }
                cartAdapter.notifyDataSetChanged();
                Log.d("=====", "onDataChange: "+snapshot+"  data   "+ myReference);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), "Get list users faild", Toast.LENGTH_SHORT).show();
                Log.d("LISTCART", "onCancelled: " + error.getMessage());
            }
        });
    }
    private void sumPriceProduct() {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String id_user = firebaseUser.getUid();
        DatabaseReference myReference = firebaseDatabase.getReference("cart").child(id_user);
        myReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<AddProductToCart> list = new ArrayList<>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    AddProductToCart product = dataSnapshot.getValue(AddProductToCart.class);
                    list.add(product);
                }

                if (list.size() == 0) {
                    return;
                } else {
                    double totalCart = caculatorTotalPrice(list);
                    totalPriceItem.setText(" " + list.size());
                    totalPriceCart.setText(String.valueOf(totalCart));

                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("Loi", "onCancelled: " + error.getMessage());
            }
        });
    }
    private double caculatorTotalPrice(List<AddProductToCart> productList) {
        double totalPrice = 0;
        for (AddProductToCart product : productList) {
            totalPrice += product.getPricetotal_product() * product.getQuantity_product();
        }
        return totalPrice;
    }

    @Override
    public void deleteItemCart(AddProductToCart products) {
        AlertDialog.Builder aBuilder = new AlertDialog.Builder(CartActivity.this);
        aBuilder.setTitle("Xóa sản phẩm trong giỏ hàng");
        aBuilder.setMessage("Bạn có chắc chắn muốn xóa " + products.getName_product() + " không?");
        aBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                String id_user = firebaseUser.getUid();
                DatabaseReference myRef = FirebaseDatabase.getInstance().getReference().child("cart").child(id_user).child(products.getId());
                myRef.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });
                dialogInterface.dismiss();
            }
        });

        aBuilder.setNegativeButton("Cancle", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        AlertDialog alertDialog = aBuilder.create();
        alertDialog.show();
    }

    @Override
    public void updateItemCart(AddProductToCart products) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        sumPriceProduct();
    }
}