package com.qianfan.qianfanddpdemo.utils;

import android.net.Uri;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.request.ImageRequestBuilder;

import java.io.File;

/**
 * 图片加载
 *
 * @author WangJing on 2016/11/18 0018 11:00
 * @e-mail wangjinggm@gmail.com
 * @see [相关类/方法](可选)
 */

public class ImageLoader {

    /**
     * 加载图片
     *
     * @param simpleDraweeView
     * @param url
     */
    public static void load(SimpleDraweeView simpleDraweeView, String url) {
        simpleDraweeView.setImageURI(Uri.parse("" + url));
    }


    /**
     * 加载图片-可能出现大图，需要利用ResizeOptions处理，否则会报错 Bitmap too large to be uploaded into a texture
     *
     * @param simpleDraweeView
     * @param url
     */
    public static void loadResize(SimpleDraweeView simpleDraweeView, String url) {
        ImageRequestBuilder imageRequestBuilder = ImageRequestBuilder.newBuilderWithSource(Uri.parse("" + url))
                .setResizeOptions(new ResizeOptions(460, 460));
        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setImageRequest(imageRequestBuilder.build())
                .setOldController(simpleDraweeView.getController())
                .build();
        simpleDraweeView.setController(controller);
    }

    /**
     * 加载图片-可能出现大图，需要利用ResizeOptions处理，否则会报错 Bitmap too large to be uploaded into a texture
     *
     * @param simpleDraweeView
     * @param url
     */
    public static void loadResize(SimpleDraweeView simpleDraweeView, String url, int re_width, int re_height) {
        if (url.startsWith("/storage/") || url.startsWith("/data")) {
            url = "file://" + url;
        }
        ImageRequestBuilder imageRequestBuilder = ImageRequestBuilder.newBuilderWithSource(Uri.parse("" + url))
                .setResizeOptions(new ResizeOptions(re_width, re_height));
        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setImageRequest(imageRequestBuilder.build())
                .setOldController(simpleDraweeView.getController())
                .build();
        simpleDraweeView.setController(controller);
    }


    /**
     * 加载图片-可能出现大图，需要利用ResizeOptions处理，否则会报错 Bitmap too large to be uploaded into a texture
     *
     * @param simpleDraweeView
     * @param url
     */
    public static void loadLocalResize(SimpleDraweeView simpleDraweeView, String url, int re_width, int re_height) {
        ImageRequestBuilder imageRequestBuilder = ImageRequestBuilder.newBuilderWithSource(Uri.fromFile(new File(url)))
                .setLocalThumbnailPreviewsEnabled(true)
                .setResizeOptions(new ResizeOptions(re_width, re_height));

        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setImageRequest(imageRequestBuilder.build())
                .setOldController(simpleDraweeView.getController())
                .build();
        simpleDraweeView.setController(controller);
    }

    /**
     * 在线考试模块 加载图片方法
     * @param simpleDraweeView
     * @param url
     */
    public static void loadExamImageWithGif(SimpleDraweeView simpleDraweeView, String url) {
        ImageRequestBuilder imageRequestBuilder = ImageRequestBuilder.newBuilderWithSource(Uri.parse(url))
                .setLocalThumbnailPreviewsEnabled(true);

        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setImageRequest(imageRequestBuilder.build())
                .setAutoPlayAnimations(true)
                .setOldController(simpleDraweeView.getController())
                .build();
        simpleDraweeView.setController(controller);
    }

//    /**
//     * picasso加载图片
//     * @param imageView
//     * @param url
//     */
//    public static void picassoLoad(ImageView imageView, String url) {
//        Picasso.with(imageView.getContext()).load("" + url).resize(400,400).into(imageView);
//    }

//    /**
//     * Glide加载图片
//     *
//     * @param imageView
//     * @param url
//     */
//    public static void glideLoad(ImageView imageView, String url) {
//        Glide.with(imageView.getContext()).load("" + url).placeholder(R.color.color_placeholder).into(imageView);
//    }
//
//    /**
//     * Glide加载图片
//     *
//     * @param imageView
//     * @param url
//     */
//    public static void glideLoadResize(ImageView imageView, String url,int width,int height) {
//        Glide.with(imageView.getContext()).load("" + url)
//                .placeholder(R.color.color_placeholder)
//                .override(width,height)
//                .into(imageView);
//    }

}

