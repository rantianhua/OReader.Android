package cn.bandu.oreader.activity;

import android.app.Activity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import cn.bandu.oreader.R;

/**
 * Created by yangmingfu on 14/12/29.
 */
@EActivity(R.layout.alert_dialog)
public class AlertDialogActivity extends Activity {
    private int position;

    @ViewById
    TextView mTitle;
    @ViewById
    Button mButton;

    @AfterViews
    public void afterViews() {
        //提示内容
        String msg = getIntent().getStringExtra("msg");
        //提示标题
        String title = getIntent().getStringExtra("title");
//		voicePath = getIntent().getStringExtra("voicePath");
        position = getIntent().getIntExtra("position", -1);
        //是否显示取消标题
        boolean isCanceTitle=getIntent().getBooleanExtra("titleIsCancel", false);
        //是否显示取消按钮
        boolean isCanceShow = getIntent().getBooleanExtra("cancel", false);
        if(msg != null)
            ((TextView)findViewById(R.id.alert_message)).setText(msg);
        if(title != null)
            mTitle.setText(title);
        if(isCanceTitle){
            mTitle.setVisibility(View.GONE);
        }
        if(isCanceShow) {
            mButton.setVisibility(View.VISIBLE);
        }
    }

    @Click
    public void chatOk(View view){
        if(position != -1)
            ChatActivity_.resendPos = position;
        setResult(RESULT_OK);
        finish();
    }
    @Click
    public void mButton(View view){
        finish();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event){
        finish();
        return true;
    }

}
