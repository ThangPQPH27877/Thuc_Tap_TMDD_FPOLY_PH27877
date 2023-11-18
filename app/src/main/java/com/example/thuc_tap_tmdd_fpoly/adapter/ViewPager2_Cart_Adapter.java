package com.example.thuc_tap_tmdd_fpoly.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.thuc_tap_tmdd_fpoly.fragment.CancleFragment;
import com.example.thuc_tap_tmdd_fpoly.fragment.ConfirmFragment;
import com.example.thuc_tap_tmdd_fpoly.fragment.DeliverFragment;
import com.example.thuc_tap_tmdd_fpoly.fragment.DoneFragment;
import com.example.thuc_tap_tmdd_fpoly.fragment.WaitFragment;


public class ViewPager2_Cart_Adapter extends FragmentStateAdapter {
    public ViewPager2_Cart_Adapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        Fragment fragment = null;
        switch (position){
            case 0:
                fragment = new WaitFragment().newInstance();
                break;
            case 1:
                fragment = new ConfirmFragment().newInstance();
                break;
            case 2:
                fragment = new DeliverFragment().newInstance();
                break;
            case 3:
                fragment = new DoneFragment().newInstance();
                break;
            case 4:
                fragment = new CancleFragment().newInstance();
                break;
            default:
                fragment = new WaitFragment().newInstance();
        }
        return fragment ;
    }

    @Override
    public int getItemCount() {
        return 5;
    }
}
