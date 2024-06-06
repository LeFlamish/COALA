package com.example.smobileeapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.Arrays;
import java.util.List;

public class ProblemSearch extends AppCompatActivity {
    private String userIdToken;
    private EditText problemSearch;
    private RadioGroup searchBy, allorone, searchByOnMyOwn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_problem_search);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("내가 푼 문제 검색");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true); // 뒤로가기 버튼 표시
        }

        Intent intent = getIntent();
        userIdToken = intent.getStringExtra("userIdToken");

        // View 초기화
        problemSearch = findViewById(R.id.problemSearch);
        searchBy = findViewById(R.id.searchBy);
        allorone = findViewById(R.id.allorone);
        searchByOnMyOwn = findViewById(R.id.searchbyonmyown);

        Button searchProblemButton = findViewById(R.id.searchtheproblem);
        searchProblemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchProblemByID();
            }
        });

        Button searchByDifficulty = findViewById(R.id.searchbydifficulty);
        searchByDifficulty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchproblemsByDifficulty();
            }
        });

        Button searchByType = findViewById(R.id.searchbytype);
        searchByType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchproblemsByType();
            }
        });

        Button searchByOnMyOwnButton = findViewById(R.id.searchbyonemyownbutton);
        searchByOnMyOwnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchByOnMyOwn();
            }
        });

        // 스피너에 어댑터 설정
        Spinner difficultySpinner = findViewById(R.id.difficultySpinner);
        List<String> difficultyLevels = Arrays.asList(getResources().getStringArray(R.array.difficulty_array));
        CustomSpinnerAdapter adapter = new CustomSpinnerAdapter(this, R.layout.custom_spinner_item, difficultyLevels);
        difficultySpinner.setAdapter(adapter);
    }

    // 문제 검색 메서드
    private void searchProblemByID() {
        String searchText = problemSearch.getText().toString().trim();
        int selectedRadioButtonId = searchBy.getCheckedRadioButtonId();

        if (searchText.isEmpty()) {
            Toast.makeText(this, "검색어를 입력하세요.", Toast.LENGTH_SHORT).show();
        } else if (selectedRadioButtonId == -1) {
            Toast.makeText(this, "검색 방법을 선택하세요.", Toast.LENGTH_SHORT).show();
        } else {
            RadioButton selectedRadioButton = findViewById(selectedRadioButtonId);
            String searchByOption = selectedRadioButton.getText().toString();

            if (searchByOption.equals("문제 번호로 검색")) {
                Intent it = new Intent(this, ProblemListByNum.class);
                it.putExtra("userIdToken", userIdToken);
                it.putExtra("problemNum", Integer.parseInt(searchText));
                startActivity(it);
                finish();
            } else if (searchByOption.equals("문제 제목으로 검색")) {
                Intent it = new Intent(this, ProblemListByTitle.class);  // 수정된 부분
                it.putExtra("userIdToken", userIdToken);
                it.putExtra("problemTitle", searchText);
                startActivity(it);
                finish();
            } else {
                Toast.makeText(this, "올바른 검색 방법을 선택하세요.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void searchproblemsByDifficulty() {
        Spinner difficultySpinner = (Spinner) findViewById(R.id.difficultySpinner);
        String difficulty = difficultySpinner.getSelectedItem().toString();

        Intent intent = new Intent(ProblemSearch.this, ProblemListByDifficulty.class);
        intent.putExtra("userIdToken", userIdToken);
        intent.putExtra("problemDifficulty", difficulty);
        startActivity(intent);
        finish();
    }

    private void searchproblemsByType() {
        int selectedRadioButtonId = allorone.getCheckedRadioButtonId();

        CheckBox bruteforceCheckBox = (CheckBox) findViewById(R.id.bruteforce);
        boolean bruteforce = bruteforceCheckBox.isChecked();

        CheckBox bfsCheckBox = (CheckBox) findViewById(R.id.BFS);
        boolean bfs = bfsCheckBox.isChecked();

        CheckBox dfsCheckBox = (CheckBox) findViewById(R.id.DFS);
        boolean dfs = dfsCheckBox.isChecked();

        CheckBox dpCheckBox = (CheckBox) findViewById(R.id.DP);
        boolean dp = dpCheckBox.isChecked();

        CheckBox backtrackingCheckBox = (CheckBox) findViewById(R.id.backtracking);
        boolean backtracking = backtrackingCheckBox.isChecked();

        CheckBox queueCheckBox = (CheckBox) findViewById(R.id.queue);
        boolean queue = queueCheckBox.isChecked();

        CheckBox stackCheckBox = (CheckBox) findViewById(R.id.stack);
        boolean stack = stackCheckBox.isChecked();

        CheckBox mathCheckBox = (CheckBox) findViewById(R.id.math);
        boolean math = mathCheckBox.isChecked();

        CheckBox realizationCheckBox = (CheckBox) findViewById(R.id.realization);
        boolean realization = realizationCheckBox.isChecked();

        CheckBox datastructureCheckBox = (CheckBox) findViewById(R.id.datastructure);
        boolean datastructure = datastructureCheckBox.isChecked();

        CheckBox greedyCheckBox = (CheckBox) findViewById(R.id.greedy);
        boolean greedy = greedyCheckBox.isChecked();

        CheckBox sortCheckBox = (CheckBox) findViewById(R.id.sort);
        boolean sort = sortCheckBox.isChecked();

        CheckBox stringCheckBox = (CheckBox) findViewById(R.id.string);
        boolean string = stringCheckBox.isChecked();

        CheckBox graphtheoryCheckBox = (CheckBox) findViewById(R.id.graphtheory);
        boolean graphtheory = graphtheoryCheckBox.isChecked();

        CheckBox graphsearchCheckBox = (CheckBox) findViewById(R.id.graphsearch);
        boolean graphsearch = graphsearchCheckBox.isChecked();

        CheckBox treeCheckBox = (CheckBox) findViewById(R.id.tree);
        boolean tree = treeCheckBox.isChecked();

        CheckBox simulationCheckBox = (CheckBox) findViewById(R.id.simulation);
        boolean simulation = simulationCheckBox.isChecked();

        StringBuilder problemTypeBuilder = new StringBuilder();

        if (bruteforce) {
            problemTypeBuilder.append("브루트포스, ");
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

        if (problemType.length() == 0) {
            Toast.makeText(ProblemSearch.this, "알고리즘 유형을 1개 이상 선택하세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (selectedRadioButtonId == -1) {
            Toast.makeText(this, "포함 유형을 선택하세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent it = new Intent(this, ProblemListByType.class);
        it.putExtra("problemType", problemType); // 문제 유형을 인텐트에 추가
        if (selectedRadioButtonId == R.id.musthaveall) {
            it.putExtra("how", 1);
        } else if (selectedRadioButtonId == R.id.atleastone) {
            it.putExtra("how", 2);
        }
        it.putExtra("userIdToken", userIdToken);
        startActivity(it);
        finish();
    }

    // 스스로 해결 여부에 따른 검색 메서드
    private void searchByOnMyOwn() {
        int selectedOnMyOwnRadioButtonId = searchByOnMyOwn.getCheckedRadioButtonId();

        if (selectedOnMyOwnRadioButtonId == -1) {
            // 라디오 버튼이 선택되지 않은 경우
            Toast.makeText(this, "스스로 해결 여부를 선택하세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        RadioButton selectedOnMyOwnRadioButton = findViewById(selectedOnMyOwnRadioButtonId);
        String onMyOwnOption = selectedOnMyOwnRadioButton.getText().toString();

        Intent it = new Intent(this, ProblemListByOnMyOwn.class);
        it.putExtra("userIdToken", userIdToken);
        if (onMyOwnOption.equals("네")) {
            // 스스로 해결한 문제 검색
            it.putExtra("onmyown", "yes");
        } else if (onMyOwnOption.equals("아니오")) {
            // 스스로 못 푼 문제 검색
            it.putExtra("onmyown", "no");
        }
        startActivity(it);
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