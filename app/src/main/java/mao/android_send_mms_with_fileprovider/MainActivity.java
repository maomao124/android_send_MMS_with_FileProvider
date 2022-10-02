package mao.android_send_mms_with_fileprovider;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import mao.android_send_mms_with_fileprovider.entiey.ImageInfo;

public class MainActivity extends AppCompatActivity
{

    private static final String[] PERMISSIONS = new String[]
            {
                    Manifest.permission.READ_EXTERNAL_STORAGE
            };

    private static final int PERMISSION_REQUEST_CODE = 1;

    private final List<ImageInfo> imageList = new ArrayList<>();

    private GridLayout gl_appendix;
    private EditText et_phone;
    private EditText et_title;
    private EditText et_message;

    private static final String TAG = "MainActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        et_phone = findViewById(R.id.et_phone);
        et_title = findViewById(R.id.et_title);
        et_message = findViewById(R.id.et_message);
        gl_appendix = findViewById(R.id.gl_appendix);

        //手动让MediaStore扫描入库
        MediaScannerConnection.scanFile(this,
                new String[]{Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString()},
                null, null);

        if (checkPermission(this, PERMISSIONS, PERMISSION_REQUEST_CODE))
        {
            // 加载图片列表
            loadImageList();

            if (imageList.size() <= 6)
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("图片不够")
                        .setMessage("图片数量不足6张,当前为" + imageList.size() + "张")
                        .setPositiveButton("确定", null)
                        .create()
                        .show();
            }

            // 显示图像网格
            showImageGrid();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE &&
                checkGrant(grantResults))
        {
            // 加载图片列表
            loadImageList();

            if (imageList.size() <= 6)
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("图片不够")
                        .setMessage("图片数量不足6张")
                        .setPositiveButton("确定", null)
                        .create()
                        .show();
            }


            // 显示图像网格
            showImageGrid();
        }
        else
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("没有权限")
                    .setMessage("没有相关权限")
                    .setPositiveButton("确定", null)
                    .create()
                    .show();
        }
    }

    // 显示图像网格
    private void showImageGrid()
    {
        gl_appendix.removeAllViews();
        for (ImageInfo image : imageList)
        {
            // image -> ImageView
            ImageView iv_appendix = new ImageView(this);
            Bitmap bitmap = BitmapFactory.decodeFile(image.getPath());
            iv_appendix.setImageBitmap(bitmap);
            // 设置图像视图的缩放类型
            iv_appendix.setScaleType(ImageView.ScaleType.FIT_CENTER);
            // 设置图像视图的布局参数
            int px = 400;
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(px, px);
            iv_appendix.setLayoutParams(params);
            // 设置图像视图的内部间距
            int padding = 30;
            iv_appendix.setPadding(padding, padding, padding, padding);
            iv_appendix.setOnClickListener(v ->
            {
                sendMms(et_phone.getText().toString(),
                        et_title.getText().toString(),
                        et_message.getText().toString(),
                        image.getPath());
            });
            // 把图像视图添加至网格布局
            gl_appendix.addView(iv_appendix);
        }
    }

    // 加载图片列表
    @SuppressLint("Range")
    private void loadImageList()
    {
        //MediaStore
        String[] columns = new String[]
                {
                        MediaStore.Images.Media._ID, // 编号
                        MediaStore.Images.Media.TITLE, // 标题
                        MediaStore.Images.Media.SIZE,// 文件大小
                        MediaStore.Images.Media.DATA,// 文件路径
                };
        // 图片大小在300KB以内
        @SuppressLint("Recycle") Cursor cursor = getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                columns,
                "_size < 307200",
                null,
                "_size DESC"
        );
        int count = 0;
        if (cursor != null)
        {
            while (cursor.moveToNext() && count < 6)
            {
                ImageInfo image = new ImageInfo();
                image.setId(cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media._ID)));
                image.setName(cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.TITLE)));
                image.setSize(cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media.SIZE)));
                image.setPath(cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA)));
                count++;
                imageList.add(image);
                Log.d(TAG, "loadImageList: " + image);
            }
        }

    }

    // 发送带图片的彩信
    private void sendMms(String phone, String title, String message, String path)
    {
        // 根据指定路径创建一个Uri对象
        Uri uri = Uri.parse(path);
        // 兼容Android7.0，把访问文件的Uri方式改为FileProvider
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
        {
            // 通过FileProvider获得文件的Uri访问方式
            uri = FileProvider.getUriForFile(this, getString(R.string.file_provider), new File(path));
        }
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        // Intent 的接受者将被准许读取Intent 携带的URI数据
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        // 彩信发送的目标号码
        intent.putExtra("address", phone);
        // 彩信的标题
        intent.putExtra("subject", title);
        // 彩信的内容
        intent.putExtra("sms_body", message);
        // 彩信的图片附件
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        // 彩信的附件为图片
        intent.setType("image/*");
        // 因为未指定要打开哪个页面，所以系统会在底部弹出选择窗口
        startActivity(intent);
        toastShow("请在弹窗中选择短信或者信息应用");
    }

    /**
     * 显示消息
     *
     * @param message 消息
     */
    private void toastShow(String message)
    {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }


    /**
     * 检查权限结果数组，
     *
     * @param grantResults 授予相应权限的结果是PackageManager.PERMISSION_GRANTED
     *                     或PackageManager.PERMISSION_DENIED
     *                     从不为空
     * @return boolean 返回true表示都已经获得授权 返回false表示至少有一个未获得授权
     */
    public static boolean checkGrant(int[] grantResults)
    {
        boolean result = true;
        if (grantResults != null)
        {
            for (int grant : grantResults)
            {
                //遍历权限结果数组中的每条选择结果
                if (grant != PackageManager.PERMISSION_GRANTED)
                {
                    //未获得授权，返回false
                    result = false;
                    break;
                }
            }
        }
        else
        {
            result = false;
        }
        return result;
    }


    /**
     * 检查某个权限
     *
     * @param act         Activity对象
     * @param permission  许可
     * @param requestCode 请求代码
     * @return boolean 返回true表示已启用该权限，返回false表示未启用该权限
     */
    public static boolean checkPermission(Activity act, String permission, int requestCode)
    {
        return checkPermission(act, new String[]{permission}, requestCode);
    }


    /**
     * 检查多个权限
     *
     * @param act         Activity对象
     * @param permissions 权限
     * @param requestCode 请求代码
     * @return boolean 返回true表示已完全启用权限，返回false表示未完全启用权限
     */
    @SuppressWarnings("all")
    public static boolean checkPermission(Activity act, String[] permissions, int requestCode)
    {
        boolean result = true;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            int check = PackageManager.PERMISSION_GRANTED;
            //通过权限数组检查是否都开启了这些权限
            for (String permission : permissions)
            {
                check = ContextCompat.checkSelfPermission(act, permission);
                if (check != PackageManager.PERMISSION_GRANTED)
                {
                    //有个权限没有开启，就跳出循环
                    break;
                }
            }
            if (check != PackageManager.PERMISSION_GRANTED)
            {
                //未开启该权限，则请求系统弹窗，好让用户选择是否立即开启权限
                ActivityCompat.requestPermissions(act, permissions, requestCode);
                result = false;
            }
        }
        return result;
    }
}