/*
 * Copyright 2017 jiajunhui<junhui_jia@163.com>
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.kk.taurus.playerbase.setting;

import android.util.Log;
import android.view.View;

/**
 * Created by Taurus on 2017/11/20.
 */

public class RenderMeasure {

    private final String TAG = "RenderMeasure";

    private int mVideoWidth;
    private int mVideoHeight;

    private int mMeasureWidth;
    private int mMeasureHeight;

    private AspectRatio mCurrAspectRatio = AspectRatio.AspectRatio_FIT_PARENT;

    public void doMeasure(int widthMeasureSpec, int heightMeasureSpec){
        int width = View.getDefaultSize(mVideoWidth, widthMeasureSpec);
        int height = View.getDefaultSize(mVideoHeight, heightMeasureSpec);
        int widthSpecMode = View.MeasureSpec.getMode(widthMeasureSpec);
        int widthSpecSize = View.MeasureSpec.getSize(widthMeasureSpec);
        int heightSpecMode = View.MeasureSpec.getMode(heightMeasureSpec);
        int heightSpecSize = View.MeasureSpec.getSize(heightMeasureSpec);
        if(mVideoWidth > 0 && mVideoHeight > 0){
            if (widthSpecMode == View.MeasureSpec.AT_MOST && heightSpecMode == View.MeasureSpec.AT_MOST) {
                float specAspectRatio = (float) widthSpecSize / (float) heightSpecSize;
                float displayAspectRatio;
                switch (mCurrAspectRatio){
                    case AspectRatio_16_9:
                        displayAspectRatio = 16.0f / 9.0f;
                        break;
                    case AspectRatio_4_3:
                        displayAspectRatio = 4.0f / 3.0f;
                        break;
                    case AspectRatio_FILL_PARENT:
                    case AspectRatio_FIT_PARENT:
                    case AspectRatio_ORIGIN:
                    default:
                        displayAspectRatio = (float) mVideoWidth / (float) mVideoHeight;
                        break;
                }
                boolean shouldBeWidth = displayAspectRatio > specAspectRatio;

                switch (mCurrAspectRatio){
                    case AspectRatio_16_9:
                    case AspectRatio_4_3:
                    case AspectRatio_FIT_PARENT:
                        if (shouldBeWidth) {
                            // too wide, fix width
                            width = widthSpecSize;
                            height = (int) (width / displayAspectRatio);
                        } else {
                            // too high, fix height
                            height = heightSpecSize;
                            width = (int) (height * displayAspectRatio);
                        }
                        break;
                    case AspectRatio_FILL_PARENT:
                        if (shouldBeWidth) {
                            // not high enough, fix height
                            height = heightSpecSize;
                            width = (int) (height * displayAspectRatio);
                        } else {
                            // not wide enough, fix width
                            width = widthSpecSize;
                            height = (int) (width / displayAspectRatio);
                        }
                        break;
                    default:
                        if (shouldBeWidth) {
                            // too wide, fix width
                            width = Math.min(mVideoWidth, widthSpecSize);
                            height = (int) (width / displayAspectRatio);
                        } else {
                            // too high, fix height
                            height = Math.min(mVideoHeight, heightSpecSize);
                            width = (int) (height * displayAspectRatio);
                        }
                        break;
                }

            }else if(widthSpecMode == View.MeasureSpec.EXACTLY && heightSpecMode == View.MeasureSpec.EXACTLY){
                // the size is fixed
                width = widthSpecSize;
                height = heightSpecSize;

                // for compatibility, we adjust size based on aspect ratio
                if (mVideoWidth * height < width * mVideoHeight) {
                    //Log.i("@@@", "image too wide, correcting");
                    width = height * mVideoWidth / mVideoHeight;
                } else if (mVideoWidth * height > width * mVideoHeight) {
                    //Log.i("@@@", "image too tall, correcting");
                    height = width * mVideoHeight / mVideoWidth;
                }
            }else if (widthSpecMode == View.MeasureSpec.EXACTLY) {
                // only the width is fixed, adjust the height to match aspect ratio if possible
                width = widthSpecSize;
                height = width * mVideoHeight / mVideoWidth;
                if (heightSpecMode == View.MeasureSpec.AT_MOST && height > heightSpecSize) {
                    // couldn't match aspect ratio within the constraints
                    height = heightSpecSize;
                }
            }else if (heightSpecMode == View.MeasureSpec.EXACTLY) {
                // only the height is fixed, adjust the width to match aspect ratio if possible
                height = heightSpecSize;
                width = height * mVideoWidth / mVideoHeight;
                if (widthSpecMode == View.MeasureSpec.AT_MOST && width > widthSpecSize) {
                    // couldn't match aspect ratio within the constraints
                    width = widthSpecSize;
                }
            }else{
                // neither the width nor the height are fixed, try to use actual video size
                width = mVideoWidth;
                height = mVideoHeight;
                if (heightSpecMode == View.MeasureSpec.AT_MOST && height > heightSpecSize) {
                    // too tall, decrease both width and height
                    height = heightSpecSize;
                    width = height * mVideoWidth / mVideoHeight;
                }
                if (widthSpecMode == View.MeasureSpec.AT_MOST && width > widthSpecSize) {
                    // too wide, decrease both width and height
                    width = widthSpecSize;
                    height = width * mVideoHeight / mVideoWidth;
                }
            }
        }
        mMeasureWidth = width;
        mMeasureHeight = height;
    }

    public void setVideoSize(int videoWidth, int videoHeight){
        Log.d(TAG,"videoWidth = " + videoWidth + " videoHeight = " + videoHeight);
        this.mVideoWidth = videoWidth;
        this.mVideoHeight = videoHeight;
    }

    public void setAspectRatio(AspectRatio aspectRatio){
        this.mCurrAspectRatio = aspectRatio;
    }

    public int getMeasureWidth() {
        return mMeasureWidth;
    }

    public int getMeasureHeight() {
        return mMeasureHeight;
    }
}
