package com.lynx.argus.util;

/**
 * Created with IntelliJ IDEA.
 * User: chris.liu
 * Date: 13-11-15 下午1:37
 */

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.widget.ImageView;
import com.lynx.lib.cache.CacheService;
import com.lynx.lib.cache.CacheService.CacheType;
import com.lynx.lib.core.LFApplication;
import com.lynx.lib.util.StringUtil;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AsyncImageLoader {
    private static final String TAG = "AsynImageLoader";
    // 缓存下载过的图片的Map
    private Context context;
    private Map<String, SoftReference<Bitmap>> caches;
    private CacheService cacheService;
    // 任务队列
    private List<Task> taskQueue;
    private boolean isRunning = false;

    public AsyncImageLoader(Context context) {
        this.context = context;
        this.cacheService = (CacheService) LFApplication.instance().service("cache");
        caches = new HashMap<String, SoftReference<Bitmap>>();
        taskQueue = new ArrayList<AsyncImageLoader.Task>();
        // 启动图片下载线程
        isRunning = true;
        new Thread(runnable).start();
    }

    /**
     * @param imageView 需要延迟加载图片的对象
     * @param url       图片的URL地址
     * @param resId     图片加载过程中显示的图片资源
     */
    public void showAsyncImage(ImageView imageView, String url, int resId) {
        imageView.setTag(url);
        Bitmap bitmap = loadAsyncImage(url, getImageCallback(imageView, resId));

        if (bitmap == null) {
            imageView.setImageResource(resId);
        } else {
            imageView.setImageBitmap(bitmap);
        }
    }

    public Bitmap loadAsyncImage(String path, ImageCallback callback) {
        Bitmap bmp = (Bitmap) cacheService.get(StringUtil.MD5Encode(path), CacheType.Memory);
        if (bmp != null) {
            return bmp;
        } else {
            CacheType[] cacheTypes = {CacheType.Memory};
            cacheService.remove(path, cacheTypes);
            Task task = new Task();
            task.path = path;
            task.callback = callback;
            if (!taskQueue.contains(task)) {
                taskQueue.add(task);
                // 唤醒任务下载队列
                synchronized (runnable) {
                    runnable.notify();
                }
            }
        }
        return null;
    }

    /**
     * @param imageView
     * @param resId     图片加载完成前显示的图片资源ID
     * @return
     */
    private ImageCallback getImageCallback(final ImageView imageView, final int resId) {
        return new ImageCallback() {
            @Override
            public void loadImage(String path, Bitmap bitmap) {
                if (path.equals(imageView.getTag().toString())) {
                    imageView.setImageBitmap(bitmap);
                } else {
                    imageView.setImageResource(resId);
                }
            }
        };
    }

    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            // 子线程中返回的下载完成的任务
            Task task = (Task) msg.obj;
            // 调用callback对象的loadImage方法，并将图片路径和图片回传给adapter
            task.callback.loadImage(task.path, task.bitmap);
            CacheType[] cacheTypes = {CacheType.Memory, CacheType.File};
            cacheService.put(StringUtil.MD5Encode(task.path), task.bitmap, cacheTypes);
        }

    };

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            while (isRunning) {
                // 当队列中还有未处理的任务时，执行下载任务
                while (taskQueue.size() > 0) {
                    // 获取第一个任务，并将之从任务队列中删除
                    Task task = taskQueue.remove(0);
                    // 将下载的图片添加到缓存
                    task.bitmap = ImageUtil.getbitmap(task.path);
                    caches.put(task.path, new SoftReference<Bitmap>(task.bitmap));
                    if (handler != null) {
                        // 创建消息对象，并将完成的任务添加到消息对象中
                        Message msg = handler.obtainMessage();
                        msg.obj = task;
                        // 发送消息回主线程
                        handler.sendMessage(msg);
                    }
                }

                //如果队列为空,则令线程等待
                synchronized (this) {
                    try {
                        this.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    };

    //回调接口
    public interface ImageCallback {
        void loadImage(String path, Bitmap bitmap);
    }

    class Task {
        String path; // 下载任务的下载路径
        Bitmap bitmap; // 下载的图片
        ImageCallback callback;// 回调对象

        @Override
        public boolean equals(Object o) {
            Task task = (Task) o;
            return task.path.equals(path);
        }
    }
}
