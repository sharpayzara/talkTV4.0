package com.sumavision.talktv2.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.sumavision.talktv2.R;
import com.sumavision.talktv2.common.ShareElement;
import com.sumavision.talktv2.http.GlideProxy;
import com.sumavision.talktv2.model.entity.HomeRecommend;
import com.sumavision.talktv2.ui.activity.Game37WanActivity;
import com.sumavision.talktv2.ui.activity.MediaDetailActivity;
import com.sumavision.talktv2.ui.activity.ProgramDetailActivity;
import com.sumavision.talktv2.ui.activity.ProgramListActivity;
import com.sumavision.talktv2.ui.activity.SpecialActivity;
import com.sumavision.talktv2.ui.activity.SpecialDetailActivity;
import com.sumavision.talktv2.ui.activity.WeBADActivity;
import com.sumavision.talktv2.ui.widget.MyADbannerView;
import com.sumavision.talktv2.ui.widget.MyChildItemHView;
import com.sumavision.talktv2.ui.widget.MyChildItemVEView;
import com.sumavision.talktv2.ui.widget.MyEnterDragView;
import com.sumavision.talktv2.ui.widget.MyItemH12View;
import com.sumavision.talktv2.ui.widget.MyItemH14View;
import com.sumavision.talktv2.ui.widget.MyItemH16View;
import com.sumavision.talktv2.ui.widget.MyItemHView;
import com.sumavision.talktv2.ui.widget.MyItemV16View;
import com.sumavision.talktv2.ui.widget.MyItemVView;
import com.sumavision.talktv2.ui.widget.MybannerView;
import com.sumavision.talktv2.ui.widget.base.MyItemBaseView;
import com.sumavision.talktv2.util.AppGlobalConsts;
import com.sumavision.talktv2.util.BusProvider;
import com.sumavision.talktv2.util.CommonUtil;
import com.sumavision.talktv2.util.NoDoubleClickListener;
import com.umeng.analytics.MobclickAgent;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by sharpay on 16-6-16.
 */
public class HomeRecommandAdapter extends RecyclerView.Adapter<HomeRecommandAdapter.RecommandHolder> {

    List<HomeRecommend> homeRecommendList;
    String nid;
    Context mContext;
    String topicId,card_id,topic_name,type;
    public HomeRecommandAdapter(Context mContext, List<HomeRecommend> homeRecommendList, String nid,String topicId,String card_id,String topic_name,String type) {
        this.homeRecommendList = homeRecommendList;
        this.nid = nid;
        this.mContext = mContext;
        this.topicId = topicId;
        this.card_id = card_id;
        this.topic_name = topic_name;
        this.type = type;
    }

    @Override
    public RecommandHolder onCreateViewHolder(ViewGroup parent, int viewType
    ) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_recommand_layout, parent, false);
        return new RecommandHolder(view);
    }

    @Override
    public void onBindViewHolder(RecommandHolder holder, int j) {
        holder.contentLayout.removeAllViews();
        if (homeRecommendList.get(j).items.size() > 0) {
            //1.先判断类型,截止到版本4.0.0.2,当前存在的模板类型有5种
            
            switch (homeRecommendList.get(j).card_style) {

                case "complex":
                    //1.1 如果是bannner
                    holder.setBannerShow(j,holder.contentLayout);
                    break;
                case "module":
                    //1.2如果是list模块
                    holder.setListShow(j, nid,holder.contentLayout);
                    break;
                case "simple":
                    //1.3  如果是单图
                    //单图分为大单图和小单图两种模板
                    if (homeRecommendList.get(j).style.equals("big")){
                        holder.setImageShow(j,holder.contentLayout);
                    }else{
                        holder.setADImageShow(j,holder.contentLayout);
                    }
                    break;
                case "hub":
                    //1.4  如果影视圈模板
                    holder.setADBannerShow(j,holder.contentLayout);
                    break;
                case "special":
                    //1.4  如果专题模板
                    holder.setSpecialShow(j,holder.contentLayout);
                    break;
                case "AD":
                    //这里是广告预留
                    break;
            }
        }
        if ("rehp3t".equals(nid) && j == homeRecommendList.size()-1){
            setEnterDragViewShow(holder.contentLayout);
        }
    }
    private void setEnterDragViewShow(LinearLayout contentLayout) {
        MyEnterDragView myEnter = new MyEnterDragView(mContext);
        contentLayout.addView(myEnter);
    }


    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return homeRecommendList.size();
    }

    class RecommandHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.content_layout)
        LinearLayout contentLayout;

        public RecommandHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

        }

        private void setBannerShow(int j,LinearLayout view) {
            MybannerView mybannerview = new MybannerView(mContext);
            mybannerview.setImageUris(homeRecommendList.get(j).items);
            view.addView(mybannerview);
        }
        private void setH1H6MoudleShow(final int j, String nid, LinearLayout contentLayout) {
            //如果是横图的1大六小模板
             MyItemH16View myItemH16View = new MyItemH16View(mContext);
            myItemH16View.setClassifyname(homeRecommendList.get(j).card_name);
            setBaseItemShow(j, 0, myItemH16View, homeRecommendList, nid);
            if (homeRecommendList.get(j).items.size() > 7 || homeRecommendList.get(j).items.size() == 7) {
                setHItemShow(j, 1, myItemH16View.getMyChildHView1(), homeRecommendList);
                setHItemShow(j, 2, myItemH16View.getMyChildHView2(), homeRecommendList);
                setHItemShow(j, 3, myItemH16View.getMyChildHView3(), homeRecommendList);
                setHItemShow(j, 4, myItemH16View.getMyChildHView4(), homeRecommendList);
                setHItemShow(j, 5, myItemH16View.getMyChildHView5(), homeRecommendList);
                setHItemShow(j, 6, myItemH16View.getMyChildHView6(), homeRecommendList);
            }
            if (homeRecommendList.get(j).hasMore == 1) {
                myItemH16View.setTopShow(true);
                myItemH16View.getMoreBtn().setOnClickListener(new NoDoubleClickListener() {
                    @Override
                    public void onNoDoubleClick(View v) {
                        MobclickAgent.onEvent(mContext, "4tjgd");
                        excuteEnter(homeRecommendList, j);
                    }
                });
            } else {
                myItemH16View.getMoreBtn().setVisibility(View.GONE);
            }
            if (homeRecommendList.get(j).hasChange == 1) {
                final int[] CLICKNUM2 = {1};
                myItemH16View.setChangeBtnShow(true);
                myItemH16View.getChangeBtn().setOnClickListener(new NoDoubleClickListener() {
                    @Override
                    public void onNoDoubleClick(View v) {
                        CLICKNUM2[0] += 6;
                        int s1 = CLICKNUM2[0];
                        if (s1 > homeRecommendList.get(j).items.size()-1) {
                            s1 = 1;
                            CLICKNUM2[0] = 1;
                        }
                        int s2 = CLICKNUM2[0] + 1;
                        if (s2 > homeRecommendList.get(j).items.size() - 1) {
                            s2 = 1;
                            CLICKNUM2[0] = 0;
                        }
                        int s3 = CLICKNUM2[0] + 2;
                        if (s3 > homeRecommendList.get(j).items.size() - 1) {
                            s3 = 1;
                            CLICKNUM2[0] = -1;
                        }
                        int s4 = CLICKNUM2[0] + 3;
                        if (s4 > homeRecommendList.get(j).items.size() - 1) {
                            s4 = 1;
                            CLICKNUM2[0] = -2;
                        }
                        int s5 = CLICKNUM2[0] + 4;
                        if (s5 > homeRecommendList.get(j).items.size() - 1) {
                            s5 = 1;
                            CLICKNUM2[0] = -3;
                        }
                        int s6 = CLICKNUM2[0] + 5;
                        if (s6 > homeRecommendList.get(j).items.size() - 1) {
                            s6 = 1;
                            CLICKNUM2[0] = -4;
                        }

                        setHItemShow(j, s1, myItemH16View.getMyChildHView1(), homeRecommendList);
                        setHItemShow(j, s2, myItemH16View.getMyChildHView2(), homeRecommendList);
                        setHItemShow(j, s3, myItemH16View.getMyChildHView3(), homeRecommendList);
                        setHItemShow(j, s4, myItemH16View.getMyChildHView4(), homeRecommendList);
                        setHItemShow(j, s5, myItemH16View.getMyChildHView5(), homeRecommendList);
                        setHItemShow(j, s6, myItemH16View.getMyChildHView6(), homeRecommendList);
                        MobclickAgent.onEvent(mContext, "4tjhyp");
                    }


                });
            } else {
                myItemH16View.setChangeBtnShow(false);
            }
            contentLayout.addView(myItemH16View);
        }

        private void setH1H4MoudleShow(final int j, String nid, LinearLayout contentLayout) {
            //如果是横图的1大四小模板
            MyItemH14View myItemH14View = new MyItemH14View(mContext);
            myItemH14View.setClassifyname(homeRecommendList.get(j).card_name);
            setBaseItemShow(j, 0, myItemH14View, homeRecommendList, nid);
            if (homeRecommendList.get(j).items.size() > 5 || homeRecommendList.get(j).items.size() == 5) {
                setHItemShow(j, 1, myItemH14View.getMyChildHView1(), homeRecommendList);
                setHItemShow(j, 2, myItemH14View.getMyChildHView2(), homeRecommendList);
                setHItemShow(j, 3, myItemH14View.getMyChildHView3(), homeRecommendList);
                setHItemShow(j, 4, myItemH14View.getMyChildHView4(), homeRecommendList);
            }
            if (homeRecommendList.get(j).hasMore == 1) {
                myItemH14View.setTopShow(true);
                myItemH14View.getMoreBtn().setOnClickListener(new NoDoubleClickListener() {
                    @Override
                    public void onNoDoubleClick(View v) {
                        MobclickAgent.onEvent(mContext, "4tjgd");
                        excuteEnter(homeRecommendList, j);
                    }
                });
            } else {
                myItemH14View.getMoreBtn().setVisibility(View.GONE);
            }
            if (homeRecommendList.get(j).hasChange == 1) {
                final int[] CLICKNUM2 = {1};
                myItemH14View.setChangeBtnShow(true);
                myItemH14View.getChangeBtn().setOnClickListener(new NoDoubleClickListener() {
                    @Override
                    public void onNoDoubleClick(View v) {
                        CLICKNUM2[0] += 4;
                        int s1 = CLICKNUM2[0];
                        if (s1 > homeRecommendList.get(j).items.size() - 1) {
                            s1 = 1;
                            CLICKNUM2[0] = 1;
                        }
                        int s2 = CLICKNUM2[0] + 1;
                        if (s2 > homeRecommendList.get(j).items.size() - 1) {
                            s2 = 1;
                            CLICKNUM2[0] = 0;
                        }
                        int s3 = CLICKNUM2[0] + 2;
                        if (s3 > homeRecommendList.get(j).items.size() - 1) {
                            s3 = 1;
                            CLICKNUM2[0] = -1;
                        }
                        int s4 = CLICKNUM2[0] + 3;
                        if (s4 > homeRecommendList.get(j).items.size() - 1) {
                            s4 = 1;
                            CLICKNUM2[0] = -2;
                        }

                        setHItemShow(j, s1, myItemH14View.getMyChildHView1(), homeRecommendList);
                        setHItemShow(j, s2, myItemH14View.getMyChildHView2(), homeRecommendList);
                        setHItemShow(j, s3, myItemH14View.getMyChildHView3(), homeRecommendList);
                        setHItemShow(j, s4, myItemH14View.getMyChildHView4(), homeRecommendList);
                        MobclickAgent.onEvent(mContext, "4tjhyp");
                    }


                });
            } else {
                myItemH14View.setChangeBtnShow(false);
            }
            contentLayout.addView(myItemH14View);
        }

        private void setH1H2moudleShow(final int j, String nid, LinearLayout contentLayout) {
            //如果是横图的1大两小模板
            MyItemH12View myItemH12View = new MyItemH12View(mContext);
            myItemH12View.setClassifyname(homeRecommendList.get(j).card_name);
            setBaseItemShow(j, 0, myItemH12View, homeRecommendList, nid);
            if (homeRecommendList.get(j).items.size() > 3 || homeRecommendList.get(j).items.size() == 3) {
                setHItemShow(j, 1, myItemH12View.getMyChildHView1(), homeRecommendList);
                setHItemShow(j, 2, myItemH12View.getMyChildHView2(), homeRecommendList);
            }
            if (homeRecommendList.get(j).hasMore == 1) {
                myItemH12View.setTopShow(true);
                myItemH12View.getMoreBtn().setOnClickListener(new NoDoubleClickListener() {
                    @Override
                    public void onNoDoubleClick(View v) {
                        MobclickAgent.onEvent(mContext, "4tjgd");
                        excuteEnter(homeRecommendList, j);
                    }
                });
            } else {
                myItemH12View.getMoreBtn().setVisibility(View.GONE);
            }
            if (homeRecommendList.get(j).hasChange == 1) {
                final int[] CLICKNUM2 = {1};
                myItemH12View.setChangeBtnShow(true);
                myItemH12View.getChangeBtn().setOnClickListener(new NoDoubleClickListener() {
                    @Override
                    public void onNoDoubleClick(View v) {
                        CLICKNUM2[0] += 2;
                        int s1 = CLICKNUM2[0];
                        if (s1 > homeRecommendList.get(j).items.size() - 1) {
                            s1 = 1;
                            CLICKNUM2[0] = 1;
                        }
                        int s2 = CLICKNUM2[0] + 1;
                        if (s2 > homeRecommendList.get(j).items.size() - 1) {
                            s2 = 1;
                            CLICKNUM2[0] = 0;
                        }

                        setHItemShow(j, s1, myItemH12View.getMyChildHView1(), homeRecommendList);
                        setHItemShow(j, s2, myItemH12View.getMyChildHView2(), homeRecommendList);
                        MobclickAgent.onEvent(mContext, "4tjhyp");
                    }


                });
            } else {
                myItemH12View.setChangeBtnShow(false);
            }
            contentLayout.addView(myItemH12View);
        }

        private void setH1V6MoudleShow(final int j, final String nid, LinearLayout contentLayout) {
            //如果是竖图的大小图模式,那么只有一种16的竖图模板
             MyItemV16View myItenm16View = new MyItemV16View(mContext);
            myItenm16View.setClassifyname(homeRecommendList.get(j).card_name);
            setBaseItemShow(j, 0, myItenm16View, homeRecommendList, nid);
            if (homeRecommendList.get(j).items.size() > 7 || homeRecommendList.get(j).items.size() == 7) {
                setVItemShow(j, 1, myItenm16View.getMyChildVView1(), homeRecommendList, nid);
                setVItemShow(j, 2, myItenm16View.getMyChildVView2(), homeRecommendList, nid);
                setVItemShow(j, 3, myItenm16View.getMyChildVView3(), homeRecommendList, nid);
                setVItemShow(j, 4, myItenm16View.getMyChildVView4(), homeRecommendList, nid);
                setVItemShow(j, 5, myItenm16View.getMyChildVView5(), homeRecommendList, nid);
                setVItemShow(j, 6, myItenm16View.getMyChildVView6(), homeRecommendList, nid);
            }
            if (homeRecommendList.get(j).hasMore == 1) {
                myItenm16View.setTopShow(true);
                myItenm16View.getMoreBtn().setOnClickListener(new NoDoubleClickListener() {
                    @Override
                    public void onNoDoubleClick(View v) {
                        MobclickAgent.onEvent(mContext, "4tjgd");
                        excuteEnter(homeRecommendList, j);
                    }
                });
            } else {
                myItenm16View.getMoreBtn().setVisibility(View.GONE);
            }
            if (homeRecommendList.get(j).hasChange == 1) {
                final int[] CLICKNUM2 = {1};
                myItenm16View.setChangeBtnShow(true);
                myItenm16View.getChangeBtn().setOnClickListener(new NoDoubleClickListener() {
                    @Override
                    public void onNoDoubleClick(View v) {
                        CLICKNUM2[0] += 6;
                        int s1 = CLICKNUM2[0];
                        if (s1 > homeRecommendList.get(j).items.size() - 1) {
                            s1 = 1;
                            CLICKNUM2[0] = 1;
                        }
                        int s2 = CLICKNUM2[0] + 1;
                        if (s2 > homeRecommendList.get(j).items.size() - 1) {
                            s2 = 1;
                            CLICKNUM2[0] = 0;
                        }
                        int s3 = CLICKNUM2[0] + 2;
                        if (s3 > homeRecommendList.get(j).items.size() - 1) {
                            s3 = 1;
                            CLICKNUM2[0] = -1;
                        }
                        int s4 = CLICKNUM2[0] + 3;
                        if (s4 > homeRecommendList.get(j).items.size() - 1) {
                            s4 = 1;
                            CLICKNUM2[0] = -2;
                        }
                        int s5 = CLICKNUM2[0] + 4;
                        if (s5 > homeRecommendList.get(j).items.size() - 1) {
                            s5 = 1;
                            CLICKNUM2[0] = -3;
                        }
                        int s6 = CLICKNUM2[0] + 5;
                        if (s6 > homeRecommendList.get(j).items.size() - 1) {
                            s6 = 1;
                            CLICKNUM2[0] = -4;
                        }

                        setVItemShow(j, s1, myItenm16View.getMyChildVView1(), homeRecommendList, nid);
                        setVItemShow(j, s2, myItenm16View.getMyChildVView2(), homeRecommendList, nid);
                        setVItemShow(j, s3, myItenm16View.getMyChildVView3(), homeRecommendList, nid);
                        setVItemShow(j, s4, myItenm16View.getMyChildVView4(), homeRecommendList, nid);
                        setVItemShow(j, s5, myItenm16View.getMyChildVView5(), homeRecommendList, nid);
                        setVItemShow(j, s6, myItenm16View.getMyChildVView6(), homeRecommendList, nid);
                        MobclickAgent.onEvent(mContext, "4tjhyp");
                    }


                });
            } else {
                myItenm16View.setChangeBtnShow(false);
            }

            contentLayout.addView(myItenm16View);
        }

        private void setListShow(int j,String nid,LinearLayout view) {
            switch (homeRecommendList.get(j).style){
                case "ver":
                    setVerMoudleShow(j,nid,view);
                    break;
                case "hor":
                    setHorMoudleShow(j,nid,view);
                    break;
                case "h1v6":
                    setH1V6MoudleShow(j,nid,view);
                    break;
                case "h1h2":
                    setH1H2moudleShow(j,nid,view);
                    break;
                case "h1h4":
                    setH1H4MoudleShow(j,nid,view);
                    break;
                case "h1h6":
                    setH1H6MoudleShow(j,nid,view);
                    break;
            }
        }

        private void setHorMoudleShow(final int j, String nid, LinearLayout view) {
             MyItemHView myItemHView = new MyItemHView(mContext);
            myItemHView.setClassifyname(homeRecommendList.get(j).card_name);
            if (homeRecommendList.get(j).items.size() > 4 || homeRecommendList.get(j).items.size() == 4) {
                setHItemShow(j, 0, myItemHView.getMyChildHView1(), homeRecommendList);
                setHItemShow(j, 1, myItemHView.getMyChildHView2(), homeRecommendList);
                setHItemShow(j, 2, myItemHView.getMyChildHView3(), homeRecommendList);
                setHItemShow(j, 3, myItemHView.getMyChildHView4(), homeRecommendList);
            }
            if (homeRecommendList.get(j).hasMore == 1) {

                myItemHView.setTopShow(true);
                myItemHView.getMoreBtn().setOnClickListener(new NoDoubleClickListener() {
                    @Override
                    public void onNoDoubleClick(View v) {
                        MobclickAgent.onEvent(mContext, "4tjgd");
                        excuteEnter(homeRecommendList, j);
                        setHItemShow(j, 0, myItemHView.getMyChildHView1(), homeRecommendList);
                        setHItemShow(j, 1, myItemHView.getMyChildHView2(), homeRecommendList);
                        setHItemShow(j, 2, myItemHView.getMyChildHView3(), homeRecommendList);
                        setHItemShow(j, 3, myItemHView.getMyChildHView4(), homeRecommendList);
                    }
                });
            } else {
                myItemHView.getMoreBtn().setVisibility(View.GONE);
            }
            if (homeRecommendList.get(j).hasChange == 1) {
                final int[] CLICKNUM = {0};
                myItemHView.setChangeBtnShow(true);
                myItemHView.getChangeBtn().setOnClickListener(new NoDoubleClickListener() {
                    @Override
                    public void onNoDoubleClick(View v) {
                        CLICKNUM[0] += 4;
                        int s1 = CLICKNUM[0];
                        if (s1>homeRecommendList.get(j).items.size()-1){
                            s1 = 0;
                            CLICKNUM[0] =0;
                        }
                        int s2 = CLICKNUM[0] + 1;
                        if (s2>homeRecommendList.get(j).items.size()-1){
                            s2 = 0;
                            CLICKNUM[0] =-1;
                        }
                        int s3 = CLICKNUM[0] + 2;
                        if (s3>homeRecommendList.get(j).items.size()-1){
                            s3 = 0;
                            CLICKNUM[0] =-2;
                        }
                        int s4 = CLICKNUM[0] + 3;
                        if (s4>homeRecommendList.get(j).items.size()-1){
                            s4 = 0;
                            CLICKNUM[0] =-3;
                        }
                        setHItemShow(j, s1, myItemHView.getMyChildHView1(), homeRecommendList);
                        setHItemShow(j, s2, myItemHView.getMyChildHView2(), homeRecommendList);
                        setHItemShow(j, s3, myItemHView.getMyChildHView3(), homeRecommendList);
                        setHItemShow(j, s4, myItemHView.getMyChildHView4(), homeRecommendList);
                        MobclickAgent.onEvent(mContext, "4tjhyp");
                    }
                });
            } else {
                myItemHView.setChangeBtnShow(false);
            }
            view.addView(myItemHView);
        }

        private void setVerMoudleShow(final int j, final String nid, LinearLayout view) {
            MyItemVView myItemVView = new MyItemVView(mContext);
            myItemVView.setClassifyname(homeRecommendList.get(j).card_name);
            if (homeRecommendList.get(j).items.size() > 6 || homeRecommendList.get(j).items.size() == 6) {
                setVItemShow(j, 0, myItemVView.getMyChildVView1(), homeRecommendList, nid);
                setVItemShow(j, 1, myItemVView.getMyChildVView2(), homeRecommendList, nid);
                setVItemShow(j, 2, myItemVView.getMyChildVView3(), homeRecommendList, nid);
                setVItemShow(j, 3, myItemVView.getMyChildVView4(), homeRecommendList, nid);
                setVItemShow(j, 4, myItemVView.getMyChildVView5(), homeRecommendList, nid);
                setVItemShow(j, 5, myItemVView.getMyChildVView6(), homeRecommendList, nid);
            }
            if (homeRecommendList.get(j).hasMore == 1) {
                myItemVView.setTopShow(true);
                myItemVView.getMoreBtn().setOnClickListener(new NoDoubleClickListener() {
                    @Override
                    public void onNoDoubleClick(View v) {
                        MobclickAgent.onEvent(mContext, "4tjgd");
                        excuteEnter(homeRecommendList, j);
                        setVItemShow(j, 0, myItemVView.getMyChildVView1(), homeRecommendList, nid);
                        setVItemShow(j, 1, myItemVView.getMyChildVView2(), homeRecommendList, nid);
                        setVItemShow(j, 2, myItemVView.getMyChildVView3(), homeRecommendList, nid);
                        setVItemShow(j, 3, myItemVView.getMyChildVView4(), homeRecommendList, nid);
                        setVItemShow(j, 4, myItemVView.getMyChildVView5(), homeRecommendList, nid);
                        setVItemShow(j, 5, myItemVView.getMyChildVView6(), homeRecommendList, nid);
                    }
                });
            } else {
                myItemVView.getMoreBtn().setVisibility(View.GONE);
            }
            if (homeRecommendList.get(j).hasChange == 1) {
                final int[] CLICKNUM2 = {0};
                myItemVView.setChangeBtnShow(true);
                myItemVView.getChangeBtn().setOnClickListener(new NoDoubleClickListener() {
                    @Override
                    public void onNoDoubleClick(View v) {
                        CLICKNUM2[0] += 6;
                        int s1 = CLICKNUM2[0];
                        if (s1>homeRecommendList.get(j).items.size()-1){
                            s1 = 0;
                            CLICKNUM2[0] =0;
                        }
                        int s2 = CLICKNUM2[0] + 1;
                        if (s2>homeRecommendList.get(j).items.size()-1){
                            s2 = 0;
                            CLICKNUM2[0] = -1;
                        }
                        int s3 = CLICKNUM2[0] + 2;
                        if (s3>homeRecommendList.get(j).items.size()-1){
                            s3 = 0;
                            CLICKNUM2[0] = -2;
                        }
                        int s4 = CLICKNUM2[0] + 3;
                        if (s4>homeRecommendList.get(j).items.size()-1){
                            s4 = 0;
                            CLICKNUM2[0] = -3;
                        }
                        int s5 = CLICKNUM2[0] + 4;
                        if (s5>homeRecommendList.get(j).items.size()-1){
                            s5 = 0;
                            CLICKNUM2[0] = -4;
                        }
                        int s6 = CLICKNUM2[0] + 5;
                        if (s6>homeRecommendList.get(j).items.size()-1){
                            s6 = 0;
                            CLICKNUM2[0] = -5;
                        }

                        setVItemShow(j, s1, myItemVView.getMyChildVView1(), homeRecommendList, nid);
                        setVItemShow(j, s2, myItemVView.getMyChildVView2(), homeRecommendList, nid);
                        setVItemShow(j, s3, myItemVView.getMyChildVView3(), homeRecommendList, nid);
                        setVItemShow(j, s4, myItemVView.getMyChildVView4(), homeRecommendList, nid);
                        setVItemShow(j, s5, myItemVView.getMyChildVView5(), homeRecommendList, nid);
                        setVItemShow(j, s6, myItemVView.getMyChildVView6(), homeRecommendList, nid);
                        MobclickAgent.onEvent(mContext, "4tjhyp");
                    }


                });
            } else {
                myItemVView.setChangeBtnShow(false);
            }

            view.addView(myItemVView);
        }

        /**
         * 设置单图模块的显示
         *
         * @param j
         */
        private void  setImageShow(final int j, LinearLayout view) {
            ImageView img = new ImageView(mContext);
            img.setMaxHeight(CommonUtil.screenHeight(mContext) * 90 / 360);
            img.setMinimumHeight(CommonUtil.screenHeight(mContext) * 90 / 360);
            GlideProxy.getInstance().loadHImage(mContext, homeRecommendList.get(j).items.get(0).img, img);
            img.setClickable(true);
            img.setScaleType(ImageView.ScaleType.FIT_XY);
            img.setOnClickListener(new NoDoubleClickListener() {
                @Override
                public void onNoDoubleClick(View v) {
                    setItemClick(j,0,homeRecommendList);
                }
            });
            view.addView(img);
            
        }
        /**
         * 设置单图模块的显示
         *
         * @param j
         */
        private void setADImageShow(final int j, LinearLayout view) {
            ImageView img = new ImageView(mContext);
            img.setMaxHeight(CommonUtil.screenHeight(mContext) * 57/ 360);
            img.setMinimumHeight(CommonUtil.screenHeight(mContext) * 57 / 360);
            GlideProxy.getInstance().loadHImage(mContext, homeRecommendList.get(j).items.get(0).img, img);
            img.setClickable(true);
            img.setScaleType(ImageView.ScaleType.FIT_XY);
            img.setOnClickListener(new NoDoubleClickListener() {
                @Override
                public void onNoDoubleClick(View v) {
                    setItemClick(j,0,homeRecommendList);
                }
            });
            view.addView(img);
        }

        private void setADBannerShow(int j,LinearLayout view) {
            MyADbannerView myADbannerView = new MyADbannerView(mContext);
            myADbannerView.setImageUris(homeRecommendList.get(j).items);
            view.addView(myADbannerView);
            
        }

        private void setSpecialShow(int j,LinearLayout view) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            View iView = inflater.inflate(R.layout.item_special_home,null);
            ImageView iv = (ImageView) iView.findViewById(R.id.img_iv);
            ViewGroup.LayoutParams params = iv.getLayoutParams();
            params.height = CommonUtil.screenWidth(mContext)/2;
            iv.setLayoutParams(params);
            GlideProxy.getInstance().loadHImage(mContext, homeRecommendList.get(j).items.get(0).img,iv);
            iv.setOnClickListener(new NoDoubleClickListener() {
                @Override
                public void onNoDoubleClick(View v) {
                    Intent intent = new Intent();
                    intent.setClass(mContext, SpecialActivity.class);
                    mContext.startActivity(intent);
                }
            });
            view.addView(iView);

        }
        private void setVItemShow(final int j, final int k, MyChildItemVEView myChildItemVEView, final List<HomeRecommend> homeRecommendList, final String id) {
            myChildItemVEView.setTitle(homeRecommendList.get(j).items.get(k).name);
            myChildItemVEView.setDes(homeRecommendList.get(j).items.get(k).brtxt);
            myChildItemVEView.setFocus(homeRecommendList.get(j).items.get(k).prompt);
            myChildItemVEView.setImgShow(homeRecommendList.get(j).items.get(k).img);
            if (k == 0 && type.equals("4")) {
                myChildItemVEView.setTopImgResource(R.mipmap.top1);
                myChildItemVEView.setTopImgShow(true);
            } else if(k == 1 && type.equals("4")){
                myChildItemVEView.setTopImgResource(R.mipmap.top2);
                myChildItemVEView.setTopImgShow(true);
            } else if(k == 2 && type.equals("4")){
                myChildItemVEView.setTopImgResource(R.mipmap.top3);
                myChildItemVEView.setTopImgShow(true);
            }else{
                myChildItemVEView.setTopImgShow(false);
            }
            myChildItemVEView.setOnClickListener(new NoDoubleClickListener() {
                @Override
                public void onNoDoubleClick(View v) {
                    MobclickAgent.onEvent(mContext, "4tjdt");
                    setItemClick(j, k, homeRecommendList);
                }
            });

        }
        private void setBaseItemShow(final int j, final int k,  MyItemBaseView myItemBaseView, final List<HomeRecommend> homeRecommendList, String nid) {
            myItemBaseView.getMyChildViewBaseView().setTitle(homeRecommendList.get(j).items.get(k).name);
            myItemBaseView.getMyChildViewBaseView().setDes(homeRecommendList.get(j).items.get(k).brtxt);
            myItemBaseView.getMyChildViewBaseView().setFocus(homeRecommendList.get(j).items.get(k).prompt);
            myItemBaseView.getMyChildViewBaseView().setImgShow(homeRecommendList.get(j).items.get(k).img);
            myItemBaseView.getMyChildViewBaseView ().setOnClickListener(new NoDoubleClickListener() {
                @Override
                public void onNoDoubleClick(View v) {
                    MobclickAgent.onEvent(mContext, "4tjdt");
                    setItemClick(j, k, homeRecommendList);
                }
            });
        }
        private void setHItemShow(final int j, final int k,  MyChildItemHView myChildItemHView, final List<HomeRecommend> homeRecommendList) {
            myChildItemHView.setTitle(homeRecommendList.get(j).items.get(k).name);
            myChildItemHView.setDes(homeRecommendList.get(j).items.get(k).brtxt);
            myChildItemHView.setFocus(homeRecommendList.get(j).items.get(k).prompt);
            myChildItemHView.setImgShow(homeRecommendList.get(j).items.get(k).img);
            if (k == 2 && type.equals("4")) {
                myChildItemHView.setTopImgShow(true);
                myChildItemHView.setTopImgResource(R.mipmap.top3);
            } else if (k == 0 && type.equals("4")){
                myChildItemHView.setTopImgShow(true);
                myChildItemHView.setTopImgResource(R.mipmap.top1);
            } else if (k == 1 && type.equals("4")){
                myChildItemHView.setTopImgShow(true);
                myChildItemHView.setTopImgResource(R.mipmap.top2);
            }else{
                myChildItemHView.setTopImgShow(false);
            }
            myChildItemHView.setOnClickListener(new NoDoubleClickListener() {
                @Override
                public void onNoDoubleClick(View v) {
                    MobclickAgent.onEvent(mContext, "4tjdt");
                    setItemClick(j, k, homeRecommendList);
                }
            });
        }

        private void excuteEnter(List<HomeRecommend> homeRecommendList, int j) {
            switch (homeRecommendList.get(j).action) {
                case "UPGC"://跳转到自媒体
                      BusProvider.getInstance().post(AppGlobalConsts.EventType.TAG_ENTER, "自媒体"+","+homeRecommendList.get(j).card_id);
                    break;
                case "Live"://跳转到直播页
                    BusProvider.getInstance().post(AppGlobalConsts.EventType.TAG_ENTER, "直播");
                    break;
                case "APP"://跳转到星秀直播间
                    BusProvider.getInstance().post(AppGlobalConsts.EventType.TAG_ENTER, "美女");
                    break;
                case "Topic"://跳转到长视频
                    Intent intent2 = new Intent();
                    intent2.setClass(mContext, ProgramListActivity.class);
                    if (homeRecommendList.get(j).card_id.contains(",")){
                        topicId = (homeRecommendList.get(j).card_id.split(","))[0];
                        card_id =(homeRecommendList.get(j).card_id.split(","))[1];
                    }else{
                        card_id = homeRecommendList.get(j).card_id;
                    }
                    intent2.putExtra("navID", card_id);
                    intent2.putExtra("idStr", topicId);
                    mContext.startActivity(intent2);
                    break;
                case "Rank"://跳转排行榜
                case "NavHome"://跳转推荐列表
              /*  String cardid = homeRecommendList.get(j).card_id;
                BusProvider.getInstance().post(AppGlobalConsts.EventType.TAG_CARDID,cardid);*/
                    Intent intent3 = new Intent();
                    intent3.setClass(mContext, ProgramListActivity.class);
                    intent3.putExtra("idStr", homeRecommendList.get(j).card_id);
                    mContext.startActivity(intent3);
                    break;
                case "AD":
                    break;
                case "Special":
                    String cardid = homeRecommendList.get(j).card_id;
                    BusProvider.getInstance().post(AppGlobalConsts.EventType.TAG_CARDID,cardid);
                    break;
                case "Game"://跳转到游戏
                    Intent intent = new Intent(mContext, Game37WanActivity.class);
                    mContext.startActivity(intent);
                    break;
            }
        }
        public void setItemClick(int j, int i, List<HomeRecommend> homeRecommendList) {
            switch (homeRecommendList.get(j).items.get(i).action) {
                case "live":
                    //跳转直播
                    BusProvider.getInstance().post(AppGlobalConsts.EventType.TAG_ENTER, "直播");
                    BusProvider.getInstance().post(AppGlobalConsts.EventType.TAG_B, homeRecommendList.get(j).items.get(i).id);
                    ShareElement.channelId = homeRecommendList.get(j).items.get(i).id;
                    break;

                case "series":
                    //跳转长视频
                    Intent intent = new Intent(mContext, ProgramDetailActivity.class);
                    intent.putExtra("idStr", homeRecommendList.get(j).items.get(i).id);
                    intent.putExtra("cpidStr", "");
                    mContext.startActivity(intent);
                    break;

                case "special":
                    Intent intent4 = new Intent();
                    //这里对id进行处理,l长视频,s短视频
                    String[] ids = homeRecommendList.get(j).items.get(i).id.trim().split(",");
                    intent4.putExtra("idStr",ids[0]);
                    if(ids[1].equals("l")){
                        //长视频专题
                        intent4.setClass(mContext, SpecialActivity.class);
                        mContext.startActivity(intent4);
                    }else{
                        //短视频专题
                        intent4.setClass(mContext, SpecialDetailActivity.class);
                        mContext.startActivity(intent4);
                    }
                    break;

                case "app"://跳转到星秀直播间
                    BusProvider.getInstance().post(AppGlobalConsts.EventType.TAG_ENTER, "美女");
                    break;
                case "ad"://web广告
                    String[] newID =homeRecommendList.get(j).items.get(i).id.trim().split(",");
                    switch (newID[0]){
                        case "web":
                            Intent intent5 = new Intent(mContext, WeBADActivity.class);
                            intent5.putExtra("url",newID[1]);
                            mContext.startActivity(intent5);
                            break;
                        case "h5":
                            Intent intent6 = new Intent(mContext, Game37WanActivity.class);
                            intent6.putExtra("url",newID[1]);
                            mContext.startActivity(intent6);
                            break;
                        case "duiba":
                            BusProvider.getInstance().post(AppGlobalConsts.EventType.TAG_ENTERDUIBA,newID[1]);
                            AppGlobalConsts.ISLOGINDUIBA = true;
                            break;
                    }
                    break;
                case "upgc"://热点
                    String[] hotID =homeRecommendList.get(j).items.get(i).id.trim().split(",");
                    Intent intent7 = new Intent(mContext, MediaDetailActivity.class);
                    intent7.putExtra("vid", hotID[0]);
                    if (hotID[1].equals("s")){
                        hotID[1]="-1";
                    }
                    if (hotID[2].equals("v")){
                        hotID[2]="-1";
                    }
                    intent7.putExtra("sdkType", hotID[1]);
                    intent7.putExtra("videoType", hotID[2]);
                    mContext.startActivity(intent7);
                    break;
            }
        }
    }
}