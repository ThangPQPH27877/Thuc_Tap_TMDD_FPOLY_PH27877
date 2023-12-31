package com.example.thuc_tap_tmdd_fpoly.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;


import com.example.thuc_tap_tmdd_fpoly.MainActivity;
import com.example.thuc_tap_tmdd_fpoly.R;
import com.example.thuc_tap_tmdd_fpoly.activity.ChangePassword_Activity;
import com.example.thuc_tap_tmdd_fpoly.activity.ListUserActivity;
import com.example.thuc_tap_tmdd_fpoly.activity.LocationActivity;
import com.example.thuc_tap_tmdd_fpoly.activity.ManHinhChoActivity;
import com.example.thuc_tap_tmdd_fpoly.activity.ManagerProductActivity;
import com.example.thuc_tap_tmdd_fpoly.activity.OrderOfShopActivity;
import com.example.thuc_tap_tmdd_fpoly.activity.StatisticalActivity;
import com.example.thuc_tap_tmdd_fpoly.activity.TopUpCardActivity;
import com.example.thuc_tap_tmdd_fpoly.database.FireBaseType;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;


import java.io.ByteArrayOutputStream;

public class ProfileFragment extends Fragment implements View.OnClickListener {
    private FirebaseAuth firebaseAuth;
    private DatabaseReference mReference;
    private FirebaseUser firebaseUser;
    private ImageView imgUser, update_admin;
    private CardView cvOut, cvOder, cvPayment, cvReview, cvTK, cvPromotion, cvQLUser, cvQLProduct, cvChangePass, cvAdddiachi;
    private TextView textViewName, textSDT, textViewEmail, textFixInfor,textWallet;
    private ImageView dialog_AVT;
    private TextInputEditText edImg;
    private boolean isUserTypeAdmin;
    private static final int PICK_IMAGE_REQUEST = 1;
    private Picasso picasso = Picasso.get();

    public ProfileFragment() {

    }

    public static ProfileFragment newInstance() {
        ProfileFragment fragment = new ProfileFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        cvOut = view.findViewById(R.id.cvOut);
        cvOder = view.findViewById(R.id.cvOderForShop);
        cvPayment = view.findViewById(R.id.cvPayment);

        cvTK = view.findViewById(R.id.cvTK);

        cvQLUser = view.findViewById(R.id.cvQLUser);
        cvQLProduct = view.findViewById(R.id.cvQLProduct);
        cvChangePass = view.findViewById(R.id.cvChangePass);
        cvAdddiachi = view.findViewById(R.id.cvAdddiachi);

        imgUser = view.findViewById(R.id.imageViewAvatar);
        update_admin = view.findViewById(R.id.update_admin);
        textViewName = view.findViewById(R.id.textViewName);
        textSDT = view.findViewById(R.id.textSDT);
        textViewEmail = view.findViewById(R.id.textViewEmail);
        textWallet = view.findViewById(R.id.textWallet);
        textFixInfor = view.findViewById(R.id.textFixInfor);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        mReference = FirebaseDatabase.getInstance().getReference();
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String name = sharedPreferences.getString("username", "");
        String phone = sharedPreferences.getString("phone", "");
        String email = sharedPreferences.getString("email", "");
        float walletFloat = sharedPreferences.getFloat("wallet", 0);
        double wallet = (double) walletFloat;
        String img = sharedPreferences.getString("img", "");


        if (name.isEmpty()) {
            setInfoProfile();
        } else {
            textViewName.setText(name);
            textSDT.setText(phone);
            textViewEmail.setText(email);
            textWallet.setText("Ví : "+wallet+" VNĐ");
            if (img.equals("")) {
                imgUser.setImageResource(R.drawable.baseline_person_24);
            } else {
                picasso.load(img).into(imgUser);
            }
        }
        firebaseAuth = FirebaseAuth.getInstance();
        setRoleListUser();
        setInfoProfile();

        cvOder.setOnClickListener(this);
        cvOut.setOnClickListener(this);

        // Đoạn mã trong phương thức onViewCreated()

        update_admin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Quản trị người dùng");

                if (isUserTypeAdmin) {
                    // Nếu user_type đang là true (quản trị), hiển thị hộp thoại hỏi muốn hạ cấp hay không
                    builder.setMessage("Bạn có muốn hạ cấp xuống người dùng?");

                    builder.setPositiveButton("Có", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            downgradeToUser();
                        }
                    });
                } else {
                    // Nếu user_type đang là false (người dùng), hiển thị hộp thoại hỏi muốn nâng cấp hay không
                    builder.setMessage("Bạn có muốn nâng cấp lên quản trị?");

                    builder.setPositiveButton("Có", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            upgradeToAdmin();
                        }
                    });
                }

                builder.setNegativeButton("Không", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // Không thực hiện hành động nào
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        cvPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), TopUpCardActivity.class);
                startActivity(intent);
            }
        });
        cvChangePass.setOnClickListener(this);
        cvQLProduct.setOnClickListener(this);

        cvTK.setOnClickListener(this);
        cvQLUser.setOnClickListener(this);
        cvAdddiachi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), LocationActivity.class);
                startActivity(intent);
            }
        });
        textFixInfor.setOnClickListener(this);
    }
    // Phương thức nâng cấp lên quản trị
    private void upgradeToAdmin() {
        String userId = firebaseUser.getUid();
        DatabaseReference userRef = mReference.child("user").child(userId);

        userRef.child("user_type").setValue(true)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getActivity(), "Nâng cấp thành công", Toast.LENGTH_SHORT).show();
                        isUserTypeAdmin = true;
                        startActivity(new Intent(getContext(), MainActivity.class));
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(), "Nâng cấp thất bại", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Phương thức hạ cấp xuống người dùng
    private void downgradeToUser() {
        String userId = firebaseUser.getUid();
        DatabaseReference userRef = mReference.child("user").child(userId);

        userRef.child("user_type").setValue(false)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getActivity(), "Hạ cấp thành công", Toast.LENGTH_SHORT).show();
                        isUserTypeAdmin = false;
                        startActivity(new Intent(getContext(), MainActivity.class));
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(), "Hạ cấp thất bại", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void setInfoProfile() {
        String id = firebaseUser.getUid();
        DatabaseReference userRef = mReference.child("user").child(id);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String name = snapshot.child("username").getValue(String.class);
                String phone = snapshot.child("phone").getValue(String.class);
                String email = snapshot.child("email").getValue(String.class);
                double wallet = snapshot.child("wallet").getValue(Double.class);
                float walletFloat = (float) wallet;
                String img = snapshot.child("img").getValue(String.class);
                textViewName.setText(name);
                textViewEmail.setText(email);
                textWallet.setText("Ví : "+wallet+" VNĐ");

                if (img.equals("") || img.isEmpty()) {
                    imgUser.setImageResource(R.drawable.baseline_person_24);
                } else {
                    picasso.load(img).into(imgUser);
                }
                if (getActivity() != null) {
                    SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("username", name);
                    editor.putString("phone", phone);
                    editor.putString("email", email);
                    editor.putFloat("wallet", walletFloat);
                    editor.putString("img", img);
                    editor.apply();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("Loi", "onCancelled: " + error.getMessage());
            }
        });
    }

    private void showDialogFigProfile() {
        Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.dialog_fix_profile);

        TextInputEditText edUserName = dialog.findViewById(R.id.edUserName);
        TextInputEditText edPhone = dialog.findViewById(R.id.edPhone);
        TextInputEditText edAddress = dialog.findViewById(R.id.edAddress);
        Button btnCancel = dialog.findViewById(R.id.btnCancel);
        Button btnConfirm = dialog.findViewById(R.id.btnConfirm);
        dialog_AVT = dialog.findViewById(R.id.dialog_AVT);
        Button btnImg = dialog.findViewById(R.id.btnImg);
        edImg = dialog.findViewById(R.id.edImg);

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        String userId = firebaseUser.getUid();

        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("user").child(userId);
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String name = dataSnapshot.child("username").getValue(String.class);
                String phone = dataSnapshot.child("phone").getValue(String.class);
                String address = dataSnapshot.child("address").getValue(String.class);
                String img = dataSnapshot.child("img").getValue(String.class);
                edUserName.setText(name);
                edPhone.setText(phone);
                edAddress.setText(address);
                edImg.setText(img);

                String imgUrl = img;
                if (!TextUtils.isEmpty(imgUrl)) {
                    Picasso.get().load(imgUrl).into(dialog_AVT);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newUserName = edUserName.getText().toString().trim();
                String newPhone = edPhone.getText().toString().trim();
                String newAddress = edAddress.getText().toString().trim();
                String newImgUrl = edImg.getText().toString().trim();

                userRef.child("username").setValue(newUserName);
                userRef.child("phone").setValue(newPhone);
                userRef.child("address").setValue(newAddress);
                userRef.child("img").setValue(newImgUrl);

                if (!newImgUrl.isEmpty()) {
                    Picasso.get().load(newImgUrl).into(imgUser);
                } else if (dialog_AVT.getDrawable() != null) {
                    Bitmap bitmap = ((BitmapDrawable) dialog_AVT.getDrawable()).getBitmap();
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos);
                    byte[] imageData = baos.toByteArray();
                    StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("images").child(userId + ".jpg");
                    UploadTask uploadTask = storageRef.putBytes(imageData);
                    uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            if (task.isSuccessful()) {
                                storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        String imageUrl = uri.toString();
                                        userRef.child("img").setValue(imageUrl);
                                        Toast.makeText(getActivity(), "Thay đổi thông tin thành công", Toast.LENGTH_SHORT).show();
                                        replaceFragment(new ProfileFragment());
                                        dialog.dismiss();
                                    }
                                });
                            } else {
                                Toast.makeText(getActivity(), "Lỗi khi tải lên ảnh", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
                Toast.makeText(getActivity(), "Thay đổi thông tin thành công", Toast.LENGTH_SHORT).show();
                replaceFragment(new ProfileFragment());
                dialog.dismiss();
            }

            private void replaceFragment(ProfileFragment profileFragment) {
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frame_layout, profileFragment);
                fragmentTransaction.commit();
            }
        });
        btnImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE_REQUEST);
            }
        });

        dialog.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            Picasso.get().load(imageUri).into(dialog_AVT);
            String imageUrl = imageUri.toString();
            edImg.setText(imageUrl);
        }
    }

    private void showDialogOut() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_out_app, null);

        Button btnCancel = view.findViewById(R.id.btnCancel);
        Button btnConfirm = view.findViewById(R.id.btnConfirm);

        builder.setView(view);
        final AlertDialog alertDialog = builder.create();
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), ManHinhChoActivity.class));
                alertDialog.dismiss();
            }
        });

        alertDialog.show();
    }

    public void setRoleListUser() {
        String id = firebaseUser.getUid();
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("user").child(id);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean isAdmin = FireBaseType.isAdmin(dataSnapshot);
                if (dataSnapshot.exists()) {
                    isUserTypeAdmin = dataSnapshot.child("user_type").getValue(Boolean.class);
                }
                if (isAdmin) {
                    cvQLUser.setVisibility(View.VISIBLE);
                    cvQLProduct.setVisibility(View.GONE);
                    cvOder.setVisibility(View.GONE);
                    cvTK.setVisibility(View.GONE);

                } else {
                    cvQLUser.setVisibility(View.GONE);
                    cvOder.setVisibility(View.VISIBLE);
                    cvQLProduct.setVisibility(View.VISIBLE);
                    cvTK.setVisibility(View.VISIBLE);
                }
                update_admin.setImageResource(isUserTypeAdmin ? R.drawable.baseline_person_24 : R.drawable.update_admin);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("Loi", "onCancelled: " + databaseError.getMessage());
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        setInfoProfile();
    }

    @Override
    public void onClick(View view) {

        if (view.getId() == R.id.cvOut) {
            showDialogOut();
            firebaseAuth.signOut();
        } else if (view.getId() == R.id.cvOderForShop) {
            startActivity(new Intent(getContext(), OrderOfShopActivity.class));
        } else if (view.getId() == R.id.cvPayment) {

        } else if (view.getId() == R.id.cvTK) {
            startActivity(new Intent(getContext(), StatisticalActivity.class));
        } else if (view.getId() == R.id.cvQLUser) {
            startActivity(new Intent(getContext(), ListUserActivity.class));
        } else if (view.getId() == R.id.cvQLProduct) {
            startActivity(new Intent(getContext(), ManagerProductActivity.class));
        } else if (view.getId() == R.id.cvChangePass) {
            startActivity(new Intent(getContext(), ChangePassword_Activity.class));
        } else if (view.getId() == R.id.textFixInfor) {
            showDialogFigProfile();
        }
    }
}