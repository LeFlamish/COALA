package com.example.smobileeapp;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

public class ProblemListByNum extends AppCompatActivity {

    private String userIdToken;
    private int problemNum;
    private SectionsPagerAdapterNum mSectionsPagerAdapter;
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_problem_list_by_num);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("번호로 문제 검색");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true); // 뒤로가기 버튼 표시
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimary));
        }

        Intent intent = getIntent();
        userIdToken = intent.getStringExtra("userIdToken");
        problemNum = intent.getIntExtra("problemNum", -1);

        getSupportActionBar().setTitle(problemNum + " 문제 번호 모아보기");

        mSectionsPagerAdapter = new SectionsPagerAdapterNum(getSupportFragmentManager(), problemNum, userIdToken);
        mViewPager = findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
    }

    public String getUserIdToken() {
        return userIdToken;
    }
    public int getProblemNum() {
        return problemNum;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish(); // 뒤로가기 버튼 클릭 시 액티비티 종료
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}