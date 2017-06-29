package com.qianfan.qianfanddpdemo.widgets;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Message;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.qianfan.qianfanddpdemo.R;
import com.qianfan.qianfanddpdemo.utils.DensityUtils;
import com.qianfan.qianfanddpdemo.utils.ImageLoader;
import com.vyou.app.sdk.utils.FileUtils;
import com.vyou.app.sdk.utils.VLog;
import com.vyou.app.sdk.utils.video.VideoLib;
import com.vyou.app.sdk.widget.WeakHandler;

import java.util.List;

/**
 * 视频分享--视频裁剪进度条
 *
 * @author wangjing
 * @date 2017-3-14
 */
public class VideoCropSeekBar extends LinearLayout implements OnGestureListener {
    private static final String TAG = "VideoCropSeekBar";
    private static final int ID_HANDLER_UPDATE_CROPBAR = 0x101;

    /**
     * 背景缩略图最大张数
     */
    public static final int MAXIMGNUM = 10;// 背景最多放10个缩略图

    private Context mContext;
    private DisplayMetrics dm;
    private GestureDetector mGestureDetector;
    private GestureMode gestureMode;
    private boolean seekEnable = true;

    /**
     * 整个裁剪区域
     */
    private View cropBarLay;
    /**
     * 裁剪左游标
     */
    private View cropLeftThumb;
    /**
     * 裁剪右游标
     */
    private View cropRightThumb;
    /**
     * 裁剪操作区域
     */
    private RelativeLayout cropSelectlay;
    /**
     * 裁剪区域左边的阴影区
     */
    private RelativeLayout cropLeftLay;
    /**
     * 视频剪裁的背景--用视频的缩略图来做
     */
    private LinearLayout cropThumbnailView;
    /**
     * 缩略图区域的布局参数
     */
    private ViewGroup.LayoutParams cropSacleLp;
    /**
     * 选择操作区域的布局参数
     */
    private RelativeLayout.LayoutParams selectLylp = null;
    /**
     * 左侧阴影区布局参数
     */
    private RelativeLayout.LayoutParams cropLeftlp = null;
    /**
     * 裁剪实际选中视图
     */
    private View cropView;
    // 时间相关控件
    private TextView leftTime, selectTime, rightTime;
    // /** 裁剪刻度跨度文字 */
    // private TextView cropText;

    /**
     * 当前操作布局的绝对位置X轴
     */
    private int locationX = 0;
    /**
     * 当前操作裁剪的区域的宽
     */
    private int cropW = 0;
    /**
     * 当前操作裁剪的最小宽度
     */
    private int cropSelectMinW = 0;

    // 左右进度
    private float leftProgress = 0f, rightProgress = 1f;
    private OnSeekListener seekListener;

    private static final int PERIOD_TIME = 40;// 延迟300ms
    private long lastScrollTime;

    // 每个缩略图的高宽
    private int thubmnailWidth;
    private int thubmnailHeight;
    private LayoutParams thumbnailLp;

    // 视频时间 ms
    private long videoTotalTime;

    // 视频的url
    private String vedioUrl;
    private VideoLib vlib;

    private ProgressBar waitBar;

    private int roundX, roundY;// 点击seekbar 滑块周边一点，也算是点击滑块，相当于增大滑块的点击范围

    public LinearLayout getCropThumbnailView() {
        return cropThumbnailView;
    }

    //
    public int getThumbnailWidth() {
        return thubmnailWidth;
    }

    public int getThubmnailHeight() {
        return thubmnailHeight;
    }

    public void setSeekListener(OnSeekListener leftSeekListener) {
        this.seekListener = leftSeekListener;
    }

    public long getVideoTime() {
        return videoTotalTime;
    }

    public void setVideo(String vedioUrl, long videoTime) {
        this.vedioUrl = vedioUrl;
        this.videoTotalTime = videoTime;
        leftProgress = 0;
        rightProgress = 1;
        updateTime(leftProgress, rightProgress);
        waitBar.setVisibility(View.VISIBLE);
    }

    public void updateVideoInfo(String vedioUrl, long videoTime) {
        this.vedioUrl = vedioUrl;
        this.videoTotalTime = videoTime;
        leftProgress = 0;
        rightProgress = 1;
        updateTime(leftProgress, rightProgress);
    }

    /**
     * 初始化seekbar，使两个滑块处于左右两端的状态
     */
    public void initSeekbar() {
        selectLylp = (RelativeLayout.LayoutParams) cropSelectlay.getLayoutParams();
        cropLeftlp = (RelativeLayout.LayoutParams) cropLeftLay.getLayoutParams();

        selectLylp.leftMargin = 0;
        selectLylp.width = cropBarLay.getWidth();
        cropSelectlay.setLayoutParams(selectLylp);
        cropLeftlp.width = selectLylp.leftMargin;// - cropLeftThumb.getWidth() / 2;
        cropLeftLay.setLayoutParams(cropLeftlp);

    }

    private enum GestureMode {
        idle,
        total,
        crop,
        left,
        right
    }

    WeakHandler<VideoCropSeekBar> uiHandler = new WeakHandler<VideoCropSeekBar>(this) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case ID_HANDLER_UPDATE_CROPBAR: {
                    // if (isFirst)
                    // {
                    // isFirst = false;
                    // RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) cropSelectlay
                    // .getLayoutParams();
                    // params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                    // params.leftMargin = 0;// cropBarLay.getWidth() / 2 - params.width / 2;
                    // cropSelectlay.setLayoutParams(params);
                    // }

                    // cropSacleView.updateCropText();
                    // cropSacleView.setCenterTime(curTime * 1000l);
                    break;
                }
            }

        }
    };

    public VideoCropSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        View.inflate(context, R.layout.widget_video_crop_seekbar, this);
        mContext = context;
        init();
    }

    private void init() {
        // 裁剪进度条
        cropBarLay = findViewById(R.id.crop_bar_lay);
        cropLeftThumb = findViewById(R.id.crop_left_thumb);
        cropRightThumb = findViewById(R.id.crop_right_thumb);
        cropSelectlay = (RelativeLayout) findViewById(R.id.crop_select_lay);
        cropLeftLay = (RelativeLayout) findViewById(R.id.crop_area_left_area);
        cropThumbnailView = (LinearLayout) findViewById(R.id.crop_scale_view);
        cropView = findViewById(R.id.crop_area_view);
        leftTime = (TextView) findViewById(R.id.crop_time_left);
        selectTime = (TextView) findViewById(R.id.crop_time_center);
        rightTime = (TextView) findViewById(R.id.crop_time_right);
        waitBar = (ProgressBar) findViewById(R.id.wait_progress);

        dm = DensityUtils.getDisplaySize(getContext());
        cropSacleLp = cropThumbnailView.getLayoutParams();
        int marginLeftAndRight = 2 * DensityUtils.dip2px(getContext(), 14);
        thubmnailWidth = (dm.widthPixels - marginLeftAndRight) / MAXIMGNUM;
        thubmnailHeight = thubmnailWidth;
        cropSacleLp.width = dm.widthPixels;
        cropSacleLp.height = thubmnailWidth + 2 * DensityUtils.dip2px(getContext(), 5);// 上下有5个dp的padding
        cropThumbnailView.setLayoutParams(cropSacleLp);

        updateTime(leftProgress, rightProgress);// 初始化时间

        // 手势
        mGestureDetector = new GestureDetector(mContext, this);
        vlib = VideoLib.getInstance();
        thumbnailLp = new LayoutParams(thubmnailWidth, thubmnailHeight);
    }

    /**
     * 更新视频缩略图
     *
     * @param imgs
     */
    public void fillVedioThumb(final List<String> imgs) {
        if (imgs == null || imgs.isEmpty()) {
            return;
        }
        cropThumbnailView.removeAllViews();
        waitBar.setVisibility(View.VISIBLE);
        for (int i = 0; i < imgs.size(); i++) {
            SimpleDraweeView simpleDraweeView=new SimpleDraweeView(getContext());
            simpleDraweeView.setAspectRatio(1f);
            ImageLoader.load(simpleDraweeView,"file://"+imgs.get(i));
            cropThumbnailView.addView(simpleDraweeView, thumbnailLp);
        }
        waitBar.setVisibility(View.GONE);
    }

    /**
     * 新增一个缩略图
     *
     * @param thumUrl
     */
    public void addVedioThumb(final String thumUrl) {
        //        if (thumUrl == null || thumUrl.isEmpty())
        //        {
        //            return;
        //        }
        //
        //        SystemUtils.asyncTaskExec(new AsyncTask<Object, Boolean, Bitmap>()
        //        {
        //            @Override
        //            protected Bitmap doInBackground(Object... params)
        //            {
        //                VLog.v(TAG, "add VedioThumb begin " + System.currentTimeMillis());
        //                Bitmap bm = ImgUtils.getImageThumbnail(thumUrl, getThumbnailWidth(), getThubmnailHeight());
        //                VLog.v(TAG, "add VedioThumb end " + System.currentTimeMillis());
        //                return bm;
        //            }
        //
        //            @Override
        //            protected void onPostExecute(Bitmap bitmap)
        //            {
        //                ImageView mView = new ImageView(getContext());
        //                mView.setScaleType(ScaleType.CENTER_CROP);
        //                mView.setImageBitmap(bitmap);
        //                cropThumbnailView.addView(mView, thumbnailLp);
        //            }
        //        });

    }

    public void addVedioThumb(final Bitmap thumBit) {
        //        if (thumBit == null)
        //        {
        //            return;
        //        }
        //        if (cropThumbnailView == null || thumbnailLp == null || !isShown())
        //        {
        //            return;
        //        }
        //        AppLib.getInstance().globalHandler.post(new Runnable()
        //        {
        //            @Override
        //            public void run()
        //            {
        //                ImageView mView = new ImageView(getContext());
        //                mView.setScaleType(ScaleType.CENTER_CROP);
        //                mView.setImageBitmap(thumBit);
        //                cropThumbnailView.addView(mView, thumbnailLp);
        //            }
        //        });
    }

    /**
     * @param leftProgress  千分之。。
     * @param rightProgress 千分之。。
     */
    private void updateTime(float leftProgress, float rightProgress) {
        if (videoTotalTime < 0) {
            VLog.e(TAG, "videoTime < 0");
            return;
        }
        leftTime.setText(generateDetailTime((long) (videoTotalTime * leftProgress)));// 还要转化成时间格式
        rightTime.setText(generateDetailTime((long) (videoTotalTime * rightProgress)));// 还要转化成时间格式
        selectTime.setText(generateDetailTime((long) (videoTotalTime * (rightProgress - leftProgress))));
    }

    /**
     * 获取视频截图名称
     *
     * @param inputFile
     * @return
     */
    private String[] getSplitImageFileName(long videoTotalTime, String inputFile) {
        if (inputFile == null || inputFile.isEmpty() || !FileUtils.isVideo(inputFile)) {
            return null;
        }
        int totalSec = (int) (videoTotalTime / 1000);
        float fps = totalSec / MAXIMGNUM;
        StringBuilder outPutFile = new StringBuilder();
        String outPutFilePre = FileUtils.getFileNameNoEx(inputFile);

        // for (int i = 1; i <= MAXIMGNUM; i++)
        // {
        // if (i > 1)
        // {
        // outPutFile.append("/");
        // }
        // outPutFile.append(outPutFilePre + "_" + i + ".jpeg");
        // }
        vlib.extractImgsFromVideo(fps, inputFile, outPutFilePre);
        return outPutFile.toString().split("/");
    }

    private Runnable updateProgressRunnable = new Runnable() {
        @Override
        public void run() {

            if (seekListener != null) {
                seekListener
                        .onSeekTimeChanged(VideoCropSeekBar.this, (long) (leftProgress * getVideoTime()), (long) (rightProgress * getVideoTime()), true);
            }
        }
    };

    /**
     * 裁剪使能
     */
    public void setSeekEnable(boolean enable) {
        seekEnable = enable;
    }

    /**
     * 等待框的显隐控制
     *
     * @param isWaiting
     */
    public void setWaiting(boolean isWaiting) {
        if (isWaiting) {
            waitBar.setVisibility(View.VISIBLE);
        } else {
            waitBar.setVisibility(View.GONE);
        }
    }

    public interface OnSeekListener {
        /**
         * 选择进度变化[0, 100]
         */
        void onSeekProgessChanged(VideoCropSeekBar seekBar, int leftProgress, int rightProgress, boolean fromUser);

        /**
         * 选择时间变化[0, @vedioTotalTime]
         */
        void onSeekTimeChanged(VideoCropSeekBar seekBar, long leftSeekTime, long rightSeekTime, boolean fromUser);

        /**
         * seek追踪开始
         */
        void onStartTrackingSeek(VideoCropSeekBar seekBar);

        /**
         * seek追踪结束
         */
        void onStopTrackingSeek(VideoCropSeekBar seekBar, long leftSeekTime, long rightSeekTime);

    }

    // ============= ================= 内部手势操作的处理 ================== ====================

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (seekListener != null) {
                seekListener.onStartTrackingSeek(this);
            }
            mGestureDetector.onTouchEvent(event);
            return true;
        }

        if (seekEnable && seekListener != null && event.getAction() == MotionEvent.ACTION_UP) {
            boolean b = mGestureDetector.onTouchEvent(event);
            if (gestureMode == GestureMode.left || gestureMode == GestureMode.right) {
                seekListener.onStopTrackingSeek(this, (long) (leftProgress * getVideoTime()), (long) (rightProgress * getVideoTime()));
            }
            return b;
        }

        return mGestureDetector.onTouchEvent(event);
    }

    @Override
    public boolean onDown(MotionEvent e) {
        roundX = DensityUtils.dip2px(getContext(), 12);
        roundY = 0;
        if (isTouchOnViewAround(cropSelectlay, e, roundX, roundY)) {
            selectLylp = (RelativeLayout.LayoutParams) cropSelectlay.getLayoutParams();
            cropLeftlp = (RelativeLayout.LayoutParams) cropLeftLay.getLayoutParams();
            cropW = selectLylp.width;
            if (cropW == -1) {
                cropW = cropBarLay.getWidth();
            }
            locationX = selectLylp.leftMargin < 0 ? 0 : selectLylp.leftMargin;
            cropSelectMinW = cropLeftThumb.getWidth() * 2;

            if (isTouchOnViewAround(cropLeftThumb, e, roundX, roundY)) {
                gestureMode = GestureMode.left;
            } else if (isTouchOnViewAround(cropRightThumb, e, roundX, roundY)) {
                gestureMode = GestureMode.right;
            } else {
                gestureMode = GestureMode.idle;// GestureMode.crop;// 暂时不支持拖动中间
            }
        } else {
            gestureMode = GestureMode.idle;
        }
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {
    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        if (gestureMode == GestureMode.idle || !seekEnable) {
            return false;
        }

        int w = 0;

        if (gestureMode == GestureMode.left) {
            w = (cropW += (int) distanceX);
            locationX -= (int) distanceX;

            if (w >= cropSelectMinW) {
                selectLylp.leftMargin = locationX;
                if (selectLylp.leftMargin < 0) {
                    w = w + selectLylp.leftMargin;
                    selectLylp.leftMargin = 0;
                }
                selectLylp.width = w;

                // rlp.leftMargin = (locationX -= (int) distanceX);
                if (selectLylp.leftMargin >= 0) {
                    cropSelectlay.setLayoutParams(selectLylp);
                    cropLeftlp.width = selectLylp.leftMargin;// - cropLeftThumb.getWidth() / 2;
                    cropLeftLay.setLayoutParams(cropLeftlp);

                }

            }
            // 在这里做左操作操作符的一个回调

            leftProgress = (float) selectLylp.leftMargin / (cropBarLay.getWidth() - cropSelectMinW);
            // rightProgress = ((float) rlp.leftMargin + cropSelectlay.getWidth()) / cropBarLay.getWidth();

            long curTime = System.currentTimeMillis();
            if (curTime - lastScrollTime >= PERIOD_TIME) {
                lastScrollTime = curTime;
                uiHandler.post(updateProgressRunnable);
            }
            // uiHandler.removeCallbacks(updateProgressRunnable);
            // uiHandler.postDelayed(updateProgressRunnable, DELAY_TIME);

        } else if (gestureMode == GestureMode.right) {
            w = (cropW -= (int) distanceX);

            if (selectLylp.leftMargin + w >= cropBarLay.getWidth()) {
                w = cropW = cropBarLay.getWidth() - selectLylp.leftMargin;
            }

            if (w > cropSelectMinW) {
                if (selectLylp.leftMargin < 0) {
                    selectLylp.leftMargin = 0;
                }
                selectLylp.width = w;
                cropSelectlay.setLayoutParams(selectLylp);

            }

            // leftProgress = (float) rlp.leftMargin / cropBarLay.getWidth();
            rightProgress = ((float) selectLylp.leftMargin + cropSelectlay.getWidth() - cropSelectMinW) / (cropBarLay
                    .getWidth() - cropSelectMinW);

            long curTime = System.currentTimeMillis();
            if (curTime - lastScrollTime >= PERIOD_TIME) {
                lastScrollTime = curTime;
                uiHandler.post(updateProgressRunnable);
            }
            // uiHandler.removeCallbacks(updateProgressRunnable);
            // uiHandler.postDelayed(updateProgressRunnable, DELAYTIME);

        }
        updateTime(leftProgress, rightProgress);
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return false;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);

        uiHandler.removeMessages(ID_HANDLER_UPDATE_CROPBAR);
        uiHandler.sendEmptyMessageDelayed(ID_HANDLER_UPDATE_CROPBAR, 30);
    }


    /**
     * 是否点击view上面
     *
     * @param v     判断的view
     * @param event 屏幕上的触摸事件对象
     * @return
     */
    private static boolean isTouchOnViewAround(View v, MotionEvent event, int offsetX, int offsetY) {
        int roundX = offsetX; // 扩大View的点击判断范围，点击 左右 一点也算点中
        int roundY = offsetY;// 扩大View的点击判断范围，点击 上下 一点也算点中
        if (v == null || event == null) {
            return false;
        }

        int[] position = new int[2];
        // 左右offset
        v.getLocationOnScreen(position);
        position[0] -= roundX;
        position[1] -= roundY;
        int w = v.getWidth() + 2 * roundX;
        int h = v.getHeight() + 2 * roundY;

        int x = (int) event.getRawX();
        int y = (int) event.getRawY();

        // 先判断x坐标，满足后再判读y坐标
        if (x >= position[0] && (x <= (position[0] + w))) {
            if (y >= position[1] && (y <= (position[1] + h))) {
                return true;
            }
        }

        return false;
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
