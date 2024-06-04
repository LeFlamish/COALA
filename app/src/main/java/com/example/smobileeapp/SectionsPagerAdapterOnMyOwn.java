package com.example.smobileeapp;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class SectionsPagerAdapterOnMyOwn extends FragmentPagerAdapter {
    private String userIdToken;
    private String onmyown;

    public SectionsPagerAdapterOnMyOwn(FragmentManager fm, String userIdToken, String onmyown) {
        super(fm);
        this.userIdToken = userIdToken;
        this.onmyown = onmyown;
    }

    @Override
    public Fragment getItem(int position) {
        // 해당 위치에 따라 다른 프래그먼트를 반환
        switch (position) {
            case 0:
                return PlaceholderOnMyOwnFragment.newInstance(0, userIdToken, onmyown); // 첫 번째 탭
            case 1:
                return PlaceholderOnMyOwnFragment.newInstance(1, userIdToken, onmyown); // 두 번째 탭
            case 2:
                return PlaceholderOnMyOwnFragment.newInstance(2, userIdToken, onmyown); // 세 번째 탭
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
            default:
                return null;
        }
    }

    public String getUserIdToken() {
        return userIdToken;
    }

    public String getOnMyOwn() {
        return onmyown;
    }
}