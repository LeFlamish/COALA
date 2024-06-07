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

public class QuestionsByOnMyOwn extends AppCompatActivity {

    private String userIdToken;
    private String searchmyquestion;
    private SectionsPagerAdapterQOnMyOwn mSectionsPagerQAdapter;
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questions_by_difficulty);

        // 툴바 설정
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("스스로 해결 유무로 질문 검색");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true); // 뒤로가기 버튼 표시
        }

        Intent intent = getIntent();
        userIdToken = intent.getStringExtra("userIdToken");
        searchmyquestion = intent.getStringExtra("searchmyquestion");
        Log.d("QuestionsByDifficulty", "userIdToken : " + userIdToken);
        Log.d("QuestionsByDifficulty", "searchmyquestion : " + searchmyquestion);
        getSupportActionBar().setTitle("내가 한 질문 검색");

        mSectionsPagerQAdapter = new SectionsPagerAdapterQOnMyOwn(getSupportFragmentManager(), userIdToken);
        mViewPager = findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerQAdapter);

        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
    }


    public String getUserIdToken() {
        return userIdToken;
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