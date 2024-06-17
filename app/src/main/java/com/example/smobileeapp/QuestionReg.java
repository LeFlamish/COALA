package com.example.smobileeapp;

import static android.content.ContentValues.TAG;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class QuestionReg extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private String userIdToken;
    private EditText etcInput; // etcInput을 전역으로 선언
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_reg);
        // 툴바 설정
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("질문 등록");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true); // 뒤로가기 버튼 표시
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimary));
        }

        Intent intent = getIntent();
        mAuth = FirebaseAuth.getInstance();
        userIdToken = mAuth.getCurrentUser().getUid();

        Log.d(TAG, "Current Question: " + userIdToken);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        Spinner difficultySpinner = findViewById(R.id.difficultySpinner);
        List<String> difficultyLevels = Arrays.asList(getResources().getStringArray(R.array.difficulty_array));
        CustomSpinnerAdapter adapter = new CustomSpinnerAdapter(this, R.layout.custom_spinner_item, difficultyLevels);
        difficultySpinner.setAdapter(adapter);

        // etcInput 초기화
        etcInput = findViewById(R.id.etc_input);

        // etcCheckBox의 상태에 따라 초기 가시성 설정
        CheckBox etcCheckBox = findViewById(R.id.etc);
        etcInput.setVisibility(etcCheckBox.isChecked() ? View.VISIBLE : View.GONE);

        // etcCheckBox의 체크 상태 변경 리스너 설정
        etcCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    etcInput.setVisibility(View.VISIBLE); // etcCheckBox가 선택된 경우, etcInput 보이기
                } else {
                    etcInput.setVisibility(View.GONE); // etcCheckBox가 선택되지 않은 경우, etcInput 숨기기
                }
            }
        });

        RadioButton noButton = findViewById(R.id.no);
        noButton.setChecked(true); // "아니오" 버튼을 기본값으로 설정
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish(); // 뒤로가기 버튼 클릭 시 액티비티 종료
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void registerQuestion(View v) {
        EditText etProblemNum = findViewById(R.id.problemNum);
        EditText etQuestionTitle = findViewById(R.id.questionTitle);
        EditText etQuestionText = findViewById(R.id.questionText);
        EditText etProblemTitle = findViewById(R.id.problemTitle);
        Spinner difficultySpinner = findViewById(R.id.difficultySpinner);

        String problemNumStr = etProblemNum.getText().toString().trim();
        String questionTitle = etQuestionTitle.getText().toString().trim();
        String questionText = etQuestionText.getText().toString().trim();
        String problemTitle = etProblemTitle.getText().toString().trim();
        String problemTier = difficultySpinner.getSelectedItem().toString();
        long currentTime = new Date().getTime();

        if (TextUtils.isEmpty(problemNumStr)) {
            Toast.makeText(this, "문제 번호를 입력해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        int problemNum = Integer.parseInt(problemNumStr);

        if (TextUtils.isEmpty(questionTitle)) {
            Toast.makeText(this, "질문 제목을 입력해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(questionText)) {
            Toast.makeText(this, "질문 내용을 입력해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(problemTitle)) {
            Toast.makeText(this, "문제 제목을 입력해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        String questionId = mDatabase.child("QuestionBulletin").child(String.valueOf(problemNum)).push().getKey();

        // Collect the problem types from the checkboxes
        ArrayList<String> problemTypes = new ArrayList<>();
        addProblemTypeIfChecked(problemTypes, R.id.bruteforce, "브루트포스");
        addProblemTypeIfChecked(problemTypes, R.id.BFS, "BFS");
        addProblemTypeIfChecked(problemTypes, R.id.DFS, "DFS");
        addProblemTypeIfChecked(problemTypes, R.id.DP, "DP");
        addProblemTypeIfChecked(problemTypes, R.id.backtracking, "백트래킹");
        addProblemTypeIfChecked(problemTypes, R.id.queue, "큐");
        addProblemTypeIfChecked(problemTypes, R.id.stack, "스택");
        addProblemTypeIfChecked(problemTypes, R.id.math, "수학");
        addProblemTypeIfChecked(problemTypes, R.id.realization, "구현");
        addProblemTypeIfChecked(problemTypes, R.id.datastructure, "자료 구조");
        addProblemTypeIfChecked(problemTypes, R.id.greedy, "그리디 알고리즘");
        addProblemTypeIfChecked(problemTypes, R.id.sort, "정렬");
        addProblemTypeIfChecked(problemTypes, R.id.string, "문자열");
        addProblemTypeIfChecked(problemTypes, R.id.graphtheory, "그래프 이론");
        addProblemTypeIfChecked(problemTypes, R.id.graphsearch, "그래프 탐색");
        addProblemTypeIfChecked(problemTypes, R.id.tree, "트리");
        addProblemTypeIfChecked(problemTypes, R.id.simulation, "시뮬레이션");

        CheckBox etcCheckBox = findViewById(R.id.etc);
        if (etcCheckBox.isChecked()) {
            String etcTypes = etcInput.getText().toString().trim();
            if (!TextUtils.isEmpty(etcTypes)) {
                problemTypes.add(etcTypes);
            }
        }

        String problemType = TextUtils.join(", ", problemTypes);

        Question question = new Question(questionId, questionTitle, questionText, problemTitle, problemTier, problemType, userIdToken, problemNum, currentTime);
        mDatabase.child("QuestionBulletin").child(String.valueOf(problemNum)).child(questionId).setValue(question);

        Toast.makeText(this, "질문을 등록하는 데 성공했습니다.", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(QuestionReg.this, QuestionDetailActivity.class);
        intent.putExtra("questionId", questionId);
        intent.putExtra("problemNum", problemNum);
        startActivity(intent);
        finish();
    }

    private void addProblemTypeIfChecked(ArrayList<String> problemTypes, int checkBoxId, String type) {
        CheckBox checkBox = findViewById(checkBoxId);
        if (checkBox.isChecked()) {
            problemTypes.add(type);
        }
    }

    public void autoTitle(View view) {
        EditText etProblemNum = findViewById(R.id.problemNum);
        String strProblemNum = etProblemNum.getText().toString();

        if (TextUtils.isEmpty(strProblemNum)) {
            showToast("문제 번호를 입력해주세요.");
            return;
        }

        int problemNum;
        try {
            problemNum = Integer.parseInt(strProblemNum);
        } catch (NumberFormatException e) {
            showToast("숫자로 유효한 문제 번호를 입력해주세요.");
            return;
        }

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
                String[] parts = result.split(": ");
                if (parts.length > 1) {
                    result = parts[1];
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