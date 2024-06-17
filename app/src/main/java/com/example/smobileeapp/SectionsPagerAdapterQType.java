package com.example.smobileeapp;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class SectionsPagerAdapterQType extends FragmentPagerAdapter {
    private String userIdToken;
    private String problemType;
    private String how;

    public SectionsPagerAdapterQType(FragmentManager fm, String problemType, String how, String userIdToken) {
        super(fm);
        this.userIdToken = userIdToken;
        this.problemType = problemType;
        this.how = how;
    }

    @Override
    public Fragment getItem(int position) {
        // 해당 위치에 따라 다른 프래그먼트를 반환
        switch (position) {
            case 0:
                return PlaceholderQTypeFragment.newInstance(0, problemType, how, userIdToken); // 첫 번째 탭
            case 1:
                return PlaceholderQTypeFragment.newInstance(1, problemType, how, userIdToken); // 두 번째 탭
            case 2:
                return PlaceholderQTypeFragment.newInstance(2, problemType, how, userIdToken); // 세 번째 탭
            case 3:
                return PlaceholderQTypeFragment.newInstance(3, problemType, how, userIdToken); // 네 번째 탭
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
            case 3:
                return "추천 순";
        }
        return null;
    }

    public String getUserIdToken() {
        return userIdToken;
    }

    public String getProblemType() {
        return problemType;
    }
}
