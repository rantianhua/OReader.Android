package cn.bandu.oreader.activity;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.PowerManager;
import android.provider.MediaStore;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.easemob.EMCallBack;
import com.easemob.EMError;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMMessage;
import com.easemob.chat.ImageMessageBody;
import com.easemob.chat.NormalFileMessageBody;
import com.easemob.chat.NotificationCompat;
import com.easemob.chat.TextMessageBody;
import com.easemob.chat.VoiceMessageBody;
import com.easemob.util.EasyUtils;
import com.easemob.util.PathUtil;
import com.easemob.util.VoiceRecorder;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.FocusChange;
import org.androidannotations.annotations.TextChange;
import org.androidannotations.annotations.Touch;
import org.androidannotations.annotations.ViewById;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import cn.bandu.oreader.OReaderApplication;
import cn.bandu.oreader.R;
import cn.bandu.oreader.dao.User;
import cn.bandu.oreader.tools.CommonUtil;
import cn.huanxin.adapter.ExpressionAdapter;
import cn.huanxin.adapter.ExpressionPagerAdapter;
import cn.huanxin.adapter.MessageAdapter;
import cn.huanxin.adapter.VoicePlayClickListener;
import cn.huanxin.tools.CommonUtils;
import cn.huanxin.tools.SmileUtils;
import cn.huanxin.view.ExpandGridView;

/**
 * Created by yangmingfu on 14/12/23.
 */
//@Fullscreen
@EActivity(R.layout.activity_chat)
public class ChatActivity  extends Activity {
    private final static String TAG = ChatActivity.class.getSimpleName();
    public static final String COPY_IMAGE = "EASEMOBIMG";

    private static final int REQUEST_CODE_EMPTY_HISTORY = 2;
    public static final int REQUEST_CODE_CONTEXT_MENU = 3;
    public static final int REQUEST_CODE_COPY_AND_PASTE = 11;
    public static final int CHATTYPE_SINGLE = 1;
    public static final int CHATTYPE_GROUP = 2;

    public static final int REQUEST_CODE_CAMERA = 18;
    public static final int REQUEST_CODE_LOCAL = 19;
    public static final int REQUEST_CODE_TEXT = 5;
    public static final int REQUEST_CODE_VOICE = 6;
    public static final int REQUEST_CODE_PICTURE = 7;
    public static final int REQUEST_CODE_LOCATION = 8;
    public static final int REQUEST_CODE_NET_DISK = 9;
    public static final int REQUEST_CODE_FILE = 10;
    public static final int REQUEST_CODE_SELECT_FILE = 24;

    @ViewById
    ProgressBar pb_load_more;
    @ViewById
    ListView list;
    @ViewById
    EditText et_sendmessage;
    @ViewById
    View edittext_layout;
    @ViewById
    ImageView micImage;
    @ViewById
    ViewPager expressionViewpager;
    @ViewById
    View btn_set_mode_keyboard;
    @ViewById
    View btn_set_mode_voice;
    @ViewById
    View btn_press_to_speak;
    @ViewById
    ImageView iv_emoticons_checked;
    @ViewById
    ImageView iv_emoticons_normal;
    @ViewById
    LinearLayout ll_face_container;
    @ViewById
    LinearLayout more;
    @ViewById
    LinearLayout ll_btn_container;
    @ViewById
    Button btn_more;
    @ViewById
    Button btn_send;
    @ViewById
    RelativeLayout recording_container;
    @ViewById
    TextView recording_hint;



    private Drawable[] micImages;
    private List<String> reslist;
    private InputMethodManager manager;

    //当前登录用户
    private User user;
    //对方
    private String toUserId;

    private File cameraFile;
    private MessageAdapter adapter;
    private EMConversation conversation;
    public static int resendPos;
    private int chatType = CHATTYPE_SINGLE;
    private NewMessageBroadcastReceiver receiver;

    public String playMsgId;
    private VoiceRecorder voiceRecorder;
    private boolean haveMoreData = true;
    private boolean isloading;
    private final int pagesize = 20;

    private Handler micImageHandler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            // 切换msg切换图片
            micImage.setImageDrawable(micImages[msg.what]);
        }
    };


    @AfterViews
    public void afterViews() {
        Intent intent = getIntent();
        toUserId = intent.getStringExtra("userId");
        initVar();
        initView();
        setUpView();
    }

    private void initVar() {
        manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        user = CommonUtil.getUserInfo(this);
        if (user == null) {
            //TODO 弹出登录
        }
    }

    private void initView() {
        iv_emoticons_normal.setVisibility(View.VISIBLE);
        iv_emoticons_checked.setVisibility(View.INVISIBLE);

        // 动画资源文件,用于录制语音时
        micImages = new Drawable[] { getResources().getDrawable(R.drawable.chat_record_animate_01),
                getResources().getDrawable(R.drawable.chat_record_animate_02), getResources().getDrawable(R.drawable.chat_record_animate_03),
                getResources().getDrawable(R.drawable.chat_record_animate_04), getResources().getDrawable(R.drawable.chat_record_animate_05),
                getResources().getDrawable(R.drawable.chat_record_animate_06), getResources().getDrawable(R.drawable.chat_record_animate_07),
                getResources().getDrawable(R.drawable.chat_record_animate_08), getResources().getDrawable(R.drawable.chat_record_animate_09),
                getResources().getDrawable(R.drawable.chat_record_animate_10), getResources().getDrawable(R.drawable.chat_record_animate_11),
                getResources().getDrawable(R.drawable.chat_record_animate_12), getResources().getDrawable(R.drawable.chat_record_animate_13),
                getResources().getDrawable(R.drawable.chat_record_animate_14), };
        // 表情list
        reslist = getExpressionRes(35);
        // 初始化表情viewpager
        List<View> views = new ArrayList<View>();
        View gv1 = getGridChildView(1);
        View gv2 = getGridChildView(2);
        views.add(gv1);
        views.add(gv2);
        expressionViewpager.setAdapter(new ExpressionPagerAdapter(views));


        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        edittext_layout.requestFocus();
        edittext_layout.setBackgroundResource(R.drawable.chat_input_bar_bg_normal);

        voiceRecorder = new VoiceRecorder(micImageHandler);
        btn_press_to_speak.setOnTouchListener(new PressToSpeakListen());
    }

    private void setUpView() {
        manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        wakeLock = ((PowerManager) getSystemService(Context.POWER_SERVICE)).newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "demo");
        ((TextView) findViewById(R.id.name)).setText(toUserId);
        conversation = EMChatManager.getInstance().getConversation(toUserId);
        Log.e("conversation.count=", String.valueOf(conversation.getMsgCount()));
        // 把此会话的未读数置为0
        conversation.resetUnreadMsgCount();
        adapter = new MessageAdapter(this, toUserId);
        // 显示消息
        list.setAdapter(adapter);
        list.setOnScrollListener(new ListScrollListener());
        int count = list.getCount();
        if (count > 0) {
            list.setSelection(count - 1);
        }

        list.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                hideKeyboard();
                more.setVisibility(View.GONE);
                iv_emoticons_normal.setVisibility(View.VISIBLE);
                iv_emoticons_checked.setVisibility(View.INVISIBLE);
                ll_face_container.setVisibility(View.GONE);
                ll_btn_container.setVisibility(View.GONE);
                return false;
            }
        });
        // 注册接收消息广播
        receiver = new NewMessageBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter(EMChatManager.getInstance().getNewMessageBroadcastAction());
        // 设置广播的优先级别大于Mainacitivity,这样如果消息来的时候正好在chat页面，直接显示消息，而不是提示消息未读
        intentFilter.setPriority(5);
        registerReceiver(receiver, intentFilter);

        // 注册一个ack回执消息的BroadcastReceiver
        IntentFilter ackMessageIntentFilter = new IntentFilter(EMChatManager.getInstance().getAckMessageBroadcastAction());
        ackMessageIntentFilter.setPriority(5);
        registerReceiver(ackMessageReceiver, ackMessageIntentFilter);

        // 注册一个消息送达的BroadcastReceiver
        IntentFilter deliveryAckMessageIntentFilter = new IntentFilter(EMChatManager.getInstance().getDeliveryAckMessageBroadcastAction());
        deliveryAckMessageIntentFilter.setPriority(5);
        registerReceiver(deliveryAckMessageReceiver, deliveryAckMessageIntentFilter);
    }

    /**
     * 发送文本信息
     * @param content
     */
    private void sendText(String content) {
        if (content.length() > 0) {
            EMMessage message = EMMessage.createSendMessage(EMMessage.Type.TXT);
            TextMessageBody txtBody = new TextMessageBody(content);
            // 设置消息body
            message.addBody(txtBody);
            // 设置要发给谁,用户username
            message.setReceipt(toUserId);
            // 把messgage加到conversation中
            conversation.addMessage(message);
            // 通知adapter有消息变动，adapter会根据加入的这条message显示消息和调用sdk的发送方法
            adapter.notifyDataSetChanged();
            list.setSelection(list.getCount() - 1);
            et_sendmessage.setText("");
            setResult(RESULT_OK);
        }
    }
    /**
    /**
     * 发送图片
     *
     * @param filePath
     */
    private void sendPicture(final String filePath) {
        // create and add image message in view
        final EMMessage message = EMMessage.createSendMessage(EMMessage.Type.IMAGE);
        message.setReceipt(toUserId);
        ImageMessageBody body = new ImageMessageBody(new File(filePath));
        // 默认超过100k的图片会压缩后发给对方，可以设置成发送原图
        // body.setSendOriginalImage(true);
        message.addBody(body);
        conversation.addMessage(message);

        list.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        list.setSelection(list.getCount() - 1);
        setResult(RESULT_OK);
        EMChatManager.getInstance().sendMessage(message, new EMCallBack(){
            @Override
            public void onSuccess() {
            }

            @Override
            public void onError(int i, String s) {
            }

            @Override
            public void onProgress(int i, String s) {
            }
        });
    }

    /**
     * 发送语音
     *
     * @param filePath
     * @param fileName
     * @param length
     * @param isResend
     */
    private void sendVoice(String filePath, String fileName, String length, boolean isResend) {
        if (!(new File(filePath).exists())) {
            return;
        }
        try {
            final EMMessage message = EMMessage.createSendMessage(EMMessage.Type.VOICE);
            // 如果是群聊，设置chattype,默认是单聊
            message.setReceipt(toUserId);
            int len = Integer.parseInt(length);
            VoiceMessageBody body = new VoiceMessageBody(new File(filePath), len);
            message.addBody(body);

            conversation.addMessage(message);
            adapter.notifyDataSetChanged();
            list.setSelection(list.getCount() - 1);
            setResult(RESULT_OK);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * 根据图库图片uri发送图片
     *
     * @param selectedImage
     */
    private void sendPicByUri(Uri selectedImage) {
        // String[] filePathColumn = { MediaStore.Images.Media.DATA };
        Cursor cursor = getContentResolver().query(selectedImage, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex("_data");
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
            cursor = null;

            if (picturePath == null || picturePath.equals("null")) {
                Toast toast = Toast.makeText(this, "找不到图片", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                return;
            }
            sendPicture(picturePath);
        } else {
            File file = new File(selectedImage.getPath());
            if (!file.exists()) {
                Toast toast = Toast.makeText(this, "找不到图片", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                return;

            }
            sendPicture(file.getAbsolutePath());
        }

    }

    /**
     * 发送文件
     *
     * @param uri
     */
    private void sendFile(Uri uri) {
        String filePath = null;
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            String[] projection = { "_data" };
            Cursor cursor = null;

            try {
                cursor = getContentResolver().query(uri, projection, null, null, null);
                int column_index = cursor.getColumnIndexOrThrow("_data");
                if (cursor.moveToFirst()) {
                    filePath = cursor.getString(column_index);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            filePath = uri.getPath();
        }
        File file = new File(filePath);
        if (file == null || !file.exists()) {
            Toast.makeText(getApplicationContext(), "文件不存在", Toast.LENGTH_SHORT).show();
            return;
        }
        if (file.length() > 10 * 1024 * 1024) {
            Toast.makeText(getApplicationContext(), "文件不能大于10M", Toast.LENGTH_SHORT).show();
            return;
        }

        // 创建一个文件消息
        EMMessage message = EMMessage.createSendMessage(EMMessage.Type.FILE);
        message.setReceipt(toUserId);
        // add message body
        NormalFileMessageBody body = new NormalFileMessageBody(new File(filePath));
        message.addBody(body);
        conversation.addMessage(message);
        list.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        list.setSelection(list.getCount() - 1);
        setResult(RESULT_OK);
    }

    /**
     * 重发消息
     */
    private void resendMessage() {
        EMMessage msg = null;
        msg = conversation.getMessage(resendPos);
        // msg.setBackSend(true);
        msg.status = EMMessage.Status.CREATE;
        adapter.notifyDataSetChanged();
        list.setSelection(resendPos);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) { // 清空消息
            if (requestCode == REQUEST_CODE_EMPTY_HISTORY) {
                // 清空会话
                boolean res = EMChatManager.getInstance().clearConversation(toUserId);
                adapter.notifyDataSetChanged();
            } else if (requestCode == REQUEST_CODE_CAMERA) { // 发送照片
                if (cameraFile != null && cameraFile.exists()) {
                    Log.e("cameraFile.getAbsolutePath()", cameraFile.getAbsolutePath());
                    sendPicture(cameraFile.getAbsolutePath());
                }
            } else if (requestCode == REQUEST_CODE_LOCAL) { // 发送本地图片
                if (data != null) {
                    Uri selectedImage = data.getData();
                    if (selectedImage != null) {
                        sendPicByUri(selectedImage);
                    }
                }
            } else if (requestCode == REQUEST_CODE_SELECT_FILE) { // 发送选择的文件
                if (data != null) {
                    Uri uri = data.getData();
                    if (uri != null) {
                        sendFile(uri);
                    }
                }

            } else if (requestCode == REQUEST_CODE_TEXT || requestCode == REQUEST_CODE_VOICE
                    || requestCode == REQUEST_CODE_PICTURE || requestCode == REQUEST_CODE_FILE) {
                resendMessage();
            } else if (conversation.getMsgCount() > 0) {
                adapter.notifyDataSetChanged();
                setResult(RESULT_OK);
            }
        }
    }

    @Touch
    public void list() {
        hideKeyboard();
        more.setVisibility(View.GONE);
        iv_emoticons_normal.setVisibility(View.VISIBLE);
        iv_emoticons_checked.setVisibility(View.INVISIBLE);
        ll_face_container.setVisibility(View.GONE);
        ll_btn_container.setVisibility(View.GONE);
    }

    @FocusChange(R.id.et_sendmessage)
    public void editTextChangeFocus() {
        if (et_sendmessage.hasFocus()) {
            edittext_layout.setBackgroundResource(R.drawable.chat_input_bar_bg_active);
        } else {
            edittext_layout.setBackgroundResource(R.drawable.chat_input_bar_bg_normal);
        }
    }

    @Click(R.id.et_sendmessage)
    public void editTextClick(){
        Log.e("click", "click");
        edittext_layout.setBackgroundResource(R.drawable.chat_input_bar_bg_active);
        more.setVisibility(View.GONE);
        iv_emoticons_normal.setVisibility(View.VISIBLE);
        iv_emoticons_checked.setVisibility(View.INVISIBLE);
        ll_face_container.setVisibility(View.GONE);
        ll_btn_container.setVisibility(View.GONE);
    }

    @TextChange(R.id.et_sendmessage)
    public void editTextChnage() {
        String s = et_sendmessage.getText().toString();
        if (!TextUtils.isEmpty(s)) {
            btn_more.setVisibility(View.GONE);
            btn_send.setVisibility(View.VISIBLE);
        } else {
            btn_more.setVisibility(View.VISIBLE);
            btn_send.setVisibility(View.GONE);
        }
    }
    @Click
    public void btn_send() {
        String s = et_sendmessage.getText().toString();
        if (!TextUtils.isEmpty(s)) {
            //TODO 提示
        }
        sendText(s);
        et_sendmessage.setText("");
    }

    @Click
    public void iv_emoticons_checked() {
        iv_emoticons_normal.setVisibility(View.VISIBLE);
        iv_emoticons_checked.setVisibility(View.INVISIBLE);
        ll_btn_container.setVisibility(View.VISIBLE);
        ll_face_container.setVisibility(View.GONE);
        more.setVisibility(View.GONE);
    }

    @Click
    public void iv_emoticons_normal() {
        more.setVisibility(View.VISIBLE);
        iv_emoticons_normal.setVisibility(View.INVISIBLE);
        iv_emoticons_checked.setVisibility(View.VISIBLE);
        ll_btn_container.setVisibility(View.GONE);
        ll_face_container.setVisibility(View.VISIBLE);
        hideKeyboard();
    }

    @Click
    public void btn_set_mode_keyboard(View view) {
        edittext_layout.setVisibility(View.VISIBLE);
        more.setVisibility(View.GONE);
        view.setVisibility(View.GONE);
        btn_set_mode_voice.setVisibility(View.VISIBLE);
        et_sendmessage.requestFocus();
        btn_press_to_speak.setVisibility(View.GONE);
        if (TextUtils.isEmpty(et_sendmessage.getText())) {
            btn_more.setVisibility(View.VISIBLE);
            btn_send.setVisibility(View.GONE);
        } else {
            btn_more.setVisibility(View.GONE);
            btn_send.setVisibility(View.VISIBLE);
        }
    }

    @Click
    public void btn_set_mode_voice(View view) {
        hideKeyboard();
        edittext_layout.setVisibility(View.GONE);
        more.setVisibility(View.GONE);
        view.setVisibility(View.GONE);
        btn_set_mode_keyboard.setVisibility(View.VISIBLE);
        btn_send.setVisibility(View.GONE);
        btn_more.setVisibility(View.VISIBLE);
        btn_press_to_speak.setVisibility(View.VISIBLE);
        iv_emoticons_normal.setVisibility(View.VISIBLE);
        iv_emoticons_checked.setVisibility(View.INVISIBLE);
        ll_btn_container.setVisibility(View.VISIBLE);
        ll_face_container.setVisibility(View.GONE);
    }

    @Click
    public void back() {
        finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Click
    public void delete_all() {
        startActivityForResult(
                new Intent(this, AlertDialogActivity_.class).putExtra("titleIsCancel", true).putExtra("msg", "是否清空所有聊天记录").putExtra("cancel", true),
                REQUEST_CODE_EMPTY_HISTORY);
    }

    @Click
    public void btn_more() {
        if (more.getVisibility() == View.GONE) {
            hideKeyboard();
            more.setVisibility(View.VISIBLE);
            ll_btn_container.setVisibility(View.VISIBLE);
            ll_face_container.setVisibility(View.GONE);
        } else {
            if (ll_face_container.getVisibility() == View.VISIBLE) {
                ll_face_container.setVisibility(View.GONE);
                ll_btn_container.setVisibility(View.VISIBLE);
                iv_emoticons_normal.setVisibility(View.VISIBLE);
                iv_emoticons_checked.setVisibility(View.INVISIBLE);
            } else {
                more.setVisibility(View.GONE);
            }
        }
    }

    @Click //点击照相图标
    public void btn_take_picture() {
        if (!CommonUtils.isExitsSdcard()) {
            Toast.makeText(getApplicationContext(), "SD卡不存在，不能拍照", Toast.LENGTH_SHORT).show();
            return;
        }

        cameraFile = new File(PathUtil.getInstance().getImagePath(), OReaderApplication.getInstance().getHxHelper().getHXId() + System.currentTimeMillis() + ".jpg");
        cameraFile.getParentFile().mkdirs();
        Log.e("cameraFile", String.valueOf(cameraFile));
        startActivityForResult(new Intent(MediaStore.ACTION_IMAGE_CAPTURE).putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(cameraFile)),
                REQUEST_CODE_CAMERA);
    }

    @Click  // 点击图片图标
    public void btn_picture() {
        Intent intent;
        if (Build.VERSION.SDK_INT < 19) {
            intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");

        } else {
            intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        }
        startActivityForResult(intent, REQUEST_CODE_LOCAL);
    }

    @Click
    public void btn_file() {
        Intent intent = null;
        if (Build.VERSION.SDK_INT < 19) {
            intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("*/*");
            intent.addCategory(Intent.CATEGORY_OPENABLE);
        } else {
            intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        }
        startActivityForResult(intent, REQUEST_CODE_SELECT_FILE);
    }

    /**
     * 覆盖手机返回键
     */
    @Override
    public void onBackPressed() {
        if (more.getVisibility() == View.VISIBLE) {
            more.setVisibility(View.GONE);
            iv_emoticons_normal.setVisibility(View.VISIBLE);
            iv_emoticons_checked.setVisibility(View.INVISIBLE);
        } else {
            super.onBackPressed();
        }
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    /**
     * 初始化表情数组
     * @param getSum
     * @return
     */
    private List<String> getExpressionRes(int getSum) {
        List<String> reslist = new ArrayList<String>();
        for (int x = 1; x <= getSum; x++) {
            String filename = "chat_ee_" + x;
            reslist.add(filename);
        }
        return reslist;
    }

    /**
     * 获取表情的gridview的子view
     *
     * @param i
     * @return
     */
    private View getGridChildView(int i) {
        View view = View.inflate(this, R.layout.expression_gridview, null);
        ExpandGridView gv = (ExpandGridView) view.findViewById(R.id.gridview);
        List<String> list = new ArrayList<String>();
        if (i == 1) {
            List<String> list1 = reslist.subList(0, 20);
            list.addAll(list1);
        } else if (i == 2) {
            list.addAll(reslist.subList(20, reslist.size()));
        }
        list.add("chat_delete_expression");
        final ExpressionAdapter expressionAdapter = new ExpressionAdapter(this, 1, list);
        gv.setAdapter(expressionAdapter);
        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String filename = expressionAdapter.getItem(position);
                Log.e("btn_set_mode_keyboard.getVisibility()", String.valueOf(btn_set_mode_keyboard.getVisibility()));
                try {
                    if (btn_set_mode_keyboard.getVisibility() != View.VISIBLE) {
                        if (filename != "chat_delete_expression") {
                            Class clz = Class.forName("cn.huanxin.tools.SmileUtils");
                            Field field = clz.getField(filename);
                            et_sendmessage.append(SmileUtils.getSmiledText(ChatActivity.this, (String) field.get(null)));
                            Log.e("filename", filename);
                        } else {
                            if (!TextUtils.isEmpty(et_sendmessage.getText())) {

                                int selectionStart = et_sendmessage.getSelectionStart();
                                if (selectionStart > 0) {
                                    String body = et_sendmessage.getText().toString();
                                    String tempStr = body.substring(0, selectionStart);
                                    int i = tempStr.lastIndexOf("[");
                                    if (i != -1) {
                                        CharSequence cs = tempStr.substring(i, selectionStart);
                                        if (SmileUtils.containsKey(cs.toString()))
                                            et_sendmessage.getEditableText().delete(i, selectionStart);
                                        else
                                            et_sendmessage.getEditableText().delete(selectionStart - 1, selectionStart);
                                    } else {
                                        et_sendmessage.getEditableText().delete(selectionStart - 1, selectionStart);
                                    }
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    Log.e("catch message", e.getMessage());
                }
            }
        });
        return view;
    }

    /**
     * 隐藏软键盘
     */
    private void hideKeyboard() {
        if (getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
            if (getCurrentFocus() != null)
                manager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }
    /**
     * 消息广播接收者
     *
     */
    private class NewMessageBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            // 记得把广播给终结掉
            abortBroadcast();

            String username = intent.getStringExtra("from");
            String msgid = intent.getStringExtra("msgid");
            // 收到这个广播的时候，message已经在db和内存里了，可以通过id获取mesage对象
            EMMessage message = EMChatManager.getInstance().getMessage(msgid);
            // 如果是群聊消息，获取到group id
            if (message.getChatType() == EMMessage.ChatType.GroupChat) {
                username = message.getTo();
            }
            if (!username.equals(toUserId)) {
                // 消息不是发给当前会话，return
                notifyNewMessage(message);
                return;
            }
            // conversation =
            // EMChatManager.getInstance().getConversation(toChatUsername);
            // 通知adapter有新消息，更新ui
            adapter.notifyDataSetChanged();
            list.setSelection(list.getCount() - 1);
        }
    }
    /**
     * 当应用在前台时，如果当前消息不是属于当前会话，在状态栏提示一下
     * 如果不需要，注释掉即可
     * @param message
     */
    protected void notifyNewMessage(EMMessage message) {
        //如果是设置了不提醒只显示数目的群组(这个是app里保存这个数据的，demo里不做判断)
        //以及设置了setShowNotificationInbackgroup:false(设为false后，后台时sdk也发送广播)
        if(!EasyUtils.isAppRunningForeground(this)){
            return;
        }

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(getApplicationInfo().icon)
                .setWhen(System.currentTimeMillis()).setAutoCancel(true);

        String ticker = CommonUtils.getMessageDigest(message, this);
        if(message.getType() == EMMessage.Type.TXT)
            ticker = ticker.replaceAll("\\[.{2,3}\\]", "[表情]");
        //设置状态栏提示
        mBuilder.setTicker(message.getFrom()+": " + ticker);

        Notification notification = mBuilder.build();
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        final int notifiId = 11;
        notificationManager.notify(notifiId, notification);
        notificationManager.cancel(notifiId);
    }

    /**
     * 消息回执BroadcastReceiver
     */
    private BroadcastReceiver ackMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            abortBroadcast();

            String msgid = intent.getStringExtra("msgid");
            String from = intent.getStringExtra("from");
            EMConversation conversation = EMChatManager.getInstance().getConversation(from);
            if (conversation != null) {
                // 把message设为已读
                EMMessage msg = conversation.getMessage(msgid);
                if (msg != null) {
                    msg.isAcked = true;
                }
            }
            adapter.notifyDataSetChanged();
        }
    };

    /**
     * 消息送达BroadcastReceiver
     */
    private BroadcastReceiver deliveryAckMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            abortBroadcast();
            String msgid = intent.getStringExtra("msgid");
            String from = intent.getStringExtra("from");
            EMConversation conversation = EMChatManager.getInstance().getConversation(from);
            if (conversation != null) {
                // 把message设为已读
                EMMessage msg = conversation.getMessage(msgid);
                if (msg != null) {
                    msg.isDelivered = true;
                }
            }
            adapter.notifyDataSetChanged();
        }
    };

    /**
     * listview滑动监听listener
     *
     */
    private class ListScrollListener implements AbsListView.OnScrollListener {
        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            switch (scrollState) {
                case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:
                    if (view.getFirstVisiblePosition() == 0 && !isloading && haveMoreData) {
                        pb_load_more.setVisibility(View.VISIBLE);
                        List<EMMessage> messages;
                        try {
                            // 获取更多messges，调用此方法的时候从db获取的messages
                            // sdk会自动存入到此conversation中
                            messages = conversation.loadMoreMsgFromDB(adapter.getItem(0).getMsgId(), pagesize);
                        } catch (Exception e1) {
                            pb_load_more.setVisibility(View.GONE);
                            return;
                        }
                        try {
                            Thread.sleep(300);
                        } catch (InterruptedException e) {

                        }
                        if (messages.size() != 0) {
                            // 刷新ui
                            adapter.notifyDataSetChanged();
                            list.setSelection(messages.size() - 1);
                            if (messages.size() != pagesize)
                                haveMoreData = false;
                        } else {
                            haveMoreData = false;
                        }
                        pb_load_more.setVisibility(View.GONE);
                        isloading = false;
                    }
                    break;
            }
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

        }

    }

    @Override
    protected void onNewIntent(Intent intent) {
        // 点击notification bar进入聊天页面，保证只有一个聊天页面
        String username = intent.getStringExtra("userId");
        if (toUserId.equals(username))
            super.onNewIntent(intent);
        else {
            finish();
            startActivity(intent);
        }
    }

    private PowerManager.WakeLock wakeLock;

    /**
     * 按住说话listener
     *
     */
    class PressToSpeakListen implements View.OnTouchListener {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if (!CommonUtils.isExitsSdcard()) {
                        Toast.makeText(ChatActivity.this, "发送语音需要sdcard支持！", Toast.LENGTH_SHORT).show();
                        return false;
                    }
                    try {
                        v.setPressed(true);
                        wakeLock.acquire();
                        if (VoicePlayClickListener.isPlaying)
                            VoicePlayClickListener.currentPlayListener.stopPlayVoice();
                        recording_container.setVisibility(View.VISIBLE);
                        recording_hint.setText(getString(R.string.chat_move_up_to_cancel));
                        recording_hint.setBackgroundColor(Color.TRANSPARENT);
                        voiceRecorder.startRecording(null, toUserId, getApplicationContext());
                    } catch (Exception e) {
                        e.printStackTrace();
                        v.setPressed(false);
                        if (wakeLock.isHeld())
                            wakeLock.release();
                        if (voiceRecorder != null)
                            voiceRecorder.discardRecording();
                        recording_container.setVisibility(View.INVISIBLE);
                        Toast.makeText(ChatActivity.this, R.string.chat_recoding_fail, Toast.LENGTH_SHORT).show();
                        return false;
                    }

                    return true;
                case MotionEvent.ACTION_MOVE: {
                    if (event.getY() < 0) {
                        recording_hint.setText(getString(R.string.chat_release_to_cancel));
                        recording_hint.setBackgroundResource(R.drawable.chat_recording_text_hint_bg);
                    } else {
                        recording_hint.setText(getString(R.string.chat_move_up_to_cancel));
                        recording_hint.setBackgroundColor(Color.TRANSPARENT);
                    }
                    return true;
                }
                case MotionEvent.ACTION_UP:
                    v.setPressed(false);
                    recording_container.setVisibility(View.INVISIBLE);
                    if (wakeLock.isHeld())
                        wakeLock.release();
                    if (event.getY() < 0) {
                        // discard the recorded audio.
                        voiceRecorder.discardRecording();

                    } else {
                        // stop recording and send voice file
                        try {
                            int length = voiceRecorder.stopRecoding();
                            if (length > 0) {
                                sendVoice(voiceRecorder.getVoiceFilePath(), voiceRecorder.getVoiceFileName(toUserId),
                                        Integer.toString(length), false);
                            } else if (length == EMError.INVALID_FILE) {
                                Toast.makeText(getApplicationContext(), "无录音权限", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getApplicationContext(), "录音时间太短", Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(ChatActivity.this, "发送失败，请检测服务器是否连接", Toast.LENGTH_SHORT).show();
                        }

                    }
                    return true;
                default:
                    recording_container.setVisibility(View.INVISIBLE);
                    if (voiceRecorder != null)
                        voiceRecorder.discardRecording();
                    return false;
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
        unregisterReceiver(ackMessageReceiver);
        unregisterReceiver(deliveryAckMessageReceiver);
    }
}