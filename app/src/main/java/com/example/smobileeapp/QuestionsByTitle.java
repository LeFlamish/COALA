package com.example.smobileeapp;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

public class QuestionsByTitle extends AppCompatActivity {

    private String userIdToken;
    private String problemTitle;
    private SectionsPagerAdapterQTitle mSectionsPagerQAdapter;
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questions_by_title);

        // 툴바 설정
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("문제 제목으로 질문 검색");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true); // 뒤로가기 버튼 표시
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimary));
        }

        Intent intent = getIntent();
        userIdToken = intent.getStringExtra("userIdToken");
        problemTitle = intent.getStringExtra("problemTitle");
        Log.d("QuestionsByDifficulty", "userIdToken : " + userIdToken);
        Log.d("QuestionsByDifficulty", "problemTitle : " + problemTitle);
        getSupportActionBar().setTitle(problemTitle + " 질문 검색");

        mSectionsPagerQAdapter = new SectionsPagerAdapterQTitle(getSupportFragmentManager(), problemTitle, userIdToken);
        mViewPager = findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerQAdapter);

        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
    }


    public String getUserIdToken() {
        return userIdToken;
    }
    public String getProblemTitle() {
        return problemTitle;
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