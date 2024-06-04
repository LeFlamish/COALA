package com.example.smobileeapp;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class SectionsPagerAdapterNum extends FragmentPagerAdapter {
    private String userIdToken;
    private int problemNum;

    public SectionsPagerAdapterNum(FragmentManager fm, int problemNum, String userIdToken) {
        super(fm);
        this.userIdToken = userIdToken;
        this.problemNum = problemNum;
    }

    @Override
    public Fragment getItem(int position) {
        // 해당 위치에 따라 다른 프래그먼트를 반환
        switch (position) {
            case 0:
                return PlaceholderNumFragment.newInstance(0, problemNum, userIdToken); // 첫 번째 탭
            case 1:
                return PlaceholderNumFragment.newInstance(1, problemNum, userIdToken); // 두 번째 탭
            case 2:
                return PlaceholderNumFragment.newInstance(2, problemNum, userIdToken); // 두 번째 탭
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
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
        }
        return null;
    }

    public String getUserIdToken() {
        return userIdToken;
    }

    public int getProblemNum() {
        return problemNum;
    }
}
