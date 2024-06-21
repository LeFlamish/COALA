package com.example.smobileeapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

public class ProblemListFragment extends Fragment {
    private View view;
    private String userIdToken;
    private FloatingActionButton fab, fabProblemReg, fabProblemSearch;
    private Animation fabOpen, fabClose, fabUpdown;
    private boolean isFabOpen = false;
    private static final String ARG_USER_ID_TOKEN = "userIdToken";


    public ProblemListFragment() {
        // Required empty public constructor
    }

    public static ProblemListFragment newInstance(String userIdToken) {
        ProblemListFragment fragment = new ProblemListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_USER_ID_TOKEN, userIdToken);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            userIdToken = getArguments().getString(ARG_USER_ID_TOKEN);
        }
    }

    private void toggleFabMenu() {
        if (isFabOpen) {
            closeFabMenu();
            fab.setImageResource(R.drawable.floating);
        } else {
            openFabMenu();
            fab.setImageResource(R.drawable.down_floating);
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_problem_list, container, false);


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
                //Toast.makeText(getContext(),"문제 등록", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(getContext(), ProblemReg.class);

                // Add the userIdToken to the intent
                intent.putExtra("userIdToken", userIdToken);

                // Start the ProblemReg activity
                startActivity(intent);

            }
        });

        fabProblemSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(getContext(), "문제 검색", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getContext(), ProblemSearch.class);

                // Add the userIdToken to the intent
                intent.putExtra("userIdToken", userIdToken);

                // Start the ProblemReg activity
                startActivity(intent);
            }
        });

        // Get reference to the TabLayout, ViewPager
        TabLayout tabLayout = view.findViewById(R.id.tab_layout);
        ViewPager viewPager = view.findViewById(R.id.view_pager);

        // Create an instance of FragmentPagerAdapter
        ProblemListPagerAdapter problemListPagerAdapter = new ProblemListPagerAdapter(getChildFragmentManager());

        // Add fragments to the adapter with new tab titles
        problemListPagerAdapter.addFragment(ProblemListPlaceholderFragment.newInstance(0, userIdToken), "최신 순");
        problemListPagerAdapter.addFragment(ProblemListPlaceholderFragment.newInstance(1, userIdToken), "문제 번호 순");
        problemListPagerAdapter.addFragment(ProblemListPlaceholderFragment.newInstance(2, userIdToken), "난이도 순");

        // Set the adapter to the ViewPager
        viewPager.setAdapter(problemListPagerAdapter);

        // Link the TabLayout with the ViewPager
        tabLayout.setupWithViewPager(viewPager);

        return view;
    }
}
