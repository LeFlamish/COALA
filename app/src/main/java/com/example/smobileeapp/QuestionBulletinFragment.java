package com.example.smobileeapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class QuestionBulletinFragment extends Fragment {

    private static final String ARG_USER_ID_TOKEN = "userIdToken";
    private String userIdToken;

    public QuestionBulletinFragment() {
        // Required empty public constructor
    }

    public static QuestionBulletinFragment newInstance(String userIdToken) {
        QuestionBulletinFragment fragment = new QuestionBulletinFragment();
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