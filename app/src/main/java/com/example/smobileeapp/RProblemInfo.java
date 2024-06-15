package com.example.smobileeapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class RProblemInfo extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private int problemNum;
    private String problemTitle;
    private String problemType;
    private String difficulty;

    private String type;
    private String userIdToken; // 유저 토큰 변수 추가

    private TextView tvProblemNum;
    private TextView tvProblemTitle;
    private TextView tvProblemDifficulty;
    private TextView tvProblemType;
    private TextView tvProblemURL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rproblem_info);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("추천 문제 정보");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        Intent intent = getIntent();

        type = intent.getStringExtra("type");
        problemNum = intent.getIntExtra("problemNum", 0);
        userIdToken = intent.getStringExtra("userIdToken"); // 유저 토큰 받아오기

        tvProblemNum = findViewById(R.id.problemNum);
        tvProblemTitle = findViewById(R.id.problemTitle);
        tvProblemDifficulty = findViewById(R.id.problemDifficulty);
        tvProblemType = findViewById(R.id.problemType);
        tvProblemURL = findViewById(R.id.problemURL);

        // Firebase 데이터베이스에서 해당 문제 정보 가져오기
        DatabaseReference databaseRef;
        if (type.equals("bronze") || type.equals("silver") || type.equals("gold") || type.equals("platinum")) {
            databaseRef = FirebaseDatabase.getInstance()
                    .getReference().child("Rproblem").child("difficulty").child(type).child(String.valueOf(problemNum));
        } else if (type.equals("samsung") || type.equals("kakao") || type.equals("naver") || type.equals("lg")) {
            databaseRef = FirebaseDatabase.getInstance()
                    .getReference().child("Rproblem").child("company").child(type).child(String.valueOf(problemNum));
        } else if (type.equals("BFS") || type.equals("DFS") || type.equals("DP") || type.equals("Greedy")) {
            databaseRef = FirebaseDatabase.getInstance()
                    .getReference().child("Rproblem").child("Algorithm").child(type).child(String.valueOf(problemNum));
        } else {
            databaseRef = FirebaseDatabase.getInstance()
                    .getReference().child("Rproblem").child("Custom").child(type).child(String.valueOf(problemNum));
        }

        databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    RProblem problem = dataSnapshot.getValue(RProblem.class);
                    if (problem != null) {
                        // 가져온 정보를 TextView에 설정
                        tvProblemNum.setText(String.valueOf(problem.getProblemNum()));
                        tvProblemTitle.setText(problem.getProblemTitle());
                        tvProblemDifficulty.setText(problem.getDifficulty());
                        tvProblemType.setText(problem.getProblemType());
                        tvProblemURL.setText(problem.getProblemURL());

                        problemTitle=problem.getProblemTitle();
                        problemType=problem.getProblemType();
                        difficulty=problem.getDifficulty();
                    } else {
                        // 해당 문제가 없는 경우
                        Toast.makeText(RProblemInfo.this, "해당 문제 정보를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show();
                        finish(); // 액티비티 종료
                    }
                } else {
                    // 해당 경로에 데이터가 없는 경우
                    Toast.makeText(RProblemInfo.this, "해당 문제가 존재하지 않습니다.", Toast.LENGTH_SHORT).show();
                    finish(); // 액티비티 종료
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // 데이터 가져오기 실패한 경우
                Toast.makeText(RProblemInfo.this, "데이터를 가져오는 데 실패했습니다.", Toast.LENGTH_SHORT).show();
            }
        });

        Button btnRegister = findViewById(R.id.btnRegister);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RProblemInfo.this, RProblemReg.class);
                intent.putExtra("userIdToken", userIdToken); // 유저 토큰 전달
                intent.putExtra("type", type); // 문제 정보 전달
                intent.putExtra("problemNum", problemNum);
                intent.putExtra("problemType", problemType);
                intent.putExtra("problemTitle", problemTitle);
                intent.putExtra("difficulty", difficulty);
                // 문제 정보 전달
                startActivity(intent);
            }
        });
    }

    // 문제 URL 열기
    public void openURL(View view) {
        String url = tvProblemURL.getText().toString();
        Intent intent = new Intent(this, ProblemURL.class);
        intent.putExtra("ProblemURL", url);
        startActivity(intent);
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
