package com.example.smobileeapp;

import android.os.AsyncTask;
import android.os.Bundle;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class EditQuestion extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private String userIdToken;
    private int problemNum;
    private String questionId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_question);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("질문 수정");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true); // 뒤로가기 버튼 표시
        }

        Intent intent = getIntent();
        userIdToken = intent.getStringExtra("userIdToken");
        problemNum = intent.getIntExtra("problemNum", -1);
        questionId = intent.getStringExtra("questionId");

        Log.d("EditQuestion", "userIdToken: " + userIdToken + ", problemNum: " + problemNum);

        Spinner difficultySpinner = findViewById(R.id.difficultySpinner);
        List<String> difficultyLevels = Arrays.asList(getResources().getStringArray(R.array.difficulty_array));
        CustomSpinnerAdapter adapter = new CustomSpinnerAdapter(this, R.layout.custom_spinner_item, difficultyLevels);
        difficultySpinner.setAdapter(adapter);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        loadProblemData();
    }

    private void loadProblemData() {
        DatabaseReference questionRef = mDatabase.child("QuestionBulletin").child(String.valueOf(problemNum)).child(questionId);
        Log.d("EditQuestion", "Database Path: " + questionRef.toString());

        questionRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Question question = dataSnapshot.getValue(Question.class);
                if (question != null) {
                    Log.d("EditQuestion", "question data loaded: " + question.toString());
                    populateUI(question);
                } else {
                    Log.d("EditQuestion", "Problem not found in database");
                    Toast.makeText(EditQuestion.this, "질문을 찾을 수 없습니다.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(EditQuestion.this, "질문 데이터를 가져오는 데 실패했습니다.", Toast.LENGTH_SHORT).show();
                Log.e("EditQuestion", "Database error: " + databaseError.getMessage());
            }
        });
    }

    private void populateUI(Question question) {
        EditText etProblemNum = findViewById(R.id.problemNum);
        etProblemNum.setText(String.valueOf(question.getProblemNum()));

        EditText etProblemTitle = findViewById(R.id.problemTitle);
        etProblemTitle.setText(question.getProblemTitle());

        Spinner difficultySpinner = findViewById(R.id.difficultySpinner);
        String[] difficulties = getResources().getStringArray(R.array.difficulty_array);
        for (int i = 0; i < difficulties.length; i++) {
            if (difficulties[i].equals(question.getProblemTier())) {
                difficultySpinner.setSelection(i);
                break;
            }
        }

        setCheckboxState(R.id.bruteforce, question.getProblemType().contains("브루트포스 알고리즘"));
        setCheckboxState(R.id.BFS, question.getProblemType().contains("BFS"));
        setCheckboxState(R.id.DFS, question.getProblemType().contains("DFS"));
        setCheckboxState(R.id.DP, question.getProblemType().contains("DP"));
        setCheckboxState(R.id.backtracking, question.getProblemType().contains("백트래킹"));
        setCheckboxState(R.id.queue, question.getProblemType().contains("큐"));
        setCheckboxState(R.id.stack, question.getProblemType().contains("스택"));
        setCheckboxState(R.id.math, question.getProblemType().contains("수학"));
        setCheckboxState(R.id.realization, question.getProblemType().contains("구현"));
        setCheckboxState(R.id.datastructure, question.getProblemType().contains("자료 구조"));
        setCheckboxState(R.id.greedy, question.getProblemType().contains("그리디 알고리즘"));
        setCheckboxState(R.id.sort, question.getProblemType().contains("정렬"));
        setCheckboxState(R.id.string, question.getProblemType().contains("문자열"));
        setCheckboxState(R.id.graphtheory, question.getProblemType().contains("그래프 이론"));
        setCheckboxState(R.id.graphsearch, question.getProblemType().contains("그래프 탐색"));
        setCheckboxState(R.id.tree, question.getProblemType().contains("트리"));
        setCheckboxState(R.id.simulation, question.getProblemType().contains("시뮬레이션"));

        EditText etQuestionTitle = findViewById(R.id.questionTitle);
        etQuestionTitle.setText(question.getQuestionTitle());

        EditText etQuestionText = findViewById(R.id.questionText);
        etQuestionText.setText(question.getQuestionText());
    }

    private void setCheckboxState(int checkBoxId, boolean isChecked) {
        CheckBox checkBox = findViewById(checkBoxId);
        checkBox.setChecked(isChecked);
    }

    public void updateQuestion(View v) {
        EditText etProblemNum = findViewById(R.id.problemNum);
        String str_problemNum = etProblemNum.getText().toString();
        int problemNum = Integer.parseInt(str_problemNum);

        EditText etProblemTitle = findViewById(R.id.problemTitle);
        String problemTitle = etProblemTitle.getText().toString();

        Spinner difficultySpinner = findViewById(R.id.difficultySpinner);
        String difficulty = difficultySpinner.getSelectedItem().toString();

        boolean bruteforce = ((CheckBox) findViewById(R.id.bruteforce)).isChecked();
        boolean bfs = ((CheckBox) findViewById(R.id.BFS)).isChecked();
        boolean dfs = ((CheckBox) findViewById(R.id.DFS)).isChecked();
        boolean dp = ((CheckBox) findViewById(R.id.DP)).isChecked();
        boolean backtracking = ((CheckBox) findViewById(R.id.backtracking)).isChecked();
        boolean queue = ((CheckBox) findViewById(R.id.queue)).isChecked();
        boolean stack = ((CheckBox) findViewById(R.id.stack)).isChecked();
        boolean math = ((CheckBox) findViewById(R.id.math)).isChecked();
        boolean realization = ((CheckBox) findViewById(R.id.realization)).isChecked();
        boolean datastructure = ((CheckBox) findViewById(R.id.datastructure)).isChecked();
        boolean greedy = ((CheckBox) findViewById(R.id.greedy)).isChecked();
        boolean sort = ((CheckBox) findViewById(R.id.sort)).isChecked();
        boolean string = ((CheckBox) findViewById(R.id.string)).isChecked();
        boolean graphtheory = ((CheckBox) findViewById(R.id.graphtheory)).isChecked();
        boolean graphsearch = ((CheckBox) findViewById(R.id.graphsearch)).isChecked();
        boolean tree = ((CheckBox) findViewById(R.id.tree)).isChecked();
        boolean simulation = ((CheckBox) findViewById(R.id.simulation)).isChecked();

        StringBuilder problemTypeBuilder = new StringBuilder();
        if (bruteforce) problemTypeBuilder.append("브루트포스 알고리즘, ");
        if (bfs) problemTypeBuilder.append("BFS, ");
        if (dfs) problemTypeBuilder.append("DFS, ");
        if (dp) problemTypeBuilder.append("DP, ");
        if (backtracking) problemTypeBuilder.append("백트래킹, ");
        if (queue) problemTypeBuilder.append("큐, ");
        if (stack) problemTypeBuilder.append("스택, ");
        if (math) problemTypeBuilder.append("수학, ");
        if (realization) problemTypeBuilder.append("구현, ");
        if (datastructure) problemTypeBuilder.append("자료 구조, ");
        if (greedy) problemTypeBuilder.append("그리디 알고리즘, ");
        if (sort) problemTypeBuilder.append("정렬, ");
        if (string) problemTypeBuilder.append("문자열, ");
        if (graphtheory) problemTypeBuilder.append("그래프 이론, ");
        if (graphsearch) problemTypeBuilder.append("그래프 탐색, ");
        if (tree) problemTypeBuilder.append("트리, ");
        if (simulation) problemTypeBuilder.append("시뮬레이션, ");

        String problemType = problemTypeBuilder.toString();
        if (problemType.endsWith(", ")) {
            problemType = problemType.substring(0, problemType.length() - 2);
        }

        EditText etQuestionTitle = findViewById(R.id.questionTitle);
        String questionTitle = etQuestionTitle.getText().toString();

        EditText etQuestionText = findViewById(R.id.questionText);
        String questionText = etQuestionText.getText().toString();

        long timePosted = System.currentTimeMillis();

        try {
            Question question = new Question(questionId, questionTitle, questionText, problemTitle, difficulty, problemType, userIdToken, problemNum, timePosted);
            mDatabase.child("QuestionBulletin").child(String.valueOf(problemNum)).child(questionId)
                    .setValue(question)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(EditQuestion.this, "질문 업데이트에 성공했습니다.", Toast.LENGTH_SHORT).show();

                        finish();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(EditQuestion.this, "Failed to update question: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    });

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

        new EditQuestion.ScrapeTask().execute(problemNum);
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish(); // 뒤로가기 버튼 클릭 시 액티비티 종료
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}