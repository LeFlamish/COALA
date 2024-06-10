package com.example.smobileeapp;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ProblemInfo extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private int problemNum;
    private String userIdToken;

    private TextView tv_problemNum;
    private TextView tv_problemTitle;
    private TextView tv_problemDifficulty;
    private TextView tv_problemType;
    private TextView tv_problemMemo;
    private TextView tv_problemURL;
    private TextView tv_problemDate;
    private TextView tv_problemOnmyown;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_problem_info);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("내가 푼 문제 정보");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimary));
        }

        Intent intent = getIntent();
        userIdToken = intent.getStringExtra("userIdToken");

        tv_problemNum = findViewById(R.id.problemNum);
        tv_problemTitle = findViewById(R.id.problemTitle);
        tv_problemDifficulty = findViewById(R.id.problemDifficulty);
        tv_problemType = findViewById(R.id.problemType);
        tv_problemMemo = findViewById(R.id.problemMemo);
        tv_problemURL = findViewById(R.id.problemURL);
        tv_problemDate = findViewById(R.id.problemDate);
        tv_problemOnmyown = findViewById(R.id.problemOnmyown);

        // 파이어베이스 데이터베이스에서 문제 정보를 가져오기 위한 DatabaseReference 초기화
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Problems");

        // Intent에서 문제 번호 가져오기
        problemNum = getIntent().getIntExtra("problemNum", -1);

        // 파이어베이스에서 문제 정보를 가져와서 화면에 표시
        mDatabase.child(userIdToken).child(String.valueOf(problemNum)).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Problem problem = dataSnapshot.getValue(Problem.class);

                    // 문제의 userIdToken과 현재 사용자의 userIdToken을 비교하여 일치할 때만 정보를 표시합니다.
                    if (problem != null) {
                        // 가져온 정보를 TextView에 표시
                        tv_problemNum.setText(String.valueOf(problem.getProblemNum()));
                        tv_problemTitle.setText(problem.getProblemTitle());
                        tv_problemDifficulty.setText(problem.getDifficulty());
                        tv_problemType.setText(problem.getProblemType());
                        tv_problemMemo.setText(problem.getProblemMemo());
                        tv_problemURL.setText("https://www.acmicpc.net/problem/" + String.valueOf(problem.getProblemNum()));
                        // 타임스탬프를 날짜 형식으로 변환하여 표시
                        Date date = new Date(problem.getTimeposted());
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                        tv_problemDate.setText(dateFormat.format(date));
                        if (problem.getOnmyown().equals("yes")) {
                            tv_problemOnmyown.setText("네");
                        } else {
                            tv_problemOnmyown.setText("아니오");
                        }
                    } else {
                        // 해당 문제가 사용자에게 속하지 않는 경우 처리
                        Toast.makeText(ProblemInfo.this, "해당 문제에 접근할 수 없습니다.", Toast.LENGTH_SHORT).show();
                        finish(); // 액티비티 종료
                    }
                } else {
                    Toast.makeText(ProblemInfo.this, "해당 문제가 존재하지 않습니다.", Toast.LENGTH_SHORT).show();
                    finish(); // 문제가 존재하지 않으면 액티비티 종료
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ProblemInfo.this, "데이터를 가져오는 데 실패했습니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void openURL(View view) {
        String url = tv_problemURL.getText().toString();

        // 인텐트를 생성하여 URL을 브라우저에서 엽니다
        // Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        // startActivity(browserIntent);
        Intent it = new Intent(this, ProblemURL.class);
        it.putExtra("ProblemURL", url);
        startActivity(it);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_problem_info, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();


        if (id == R.id.action_settings6) {
            deleteProblem();
            return true;
        } else if (id == R.id.action_settings8) {
            Intent it = new Intent(this, EditProblemInfo.class);
            it.putExtra("userIdToken", userIdToken);
            it.putExtra("problemNum", problemNum);
            startActivity(it);
            finish();
            return true;
        } if (item.getItemId() == android.R.id.home) {
            finish(); // 뒤로가기 버튼 클릭 시 액티비티 종료
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        mDatabase.child(userIdToken).child(String.valueOf(problemNum)).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Problem problem = dataSnapshot.getValue(Problem.class);

                    // 문제의 userIdToken과 현재 사용자의 userIdToken을 비교하여 일치할 때만 정보를 표시합니다.
                    if (problem != null) {
                        // 가져온 정보를 TextView에 표시
                        tv_problemNum.setText(String.valueOf(problem.getProblemNum()));
                        tv_problemTitle.setText(problem.getProblemTitle());
                        tv_problemDifficulty.setText(problem.getDifficulty());
                        tv_problemType.setText(problem.getProblemType());
                        tv_problemMemo.setText(problem.getProblemMemo());
                        tv_problemURL.setText("https://www.acmicpc.net/problem/" + problem.getProblemNum());

                        // 타임스탬프를 날짜 형식으로 변환하여 표시
                        Date date = new Date(problem.getTimeposted());
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                        tv_problemDate.setText(dateFormat.format(date));
                    } else {
                        // 해당 문제가 사용자에게 속하지 않는 경우 처리
                        Toast.makeText(ProblemInfo.this, "해당 문제에 접근할 수 없습니다.", Toast.LENGTH_SHORT).show();
                        finish(); // 액티비티 종료
                    }
                } else {
                    Toast.makeText(ProblemInfo.this, "해당 문제가 존재하지 않습니다.", Toast.LENGTH_SHORT).show();
                    finish(); // 문제가 존재하지 않으면 액티비티 종료
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ProblemInfo.this, "데이터를 가져오는 데 실패했습니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteProblem() {
        // 파이어베이스에서 문제 삭제
        mDatabase.child(userIdToken).child(String.valueOf(problemNum)).removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(ProblemInfo.this, "문제가 삭제되었습니다.", Toast.LENGTH_SHORT).show();
                finish(); // 현재 액티비티 종료
            } else {
                Toast.makeText(ProblemInfo.this, "파이어베이스에서 문제 삭제에 실패했습니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}