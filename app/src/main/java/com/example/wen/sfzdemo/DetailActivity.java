package com.example.wen.sfzdemo;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.ocr.sdk.OCR;
import com.baidu.ocr.sdk.OnResultListener;
import com.baidu.ocr.sdk.exception.OCRError;
import com.baidu.ocr.sdk.model.IDCardParams;
import com.baidu.ocr.sdk.model.IDCardResult;

import java.io.File;

/**
 *
 * Created by jwj on 2018/1/10.
 */

public class DetailActivity extends Activity {

    private String cardtype;
    private String cardimage;

    private TextView mContent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        mContent = (TextView) findViewById(R.id.content);
        Intent intent = getIntent();
        cardtype = intent.getStringExtra("cardtype");
        cardimage = intent.getStringExtra("cardimage");
        Log.i("charge ID card", cardtype);
        Log.i("charge ID card", cardimage);
        File file = new File(cardimage);
        ImageView cardimg = (ImageView) findViewById(R.id.cardimage);
        if (file.exists()) {
            Bitmap bm = BitmapFactory.decodeFile(cardimage);
            cardimg.setImageBitmap(bm);
        }
            recIDCard(cardtype, cardimage);
    }

    /**
     *
     * 解析身份证图片
     *
     * @param idCardSide 身份证正反面
     * @param filePath   图片路径
     */
    private void recIDCard(final String idCardSide, String filePath) {

        Log.i("charge ID card", idCardSide);

        IDCardParams param = new IDCardParams();
        param.setImageFile(new File(filePath));
        // 设置身份证正反面
        param.setIdCardSide(idCardSide);
        // 设置方向检测
        param.setDetectDirection(true);
        // 设置图像参数压缩质量0-100, 越大图像质量越好但是请求时间越长。 不设置则默认值为20
        param.setImageQuality(40);

        OCR.getInstance().recognizeIDCard(param, new OnResultListener<IDCardResult>() {
            @Override
            public void onResult(IDCardResult idCardResult) {

                Log.i("charge ID card", String.valueOf(idCardResult));

                if (idCardResult != null) {

                    if (idCardSide.equals("back")) {

                        String signDate = "";
                        String expiryDate = "";
                        String issueAuthority = "";
                        if (idCardResult.getSignDate() != null) {
                            signDate = idCardResult.getSignDate().toString();
                        }
                        if (idCardResult.getExpiryDate() != null) {
                            expiryDate = idCardResult.getExpiryDate().toString();
                        }
                        if (idCardResult.getIssueAuthority() != null) {
                            issueAuthority = idCardResult.getIssueAuthority().toString();
                        }

                        mContent.setText("签发机关: " + issueAuthority + "\n\n" +
                                "有效期限: " + signDate + "-" + expiryDate + "\n\n");
                    }else {

                        String name = "";
                        String sex = "";
                        String nation = "";
                        String num = "";
                        String address = "";
                        if (idCardResult.getName() != null) {
                            name = idCardResult.getName().toString();
                        }
                        if (idCardResult.getGender() != null) {
                            sex = idCardResult.getGender().toString();
                        }
                        if (idCardResult.getEthnic() != null) {
                            nation = idCardResult.getEthnic().toString();
                        }
                        if (idCardResult.getIdNumber() != null) {
                            num = idCardResult.getIdNumber().toString();
                        }
                        if (idCardResult.getAddress() != null) {
                            address = idCardResult.getAddress().toString();
                        }

                        mContent.setText("姓名: " + name + "\n\n" +
                                "性别: " + sex + "\n\n" +
                                "民族: " + nation + "\n\n" +
                                "身份证号码: " + num + "\n\n" +
                                "住址: " + address + "\n\n");
                    }


                }

            }

            @Override
            public void onError(OCRError ocrError) {

                Toast.makeText(DetailActivity.this, "识别出错,请查看log错误代码", Toast.LENGTH_SHORT).show();
                Log.d("MainActivity", "onError: " + ocrError.getMessage());

            }
        });

    }




    @Override
    protected void onDestroy() {
        super.onDestroy();

        // 释放内存资源
        OCR.getInstance().release();
    }

}
