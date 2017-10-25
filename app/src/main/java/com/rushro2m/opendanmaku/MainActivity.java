package com.rushro2m.opendanmaku;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import com.opendanmaku.DanmakuItem;
import com.opendanmaku.DanmakuView;
import com.opendanmaku.IDanmakuItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    //弹幕控件
    private DanmakuView mDanmakuView;
    private Button hide, send;
    private EditText msg;
    private VideoView videoView;

    //播放源
    public static final String VIDEO_URL = "http://jzvd.nathen.cn/c6e3dc12a1154626b3476d9bf3bd7266/6b56c5f0dc31428083757a45764763b0-5287d2089db37e62345123a1be272f8b.mp4";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //实例化控件
        initView();
        //设置播放器
        setVideoView();
        //设置数据
        initData();
        //设置监听器
        setListener();
    }


    //实例化所有控件
    private void initView() {
        videoView = (VideoView) findViewById(R.id.videoView);
        mDanmakuView = (DanmakuView) findViewById(R.id.danmakuView);
        hide = (Button) findViewById(R.id.hide);
        send = (Button) findViewById(R.id.send);
        msg = (EditText) findViewById(R.id.text);
    }

    private void initData() {
        List<IDanmakuItem> list = initItems();
        //将数据排列打乱
        Collections.shuffle(list);
        //弹幕是否在后台执行
        mDanmakuView.addItem(list, true);

    }

    private List<IDanmakuItem> initItems() {
        List<IDanmakuItem> list = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            IDanmakuItem item = new DanmakuItem(this, "这是第" + i + "条文字弹幕", mDanmakuView.getWidth());
            list.add(item);
        }
        for (int i = 0; i < 1000; i++) {
            //参数：上下文对象，图片资源
            ImageSpan imageSpan = new ImageSpan(this, R.drawable.em);
            SpannableString spannableString = new SpannableString("这是第" + i + "条图片弹幕");
            //参数：插入的内容，插入开始的位置，插入结束的位置，插入的类型
            spannableString.setSpan(imageSpan, spannableString.length() - 2,
                    spannableString.length() - 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            IDanmakuItem item = new DanmakuItem(this, spannableString, mDanmakuView.getWidth(), 0, 0, 0, 1.5f);
            list.add(item);
        }
        return list;
    }

    private void setVideoView() {

        //准备完毕后，进行播放
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                videoView.start();
            }
        });

        //
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                videoView.start();
            }
        });

        //播放发生错误时，进行吐丝
        videoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                Toast.makeText(MainActivity.this, "播放出错", Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        //设置控制器
        videoView.setMediaController(new MediaController(this));
        //设置播放源
        videoView.setVideoPath(VIDEO_URL);
    }

    private void setListener() {
        hide.setOnClickListener(this);
        send.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.hide:
                if (mDanmakuView.isPaused()) {
                    hide.setText(R.string.hide);
                    mDanmakuView.show();
                } else {
                    hide.setText(R.string.show);
                    mDanmakuView.hide();
                }
                break;
            case R.id.send:
                sendDanmaku();
                break;
        }
    }

    //点击发送按钮发送弹幕
    private void sendDanmaku() {
        String input = msg.getText().toString();
        if (TextUtils.isEmpty(input)) {
            Toast.makeText(this, "请输入内容", Toast.LENGTH_SHORT).show();
        } else {
            IDanmakuItem item = new DanmakuItem(this, new SpannableString(input), mDanmakuView.getWidth(), 0, R.color.colorAccent, 0, 1);
            mDanmakuView.addItemToHead(item);
        }
        msg.setText("");
    }

    @Override
    protected void onResume() {
        super.onResume();
        mDanmakuView.show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mDanmakuView.hide();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDanmakuView.clear();
    }
}
