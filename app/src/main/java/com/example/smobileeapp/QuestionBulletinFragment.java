package com.example.smobileeapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;


import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class QuestionBulletinFragment extends Fragment {

    private FirebaseAuth mFirebaseAuth; // 파이어베이스 인증
    private FirebaseUser currentUser;
    private DatabaseReference mDatabase;
    private String userIdToken;
    private FloatingActionButton fab, fabProblemReg, fabProblemSearch;
    private Animation fabOpen, fabClose, fabUpdown;
    private boolean isFabOpen = false;


    public static QuestionBulletinFragment newInstance(String userIdToken) {
        QuestionBulletinFragment fragment = new QuestionBulletinFragment();
        Bundle args = new Bundle();
        args.putString("userIdToken", userIdToken);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            userIdToken = getArguments().getString("userIdToken");
        }

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        userIdToken = currentUser.getUid();
    }
    private void toggleFabMenu() {
        if (isFabOpen) {
            closeFabMenu();
        } else {
            openFabMenu();
        }
    }

    private void openFabMenu() {
        isFabOpen = true;
        fab.startAnimation(fabUpdown);
        fabProblemReg.setVisibility(View.VISIBLE);
        fabProblemSearch.setVisibility(View.VISIBLE);
        fabProblemReg.startAnimation(fabOpen);
        fabProblemSearch.startAnimation(fabOpen);
        fabProblemReg.setClickable(true);
        fabProblemSearch.setClickable(true);
    }

    private void closeFabMenu() {
        isFabOpen = false;
        fabProblemReg.setVisibility(View.INVISIBLE);
        fabProblemSearch.setVisibility(View.INVISIBLE);
        fabProblemReg.startAnimation(fabClose);
        fabProblemSearch.startAnimation(fabClose);
        fabProblemReg.setClickable(false);
        fabProblemSearch.setClickable(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_question_bulletin, container, false);
        // Get reference to the TabLayout, ViewPager
        TabLayout tabLayout = view.findViewById(R.id.tab_layout);
        ViewPager viewPager = view.findViewById(R.id.view_pager);


        // onViewCreated() 메서드 호출
        super.onViewCreated(view, savedInstanceState);
        fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fabProblemReg = view.findViewById(R.id.fab_problem_reg);
        fabProblemSearch = view.findViewById(R.id.fab_problem_search);
        fabOpen = AnimationUtils.loadAnimation(getContext().getApplicationContext(), R.anim.fab_open);
        fabClose = AnimationUtils.loadAnimation(getContext().getApplicationContext(), R.anim.fab_close);
        fabUpdown = AnimationUtils.loadAnimation(getContext().getApplicationContext(), R.anim.fabupdown);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleFabMenu();
            }
        });
        fabProblemReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(),"질문 등록", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(getContext(), QuestionReg.class);

                // Add the userIdToken to the intent
                intent.putExtra("userIdToken", userIdToken);

                // Start the ProblemReg activity
                startActivity(intent);

            }
        });

        fabProblemSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "질문 검색", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getContext(), QuestionSearch.class);

                // Add the userIdToken to the intent
                intent.putExtra("userIdToken", userIdToken);

                // Start the ProblemReg activity
                startActivity(intent);
            }
        });

        // Create an instance of FragmentPagerAdapter
        QuestionBulletinPagerAdapter pagerAdapter = new QuestionBulletinPagerAdapter(getChildFragmentManager());

        // Add fragments to the adapter with new tab titles
        pagerAdapter.addFragment(QuestionBulletinPlaceholderFragment.newInstance(0, userIdToken), "최신 순");
        pagerAdapter.addFragment(QuestionBulletinPlaceholderFragment.newInstance(1, userIdToken), "문제 번호 순");
        pagerAdapter.addFragment(QuestionBulletinPlaceholderFragment.newInstance(2, userIdToken), "난이도 순");

        // Set the adapter to the ViewPager
        viewPager.setAdapter(pagerAdapter);

        // Link the TabLayout with the ViewPager
        tabLayout.setupWithViewPager(viewPager);

        return view;

    }

    private static class QuestionBulletinPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> fragmentList = new ArrayList<>();
        private final List<String> fragmentTitleList = new ArrayList<>();

        public QuestionBulletinPagerAdapter(FragmentManager fm) {
            super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            fragmentList.add(fragment);
            fragmentTitleList.add(title);
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return fragmentTitleList.get(position);
        }
    }
}
