package com.example.smobileeapp;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
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

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

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
    private TextView tvProblemText; // 크롤링한 문제 내용을 표시할 TextView
    private TextView tvProblemInput;
    private TextView tvProblemOutput;

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
        tvProblemText = findViewById(R.id.problemText); // 새로운 TextView
        tvProblemInput = findViewById(R.id.problemInput);
        tvProblemOutput = findViewById(R.id.problemOutput);

        // Firebase 데이터베이스에서 해당 문제 정보 가져오기
        DatabaseReference databaseRef;
        if (type.equals("bronze") || type.equals("silver") || type.equals("gold") || type.equals("platinum")) {
            databaseRef = FirebaseDatabase.getInstance()
                    .getReference().child("Rproblem").child("difficulty").child(type).child(String.valueOf(problemNum));
        } else if (type.equals("samsung") || type.equals("kakao") || type.equals("naver") || type.equals("lg")) {
            databaseRef = FirebaseDatabase.getInstance()
                    .getReference().child("Rproblem").child("company").child(type).child(String.valueOf(problemNum));
        } else if (type.equals("BFS") || type.equals("DFS") || type.equals("DP") || type.equals("Greedy") || type.equals("Back") || type.equals("Data")) {
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
                        String problemURL = "https://www.acmicpc.net/problem/" + problemNum;
                        tvProblemURL.setText(problemURL);

                        problemTitle=problem.getProblemTitle();
                        problemType=problem.getProblemType();
                        difficulty=problem.getDifficulty();

                        // 문제 URL을 통해 크롤링한 내용 설정
                        new FetchProblemContentTask().execute(problemURL);
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

    private class FetchProblemContentTask extends AsyncTask<String, Void, String[]> {

        @Override
        protected String[] doInBackground(String... urls) {
            String url = urls[0];
            Log.d("FetchProblemContentTask", "Fetching content from URL: " + url); // URL 로그 추가
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(url)
                    .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.3")
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    Log.e("FetchProblemContentTask", "Failed to fetch content, response code: " + response.code()); // 오류 코드 로그 추가
                    return null;
                }

                Document document = Jsoup.parse(response.body().string());
                Element descriptionElement = document.getElementById("problem_description");
                Element inputElement = document.getElementById("problem_input");
                Element outputElement = document.getElementById("problem_output");

                String descriptionText = extractTextFromElement(descriptionElement);
                String inputText = extractTextFromElement(inputElement);
                String outputText = extractTextFromElement(outputElement);

                return new String[]{descriptionText, inputText, outputText};

            } catch (IOException e) {
                Log.e("FetchProblemContentTask", "Error fetching problem content", e); // 예외 발생 시 로그 추가
                return null;
            }
        }

        private String extractTextFromElement(Element element) {
            if (element != null) {
                StringBuilder contentBuilder = new StringBuilder();
                for (Element paragraph : element.select("p")) {
                    contentBuilder.append(paragraph.text()).append("\n\n"); // <p> 태그 사이의 텍스트를 가져옴
                }
                return contentBuilder.toString();
            } else {
                Log.e("FetchProblemContentTask", "Element not found"); // 요소를 찾지 못한 경우 로그 추가
                return null;
            }
        }

        @Override
        protected void onPostExecute(String[] result) {
            if (result != null) {
                tvProblemText.setText(result[0]); // 가져온 설명 텍스트를 설정
                tvProblemInput.setText(result[1]); // 가져온 입력 텍스트를 설정
                tvProblemOutput.setText(result[2]); // 가져온 출력 텍스트를 설정
            } else {
                tvProblemText.setText("문제 내용을 가져오지 못했습니다.");
                tvProblemInput.setText("입력 조건을 가져오지 못했습니다.");
                tvProblemOutput.setText("출력 조건을 가져오지 못했습니다.");
            }
        }
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