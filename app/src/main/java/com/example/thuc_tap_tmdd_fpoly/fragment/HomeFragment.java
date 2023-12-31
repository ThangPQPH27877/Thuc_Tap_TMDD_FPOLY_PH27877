package com.example.thuc_tap_tmdd_fpoly.fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;


import com.example.thuc_tap_tmdd_fpoly.R;
import com.example.thuc_tap_tmdd_fpoly.activity.CartActivity;
import com.example.thuc_tap_tmdd_fpoly.adapter.MessageAdapter;
import com.example.thuc_tap_tmdd_fpoly.adapter.ProductAdapter;
import com.example.thuc_tap_tmdd_fpoly.adapter.ProductHomeAdapter;
import com.example.thuc_tap_tmdd_fpoly.adapter.SlideImageAdapter;
import com.example.thuc_tap_tmdd_fpoly.adapter.SliderImageAdapter;

import com.example.thuc_tap_tmdd_fpoly.model.ChatBot;
import com.example.thuc_tap_tmdd_fpoly.model.Product;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import me.relex.circleindicator.CircleIndicator;

public class HomeFragment extends Fragment {
    private Timer timer;
    private ViewPager slideImage;
    private CircleIndicator circleIndicator;
    private TextView textViewName;
    private ImageView imgCart;
    private FirebaseUser firebaseUser;
    private DatabaseReference mReference;
    RecyclerView rvManager;

    ProductAdapter productAdapter;

    private ChatBot chatBot;
    private MessageAdapter messageAdapter;
    private Spinner spinnerQuestionList;
    private RecyclerView recyclerView;
    List<Product> productList;
    DatabaseReference databaseReference;

    ProductHomeAdapter productHomeAdapter;
    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        mReference = FirebaseDatabase.getInstance().getReference();
        productList = new ArrayList<>();


        loadDataFromFirebase();
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Handler handler = new Handler();
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        textViewName = view.findViewById(R.id.txtName);
        imgCart = view.findViewById(R.id.imageView4);
        imgCart = view.findViewById(R.id.imageView3);
        slideImage = view.findViewById(R.id.silde_image);
        circleIndicator = view.findViewById(R.id.circle_indicator);
        RecyclerView recyclerView = view.findViewById(R.id.view1);
        RecyclerView recyclerView2 = view.findViewById(R.id.view2);
        FloatingActionButton floatMess = view.findViewById(R.id.floatMess);
        floatMess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialogChatbot();
            }
        });
        imgCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), CartActivity.class);
                startActivity(intent);
            }
        });

        productHomeAdapter = new ProductHomeAdapter(getContext(), productList);
        recyclerView.setAdapter(productHomeAdapter);
        recyclerView2.setAdapter(productHomeAdapter);
        List<Integer> listImageSlide =new ArrayList<>();
        listImageSlide.add(R.drawable.img);
        listImageSlide.add(R.drawable.img1);
        listImageSlide.add(R.drawable.img2);
        listImageSlide.add(R.drawable.img3);
        listImageSlide.add(R.drawable.img4);
        SlideImageAdapter slideImageAdapter = new SlideImageAdapter(getContext(),listImageSlide);
        slideImage.setAdapter(slideImageAdapter);
        circleIndicator.setViewPager(slideImage);
        slideImageAdapter.registerDataSetObserver(circleIndicator.getDataSetObserver());
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (isAdded() && getActivity() != null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            int currentItem = slideImage.getCurrentItem();
                            int totalItems = slideImage.getAdapter().getCount();
                            int nextItem = (currentItem + 1) % totalItems;
                            slideImage.setCurrentItem(nextItem);
                        }
                    });
                }
            }
        },2000,2000);
        LinearLayoutManager layoutManager2 = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        ViewPager2 viewPager2 = view.findViewById(R.id.viewPaperSlider);

        viewPager2.setAdapter(new SliderImageAdapter(getContext()));
        viewPager2.setClipToPadding(false);
        viewPager2.setClipChildren(false);
        viewPager2.setOffscreenPageLimit(5);
        viewPager2.getChildAt(0).setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);
        CompositePageTransformer compositePageTransformer = new CompositePageTransformer();
        compositePageTransformer.addTransformer(new MarginPageTransformer(40));
        compositePageTransformer.addTransformer(new ViewPager2.PageTransformer() {
            @Override
            public void transformPage(@NonNull View page, float position) {
                float r = 1 - Math.abs(position);
                page.setScaleY(0.85f + r * 0.15f);
            }
        });
        viewPager2.setPageTransformer(compositePageTransformer);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView2.setLayoutManager(layoutManager2);


        Runnable sliderRunnable = new Runnable() {
            @Override
            public void run() {
                viewPager2.setCurrentItem(viewPager2.getCurrentItem() + 1);
            }
        };

        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                handler.removeCallbacks(sliderRunnable);
                handler.postDelayed(sliderRunnable, 3000);
            }
        });


        return view;
    }
    private void showDialogChatbot() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());

        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_chatbot, null);

        builder.setView(dialogView);

        AlertDialog dialog = builder.create();
        dialog.show();
        recyclerView = dialogView.findViewById(R.id.recyclerView);
        messageAdapter = new MessageAdapter();
        recyclerView.setAdapter(messageAdapter);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(requireContext());
        recyclerView.setLayoutManager(layoutManager);

        chatBot = new ChatBot();

        spinnerQuestionList = dialogView.findViewById(R.id.spinnerQuestionList);
        List<String> questionsList = chatBot.getQuestions();
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, questionsList);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerQuestionList.setAdapter(spinnerAdapter);

        Button btnSend = dialogView.findViewById(R.id.btnSend);
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String selectedQuestion = spinnerQuestionList.getSelectedItem().toString();

                messageAdapter.addUserMessage(selectedQuestion);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        String botResponse = chatBot.getResponse(selectedQuestion);
                        messageAdapter.addBotMessage(botResponse);
                    }
                }, 2000);

                spinnerQuestionList.setSelection(0);
            }
        });
    }
    private void loadDataFromFirebase() {
        databaseReference = FirebaseDatabase.getInstance().getReference("products");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                productList.clear();

                String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Product product = snapshot.getValue(Product.class);
                    if (product.getQuantity() > 0) {
                        if (product.getSellerId().equals(currentUserId)) {
                            continue;
                        }
                        productList.add(product);
                    }
                }

                productHomeAdapter.setData(productList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

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
                textViewName.setText(name);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("Loi", "onCancelled: " + error.getMessage());
            }
        });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setInfoProfile();
    }
}