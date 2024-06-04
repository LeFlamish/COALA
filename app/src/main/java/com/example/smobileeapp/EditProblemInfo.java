package com.example.smobileeapp;

import android.os.Bundle;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;
import java.util.List;

public class EditProblemInfo extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private String userIdToken;
    private int problemNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_problem_info);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("문제 정보 수정");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true); // 뒤로가기 버튼 표시
        }

        Intent intent = getIntent();
        userIdToken = intent.getStringExtra("userIdToken");
        problemNum = intent.getIntExtra("problemNum", -1);

        Log.d("EditProblemInfo", "userIdToken: " + userIdToken + ", problemNum: " + problemNum);

        Spinner difficultySpinner = findViewById(R.id.difficultySpinner);
        List<String> difficultyLevels = Arrays.asList(getResources().getStringArray(R.array.difficulty_array));
        CustomSpinnerAdapter adapter = new CustomSpinnerAdapter(this, R.layout.custom_spinner_item, difficultyLevels);
        difficultySpinner.setAdapter(adapter);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        loadProblemData();
    }

    private void loadProblemData() {
        DatabaseReference problemRef = mDatabase.child("Problems").child(userIdToken).child(String.valueOf(problemNum));
        Log.d("EditProblemInfo", "Database Path: " + problemRef.toString());

        problemRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Problem problem = dataSnapshot.getValue(Problem.class);
                if (problem != null) {
                    Log.d("EditProblemInfo", "Problem data loaded: " + problem.toString());
                    populateUI(problem);
                } else {
                    Log.d("EditProblemInfo", "Problem not found in database");
                    Toast.makeText(EditProblemInfo.this, "문제를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(EditProblemInfo.this, "문제 데이터를 가져오는 데 실패했습니다.", Toast.LENGTH_SHORT).show();
                Log.e("EditProblemInfo", "Database error: " + databaseError.getMessage());
            }
        });
    }

    private void populateUI(Problem problem) {
        EditText etProblemNum = findViewById(R.id.problemNum);
        etProblemNum.setText(String.valueOf(problem.getProblemNum()));

        EditText etProblemTitle = findViewById(R.id.problemTitle);
        etProblemTitle.setText(problem.getProblemTitle());

        Spinner difficultySpinner = findViewById(R.id.difficultySpinner);
        String[] difficulties = getResources().getStringArray(R.array.difficulty_array);
        for (int i = 0; i < difficulties.length; i++) {
            if (difficulties[i].equals(problem.getDifficulty())) {
                difficultySpinner.setSelection(i);
                break;
            }
        }

        setCheckboxState(R.id.bruteforce, problem.getProblemType().contains("브루트포스 알고리즘"));
        setCheckboxState(R.id.BFS, problem.getProblemType().contains("BFS"));
        setCheckboxState(R.id.DFS, problem.getProblemType().contains("DFS"));
        setCheckboxState(R.id.DP, problem.getProblemType().contains("DP"));
        setCheckboxState(R.id.backtracking, problem.getProblemType().contains("백트래킹"));
        setCheckboxState(R.id.queue, problem.getProblemType().contains("큐"));
        setCheckboxState(R.id.stack, problem.getProblemType().contains("스택"));
        setCheckboxState(R.id.math, problem.getProblemType().contains("수학"));
        setCheckboxState(R.id.realization, problem.getProblemType().contains("구현"));
        setCheckboxState(R.id.datastructure, problem.getProblemType().contains("자료 구조"));
        setCheckboxState(R.id.greedy, problem.getProblemType().contains("그리디 알고리즘"));
        setCheckboxState(R.id.sort, problem.getProblemType().contains("정렬"));
        setCheckboxState(R.id.string, problem.getProblemType().contains("문자열"));
        setCheckboxState(R.id.graphtheory, problem.getProblemType().contains("그래프 이론"));
        setCheckboxState(R.id.graphsearch, problem.getProblemType().contains("그래프 탐색"));
        setCheckboxState(R.id.tree, problem.getProblemType().contains("트리"));
        setCheckboxState(R.id.simulation, problem.getProblemType().contains("시뮬레이션"));

        RadioGroup radioGroup = findViewById(R.id.onmyown);
        if (problem.getOnmyown().equals("yes")) {
            radioGroup.check(R.id.yes);
        } else {
            radioGroup.check(R.id.no);
        }

        EditText etProblemMemo = findViewById(R.id.problemMemo);
        etProblemMemo.setText(problem.getProblemMemo());
    }

    private void setCheckboxState(int checkBoxId, boolean isChecked) {
        CheckBox checkBox = findViewById(checkBoxId);
        checkBox.setChecked(isChecked);
    }

    public void updateProblem(View v) {
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

        RadioGroup onmyownGroup = findViewById(R.id.onmyown);
        String onmyown = "";
        if (onmyownGroup.getCheckedRadioButtonId() == R.id.yes) {
            onmyown = "yes";
        }
        if (onmyownGroup.getCheckedRadioButtonId() == R.id.no) {
            onmyown = "no";
        }

        EditText etProblemMemo = findViewById(R.id.problemMemo);
        String problemMemo = etProblemMemo.getText().toString();

        Long timeposted = System.currentTimeMillis(); // 현재 시간 저장

        try {
            Problem problem = new Problem(problemNum, problemTitle, difficulty, problemType, problemMemo, userIdToken, timeposted, onmyown);
            mDatabase.child("Problems").child(userIdToken).child(String.valueOf(problemNum)).setValue(problem);

            Intent it = new Intent(this, ProblemInfo.class);
            it.putExtra("userIdToken", userIdToken);
            it.putExtra("problemNum", problemNum);
            startActivity(it);
            finish();
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(EditProblemInfo.this, ProblemInfo.class);
        intent.putExtra("userIdToken", userIdToken);
        intent.putExtra("problemNum", problemNum);
        startActivity(intent);
        finish();
    }
}