package com.qianfan.qianfanddpdemo.ddp.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.ddp.sdk.base.DDPSDK;
import com.ddp.sdk.cambase.utils.VTask;
import com.ddp.sdk.video.operation.VideoOperateServer;
import com.facebook.drawee.view.SimpleDraweeView;
import com.qianfan.qianfanddpdemo.MyApplication;
import com.qianfan.qianfanddpdemo.R;
import com.qianfan.qianfanddpdemo.base.BaseActivity;
import com.qianfan.qianfanddpdemo.common.AppConfig;
import com.qianfan.qianfanddpdemo.utils.ImageLoader;
import com.qianfan.qianfanddpdemo.utils.LogUtil;

import java.util.Arrays;
import java.util.List;

/**
 * 交通违法举报页面-更改图片(根据视频地址获取视频10张缩略图视频，选择图片页面)
 * Created by wangjing on 2017/3/30.
 */

public class ChooseVideoImageActivity extends BaseActivity {
    private static final String RESULT_KEY_CAPTURE_PIC = "result_key_capture_pic";      // 截取图片，查询操作结果
    private String lastResultKey;   // 最近一次操作的key

    private Toolbar toolbar;
    private SimpleDraweeView simpleDraweeView;
    private TextView tv_commit;
    private TextView tv_time;
    private GridView gridView;
    private SeekBar seekBar;

    private long totalTime;
    private String videoPath;
    private String[] datas;

    private ProgressDialog progressDialog;
    private VideoOperateServer videoServer;

    private String outPutPicPath;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentViewWhite(R.layout.activity_choosevideoimage);
        DDPSDK.init(this, "");
        initView();
        initData();
    }


    private void initView() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        simpleDraweeView = (SimpleDraweeView) findViewById(R.id.simpleDraweeView);
        tv_commit = (TextView) findViewById(R.id.tv_commit);
        tv_time = (TextView) findViewById(R.id.tv_time);
        gridView = (GridView) findViewById(R.id.gridView);
        seekBar = (SeekBar) findViewById(R.id.seekBar);
    }

    private void initData() {
        setBaseBackToolbar(toolbar, "设置视频封面");
        videoPath = getIntent().getStringExtra("videoPath");
        datas = getIntent().getStringArrayExtra("datas");
        totalTime = getIntent().getLongExtra("totalTime", 0);
        LogUtil.e("totalTime", "totalTime==>" + totalTime);

        videoServer = VideoOperateServer.instance();

        tv_time.setText("" + generateDetailTime(totalTime));
        tv_commit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (!MyApplication.isLogin()){
//                    IntentUtils.jumpLogin(ChooseVideoImageActivity.this);
//                    return;
//                }
                setResult(RESULT_OK, new Intent().putExtra("imagepath", outPutPicPath));
                finish();
            }
        });

        seekBar.setMax((int) totalTime);
        if (datas != null) {
            ImageLoader.load(simpleDraweeView, "file://" + datas[0]);
            gridView.setAdapter(new GridViewAdapter(this, Arrays.asList(datas)));
        }
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                //滚动时的回调函数
                tv_time.setText("" + generateDetailTime(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                //SeekBar开始滚动的回调函数
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //SeekBar停止滚动的回调函数
                extractImgFromVideo(seekBar.getProgress());

            }


        });
    }

    private void extractImgFromVideo(final int time) {
        //截取xx时间的画面
        outPutPicPath = AppConfig.TEMP + "capture" + System.currentTimeMillis() + ".jpg";
        new VTask<Object, Integer>() {
            @Override
            protected void doPre() {
                super.doPre();
                showProgressDialog("正在获取当前选中时间缩略图……");
            }

            @Override
            protected Integer doBackground(Object o) {
                lastResultKey = RESULT_KEY_CAPTURE_PIC;
                return videoServer.extractImgFromVideo(videoPath, outPutPicPath, 1280, 720, time, RESULT_KEY_CAPTURE_PIC);
            }

            @Override
            protected void doPost(Integer rst) {
                LogUtil.e("doPost", "截取视频图片 rst = " + rst);
                LogUtil.e("doPost", "getOperateResult RESULT_KEY_CAPTURE_PIC = " + videoServer.getOperateResult(RESULT_KEY_CAPTURE_PIC));
                dismissDialog();
                if (rst == 0) {
//                    Toast.makeText(ChooseVideoImageActivity.this, outPutPicPath + "截取视频图片成功", Toast.LENGTH_LONG).show();
                    ImageLoader.load(simpleDraweeView, "file://" + outPutPicPath);
                } else {
                    Toast.makeText(ChooseVideoImageActivity.this, "截取视频图片失败", Toast.LENGTH_LONG).show();
                }

            }
        };
    }

    private void showProgressDialog(String message) {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
        }
        if (TextUtils.isEmpty(message)) {
            progressDialog.setMessage("请稍候……");
        } else {
            progressDialog.setMessage("" + message);
        }
        progressDialog.show();
    }

    private void dismissDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }


    public class GridViewAdapter extends BaseAdapter {
        private List<String> infos;
        private LayoutInflater inflater;

        public GridViewAdapter(Context context, List<String> infos) {
            inflater = LayoutInflater.from(context);
            this.infos = infos;
        }

        @Override
        public int getCount() {
            return infos == null ? 0 : infos.size();
        }

        @Override
        public Object getItem(int position) {
            return infos.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.item_choosevideoimageadapter, parent, false);
                viewHolder = new ViewHolder();
                viewHolder.simpleDraweeView = (SimpleDraweeView) convertView.findViewById(R.id.simpleDraweeView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            ImageLoader.loadResize(viewHolder.simpleDraweeView, "file://" + infos.get(position));
            return convertView;
        }

        class ViewHolder {
            public SimpleDraweeView simpleDraweeView;
        }
    }

    /**
     * 将给的毫秒转化为时间,具体到ms数
     *
     * @param ms
     * @return
     */
    private static String generateDetailTime(long ms) {
        int ss = 1000;
        int mi = ss * 60;
        int hh = mi * 60;
        int dd = hh * 24;

        long day = ms / dd;
        long hour = (ms - day * dd) / hh;
        long minute = (ms - day * dd - hour * hh) / mi;
        long second = (ms - day * dd - hour * hh - minute * mi) / ss;
        long milliSecond = (ms - day * dd - hour * hh - minute * mi - second * ss) / 10; // 小于10ms不显示

        String strDay = (day <= 0 ? "" : day + "/");
        String strHour = (hour <= 0 ? "" : (hour < 10 ? "0" + hour + ":" : "" + hour + ":"));
        String strMinute = (minute < 10 ? "0" + minute : "" + minute);
        String strSecond = (second < 10 ? "0" + second : "" + second);
        String strMilliSecond = (milliSecond < 10 ? "0" + milliSecond : "" + milliSecond);
        // strMilliSecond = milliSecond < 100 ? "0" + strMilliSecond : "" + strMilliSecond;
        return strDay + strHour + strMinute + ":" + strSecond + "." + strMilliSecond;
    }
}
