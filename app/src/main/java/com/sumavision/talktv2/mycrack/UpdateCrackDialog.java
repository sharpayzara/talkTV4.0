package com.sumavision.talktv2.mycrack;

import android.app.Dialog;
import android.content.Context;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.sumavision.talktv2.R;


/**
 * 更新破解jar包和so文件时使用
 *
 * @author zhangyisu
 */
public class UpdateCrackDialog extends Dialog {
    TextView titleTv;
    private Context mctx;
    String noticeStr;
    public UpdateCrackDialog(Context mctx, int theme) {
        super(mctx, theme);
        this.mctx = mctx;
        setContentView(R.layout.update_crack_dialog);
        titleTv = (TextView) findViewById(R.id.title);
        noticeStr = "正在更新播放组件\n请稍候……";
        titleTv.setText(noticeStr);
        Window win = getWindow();
        WindowManager.LayoutParams params = win.getAttributes();
        params.y -= 50; //向上移动一些，遮挡加载圈
        win.setAttributes(params);
    }
    public void onProgressUpdate(String str){
        titleTv.setText(noticeStr+str+"%");
    }

}
