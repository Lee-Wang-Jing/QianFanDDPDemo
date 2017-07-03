package com.qianfan.qianfanddpdemo.ddp.activity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.VideoView;

import com.ddp.sdk.base.DDPSDK;
import com.ddp.sdk.cam.resmgr.CameraResMgr;
import com.ddp.sdk.cam.resmgr.model.EventImage;
import com.ddp.sdk.cambase.utils.VTask;
import com.ddp.sdk.video.operation.VideoOperateServer;
import com.facebook.drawee.view.SimpleDraweeView;
import com.qianfan.qianfanddpdemo.R;
import com.qianfan.qianfanddpdemo.base.BaseActivity;
import com.qianfan.qianfanddpdemo.common.AppConfig;
import com.qianfan.qianfanddpdemo.entity.IllegalCategoriesEntity;
import com.qianfan.qianfanddpdemo.event.WeiZhangJuBaoEvent;
import com.qianfan.qianfanddpdemo.utils.DensityUtils;
import com.qianfan.qianfanddpdemo.utils.ImageLoader;
import com.qianfan.qianfanddpdemo.utils.LogUtil;
import com.qianfan.qianfanddpdemo.utils.TimeUtils;
import com.qianfan.qianfanddpdemo.utils.ToastUtil;
import com.qianfan.qianfanddpdemo.widgets.AllCapTransformationMethod;
import com.qianfan.qianfanddpdemo.widgets.MyMediaController;
import com.qianfan.qianfanddpdemo.widgets.SquareTextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;


/**
 * 盯盯拍-交通违法举报
 * Created by wangjing on 2017/3/22.
 */

public class WeiZhangJuBaoActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = WeiZhangJuBaoActivity.class.getSimpleName();
    // 这些操作结果的key由用户自己定义，自己保存
    private static final String RESULT_KEY_CAPTURE_PICS = "result_key_capture_pics";      // 截取图片数组，查询操作结果
    public static final int RequestCode = 188;
    public static final int RequestCode_GPS = 189;
    private static final String[] citys = new String[]{"粤", "京", "津", "沪", "渝", "蒙", "新", "桂",
            "宁", "藏", "青", "冀", "晋", "辽", "吉", "黑",
            "苏", "浙", "皖", "闽", "赣", "鲁", "豫", "鄂",
            "湘", "琼", "川", "陕", "甘", "贵", "云"};
    private static final String[] citys_AZ = new String[]{"A", "B", "C", "D", "E", "F", "G", "H",
            "J", "K", "L", "M", "N", "P", "Q", "R",
            "S", "T", "U", "V", "W", "X", "Y", "Z"};

    private Toolbar toolbar;
    private LinearLayout ll_root;
    private SimpleDraweeView simpleDraweeView;
    private VideoView videoView;
    private FrameLayout fl_normal;

    private SimpleDraweeView simpleDraweeView1, simpleDraweeView2, simpleDraweeView3;
    private ImageView iv_change1, iv_change2, iv_change3;

    private TextView tv_time, tv_city, tv_address, tv_type, tv_commit;
    private ImageView imv_location;
    private RadioGroup radioGroup;
    private EditText et_card;
    private int car_type = 0;//车辆类型 1：小车 2：大车

    private VideoOperateServer videoServer;
    private long totalTime;//视频总时间
    private String[] videoImgs;

    private String path;
    private long time;
    private MyMediaController mediaController;

    private AlertDialog type_dialog;
    private PopupWindow city_popupWindow;
    private TextView city_dialog_content;
    private int select_city = 16;
    private int select_city_AZ = 3;

    private String selectDate;
    private DatePickerDialog datePickerDialog;
    private TimePickerDialog timePickerDialog;

    private String imageOnePath;
    private String imageTwoPath;
    private String imageThreePath;
    private int lastKey = 0;

    double latitude = 0L;
    double longitude = 0L;

    private ProgressDialog progressDialog;
    private List<IllegalCategoriesEntity> illegalCategoriesEntities;
    private int illegalID;
//    private GeocodeSearch geocodeSearch;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentViewWhite(R.layout.activity_weizhangjubao);

        EventBus.getDefault().register(this);
        path = getIntent().getStringExtra("path");
        time = getIntent().getLongExtra("time", 0);
        DDPSDK.init(this, "");
        videoServer = VideoOperateServer.instance();
        initView();
        initData();
        getImagesFromVideo();
    }

    private void initView() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        ll_root = (LinearLayout) findViewById(R.id.ll_root);
        simpleDraweeView = (SimpleDraweeView) findViewById(R.id.simpleDraweeView);
        videoView = (VideoView) findViewById(R.id.videoView);
        fl_normal = (FrameLayout) findViewById(R.id.fl_normal);
        simpleDraweeView1 = (SimpleDraweeView) findViewById(R.id.simpleDraweeView1);
        simpleDraweeView2 = (SimpleDraweeView) findViewById(R.id.simpleDraweeView2);
        simpleDraweeView3 = (SimpleDraweeView) findViewById(R.id.simpleDraweeView3);
        tv_time = (TextView) findViewById(R.id.tv_time);
        tv_city = (TextView) findViewById(R.id.tv_city);
        tv_address = (TextView) findViewById(R.id.tv_address);
        tv_type = (TextView) findViewById(R.id.tv_type);
        tv_commit = (TextView) findViewById(R.id.tv_commit);
        imv_location = (ImageView) findViewById(R.id.imv_location);
        iv_change1 = (ImageView) findViewById(R.id.iv_change1);
        iv_change2 = (ImageView) findViewById(R.id.iv_change2);
        iv_change3 = (ImageView) findViewById(R.id.iv_change3);
        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        et_card = (EditText) findViewById(R.id.et_card);
    }

    private void initData() {
        setBaseBackToolbar(toolbar, "交通违法举报");
        videoView.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DensityUtils.getScreenWidth(this) / 16 * 9));
        et_card.setTransformationMethod(new AllCapTransformationMethod());
        fl_normal.setOnClickListener(this);
        simpleDraweeView1.setOnClickListener(this);
        simpleDraweeView2.setOnClickListener(this);
        simpleDraweeView3.setOnClickListener(this);
        iv_change1.setOnClickListener(this);
        iv_change2.setOnClickListener(this);
        iv_change3.setOnClickListener(this);
        imv_location.setOnClickListener(this);
        tv_commit.setOnClickListener(this);
        tv_time.setOnClickListener(this);
        tv_city.setOnClickListener(this);
        tv_type.setOnClickListener(this);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                if (checkedId == R.id.rb_small) {
                    car_type = 1;
                } else {
                    car_type = 2;
                }
            }
        });

        tv_time.setText("" + TimeUtils.millis2String(time, "yyyy年MM月dd日HH:mm"));

        mediaController = new MyMediaController(this);
        videoView.setVideoPath(path);
        videoView.setMediaController(mediaController);

        //地理搜索类
//        geocodeSearch = new GeocodeSearch(this);
//        geocodeSearch.setOnGeocodeSearchListener(this);

        CameraResMgr cameraResMgr = CameraResMgr.instance();
        EventImage attachImg = cameraResMgr.queryImageByAttachVideoPath(path);
        if (attachImg != null) {
            latitude = attachImg.latitude;
            longitude = attachImg.longitude;
            if (latitude > 0 && longitude > 0) {
                convertGPS();
            }
        } else {

        }
        if (latitude != 0L && longitude != 0L) {
            getAddressByLatlng(latitude, longitude);
        }
        tv_address.setText(latitude + " " + longitude);
    }

    /**
     * 将GPS转化成高德的GPS
     */
    private void convertGPS() {
        try {
//            DPoint sourceLatLng = new DPoint(latitude, longitude);
//            CoordinateConverter converter = new CoordinateConverter(this);
//            // CoordType.GPS 待转换坐标类型
//            converter.from(CoordinateConverter.CoordType.GPS);
//            // sourceLatLng待转换坐标点 DPoint类型
//            converter.coord(sourceLatLng);
//            // 执行转换操作
//            DPoint desLatLng = converter.convert();
//
//            latitude = desLatLng.getLatitude();
//            longitude = desLatLng.getLongitude();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 逆地理编码查询条件：逆地理编码查询的地理坐标点、查询范围、坐标类型。
     *
     * @param latitude
     * @param longitude
     */
    private void getAddressByLatlng(double latitude, double longitude) {
        //逆地理编码查询条件：逆地理编码查询的地理坐标点、查询范围、坐标类型。
//        LatLonPoint latLonPoint = new LatLonPoint(latitude, longitude);
//        RegeocodeQuery query = new RegeocodeQuery(latLonPoint, 500f, GeocodeSearch.AMAP);
//        //异步查询
//        geocodeSearch.getFromLocationAsyn(query);
    }

    /**
     * 通过视频获取缩略图
     * videoServer.getOperateResult 0:空闲等待状态 1：正在处理状态 2：处理完成状态 3：处理失败状态
     * 获取视频截图,并返回截图文件列表名称
     * fps       截图时间间隔（秒）如 5秒 ，对应每五秒截一张，频率=1/fps =0.2张一秒
     * inputFile 视频文件路径
     * outPutDir 截图输出目录(可为null,此时在同级目录下创建一个子目录)
     * resultKey 可以通过这个key来查找操作结果，这个key由用户自己定义和存储
     */
    private void getImagesFromVideo() {
        showProgressDialog("正在获取视频截图……");
        totalTime = videoServer.getVideoDuration(path);
        LogUtil.e("getImageFromVideo", "totalTime==>" + totalTime + " totalTime / 10==>" + totalTime / 10);
        final String outPutPicPath = AppConfig.TEMP;
        new VTask<Object, String[]>() {

            @Override
            protected String[] doBackground(Object o) {
                try {
                    return videoServer.extractImgsFromVideo(totalTime / 10, path, outPutPicPath, null, RESULT_KEY_CAPTURE_PICS);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return new String[0];
            }

            @Override
            protected void doPost(String[] strings) {
                LogUtil.e("getImageFromVideo", "截取视频图片数组 strings.length = " + strings.length);
                LogUtil.e("getImageFromVideo", "getOperateResult RESULT_KEY_CAPTURE_PICS = " + videoServer.getOperateResult(RESULT_KEY_CAPTURE_PICS));
                if (strings.length > 0) {
                    LogUtil.e("getImageFromVideo", outPutPicPath + "截取视频图片成功");
                    videoImgs = strings;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                imageOnePath = videoImgs[0];
                                imageTwoPath = videoImgs[1];
                                imageThreePath = videoImgs[2];
                                ImageLoader.load(simpleDraweeView, "file://" + videoImgs[0]);
                                ImageLoader.load(simpleDraweeView1, "file://" + imageOnePath);
                                ImageLoader.load(simpleDraweeView2, "file://" + imageTwoPath);
                                ImageLoader.load(simpleDraweeView3, "file://" + imageThreePath);
                                dismissDialog();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                } else {
                    dismissDialog();
                    ImageLoader.load(simpleDraweeView, "file://" + path);
                    Toast.makeText(WeiZhangJuBaoActivity.this, "截取视频图片失败", Toast.LENGTH_LONG).show();
                }
            }
        };
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fl_normal:
                videoView.setVisibility(View.VISIBLE);
                fl_normal.setVisibility(View.GONE);
                videoView.start();
                videoView.requestFocus();
                break;
            case R.id.iv_change1:
                lastKey = 1;
                chooseVideoImage();
                break;
            case R.id.iv_change2:
                lastKey = 2;
                chooseVideoImage();
                break;
            case R.id.iv_change3:
                lastKey = 3;
                chooseVideoImage();
                break;
            case R.id.simpleDraweeView1:
                ArrayList<String> list = new ArrayList<>();
                if (videoImgs != null) {
                    for (int i = 0; i < videoImgs.length; i++) {
                        if (i > 2) {
                            break;
                        }
                        list.add(videoImgs[i]);
                    }
                    ToastUtil.TLong(WeiZhangJuBaoActivity.this, "跳转图片查看页面");
//                    IntentUtils.jumpPhoto_Select_Preview(WeiZhangJuBaoActivity.this, 0, list, true);
                }
                break;
            case R.id.simpleDraweeView2:
                ArrayList<String> list1 = new ArrayList<>();
                if (videoImgs != null) {
                    for (int i = 0; i < videoImgs.length; i++) {
                        if (i > 2) {
                            break;
                        }
                        list1.add(videoImgs[i]);
                    }
//                    IntentUtils.jumpPhoto_Select_Preview(WeiZhangJuBaoActivity.this, 1, list1, true);
                    ToastUtil.TLong(WeiZhangJuBaoActivity.this, "跳转图片查看页面");
                }
                break;
            case R.id.simpleDraweeView3:
                ArrayList<String> list2 = new ArrayList<>();
                if (videoImgs != null) {
                    for (int i = 0; i < videoImgs.length; i++) {
                        if (i > 2) {
                            break;
                        }
                        list2.add(videoImgs[i]);
                    }
                    ToastUtil.TLong(WeiZhangJuBaoActivity.this, "跳转图片查看页面");
//                    IntentUtils.jumpPhoto_Select_Preview(WeiZhangJuBaoActivity.this, 2, list2, true);
                }
                break;
            case R.id.tv_time:
                showDateDialog();
                break;
            case R.id.imv_location:
                ToastUtil.TLong(WeiZhangJuBaoActivity.this, "跳转地图选点页面");
//                if (NetworkUtils.getConnectWifiSsid(this).contains("vYou_")) {
//                    ToastUtil.TShort(this, "未检测到网络，请切换其他网络");
//                } else {
//                    IntentUtils.jumpChooseMapActivity(this, RequestCode_GPS);
//                }
                break;
            case R.id.tv_city:
                showCityDialog();
                break;
            case R.id.tv_type://违法类型
//                if (NetworkUtils.getConnectWifiSsid(this).contains("vYou_")) {
//                    ToastUtil.TShort(this, "未检测到网络，请切换其他网络");
//                } else {
//                    getIllegalcategories();
//                }
                ToastUtil.TLong(WeiZhangJuBaoActivity.this, "请求违法类型");
                break;
            case R.id.tv_commit:
                if (TextUtils.isEmpty(tv_address.getText().toString()) || latitude == 0L || longitude == 0L) {
                    ToastUtil.TLong(this, "请选择违法地点");
                    return;
                }
                if (car_type == 0) {
                    ToastUtil.TLong(this, "请选择车辆类型");
                    return;
                }
                if (TextUtils.isEmpty(et_card.getText().toString())) {
                    ToastUtil.TLong(this, "请输入车牌号");
                    return;
                }
                if (TextUtils.isEmpty(tv_type.getText().toString())) {
                    ToastUtil.TLong(this, "请选择违法类型");
                    return;
                }
//                if (NetworkUtils.getConnectWifiSsid(this).contains("vYou_")) {
//                    ToastUtil.TShort(this, "未检测到网络，请切换其他网络");
//                }
                ToastUtil.TLong(this, "进行违法上报");
//                showProgressDialog("正在上传中");
//                commitData();
                break;
            default:
                break;
        }
    }

    /**
     * 选择视频截图
     */
    private void chooseVideoImage() {
        Intent intent = new Intent(this, ChooseVideoImageActivity.class);
        intent.putExtra("datas", videoImgs);
        intent.putExtra("totalTime", totalTime);
        intent.putExtra("videoPath", "" + path);
        startActivityForResult(intent, RequestCode);
    }


    /**
     * 违法上报
     */
    private void commitData() {
        LogUtil.e("commitData", "videoTime==>" + totalTime);
        LogUtil.e("commitData", "videoPath==>" + path);
        LogUtil.e("commitData", "imageOnePath==>" + imageOnePath);
        LogUtil.e("commitData", "imageTwoPath==>" + imageTwoPath);
        LogUtil.e("commitData", "imageThreePath==>" + imageThreePath);
        tv_commit.setEnabled(false);
        List<String> list = new ArrayList<>();
        list.add(imageOnePath);
        list.add(imageTwoPath);
        list.add(imageThreePath);

//        WeiZhangJuBaoEntity entity = new WeiZhangJuBaoEntity();
//        entity.setOccur_time(String.valueOf(time/1000));
//        entity.setAddress(tv_address.getText().toString());
//        entity.setCar_type(String.valueOf(car_type));
//        entity.setIllegal_category(String.valueOf(illegalID));
//        entity.setPlate_number_prefix(tv_city.getText().toString());
//        entity.setPlate_number(et_card.getText().toString());
//        entity.setLongitude(String.valueOf(longitude));
//        entity.setLatitude(String.valueOf(latitude));
//        entity.setVideo(path);
//        entity.setVideoDuration(String.valueOf(totalTime));
//        entity.setScreenshot(list);
//
//        Intent intent = new Intent(this, UpLoadService.class);
//        intent.putExtra("type", 107);
//        intent.putExtra("entity", entity);
//        startService(intent);
        ToastUtil.TLong(this, "进行违法上报");
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        LogUtil.e("requestCode", "requestCode==>" + requestCode);
        LogUtil.e("resultCode", "resultCode==>" + resultCode);
        if (resultCode == RESULT_OK) {
            if (requestCode == RequestCode) {
                if (data != null) {
                    String imagepath = data.getStringExtra("imagepath");
                    switch (lastKey) {
                        case 1:
                            imageOnePath = imagepath;
                            ImageLoader.load(simpleDraweeView1, "file://" + imageOnePath);
                            break;
                        case 2:
                            imageTwoPath = imagepath;
                            ImageLoader.load(simpleDraweeView2, "file://" + imageTwoPath);
                            break;
                        case 3:
                            imageThreePath = imagepath;
                            ImageLoader.load(simpleDraweeView3, "file://" + imageThreePath);
                            break;

                    }
                }
            } else if (requestCode == RequestCode_GPS) {
                if (data != null) {
                    latitude = data.getDoubleExtra("latitude", 0L);
                    longitude = data.getDoubleExtra("longitude", 0L);
                    String title = data.getStringExtra("title");
                    tv_address.setText("" + title);
                }
            }
        }
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


    /**
     * 显示时间选择dialog
     */
    private void showDateDialog() {
        final Calendar calendar = Calendar.getInstance();
        if (datePickerDialog == null) {
            datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                    selectDate = year + "年" + (month + 1) + "月" + dayOfMonth + "日";
                    if (timePickerDialog == null) {
                        timePickerDialog = new TimePickerDialog(WeiZhangJuBaoActivity.this, new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                selectDate = selectDate + hourOfDay + ":" + minute;
                                tv_time.setText("" + selectDate);
                            }
                        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);
                    }
                    timePickerDialog.show();
                }
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        }
        datePickerDialog.show();
    }

    /**
     * 显示车牌城市选择Dialog
     */
    private void showCityDialog() {
        if (city_popupWindow == null) {
            View layout = LayoutInflater.from(this).inflate(R.layout.dialog_ddp_weizhang_city, null);
            RelativeLayout rel_root = (RelativeLayout) layout.findViewById(R.id.rel_root);
            city_dialog_content = (TextView) layout.findViewById(R.id.tv_content);
            GridView gridView = (GridView) layout.findViewById(R.id.gridView);
            GridView gridView2 = (GridView) layout.findViewById(R.id.gridView2);
            TextView tv_ok = (TextView) layout.findViewById(R.id.tv_ok);
            city_dialog_content.setText("" + tv_city.getText().toString());
            gridView.setAdapter(new CityAdapter(this, Arrays.asList(citys)));
            gridView2.setAdapter(new City2Adapter(this, Arrays.asList(citys_AZ)));
            rel_root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    tv_city.setText("" + citys[select_city] + citys_AZ[select_city_AZ]);
                    city_popupWindow.dismiss();
                }
            });
            tv_ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    tv_city.setText("" + citys[select_city] + citys_AZ[select_city_AZ]);
                    city_popupWindow.dismiss();
                }
            });

            city_popupWindow = new PopupWindow(layout, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
            city_popupWindow.setTouchable(true);
            city_popupWindow.setOutsideTouchable(true);
            city_popupWindow.setBackgroundDrawable(ContextCompat.getDrawable(this, R.color.transparent));

            city_popupWindow.showAtLocation(ll_root, Gravity.BOTTOM, 0, 0);
        } else {
            city_popupWindow.showAtLocation(ll_root, Gravity.BOTTOM, 0, 0);
        }
    }

    /**
     * 显示违法类型Dialog
     */
    private void showTypeDialog() {
        if (type_dialog == null) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            View layout = LayoutInflater.from(this).inflate(R.layout.dialog_ddp_cartype, null);
            builder.setView(layout);
            builder.setTitle("请选择违法类型");
            ListView listview = (ListView) layout.findViewById(R.id.listview);

            listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    tv_type.setText("" + illegalCategoriesEntities.get(position).getName());
                    type_dialog.dismiss();
                }
            });
            listview.setAdapter(new TypeAdapter(this, illegalCategoriesEntities));
            type_dialog = builder.create();
            type_dialog.show();
        } else {
            type_dialog.show();
        }
    }


    /**
     * 获取违法类型
     */
    private void getIllegalcategories() {
//        if (illegalCategoriesEntities != null) {
//            showTypeDialog();
//            return;
//        }
//        if (progressDialog == null) {
//            progressDialog = new ProgressDialog(this);
//        }
//        progressDialog.setMessage("正在获取中");
//        progressDialog.show();
//        call = RetrofitUtils.creatApi(HomeService.class).getIllegalCategories();
//        call.enqueue(new MyCallback<BaseCallEntity<List<IllegalCategoriesEntity>>>() {
//            @Override
//            public void onSuc(Response<BaseCallEntity<List<IllegalCategoriesEntity>>> response) {
//                progressDialog.dismiss();
//                if (response.body().getData() != null) {
//                    illegalCategoriesEntities = response.body().getData();
//                    showTypeDialog();
//                }
//            }
//
//            @Override
//            public void onSucOther(Response<BaseCallEntity<List<IllegalCategoriesEntity>>> response) {
//                progressDialog.dismiss();
//                ToastUtil.TShort(WeiZhangJuBaoActivity.this, response.body().status + " " + response.body().error);
//            }
//
//            @Override
//            public void onFail(String message) {
//                progressDialog.dismiss();
//                Toast.makeText(WeiZhangJuBaoActivity.this, "" + message, Toast.LENGTH_LONG).show();
//            }
//        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (videoView != null) {
            videoView.pause();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (videoView != null) {
            videoView.stopPlayback();
        }
    }

//    @Override
//    public void onRegeocodeSearched(RegeocodeResult regeocodeResult, int i) {
//        RegeocodeAddress regeocodeAddress = regeocodeResult.getRegeocodeAddress();
//        String formatAddress = regeocodeAddress.getFormatAddress();
//        String simpleAddress = formatAddress.substring(9);
////        if (TextUtils.isEmpty(tv_address.getText())) {
//        tv_address.setText("" + simpleAddress);
////        }
//    }

//    @Override
//    public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {
//
//    }


    public class TypeAdapter extends BaseAdapter {
        private List<IllegalCategoriesEntity> infos = new ArrayList<>();
        private LayoutInflater inflater;

        public TypeAdapter(Context mContext, List<IllegalCategoriesEntity> infos) {
            this.infos = infos;
            inflater = LayoutInflater.from(mContext);
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
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.dialog_ddp_cartype_item, parent, false);
                viewHolder = new ViewHolder();
                viewHolder.tv_weizhangtype = (TextView) convertView.findViewById(R.id.tv_weizhangtype);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.tv_weizhangtype.setText("" + infos.get(position).getName());
            viewHolder.tv_weizhangtype.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    tv_type.setText("" + infos.get(position).getName());
                    illegalID = infos.get(position).getId();
                    if (type_dialog != null) {
                        type_dialog.dismiss();
                    }
                }
            });
            return convertView;
        }

        class ViewHolder {
            public TextView tv_weizhangtype;
        }
    }


    public class CityAdapter extends BaseAdapter {
        private List<String> infos = new ArrayList<>();
        private LayoutInflater inflater;
        private Context mContext;

        public CityAdapter(Context mContext, List<String> infos) {
            this.mContext = mContext;
            this.infos = infos;
            inflater = LayoutInflater.from(mContext);
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
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.dialog_ddp_weizhang_city_item, parent, false);
                viewHolder = new ViewHolder();
                viewHolder.tv_content = (SquareTextView) convertView.findViewById(R.id.tv_content);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            if (select_city == position) {
                viewHolder.tv_content.setBackgroundResource(R.drawable.drawable_ddp_weizhang_city);
                viewHolder.tv_content.setTextColor(ContextCompat.getColor(mContext, R.color.white));
            } else {
                viewHolder.tv_content.setBackgroundResource(R.drawable.drawable_ddp_weizhang_city_normal);
                viewHolder.tv_content.setTextColor(ContextCompat.getColor(mContext, R.color.color_666666));
            }
            viewHolder.tv_content.setText("" + infos.get(position));
            viewHolder.tv_content.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    select_city = position;
                    notifyDataSetChanged();
                    city_dialog_content.setText("" + citys[select_city] + citys_AZ[select_city_AZ]);
                }
            });
            return convertView;
        }

        class ViewHolder {
            public SquareTextView tv_content;
        }
    }


    public class City2Adapter extends BaseAdapter {
        private List<String> infos = new ArrayList<>();
        private LayoutInflater inflater;
        private Context mContext;

        public City2Adapter(Context mContext, List<String> infos) {
            this.mContext = mContext;
            this.infos = infos;
            inflater = LayoutInflater.from(mContext);
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
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.dialog_ddp_weizhang_city_item, parent, false);
                viewHolder = new ViewHolder();
                viewHolder.tv_content = (TextView) convertView.findViewById(R.id.tv_content);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            if (select_city_AZ == position) {
                viewHolder.tv_content.setBackgroundResource(R.drawable.drawable_ddp_weizhang_city);
                viewHolder.tv_content.setTextColor(ContextCompat.getColor(mContext, R.color.white));
            } else {
                viewHolder.tv_content.setBackgroundResource(R.drawable.drawable_ddp_weizhang_city_normal);
                viewHolder.tv_content.setTextColor(ContextCompat.getColor(mContext, R.color.color_666666));
            }
            viewHolder.tv_content.setText("" + infos.get(position));
            viewHolder.tv_content.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    select_city_AZ = position;
                    notifyDataSetChanged();
                    city_dialog_content.setText("" + citys[select_city] + citys_AZ[select_city_AZ]);
                }
            });
            return convertView;
        }

        class ViewHolder {
            public TextView tv_content;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(WeiZhangJuBaoEvent event) {
        Log.d(TAG, "WeiZhangJuBaoEvent");
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        if (event.isSuccess()) {
            Toast.makeText(this, "上传成功", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            tv_commit.setEnabled(true);
            Toast.makeText(this, "上传失败", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        if (type_dialog != null && type_dialog.isShowing()) {
            type_dialog.dismiss();
        }
        super.onDestroy();
//        if (call != null) {
//            call.cancel();
//        }
        EventBus.getDefault().unregister(this);
    }
}
