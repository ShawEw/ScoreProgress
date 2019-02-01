# ScoreProgress
自定义分数进度条

## maven使用
```
allprojects {
    repositories {
        google()
        jcenter()
        maven{url "https://raw.githubusercontent.com/ShawEw/ScoreProgress/master"}
    }
}

api "lib.hz.com:scoreProgress:1.0"
```

## 代码使用
### xml
```
<lib.hz.com.scoreprogressview.view.ScoreProgressView
        android:id="@+id/spv"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_marginTop="20dp"
        android:paddingLeft="10dp"
        android:paddingRight="10dp"
        android:paddingBottom="10dp"
        app:back_color="@color/colorPrimary"
        app:fore_color="@color/colorAccent"
        app:start_score="0"
        app:total_score="100"
        app:score_height="10dp"
        app:score_text_size="15sp"
        app:info_color="#000000"/>
```
### 代码使用
```
scoreProgressView.resetLevelProgress(0,100,60,"当前的分数60");
```

