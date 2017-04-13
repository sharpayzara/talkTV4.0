package com.sumavision.talktv2.ui.activity;

import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.sumavision.talktv2.R;
import com.sumavision.talktv2.http.GlideProxy;
import com.sumavision.talktv2.model.entity.decor.LiveDetailData;
import com.sumavision.talktv2.presenter.LiveDetailPresenter;
import com.sumavision.talktv2.ui.activity.base.BaseActivity;
import com.sumavision.talktv2.ui.adapter.LiveProgramAdapter;
import com.sumavision.talktv2.ui.adapter.WeekListAdapter;
import com.sumavision.talktv2.ui.iview.ILiveDetailView;
import com.sumavision.talktv2.util.DateUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 直播详情界面
 * Created by zjx on 2016/6/14.
 */
public class LiveDetailActivity extends BaseActivity<LiveDetailPresenter> implements ILiveDetailView{
    @BindView(R.id.channel_name)
    TextView channel;
    @BindView(R.id.fav)
    ImageButton fav;
    @BindView(R.id.list_week)
    ListView weekList;
    @BindView(R.id.list_program)
    ListView programList;
    @BindView(R.id.channel_pic)
    ImageView pic;
    @BindView(R.id.current_program)
    TextView program;
    @BindView(R.id.no_program)
    TextView noprogram;

    private String channelId;
    private boolean isFav;
    private boolean isfav;
    private int weekPos;

    private ArrayList<String> listWeekDate = new ArrayList<>();
    private HashMap<Integer, ArrayList<LiveDetailData.ContentBean.DayBean.ProgramBean>> programDatas = new HashMap<>();

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_live_detail;
    }

    @Override
    protected void initPresenter() {
        presenter = new LiveDetailPresenter(this, this);
        presenter.init();
        weekPos = Integer.parseInt(DateUtils.StringData());
    }

    @Override
    public void initView() {
        Intent intent = getIntent();
        isFav = intent.getBooleanExtra("isFav", false);
        isfav = isFav;
        channelId = intent.getStringExtra("channelId");
        presenter.getLiveDetail(channelId);
        channel.setText(intent.getStringExtra("channelName"));
        program.setText(intent.getStringExtra("program"));
        GlideProxy.getInstance().loadVImage(this, intent.getStringExtra("pic"), pic);
        setView();
//        presenter.judgeNetwork();
    }


    private void setView() {
        if(isfav) {
            fav.setImageResource(R.mipmap.livetv_playbill_collect_selected_btn);
        }
        else {
            fav.setImageResource(R.mipmap.livetv_playbill_collect_nor_btn);
        }
    }

    /**
     * 成功获取频道详情节目
     * @param liveDetailData
     */
    @Override
    public void getLiveDetail(LiveDetailData liveDetailData) {
        if("success".equals(liveDetailData.getMsg())) {
            List<LiveDetailData.ContentBean.DayBean> days = liveDetailData.getContent().getDay();
            if(days != null) {
                int count = days.size();
                for(int i=0; i<count; i++) {
                    LiveDetailData.ContentBean.DayBean dayBean = days.get(i);
                    listWeekDate.add(dayBean.getWeek());
                    List<LiveDetailData.ContentBean.DayBean.ProgramBean> programBeens = dayBean.getProgram();
                    programDatas.put(i, (ArrayList<LiveDetailData.ContentBean.DayBean.ProgramBean>) programBeens);
                }
            }
         }

        initWeekList();
        initProgramList(weekPos);
    }

    private void initWeekList () {
        final WeekListAdapter weekListAdapter = new WeekListAdapter(this, listWeekDate);
        weekListAdapter.setSelector(weekPos);
        weekList.setAdapter(weekListAdapter);
        weekList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                weekListAdapter.setSelector(position);
                weekListAdapter.notifyDataSetChanged();

                initProgramList(position);
            }
        });
    }

    private void initProgramList (int position) {

        if(programDatas.get(position) != null && programDatas.get(position).size()<=0) {
            noprogram.setVisibility(View.VISIBLE);
            programList.setVisibility(View.GONE);
            return;
        }
        else {
            noprogram.setVisibility(View.GONE);
            programList.setVisibility(View.VISIBLE);
        }

        int state = 0;
        int programNum = -1;
        if(position == weekPos) {
            programNum = DateUtils.getmCurrentIndex(programDatas.get(position));
            state = 1;
        }
        else if(position>weekPos)
            state = 2;

        LiveProgramAdapter liveProgramAdapter = new LiveProgramAdapter(this, state, programNum);
        liveProgramAdapter.setProgramDatas(programDatas.get(position));
        programList.setAdapter(liveProgramAdapter);
        programList.setSelection(programNum);
    }

    @OnClick({ R.id.fav})
    public void onClick(View view){
        switch (view.getId()) {
            case R.id.fav:
                favChannel();
                break;
        }
    }

    /**
     * 收藏和取消收藏
     */
    private void favChannel() {
        if(isfav) {
            fav.setImageResource(R.mipmap.livetv_playbill_collect_nor_btn);
            presenter.cancelFav(channelId);
        }
        else {
            fav.setImageResource(R.mipmap.livetv_playbill_collect_selected_btn);
            presenter.favChannel(channelId);
        }
        isfav = !isfav;
    }

    private void setResult() {
        boolean change = (isFav != isfav);
        Intent intent = new Intent();
        intent.putExtra("change", change);
        setResult(RESULT_OK, intent);
    }

    @OnClick(R.id.back)
    public void back() {
        setResult();
        finish();
    }

    @Override
    public void onBackPressed() {
        setResult();
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}
