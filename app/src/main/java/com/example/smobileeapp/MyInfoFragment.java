package com.example.smobileeapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;

public class MyInfoFragment extends Fragment {

    private static final String ARG_USER_ID_TOKEN = "userIdToken";
    private String userIdToken;

    public MyInfoFragment() {
        // Required empty public constructor
    }

    public static MyInfoFragment newInstance(String userIdToken) {
        MyInfoFragment fragment = new MyInfoFragment();
        Bundle args = new Bundle();
        args.putString(ARG_USER_ID_TOKEN, userIdToken);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            userIdToken = getArguments().getString(ARG_USER_ID_TOKEN);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_info, container, false);

        TabLayout tabLayout = view.findViewById(R.id.tab_layout);
        ViewPager viewPager = view.findViewById(R.id.view_pager);

        MyInfoPagerAdapter adapter = new MyInfoPagerAdapter(getChildFragmentManager());
        adapter.addFragment(new CalendarFragment(), "캘린더");
        adapter.addFragment(new PieChartFragment(), "난이도 별 원형 그래프");
        adapter.addFragment(new RadarChartFragment(), "유형 별 다각 그래프");

        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);

        return view;
    }
}
