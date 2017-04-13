package com.sumavision.talktv2.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.sumavision.talktv2.R;
import com.sumavision.talktv2.model.entity.VideoType;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by sharpay on 16-6-21.
 */
public class TestPlayActivity extends Activity {
    @BindView(R.id.movie)
    Button movie;
    @BindView(R.id.tv)
    Button tv;
    @BindView(R.id.arts)
    Button arts;
    @BindView(R.id.anim)
    Button anim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_play);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.movie, R.id.tv, R.id.arts, R.id.anim,R.id.program_list, R.id.watch_history})
    public void onClick(View view) {
        Intent intent = new Intent(this,ProgramDetailActivity.class);
        switch (view.getId()) {
            case R.id.movie:
                intent.putExtra("type", VideoType.movie);
                intent.putExtra("idStr","se1g35r0");
                intent.putExtra("cpidStr","cpa8ip");
                break;
            case R.id.tv:
                intent.putExtra("type", VideoType.tv);
                intent.putExtra("idStr","se2pwwdb");
                intent.putExtra("cpidStr","cpamdv");

                break;
            case R.id.arts:
                intent.putExtra("type", VideoType.acts);
                intent.putExtra("idStr","se8p8wui");
                intent.putExtra("cpidStr","cp76we");
                break;
            case R.id.anim:
                intent.putExtra("type", VideoType.anim);
                intent.putExtra("idStr","sedp5ca7");
                intent.putExtra("cpidStr","cpebqh");
                break;
            case R.id.program_list:
                Intent listIntent = new Intent(this,ProgramListActivity.class);
                startActivity(listIntent);
                return;
            case R.id.watch_history:
                Intent intent2 = new Intent(this,WatchHistoryActivity.class);
                startActivity(intent2);
                return;


        }
        startActivity(intent);
    }
}
