package com.sumavision.talktv2.ui.fragment;

import android.content.Intent;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

import com.sumavision.talktv2.BaseApp;
import com.sumavision.talktv2.R;
import com.sumavision.talktv2.common.ACache;
import com.sumavision.talktv2.common.DividerGridItemDecoration;
import com.sumavision.talktv2.dao.MyItemTouchCallback;
import com.sumavision.talktv2.dao.OnRecyclerItemClickListener;
import com.sumavision.talktv2.model.entity.ClassifyItem;
import com.sumavision.talktv2.model.entity.decor.ClassifyData;
import com.sumavision.talktv2.presenter.base.MyGridPresenter;
import com.sumavision.talktv2.ui.activity.TVFANActivity;
import com.sumavision.talktv2.ui.adapter.RecyclerAdapter;
import com.sumavision.talktv2.ui.fragment.Base.BaseFragment;
import com.sumavision.talktv2.ui.iview.base.IBaseView;
import com.sumavision.talktv2.util.AppGlobalConsts;
import com.sumavision.talktv2.util.BusProvider;
import com.sumavision.talktv2.util.VibratorUtil;

import java.util.ArrayList;

import butterknife.BindView;

/**
 * Created by zhoutao on 2016/5/26.
 */
public class MyGridFragment extends BaseFragment<MyGridPresenter> implements IBaseView,MyItemTouchCallback.OnDragListener{
    @BindView(R.id.rv_mygrid)
    RecyclerView rv_mygrid;
    private ClassifyData data ;
    private ArrayList<ClassifyItem> items;


    @Override
    public boolean onBackPressed() {
        return false;
    }

    @Override
    public void onDestroyView() {
        BusProvider.getInstance().post("refreshShow","refreshShow");
        super.onDestroyView();
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_mygrid_layout;
    }

    @Override
    protected void initPresenter() {
        presenter = new MyGridPresenter(this.getActivity(), this);
        presenter.init();
    }

    private ItemTouchHelper itemTouchHelper;

    @Override
    public void initView() {
        //初始化数据，如果缓存中有就使用缓存中的
        data = (ClassifyData) BaseApp.getACache().getAsObject("classifydata");
        if (data!=null) {
            //获取缓存中的 ArrayList<ClassifyItem>
            items =data.results;
        } else {
            items  = new ArrayList<>();
        }
        RecyclerAdapter adapter = new RecyclerAdapter(R.layout.dragsetting_item_layout,items);
        rv_mygrid.setSelected(false);
        rv_mygrid.setHasFixedSize(true);
        rv_mygrid.setAdapter(adapter);
        rv_mygrid.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        rv_mygrid.addItemDecoration(new DividerGridItemDecoration(getActivity()));
        itemTouchHelper = new ItemTouchHelper(new MyItemTouchCallback(adapter).setOnDragListener(this));
        itemTouchHelper.attachToRecyclerView(rv_mygrid);

        rv_mygrid.addOnItemTouchListener(new OnRecyclerItemClickListener(rv_mygrid) {
            @Override
            public void onLongClick(RecyclerView.ViewHolder vh) {
                if (vh.getLayoutPosition()!=0) {
                    itemTouchHelper.startDrag(vh);
                    VibratorUtil.Vibrate(getActivity(), 70);   //震动70ms
                }
            }
            @Override
            public void onItemClick(RecyclerView.ViewHolder vh) {
               /* ClassifyItem item = items.get(vh.getLayoutPosition());
                Intent intent2 = new Intent();
                intent2.setClass(getContext(), ProgramListActivity.class);
                intent2.putExtra("navID","");
                intent2.putExtra("idStr", item.navId);
                getContext().startActivity(intent2);*/
                ClassifyItem item = items.get(vh.getLayoutPosition());
                Intent intent2 = new Intent();
                intent2.setClass(getContext(), TVFANActivity.class);
                intent2.putExtra("cardid", item.navId);
                AppGlobalConsts.ISFROMGRID = true;
                getContext().startActivity(intent2);

            }
        });
    }

    @Override
    public void onFinishDrag() {
        //存入缓存
        ACache.get(getActivity()).put("classifydata",(ClassifyData)data);
    }
}