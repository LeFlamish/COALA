package com.example.smobileeapp;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class SectionsPagerAdapterTitle extends FragmentPagerAdapter {
    private final String problemTitle;
    private final String userIdToken;

    public SectionsPagerAdapterTitle(@NonNull FragmentManager fm, String problemTitle, String userIdToken) {
        super(fm);
        this.problemTitle = problemTitle;
        this.userIdToken = userIdToken;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        // 해당 위치에 따라 다른 프래그먼트를 반환
        switch (position) {
            case 0:
                return PlaceholderTitleFragment.newInstance(0, problemTitle, userIdToken); // 첫 번째 탭
            case 1:
                return PlaceholderTitleFragment.newInstance(1, problemTitle, userIdToken); // 두 번째 탭
            case 2:
                return PlaceholderTitleFragment.newInstance(2, problemTitle, userIdToken); // 세 번째 탭
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        // Show 3 total pages.
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        // 각 탭의 제목을 반환
        switch (position) {
            case 0:
                return "등록 시간 순";
            case 1:
                return "문제 번호 순";
            case 2:
                return "문제 난이도 순";
            default:
                return null;
        }
    }
}
