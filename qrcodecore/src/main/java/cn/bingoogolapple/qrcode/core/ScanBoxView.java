package cn.bingoogolapple.qrcode.core;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

public class ScanBoxView extends View {
    private int mMoveStepDistance;
    private int mAnimDelayTime;

    private Rect mFramingRect;
    private float mScanLineTop;
    private float mScanLineLeft;
    private Paint mPaint;
    private TextPaint mTipPaint;

    private int mMaskColor;
    private int mCornerColor;
    private int mCornerLength;
    private int mCornerSize;
    private int mRectWidth;
    private int mRectHeight;
    private int mTopOffset;
    private int mScanLineSize;
    private int mScanLineColor;
    private int mScanLineMargin;
    private boolean mIsShowDefaultScanLineDrawable;
    private Drawable mCustomScanLineDrawable;
    private Bitmap mScanLineBitmap;
    private int mBorderSize;
    private int mBorderColor;
    private int mAnimTime;
    private boolean mIsCenterVertical;
    private int mToolbarHeight;
    private boolean mIsBarcode;
    private String mTipText;
    private int mTipTextSize;
    private int mTipTextColor;
    private boolean mIsTipTextBelowRect;
    private int mTipTextMargin;
    private int mTipBackgroundColor;

    private float mHalfCornerSize;
    private StaticLayout mTipTextSl;
    private int mTipBackgroundRadius;

    public ScanBoxView(Context context) {
        super(context);
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mMaskColor = Color.parseColor("#33FFFFFF");
        mCornerColor = Color.WHITE;
        mCornerLength = BGAQRCodeUtil.dp2px(context, 20);
        mCornerSize = BGAQRCodeUtil.dp2px(context, 3);
        mScanLineSize = BGAQRCodeUtil.dp2px(context, 1);
        mScanLineColor = Color.WHITE;
        mTopOffset = BGAQRCodeUtil.dp2px(context, 90);
        mRectWidth = BGAQRCodeUtil.dp2px(context, 200);
        mRectHeight = BGAQRCodeUtil.dp2px(context, 200);
        mScanLineMargin = 0;
        mIsShowDefaultScanLineDrawable = false;
        mCustomScanLineDrawable = null;
        mScanLineBitmap = null;
        mBorderSize = BGAQRCodeUtil.dp2px(context, 1);
        mBorderColor = Color.WHITE;
        mAnimTime = 1000;
        mIsCenterVertical = false;
        mToolbarHeight = 0;
        mIsBarcode = false;
        mMoveStepDistance = BGAQRCodeUtil.dp2px(context, 2);
//        mTipText = "将取景框对准二维码\n即可自动扫描";
        mTipText = "将二维码/条形码放到框内，即可自动扫描";
        mTipTextSize = BGAQRCodeUtil.sp2px(context, 14);
        mTipTextColor = Color.WHITE;
        mIsTipTextBelowRect = false;
        mTipTextMargin = BGAQRCodeUtil.dp2px(context, 20);
        mTipBackgroundColor = Color.parseColor("#22000000");

        mTipPaint = new TextPaint();
        mTipPaint.setAntiAlias(true);
        mTipPaint.setTextSize(mTipTextSize);
        mTipPaint.setColor(mTipTextColor);

        mTipBackgroundRadius = BGAQRCodeUtil.dp2px(context, 4);
    }

    public void initCustomAttrs(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.QRCodeView);
        final int count = typedArray.getIndexCount();
        for (int i = 0; i < count; i++) {
            initCustomAttr(typedArray.getIndex(i), typedArray);
        }
        typedArray.recycle();

        afterInitCustomAttrs();
    }

    private void initCustomAttr(int attr, TypedArray typedArray) {
        if (attr == R.styleable.QRCodeView_qrcv_topOffset) {
            mTopOffset = typedArray.getDimensionPixelSize(attr, mTopOffset);
        } else if (attr == R.styleable.QRCodeView_qrcv_cornerSize) {
            mCornerSize = typedArray.getDimensionPixelSize(attr, mCornerSize);
        } else if (attr == R.styleable.QRCodeView_qrcv_cornerLength) {
            mCornerLength = typedArray.getDimensionPixelSize(attr, mCornerLength);
        } else if (attr == R.styleable.QRCodeView_qrcv_scanLineSize) {
            mScanLineSize = typedArray.getDimensionPixelSize(attr, mScanLineSize);
        } else if (attr == R.styleable.QRCodeView_qrcv_rectWidth) {
            mRectWidth = typedArray.getDimensionPixelSize(attr, mRectWidth);
        } else if (attr == R.styleable.QRCodeView_qrcv_maskColor) {
            mMaskColor = typedArray.getColor(attr, mMaskColor);
        } else if (attr == R.styleable.QRCodeView_qrcv_cornerColor) {
            mCornerColor = typedArray.getColor(attr, mCornerColor);
        } else if (attr == R.styleable.QRCodeView_qrcv_scanLineColor) {
            mScanLineColor = typedArray.getColor(attr, mScanLineColor);
        } else if (attr == R.styleable.QRCodeView_qrcv_scanLineMargin) {
            mScanLineMargin = typedArray.getDimensionPixelSize(attr, mScanLineMargin);
        } else if (attr == R.styleable.QRCodeView_qrcv_isShowDefaultScanLineDrawable) {
            mIsShowDefaultScanLineDrawable = typedArray.getBoolean(attr, mIsShowDefaultScanLineDrawable);
        } else if (attr == R.styleable.QRCodeView_qrcv_customScanLineDrawable) {
            mCustomScanLineDrawable = typedArray.getDrawable(attr);
        } else if (attr == R.styleable.QRCodeView_qrcv_borderSize) {
            mBorderSize = typedArray.getDimensionPixelSize(attr, mBorderSize);
        } else if (attr == R.styleable.QRCodeView_qrcv_borderColor) {
            mBorderColor = typedArray.getColor(attr, mBorderColor);
        } else if (attr == R.styleable.QRCodeView_qrcv_animTime) {
            mAnimTime = typedArray.getInteger(attr, mAnimTime);
        } else if (attr == R.styleable.QRCodeView_qrcv_isCenterVertical) {
            mIsCenterVertical = typedArray.getBoolean(attr, mIsCenterVertical);
        } else if (attr == R.styleable.QRCodeView_qrcv_toolbarHeight) {
            mToolbarHeight = typedArray.getDimensionPixelSize(attr, mToolbarHeight);
        } else if (attr == R.styleable.QRCodeView_qrcv_rectHeight) {
            mRectHeight = typedArray.getDimensionPixelSize(attr, mRectHeight);
        } else if (attr == R.styleable.QRCodeView_qrcv_isBarcode) {
            mIsBarcode = typedArray.getBoolean(attr, mIsBarcode);
        }
    }

    private void afterInitCustomAttrs() {
        if (mIsShowDefaultScanLineDrawable) {
            Bitmap defaultScanLineBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.qrcode_default_scan_line);
            mScanLineBitmap = BGAQRCodeUtil.makeTintBitmap(defaultScanLineBitmap, mScanLineColor);

            if (mIsBarcode) {
                mScanLineBitmap = BGAQRCodeUtil.adjustPhotoRotation(mScanLineBitmap, 90);
            }
        }
        if (mCustomScanLineDrawable != null) {
            mScanLineBitmap = ((BitmapDrawable) mCustomScanLineDrawable).getBitmap();
        }

        if (mIsBarcode) {
            mAnimDelayTime = (int) ((1.0f * mAnimTime * mMoveStepDistance) / mRectWidth);
        } else {
            mAnimDelayTime = (int) ((1.0f * mAnimTime * mMoveStepDistance) / mRectHeight);
        }
        mHalfCornerSize = 1.0f * mCornerSize / 2;

        if (mIsCenterVertical) {
            int screenHeight = BGAQRCodeUtil.getScreenResolution(getContext()).y;
            if (mToolbarHeight == 0) {
                mTopOffset = (screenHeight - mRectHeight) / 2;
            } else {
                mTopOffset = (screenHeight - mRectHeight) / 2 + mToolbarHeight / 2;
            }
        }

        mTipTextSl = new StaticLayout(mTipText, mTipPaint, mRectWidth, Layout.Alignment.ALIGN_CENTER, 1.0f, 0, true);
    }

    @Override
    public void onDraw(Canvas canvas) {
        if (mFramingRect == null) {
            return;
        }

        // 画遮罩层
        drawMask(canvas);

        // 画边框线
        drawBorderLine(canvas);

        // 画四个直角的线
        drawCornerLine(canvas);

        // 画扫描线
        drawScanLine(canvas);

        // 画提示文本
        drawTipText(canvas);

        // 移动扫描线的位置
        moveScanLine();

    }

    /**
     * 画遮罩层
     *
     * @param canvas
     */
    private void drawMask(Canvas canvas) {
        int width = canvas.getWidth();
        int height = canvas.getHeight();

        if (mMaskColor != Color.TRANSPARENT) {
            mPaint.setStyle(Paint.Style.FILL);
            mPaint.setColor(mMaskColor);
            canvas.drawRect(0, 0, width, mFramingRect.top, mPaint);
            canvas.drawRect(0, mFramingRect.top, mFramingRect.left, mFramingRect.bottom + 1, mPaint);
            canvas.drawRect(mFramingRect.right + 1, mFramingRect.top, width, mFramingRect.bottom + 1, mPaint);
            canvas.drawRect(0, mFramingRect.bottom + 1, width, height, mPaint);
        }
    }

    /**
     * 画边框线
     *
     * @param canvas
     */
    private void drawBorderLine(Canvas canvas) {
        if (mBorderSize > 0) {
            mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setColor(mBorderColor);
            mPaint.setStrokeWidth(mBorderSize);
            canvas.drawRect(mFramingRect, mPaint);
        }
    }

    /**
     * 画四个直角的线
     *
     * @param canvas
     */
    private void drawCornerLine(Canvas canvas) {
        if (mHalfCornerSize > 0) {
            mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setColor(mCornerColor);
            mPaint.setStrokeWidth(mCornerSize);
            canvas.drawLine(mFramingRect.left - mHalfCornerSize, mFramingRect.top, mFramingRect.left - mHalfCornerSize + mCornerLength, mFramingRect.top, mPaint);
            canvas.drawLine(mFramingRect.left, mFramingRect.top - mHalfCornerSize, mFramingRect.left, mFramingRect.top - mHalfCornerSize + mCornerLength, mPaint);
            canvas.drawLine(mFramingRect.right + mHalfCornerSize, mFramingRect.top, mFramingRect.right + mHalfCornerSize - mCornerLength, mFramingRect.top, mPaint);
            canvas.drawLine(mFramingRect.right, mFramingRect.top - mHalfCornerSize, mFramingRect.right, mFramingRect.top - mHalfCornerSize + mCornerLength, mPaint);

            canvas.drawLine(mFramingRect.left - mHalfCornerSize, mFramingRect.bottom, mFramingRect.left - mHalfCornerSize + mCornerLength, mFramingRect.bottom, mPaint);
            canvas.drawLine(mFramingRect.left, mFramingRect.bottom + mHalfCornerSize, mFramingRect.left, mFramingRect.bottom + mHalfCornerSize - mCornerLength, mPaint);
            canvas.drawLine(mFramingRect.right + mHalfCornerSize, mFramingRect.bottom, mFramingRect.right + mHalfCornerSize - mCornerLength, mFramingRect.bottom, mPaint);
            canvas.drawLine(mFramingRect.right, mFramingRect.bottom + mHalfCornerSize, mFramingRect.right, mFramingRect.bottom + mHalfCornerSize - mCornerLength, mPaint);
        }
    }

    /**
     * 画扫描线
     *
     * @param canvas
     */
    private void drawScanLine(Canvas canvas) {
        if (mIsBarcode) {
            if (mScanLineBitmap == null) {
                mPaint.setStyle(Paint.Style.FILL);
                mPaint.setColor(mScanLineColor);
                canvas.drawRect(mScanLineLeft, mFramingRect.top + mHalfCornerSize + mScanLineMargin, mScanLineLeft + mScanLineSize, mFramingRect.bottom - mHalfCornerSize - mScanLineMargin, mPaint);
            } else {
                RectF lineRect = new RectF(mScanLineLeft, mFramingRect.top + mHalfCornerSize + mScanLineMargin, mScanLineLeft + mScanLineBitmap.getWidth(), mFramingRect.bottom - mHalfCornerSize - mScanLineMargin);
                canvas.drawBitmap(mScanLineBitmap, null, lineRect, mPaint);
            }
        } else {
            if (mScanLineBitmap == null) {
                mPaint.setStyle(Paint.Style.FILL);
                mPaint.setColor(mScanLineColor);
                canvas.drawRect(mFramingRect.left + mHalfCornerSize + mScanLineMargin, mScanLineTop, mFramingRect.right - mHalfCornerSize - mScanLineMargin, mScanLineTop + mScanLineSize, mPaint);
            } else {
                RectF lineRect = new RectF(mFramingRect.left + mHalfCornerSize + mScanLineMargin, mScanLineTop, mFramingRect.right - mHalfCornerSize - mScanLineMargin, mScanLineTop + mScanLineBitmap.getHeight());
                canvas.drawBitmap(mScanLineBitmap, null, lineRect, mPaint);
            }
        }
    }

    /**
     * 画提示文本
     *
     * @param canvas
     */
    private void drawTipText(Canvas canvas) {
        if (TextUtils.isEmpty(mTipText) || mTipTextSl == null) {
            return;
        }

        if (mIsTipTextBelowRect) {
            mPaint.setColor(mTipBackgroundColor);
            canvas.drawRoundRect(new RectF(mFramingRect.left, mFramingRect.bottom + mTipTextMargin, mFramingRect.right, mFramingRect.bottom + mTipTextMargin + mTipTextSl.getHeight()), mTipBackgroundRadius, mTipBackgroundRadius, mPaint);
            canvas.save();
            canvas.translate(mFramingRect.left, mFramingRect.bottom + mTipTextMargin);
            mTipTextSl.draw(canvas);
            canvas.restore();
        } else {
            mPaint.setColor(mTipBackgroundColor);
            canvas.drawRoundRect(new RectF(mFramingRect.left, mFramingRect.top - mTipTextMargin - mTipTextSl.getHeight(), mFramingRect.right, mFramingRect.top - mTipTextMargin), mTipBackgroundRadius, mTipBackgroundRadius, mPaint);
            canvas.save();
            canvas.translate(mFramingRect.left, mFramingRect.top - mTipTextMargin - mTipTextSl.getHeight());
            mTipTextSl.draw(canvas);
            canvas.restore();
        }
    }

    /**
     * 移动扫描线的位置
     */
    private void moveScanLine() {
        if (mIsBarcode) {
            mScanLineLeft += mMoveStepDistance;

            int scanLineSize = mScanLineSize;
            if (mScanLineBitmap != null) {
                scanLineSize = mScanLineBitmap.getWidth();
            }
            if (mScanLineLeft < mFramingRect.left + mHalfCornerSize || mScanLineLeft + scanLineSize > mFramingRect.right - mHalfCornerSize) {
                mMoveStepDistance = -mMoveStepDistance;
            }
        } else {
            mScanLineTop += mMoveStepDistance;

            int scanLineSize = mScanLineSize;
            if (mScanLineBitmap != null) {
                scanLineSize = mScanLineBitmap.getHeight();
            }
            if (mScanLineTop < mFramingRect.top + mHalfCornerSize || mScanLineTop + scanLineSize > mFramingRect.bottom - mHalfCornerSize) {
                mMoveStepDistance = -mMoveStepDistance;
            }
        }
        postInvalidateDelayed(mAnimDelayTime, mFramingRect.left, mFramingRect.top, mFramingRect.right, mFramingRect.bottom);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        Point screenResolution = BGAQRCodeUtil.getScreenResolution(getContext());
        int leftOffset = (screenResolution.x - mRectWidth) / 2;
        mFramingRect = new Rect(leftOffset, mTopOffset, leftOffset + mRectWidth, mTopOffset + mRectHeight);

        if (mIsBarcode) {
            mScanLineLeft = leftOffset + mHalfCornerSize + 0.5f;
        } else {
            mScanLineTop = mTopOffset + mHalfCornerSize + 0.5f;
        }
    }

}