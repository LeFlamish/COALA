package com.example.smobileeapp;

import android.os.Build;
import android.os.Bundle;
import android.content.Intent;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class EditProblemInfo extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private String userIdToken;
    private int problemNum;
    private EditText etcInput; // etcInput을 전역으로 선언

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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimary));
        }

        Intent intent = getIntent();
        userIdToken = intent.getStringExtra("userIdToken");
        problemNum = intent.getIntExtra("problemNum", -1);

        Log.d("EditProblemInfo", "userIdToken: " + userIdToken + ", problemNum: " + problemNum);

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

        // Predefined types
        Set<String> predefinedTypes = new HashSet<>(Arrays.asList(
                "브루트포스", "BFS", "DFS", "DP", "백트래킹", "큐", "스택", "수학",
                "구현", "자료 구조", "그리디 알고리즘", "정렬", "문자열",
                "그래프 이론", "그래프 탐색", "트리", "시뮬레이션"));

        // Split the problem types
        String[] problemTypes = problem.getProblemType().split(", ");
        StringBuilder etcTypes = new StringBuilder();

        for (String type : problemTypes) {
            if (predefinedTypes.contains(type)) {
                setCheckboxState(getCheckboxId(type), true);
            } else {
                if (etcTypes.length() > 0) {
                    etcTypes.append(", ");
                }
                etcTypes.append(type);
            }
        }

        if (etcTypes.length() > 0) {
            setCheckboxState(R.id.etc, true);
            etcInput.setText(etcTypes.toString());
        }

        RadioGroup radioGroup = findViewById(R.id.onmyown);
        if (problem.getOnmyown().equals("yes")) {
            radioGroup.check(R.id.yes);
        } else {
            radioGroup.check(R.id.no);
        }

        EditText etProblemMemo = findViewById(R.id.problemMemo);
        etProblemMemo.setText(problem.getProblemMemo());
    }

    private int getCheckboxId(String type) {
        switch (type) {
            case "브루트포스":
                return R.id.bruteforce;
            case "BFS":
                return R.id.BFS;
            case "DFS":
                return R.id.DFS;
            case "DP":
                return R.id.DP;
            case "백트래킹":
                return R.id.backtracking;
            case "큐":
                return R.id.queue;
            case "스택":
                return R.id.stack;
            case "수학":
                return R.id.math;
            case "구현":
                return R.id.realization;
            case "자료 구조":
                return R.id.datastructure;
            case "그리디 알고리즘":
                return R.id.greedy;
            case "정렬":
                return R.id.sort;
            case "문자열":
                return R.id.string;
            case "그래프 이론":
                return R.id.graphtheory;
            case "그래프 탐색":
                return R.id.graphsearch;
            case "트리":
                return R.id.tree;
            case "시뮬레이션":
                return R.id.simulation;
            default:
                return -1;
        }
    }

    private void setCheckboxState(int checkBoxId, boolean isChecked) {
        if (checkBoxId != -1) {
            CheckBox checkBox = findViewById(checkBoxId);
            checkBox.setChecked(isChecked);
        }
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
        boolean etc = ((CheckBox) findViewById(R.id.etc)).isChecked();

        StringBuilder problemTypeBuilder = new StringBuilder();
        if (bruteforce) problemTypeBuilder.append("브루트포스, ");
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
        if (etc) {
            String etcTypes = etcInput.getText().toString();
            problemTypeBuilder.append(etcTypes).append(", ");
        }

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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish(); // 뒤로가기 버튼 클릭 시 액티비티 종료
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
