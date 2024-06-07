package com.example.smobileeapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

public class QuestionsByType extends AppCompatActivity {

    private String userIdToken;
    private String problemType;
    private String how;
    private SectionsPagerAdapterQType mSectionsPagerQAdapter;
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questions_by_type);

        // 툴바 설정
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("문제 유형으로 질문 검색");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true); // 뒤로가기 버튼 표시
        }

        Intent intent = getIntent();
        userIdToken = intent.getStringExtra("userIdToken");
        problemType = intent.getStringExtra("problemType");
        how = intent.getStringExtra("how");
        Log.d("QuestionsByDifficulty", "userIdToken : " + userIdToken);
        Log.d("QuestionsByDifficulty", "problemType : " + problemType);
        getSupportActionBar().setTitle(problemType + " 질문 검색");

        mSectionsPagerQAdapter = new SectionsPagerAdapterQType(getSupportFragmentManager(), problemType, how, userIdToken);
        mViewPager = findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerQAdapter);

        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
    }


    public String getUserIdToken() {
        return userIdToken;
    }
    public String getProblemType() {
        return problemType;
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