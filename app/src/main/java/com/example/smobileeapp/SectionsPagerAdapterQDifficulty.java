package com.example.smobileeapp;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class SectionsPagerAdapterQDifficulty extends FragmentPagerAdapter {

    private String selectedDifficulty;
    private String userIdToken;

    public SectionsPagerAdapterQDifficulty(@NonNull FragmentManager fm, String selectedDifficulty, String userIdToken) {
        super(fm);
        this.selectedDifficulty = selectedDifficulty;
        this.userIdToken = userIdToken;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        // 해당 위치에 따라 다른 프래그먼트를 반환
        switch (position) {
            case 0:
                return PlaceholderQDifficultyFragment.newInstance(0, selectedDifficulty); // 첫 번째 탭
            case 1:
                return PlaceholderQDifficultyFragment.newInstance(1, selectedDifficulty); // 두 번째 탭
            case 2:
                return PlaceholderQDifficultyFragment.newInstance(2, selectedDifficulty); // 세 번째 탭
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
                return "추천 순";
        }
        return null;
    }
}
