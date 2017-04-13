package com.sumavision.talktv2.ui.widget;

import android.content.Context;
import android.os.Build;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.sumavision.talktv2.R;
import com.sumavision.talktv2.model.entity.decor.LiveDetailData;
import com.sumavision.talktv2.presenter.LiveProgramListDialogPresenter;
import com.sumavision.talktv2.ui.adapter.LiveProgramListAdapter;
import com.sumavision.talktv2.ui.adapter.WeekAdapter;
import com.sumavision.talktv2.ui.iview.ILiveProgramListDialogView;
import com.sumavision.talktv2.ui.widget.base.BaseDialog;
import com.sumavision.talktv2.util.DateUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;

/**
 * 播放器内节目单列表的Dialog
 * Created by zjx on 2016/6/21.
 */
public class LiveProgramListDialog extends BaseDialog<LiveProgramListDialogPresenter> implements ILiveProgramListDialogView{

    @BindView(R.id.lv_program_weeklist)
    ListView weekList;
    @BindView(R.id.lv_program_content)
    ListView programList;
    @BindView(R.id.channalname)
    TextView channelName;
    @BindView(R.id.no_program)
    TextView noprogram;

    private Context mContext;

    private ArrayList<String> listWeekDate = new ArrayList<>();
    private HashMap<Integer, ArrayList<LiveDetailData.ContentBean.DayBean.ProgramBean>> programDatas = new HashMap<>();
    private int weekPos;

    public LiveProgramListDialog(Context context, int themeResId, int windowHeight) {
        super(context, themeResId);
        this.mContext = context;
        weekPos = Integer.parseInt(DateUtils.StringData());
        initLayout(windowHeight);
        setCanceledOnTouchOutside(true);
    }

    /**
     * 给dialog定位和设置大小
     */
    private void initLayout(int windowHeight) {
        Window dialogWindow = getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        dialogWindow.setGravity(Gravity.LEFT | Gravity.TOP);
        setFlag();
        lp.x = 0;
        lp.y = 0;
        lp.height = windowHeight;
        dialogWindow.setAttributes(lp);
    }

    private void setFlag() {
        if (Build.VERSION.SDK_INT < 19) {
            return;
        }
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

    }

    @Override
    protected int getLayoutResId() {
        return R.layout.dialog_program;
    }

    @Override
    protected void initPresenter() {
        presenter = new LiveProgramListDialogPresenter(mContext, this);
    }

    @Override
    public void initView() {}

    public void setChannelName(String name ) {
        channelName.setText(name);
    }

    public void setData(List<LiveDetailData.ContentBean.DayBean> dayBeans){
        weekPos = Integer.parseInt(DateUtils.StringData());
        if(dayBeans != null) {
            int count = dayBeans.size();
            for(int i=0; i<count; i++) {
                LiveDetailData.ContentBean.DayBean dayBean = dayBeans.get(i);
                listWeekDate.add(dayBean.getWeek());
                List<LiveDetailData.ContentBean.DayBean.ProgramBean> programBeens = dayBean.getProgram();
                programDatas.put(i, (ArrayList<LiveDetailData.ContentBean.DayBean.ProgramBean>) programBeens);
            }
        }

        initWeekList();
        initProgramList(weekPos);
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

        LiveProgramListAdapter liveProgramListAdapter = new LiveProgramListAdapter(mContext, programDatas.get(position), state, programNum);
        programList.setAdapter(liveProgramListAdapter);
        programList.setSelection(programNum);
    }

    /**
     * 初始化列表
     */
    private void initWeekList() {
        final WeekAdapter weekAdapter = new WeekAdapter(mContext);
        weekAdapter.setSelector(weekPos);
        weekList.setAdapter(weekAdapter);
        weekList.setSelection(weekPos);
        weekList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                weekAdapter.setSelector(position);
                weekAdapter.notifyDataSetChanged();
                initProgramList(position);
            }
        });
    }


}
