package com.example.smobileeapp;

import android.os.AsyncTask;
import android.os.Bundle;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ProblemReg extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private String userIdToken;
    // AsyncTask 실행 여부를 나타내는 변수
    private boolean scrapeTaskRunning = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_problem_reg);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("내가 푼 문제 등록");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true); // 뒤로가기 버튼 표시
        }

        Intent intent = getIntent();
        userIdToken = intent.getStringExtra("userIdToken");

        mDatabase = FirebaseDatabase.getInstance().getReference();

        // 스피너에 어댑터 설정
        Spinner difficultySpinner = findViewById(R.id.difficultySpinner);
        List<String> difficultyLevels = Arrays.asList(getResources().getStringArray(R.array.difficulty_array));
        CustomSpinnerAdapter adapter = new CustomSpinnerAdapter(this, R.layout.custom_spinner_item, difficultyLevels);
        difficultySpinner.setAdapter(adapter);
    }



    public void register(View v) {
        EditText etProblemNum = findViewById(R.id.problemNum);
        String strProblemNum = etProblemNum.getText().toString();

        if (TextUtils.isEmpty(strProblemNum)) {
            // 문제 번호가 입력되지 않은 경우
            Toast.makeText(this, "문제 번호를 입력해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        int problemNum;
        try {
            problemNum = Integer.parseInt(strProblemNum);
        } catch (NumberFormatException e) {
            // 숫자로 변환할 수 없는 경우 예외 처리
            Toast.makeText(this, "숫자로 유효한 문제 번호를 입력해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        // 문제 번호 중복 확인
        mDatabase.child("Problems").child(userIdToken).child(String.valueOf(problemNum))
                .addListenerForSingleValueEvent(new com.google.firebase.database.ValueEventListener() {
                    @Override
                    public void onDataChange(com.google.firebase.database.DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            // 문제 번호가 이미 존재하는 경우
                            Toast.makeText(ProblemReg.this, "이미 등록된 문제입니다.", Toast.LENGTH_SHORT).show();
                        } else {
                            // 문제 번호가 존재하지 않는 경우, 문제 등록 로직 실행
                            registerProblem(problemNum);
                        }
                    }

                    @Override
                    public void onCancelled(com.google.firebase.database.DatabaseError databaseError) {
                        Toast.makeText(ProblemReg.this, "Database error: " + databaseError.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void registerProblem(int problemNum) {
        EditText etProblemTitle = findViewById(R.id.problemTitle);
        String problemTitle = etProblemTitle.getText().toString();

        Spinner difficultySpinner = findViewById(R.id.difficultySpinner);
        String difficulty = difficultySpinner.getSelectedItem().toString();

        CheckBox bruteforceCheckBox = findViewById(R.id.bruteforce);
        boolean bruteforce = bruteforceCheckBox.isChecked();

        CheckBox bfsCheckBox = findViewById(R.id.BFS);
        boolean bfs = bfsCheckBox.isChecked();

        CheckBox dfsCheckBox = findViewById(R.id.DFS);
        boolean dfs = dfsCheckBox.isChecked();

        CheckBox dpCheckBox = findViewById(R.id.DP);
        boolean dp = dpCheckBox.isChecked();

        CheckBox backtrackingCheckBox = findViewById(R.id.backtracking);
        boolean backtracking = backtrackingCheckBox.isChecked();

        CheckBox queueCheckBox = findViewById(R.id.queue);
        boolean queue = queueCheckBox.isChecked();

        CheckBox stackCheckBox = findViewById(R.id.stack);
        boolean stack = stackCheckBox.isChecked();

        CheckBox mathCheckBox = findViewById(R.id.math);
        boolean math = mathCheckBox.isChecked();

        CheckBox realizationCheckBox = findViewById(R.id.realization);
        boolean realization = realizationCheckBox.isChecked();

        CheckBox datastructureCheckBox = findViewById(R.id.datastructure);
        boolean datastructure = datastructureCheckBox.isChecked();

        CheckBox greedyCheckBox = findViewById(R.id.greedy);
        boolean greedy = greedyCheckBox.isChecked();

        CheckBox sortCheckBox = findViewById(R.id.sort);
        boolean sort = sortCheckBox.isChecked();

        CheckBox stringCheckBox = findViewById(R.id.string);
        boolean string = stringCheckBox.isChecked();

        CheckBox graphtheoryCheckBox = findViewById(R.id.graphtheory);
        boolean graphtheory = graphtheoryCheckBox.isChecked();

        CheckBox graphsearchCheckBox = findViewById(R.id.graphsearch);
        boolean graphsearch = graphsearchCheckBox.isChecked();

        CheckBox treeCheckBox = findViewById(R.id.tree);
        boolean tree = treeCheckBox.isChecked();

        CheckBox simulationCheckBox = findViewById(R.id.simulation);
        boolean simulation = simulationCheckBox.isChecked();

        StringBuilder problemTypeBuilder = new StringBuilder();

        if (bruteforce) {
            problemTypeBuilder.append("브루트포스 알고리즘, ");
        }
        if (bfs) {
            problemTypeBuilder.append("BFS, ");
        }
        if (dfs) {
            problemTypeBuilder.append("DFS, ");
        }
        if (dp) {
            problemTypeBuilder.append("DP, ");
        }
        if (backtracking) {
            problemTypeBuilder.append("백트래킹, ");
        }
        if (queue) {
            problemTypeBuilder.append("큐, ");
        }
        if (stack) {
            problemTypeBuilder.append("스택, ");
        }
        if (math) {
            problemTypeBuilder.append("수학, ");
        }
        if (realization) {
            problemTypeBuilder.append("구현, ");
        }
        if (datastructure) {
            problemTypeBuilder.append("자료 구조, ");
        }
        if (greedy) {
            problemTypeBuilder.append("그리디 알고리즘, ");
        }
        if (sort) {
            problemTypeBuilder.append("정렬, ");
        }
        if (string) {
            problemTypeBuilder.append("문자열, ");
        }
        if (graphtheory) {
            problemTypeBuilder.append("그래프 이론, ");
        }
        if (graphsearch) {
            problemTypeBuilder.append("그래프 탐색, ");
        }
        if (tree) {
            problemTypeBuilder.append("트리, ");
        }
        if (simulation) {
            problemTypeBuilder.append("시뮬레이션, ");
        }

        String problemType = problemTypeBuilder.toString();
        if (problemType.endsWith(", ")) {
            problemType = problemType.substring(0, problemType.length() - 2);
        }

        EditText etProblemMemo = findViewById(R.id.problemMemo);
        String problemMemo = etProblemMemo.getText().toString();

        Long timeposted = System.currentTimeMillis(); // 현재 시간 저장

        // 스스로 푸는지 여부 저장
        RadioGroup onmyownGroup = findViewById(R.id.onmyown);
        String onmyown = "";
        if (onmyownGroup.getCheckedRadioButtonId() == R.id.yes) {
            onmyown = "yes";
        }
        if (onmyownGroup.getCheckedRadioButtonId() == R.id.no) {
            onmyown = "no";
        }

        try {
            Problem problem = new Problem(problemNum, problemTitle, difficulty, problemType, problemMemo, userIdToken, timeposted, onmyown);
            mDatabase.child("Problems").child(userIdToken).child(String.valueOf(problemNum)).setValue(problem);
            showToast("문제가 성공적으로 등록되었습니다.");
            // Intent it = new Intent(this, ProblemList.class);
            // it.putExtra("userIdToken", userIdToken);
            // startActivity(it);
            finish();
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    public void autoTitle(View view) {
        EditText etProblemNum = findViewById(R.id.problemNum);
        String strProblemNum = etProblemNum.getText().toString();

        if (TextUtils.isEmpty(strProblemNum)) {
            showToast("문제 번호를 입력해주세요.");
            return;
        }

        int problemNum = Integer.parseInt(strProblemNum);

        // AsyncTask를 사용하여 백그라운드에서 네트워크 요청 실행
        new ScrapeTask().execute(problemNum);
    }

    private class ScrapeTask extends AsyncTask<Integer, Void, String> {
        @Override
        protected String doInBackground(Integer... params) {
            int problemNum = params[0];
            return scrapeProblemTitle(problemNum);
        }

        @Override
        protected void onPostExecute(String result) {
            if (TextUtils.isEmpty(result)) {
                showToast("문제 제목을 가져오는 데 실패했습니다. 직접 입력해주세요.");
            } else {
                // 여기서 "1001번: " 부분을 제거하고 뒤의 제목만 사용합니다.
                String[] parts = result.split(": ");
                if (parts.length > 1) {
                    result = parts[1]; // "1001번: " 이후의 문자열만 사용합니다.
                }
                EditText etProblemTitle = findViewById(R.id.problemTitle);
                etProblemTitle.setText(result);
            }
        }
    }

    private String scrapeProblemTitle(int problemNum) {
        String url = "https://www.acmicpc.net/problem/" + problemNum;
        try {
            Document doc = Jsoup.connect(url).get();
            Element titleElement = doc.selectFirst("title");
            if (titleElement != null) {
                return titleElement.text();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}