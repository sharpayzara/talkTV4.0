package com.sumavision.talktv2.ui.widget;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.sumavision.talktv2.R;

/**
 * Created by zjx on 2016/10/9.
 */
public class NetChangeDialog extends Dialog {

    private Context mContext;
    private Button btn_exit_cancel,btn_exit_ok;

    public NetChangeDialog(Context context, int themeResId) {
        super(context, themeResId);
        mContext = context;
        initLayout();
        initView();
    }

    /**
     * 给dialog定位和设置大小
     */
    private void initLayout() {
        Window dialogWindow = getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        dialogWindow.setGravity(Gravity.CENTER);
        dialogWindow.setAttributes(lp);
    }


    private void initView() {
        setContentView(R.layout.dialog_netchange);
        btn_exit_cancel = (Button) findViewById(R.id.btn_exit_cancel);
        btn_exit_ok = (Button) findViewById(R.id.btn_exit_ok);
    }

    public Button getOkButton(){
        return btn_exit_ok;
    }
    public Button getCancelButton(){
        return btn_exit_cancel;
    }

}
