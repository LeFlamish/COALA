package com.example.smobileeapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

public class ProblemListFragment extends Fragment {

    private static final String ARG_USER_ID_TOKEN = "userIdToken";
    private String userIdToken;

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_problem_list, container, false);

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
