package com.example.thuc_tap_tmdd_fpoly.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.thuc_tap_tmdd_fpoly.MainActivity;
import com.example.thuc_tap_tmdd_fpoly.R;
import com.example.thuc_tap_tmdd_fpoly.adapter.OrderDetailsAdapter;
import com.example.thuc_tap_tmdd_fpoly.model.Discount;
import com.example.thuc_tap_tmdd_fpoly.model.Order;
import com.example.thuc_tap_tmdd_fpoly.model.Product;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class orderDetailsActivity extends AppCompatActivity {
    private Button btnOrder;
    private RecyclerView rcvOrderDetail;
    private TextView txtSubtotal, txtDelivery, txtTax, txtTotal, txtAddress, txtPayment, txtPhone, txtMoney;
    private LinearLayout linearLayouAddress, idlr;
    private EditText notes;
    private List<Product> productList;
    private List<Integer> colorList;
    private OrderDetailsAdapter adapter;
    private String idProduct;
    private DatabaseReference productRef, userRef;
    private FirebaseUser firebaseUser;
    private int quantity = 0;
    private double delivery;
    private double tax;
    private double discountAmount;

    private double discountedPrice;
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private boolean finalPaid;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        btnOrder = findViewById(R.id.btnOrder);
        rcvOrderDetail = findViewById(R.id.rcvOrder_dt);
        txtSubtotal = findViewById(R.id.txtSubtotal);
        txtDelivery = findViewById(R.id.txtDelivery);
        txtTax = findViewById(R.id.txtTax);
        txtTotal = findViewById(R.id.txtTotal);
        txtAddress = findViewById(R.id.txtAddress);
        txtPhone = findViewById(R.id.txtPhone);
        txtPayment = findViewById(R.id.txtPayment);
        txtMoney = findViewById(R.id.txtMoney);
        notes = findViewById(R.id.editTextMessageToSeller);
        linearLayouAddress = findViewById(R.id.lrlAddress);
        idlr = findViewById(R.id.idlr);
        userRef = FirebaseDatabase.getInstance().getReference();
        poppuGetListPayment();

        Intent intent = getIntent();
        idProduct = intent.getStringExtra("idPro");
        productRef = FirebaseDatabase.getInstance().getReference().child("products").child(idProduct);
        String buyerID = firebaseUser.getUid();
        DatabaseReference buyerRef = userRef.child("user").child(buyerID).child("wallet");
        buyerRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    double walletAmount = dataSnapshot.getValue(Double.class);
                    txtMoney.setText(String.format(walletAmount + " VND"));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Xử lý khi có lỗi xảy ra
            }
        });
        productRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String name = dataSnapshot.child("name").getValue(String.class);
                    String idseller = dataSnapshot.child("sellerId").getValue(String.class);
                    String productType = dataSnapshot.child("productType").getValue(String.class);
                    String categoryID = dataSnapshot.child("categoryID").getValue(String.class);
                    String brand = dataSnapshot.child("brand").getValue(String.class);
                    String description = dataSnapshot.child("description").getValue(String.class);
                    String reviewId = dataSnapshot.child("reviewId").getValue(String.class);
                    int sold = dataSnapshot.child("sold").getValue(Integer.class);
                    double price = dataSnapshot.child("price").getValue(Double.class);
                    int currentQuantity = dataSnapshot.child("quantity").getValue(Integer.class);
                    List<String> imgProduct = dataSnapshot.child("imgProduct").getValue(new GenericTypeIndicator<List<String>>() {
                    });

                    Bundle bundle = getIntent().getBundleExtra("productData");
                    adapter = new OrderDetailsAdapter();

                    if (bundle != null) {

                        colorList = bundle.getIntegerArrayList("Color");
                        String size = bundle.getString("Size");
                        quantity = bundle.getInt("Quantity");
                        List<String> sizeList = new ArrayList<>();
                        sizeList.add(size);

                        DataSnapshot discountSnapshot = dataSnapshot.child("discount");
                        double discountPercentage = 0.0;
                        if (discountSnapshot.exists()) {
                            discountAmount = discountSnapshot.child("amount").getValue(Double.class);
                            double originalPrice = price * quantity;
                            discountPercentage = discountAmount / 100.0;
                            discountedPrice = originalPrice - (originalPrice * discountPercentage);

                            txtSubtotal.setText(String.format("%.0f VND", discountedPrice));

                            double discountValue = originalPrice * discountPercentage;
                            String discountMessage = String.format("Sản phẩm đã được giảm: %.0f VND", discountValue);
                            Toast.makeText(orderDetailsActivity.this, discountMessage, Toast.LENGTH_SHORT).show();

                        }
                        Discount discount = new Discount(discountAmount);

                        Product product = new Product(idProduct, idseller, name, null, categoryID, brand,
                                description, imgProduct, colorList, sold, reviewId, quantity, price, Collections.singletonList(size), discount);

                        product.setSelectedQuantity(quantity);
                        productList = new ArrayList<>();
                        productList.add(product);

                        adapter.setProductList(productList);
                        adapter.setColorList(colorList);

                        rcvOrderDetail.setLayoutManager(new LinearLayoutManager(orderDetailsActivity.this));
                        rcvOrderDetail.setAdapter(adapter);

                        delivery = 0.0;
                        tax = 0.0;

                        String deliveryText = txtDelivery.getText().toString().replace("VND", "");
                        String taxText = txtTax.getText().toString().replace("VND", "");

                        if (!TextUtils.isEmpty(deliveryText)) {
                            delivery = Double.parseDouble(deliveryText);
                        }

                        if (!TextUtils.isEmpty(taxText)) {
                            tax = Double.parseDouble(taxText);
                        }

                        double total = discountedPrice + delivery + tax;
                        txtTotal.setText(String.format(total + " VND"));

                    } else {
                        productList = new ArrayList<>();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        btnOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference orderRef = FirebaseDatabase.getInstance().getReference("list_order");
                String newKey = orderRef.push().getKey();
                String idBuyer = firebaseUser.getUid();
                String date = getCurrentTime();

                if (txtAddress.getText().toString().equalsIgnoreCase("Enter your address")) {
                    Toast.makeText(orderDetailsActivity.this, "Vui lòng chọn địa chỉ", Toast.LENGTH_SHORT).show();
                    return;
                } else if (txtPayment.getText().toString().equalsIgnoreCase("Payment methods")) {
                    Toast.makeText(orderDetailsActivity.this, "Vui lòng chọn phương thức thanh toán", Toast.LENGTH_SHORT).show();
                    return;
                }

                finalPaid = false;
                if (txtPayment.getText().toString().equalsIgnoreCase("Payment on delivery")) {
                    finalPaid = false;
                } else if (txtPayment.getText().toString().equalsIgnoreCase("Pay with wallet")) {
                    finalPaid = true;
                }

                if (finalPaid) {
                    double totalAmount = Double.parseDouble(txtTotal.getText().toString());
                    String buyerID = firebaseUser.getUid();
                    checkSufficientWalletAmount(buyerID, totalAmount, newKey, idBuyer, date);
                } else {
                    performNextSteps(newKey, idBuyer, date);
                }
            }
        });
        linearLayouAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDiaLogAddress();
            }
        });
    }
    private void showDiaLogAddress() {
        Intent intent = new Intent(orderDetailsActivity.this, ShowListLocationActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("key", "value");
        intent.putExtras(bundle);
        startActivityForResult(intent, Activity.RESULT_CANCELED);
    }
    private void On_Create_Bill(Order order) {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference myRef = firebaseDatabase.getReference("list_order");
        String id = order.getId();
        myRef.child(id).setValue(order, (error, ref) -> {
            if (error == null) {
                showDialogOrder();
            } else {
                Toast.makeText(orderDetailsActivity.this, "Lỗi khi lưu sản phẩm vào Realtime Database", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void poppuGetListPayment() {
        String[] listPayment = {"Payment on delivery", "Pay with wallet"};
        txtPayment.setOnClickListener(view -> {
            PopupMenu popupMenu = new PopupMenu(orderDetailsActivity.this, txtPayment);
            for (String address : listPayment) {
                popupMenu.getMenu().add(address);
            }
            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    String selectedAddress = item.getTitle().toString();
                    txtPayment.setText(selectedAddress);
                    return true;
                }
            });
            popupMenu.show();
        });
    }
    private void checkSufficientWalletAmount(String buyerID, double amount, String newKey, String idBuyer, String date) {
        DatabaseReference buyerRef = userRef.child("user").child(buyerID).child("wallet");
        buyerRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                double walletAmount = snapshot.getValue(Double.class);
                boolean isWalletEnough = walletAmount >= amount;
                if (isWalletEnough) {
                    performNextSteps(newKey, idBuyer, date);
                } else {
                    Toast.makeText(orderDetailsActivity.this, "Số tiền trong ví không đủ", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Xử lý khi có lỗi xảy ra trong quá trình truy vấn Firebase
            }
        });
    }
    private void performNextSteps(String newKey, String idBuyer, String date) {
        DatabaseReference productsRef = FirebaseDatabase.getInstance().getReference("products");
        for (int i = 0; i < adapter.getItemCount(); i++) {
            Product product = adapter.getProduct(i);
            if (product != null) {
                List<Integer> listColor = product.getColor();
                int color = listColor.get(i);

                Bundle bundle = getIntent().getBundleExtra("productData");
                if (bundle != null) {
                    int receivedQuantity = bundle.getInt("Quantity");

                    productsRef.child(product.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            int currentQuantity = dataSnapshot.child("quantity").getValue(Integer.class);
                            int remainingQuantity = currentQuantity - receivedQuantity;

                            if (remainingQuantity >= 0) {
                                productsRef.child(product.getId()).child("quantity").setValue(remainingQuantity);

                                Order order = new Order(newKey, idBuyer, product.getSellerId(), product.getId(), product.getName(),
                                        product.getImgProduct().get(0), color, Double.parseDouble(txtTotal.getText().toString()),
                                        date, txtAddress.getText().toString(), txtPhone.getText().toString(),
                                        receivedQuantity, notes.getText().toString(), finalPaid, "waiting");

                                On_Create_Bill(order);
                                Log.d("=====", "onClick: sl" + product.getSellerId() + " pr " + product.getId());
                            } else {
                                Toast.makeText(orderDetailsActivity.this, "Số lượng sản phẩm không đủ", Toast.LENGTH_SHORT).show();
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            // Xử lý khi có lỗi xảy ra trong quá trình truy vấn Firebase
                        }
                    });
                }
            }
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            Bundle bundle = data.getExtras();
            if (bundle == null) {
                Toast.makeText(this, "Chưa chọn địa chỉ", Toast.LENGTH_SHORT).show();
                Log.d("GGGGGGG", "setLocation: ");
            } else {
                idlr.setVisibility(View.VISIBLE);
                String name = bundle.getString("nameLocation");
                Log.d("HHHHHHHHHHH", "setLocation: " + name);
                String phone = bundle.getString("phoneLocation");
                String location = bundle.getString("location");
                txtAddress.setText(location);
                txtPhone.setVisibility(View.VISIBLE);
                txtPhone.setText(phone);
            }
        }
    }
    private void addAmountToSellerWallet(String idSeller, double amount) {
        DatabaseReference sellerRef = userRef.child("user").child(idSeller).child("wallet");
        sellerRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                double walletAmount = snapshot.getValue(Double.class);
                walletAmount += amount;
                sellerRef.setValue(walletAmount);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void showDialogOrder() {
        ConstraintLayout successDialog = findViewById(R.id.successDialog);
        View view = LayoutInflater.from(orderDetailsActivity.this).inflate(R.layout.success_dialog_order, successDialog);
        Button successDone = view.findViewById(R.id.btnDone);
        AlertDialog.Builder builder = new AlertDialog.Builder(orderDetailsActivity.this);
        builder.setView(view);
        final AlertDialog alertDialog = builder.create();
        successDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        alertDialog.show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (alertDialog.isShowing()) {
                    Intent intent = new Intent(orderDetailsActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        }, 1000);
    }

    private String getCurrentTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
        Date currentDate = new Date();
        return sdf.format(currentDate);
    }
}