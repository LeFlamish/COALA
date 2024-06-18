package com.example.smobileeapp;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class SectionsPagerAdapterQTitle extends FragmentPagerAdapter {
    private String userIdToken;
    private String problemTitle;

    public SectionsPagerAdapterQTitle(FragmentManager fm, String problemTitle, String userIdToken) {
        super(fm);
        this.userIdToken = userIdToken;
        this.problemTitle = problemTitle;
    }

    @Override
    public Fragment getItem(int position) {
        // 해당 위치에 따라 다른 프래그먼트를 반환
        switch (position) {
            case 0:
                return PlaceholderQTitleFragment.newInstance(0, problemTitle, userIdToken); // 첫 번째 탭
            case 1:
                return PlaceholderQTitleFragment.newInstance(1, problemTitle, userIdToken); // 두 번째 탭
            case 2:
                return PlaceholderQTitleFragment.newInstance(2, problemTitle, userIdToken); // 세 번째 탭
            case 3:
                return PlaceholderQTitleFragment.newInstance(3, problemTitle, userIdToken); // 네 번째 탭
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 4;
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
            case 3:
                return "추천 순";
        }
        return null;
    }

    public String getUserIdToken() {
        return userIdToken;
    }

    public String getProblemTitle() {
        return problemTitle;
    }
}
