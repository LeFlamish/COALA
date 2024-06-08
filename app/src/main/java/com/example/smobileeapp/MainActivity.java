package com.example.smobileeapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
    private String userIdToken;
    private Toolbar toolbar;
    private BottomNavigationView bottomNavigationView;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = getIntent();
        userIdToken = intent.getStringExtra("userIdToken");

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mAuth = FirebaseAuth.getInstance();

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            if (item.getItemId() == R.id.navigation_problem_list) {
                selectedFragment = ProblemListFragment.newInstance(userIdToken);
                setToolbarTitle("내가 푼 문제");
            } else if (item.getItemId() == R.id.navigation_question_bulletin) {
                selectedFragment = new QuestionBulletinFragment();
                setToolbarTitle("질문 게시판");
            } else if (item.getItemId() == R.id.navigation_recommend) {
                selectedFragment = new RecommendFragment();
                setToolbarTitle("추천 문제");
            } else if (item.getItemId() == R.id.navigation_my_info) {
                selectedFragment = new MyInfoFragment();
                setToolbarTitle("내 정보");
            }

            if (selectedFragment != null) {
                switchFragment(selectedFragment);
            }
            return true;
        });

        // 초기로 ProblemListFragment를 띄움
        switchFragment(ProblemListFragment.newInstance(userIdToken));
        setToolbarTitle("내가 푼 문제");
    }

    private void switchFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }

    private void setToolbarTitle(String title) {
        getSupportActionBar().setTitle(title);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();


        if (id == R.id.action_settings12) {
            Intent intent = new Intent(this, QuestionSearch.class);
            intent.putExtra("userIdToken", userIdToken);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_settings11) {
            Intent intent = new Intent(this, QuestionReg.class);
            intent.putExtra("userIdToken", userIdToken);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_settings10) {
            Intent intent = new Intent(this, ProblemSearch.class);
            intent.putExtra("userIdToken", userIdToken);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_settings5) {
            Intent intent = new Intent(this, ProblemReg.class);
            intent.putExtra("userIdToken", userIdToken);
            startActivity(intent);
            return true;
        } else if (id == R.id.logout) {
            Intent logoutIntent = new Intent(this, LogoutActivity.class);
            startActivity(logoutIntent);
        }

        return super.onOptionsItemSelected(item);
    }

    public String getUserIdToken() { return this.userIdToken; }
}
