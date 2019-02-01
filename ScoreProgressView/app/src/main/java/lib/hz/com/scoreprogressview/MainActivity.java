package lib.hz.com.scoreprogressview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import lib.hz.com.scoreprogress.ScoreProgressView;

public class MainActivity extends AppCompatActivity {

    ScoreProgressView scoreProgressView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        scoreProgressView = findViewById(R.id.spv);
        scoreProgressView.resetLevelProgress(0,100,60,"当前的分数60");
    }
}
