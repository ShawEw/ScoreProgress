package lib.hz.com.scoreprogress;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.view.WindowManager;

/**
 * @author jh.jiang
 * @date 2019/1/31.
 */
public class ScoreProgressView extends View {

    private static final int DEFAULT_TEXT_COLOR = 0XFFFC00D1;
    private static final int DEFAULT_TEXT_SIZE = 10;
    private static final int DEFAULT_COLOR_UNREACHED_COLOR = 0xFFd3d6da;
    private static final int DEFAULT_COLOR_REACHED_COLOR = 0xFFFF0000;
    //设置进度显示文字的所有边距
    private static final int TEXT_LEFT_RIGHT_PADDING = 6;

    private Paint mScorePaint;
    private Paint mTextPaint;

    private Context mContext;

    //进度条的底色和完成进度的颜色
    private int mScoreBackColor;
    private int mScoreForeColor;

    //进度条上方现实的文字
    private String mScoreInfo;
    //进度文字的颜色
    private int mInfoColor;
    //进度文字的字体大小
    private int mInfoTextSize;

    //进度条的起始值，当前值和结束值
    private int mCurrentScore;
    private int mStartScore;
    private int mTotalScore;

    //进度条的高度
    private int mScoreHeight;

    //view的上下内边距
    private int mPaddingTop;
    private int mPaddingBottom;
    private int mPaddingLeft;
    private int mPaddingRight;

    //用于测量文字显示区域的宽度和高度
    private Paint.FontMetricsInt mTextFontMetrics;
    private Rect mTextBound;

    //用于绘制三角形的箭头
    private Path mPath;

    //进度条和进度文字显示框的间距
    private int mLineToTextHeight;

    //三角形箭头的高度
    private int mTriangleHeight;

    //绘制进度条圆角矩形的圆角
    private int mRectCorn;

    private int mTextToScorePadding = 5;

    public ScoreProgressView(Context context) {
        this(context, null);
    }

    public ScoreProgressView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ScoreProgressView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        if (attrs != null) {
            obtainStyledAttributes(attrs);
        }
        init();
    }

    private void obtainStyledAttributes(AttributeSet attrs) {
        // 获取自定义属性
        final TypedArray attributes = getContext().obtainStyledAttributes(attrs, R.styleable.ScoreProgressView);
        mInfoColor = attributes.getColor(R.styleable.ScoreProgressView_info_color, DEFAULT_TEXT_COLOR);
        mInfoTextSize = (int) attributes.getDimension(R.styleable.ScoreProgressView_score_text_size, DEFAULT_TEXT_SIZE);
        mStartScore = attributes.getInt(R.styleable.ScoreProgressView_start_score, 0);
        mTotalScore = attributes.getInt(R.styleable.ScoreProgressView_total_score, 100);
        mScoreBackColor = attributes.getColor(R.styleable.ScoreProgressView_back_color, DEFAULT_COLOR_UNREACHED_COLOR);
        mScoreForeColor = attributes.getColor(R.styleable.ScoreProgressView_fore_color, DEFAULT_COLOR_REACHED_COLOR);
        mScoreHeight = (int) attributes.getDimension(R.styleable.ScoreProgressView_score_height, 20);
        mRectCorn = (int) attributes.getDimension(R.styleable.ScoreProgressView_rect_corn, 5);
        attributes.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(getMeasuredWidth(), getPaddingTop() + mTextFontMetrics.bottom - mTextFontMetrics.top + mTriangleHeight + mLineToTextHeight + mScoreHeight + mPaddingBottom);
    }

    private void init() {
        mTriangleHeight = 10;
        mLineToTextHeight = 0;
        mRectCorn = mScoreHeight / 2;

        mTextBound = new Rect();

        mScorePaint = new Paint();
        mScorePaint.setStyle(Paint.Style.FILL);
        mScorePaint.setStrokeWidth(mScoreHeight);

        mTextPaint = new Paint();
        mTextPaint.setColor(mInfoColor);

        reCalculateTextSize();

        mPaddingTop = getPaddingTop();
        mPaddingBottom = getPaddingBottom();
        mPaddingLeft = getPaddingLeft();
        mPaddingRight = getPaddingRight();

        mPath = new Path();
    }

    private void reCalculateTextSize() {
        mTextPaint.setTextSize(mInfoTextSize);
        mTextFontMetrics = mTextPaint.getFontMetricsInt();
        if (mScoreInfo == null) {
            mScoreInfo = String.valueOf(mCurrentScore);
        }
        mTextPaint.getTextBounds(mScoreInfo, 0, mScoreInfo.length(), mTextBound);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //绘制前清理上次绘制的痕迹
        mPath.reset();

        //绘制最低分 和 最高分
        Paint scorePaint = new Paint();
        scorePaint.setColor(Color.BLACK);
        scorePaint.setAntiAlias(true);
        scorePaint.setTextSize(mInfoTextSize);
        String startText = String.valueOf(mStartScore);
        Rect rect = new Rect();
        scorePaint.getTextBounds(startText, 0, startText.length(), rect);
        int startWidth = rect.width();//文本的宽度
        int startHeight = rect.height();//文本的高度

        float textY = mPaddingTop - mTextFontMetrics.top + mTextFontMetrics.bottom + mTriangleHeight + mLineToTextHeight + mScoreHeight / 2 + startHeight / 2;
        canvas.drawText(mStartScore + "", mPaddingLeft, textY, scorePaint);

        String totalText = String.valueOf(mTotalScore);
        Rect totalRect = new Rect();
        scorePaint.getTextBounds(totalText, 0, totalText.length(), totalRect);
        int totalWidth = totalRect.width();//文本的宽度
        canvas.drawText(mTotalScore + "", getScreenWidth() - totalWidth - mPaddingRight, textY, scorePaint);


        mScorePaint.setColor(mScoreBackColor);

        //计算开始绘制进度条的y坐标
        int startLineLocationY = mPaddingTop - mTextFontMetrics.top + mTextFontMetrics.bottom + mTriangleHeight + mLineToTextHeight;

        //绘制进度条底部背景
        canvas.drawRoundRect(startWidth + mTextToScorePadding + mPaddingLeft, startLineLocationY, getScreenWidth() - (totalWidth + mTextToScorePadding + mPaddingRight), startLineLocationY + mScoreHeight, mRectCorn, mRectCorn, mScorePaint);

        //绘制已经完成了的进度条
        mScorePaint.setColor(mScoreForeColor);
        double progress = 1.0 * (mCurrentScore - mStartScore) / (mTotalScore - mStartScore);
        int currProgress = (int) ((getScreenWidth() - (totalWidth + startWidth + mTextToScorePadding * 2 + mPaddingRight + mPaddingLeft)) * progress);
        canvas.drawRoundRect(startWidth + mTextToScorePadding + mPaddingLeft, startLineLocationY, currProgress + mPaddingLeft + startWidth + mTextToScorePadding, startLineLocationY + mScoreHeight, mRectCorn, mRectCorn, mScorePaint);
        /*
        绘制显示文字三角形框
         */
        //计算文字显示区域的宽度和高度
        int textWidth = mTextBound.right - mTextBound.left;
        int textHeight = mTextFontMetrics.bottom - mTextFontMetrics.top;

        //计算三角形定点开始时的y坐标
        int startTriangleY = startLineLocationY - mLineToTextHeight;

        float triangleX = currProgress + mPaddingLeft + startWidth + mTextToScorePadding;

        mPath.moveTo(triangleX, startTriangleY);
        mPath.lineTo(triangleX + 10, startTriangleY - mTriangleHeight);
        mPath.lineTo(triangleX - 10, startTriangleY - mTriangleHeight);
        mPath.close();
        canvas.drawPath(mPath, mScorePaint);

        //绘制文字
        //考虑左右超出情况
        float textX = (triangleX - textWidth / 2);
        if ((textX + textWidth) > getScreenWidth()) {
            textX = getScreenWidth() - mPaddingRight - textWidth;
        }
        if((textX - textWidth / 2) < 0){
            textX = mPaddingLeft;
        }
        canvas.drawText(mScoreInfo, textX, mPaddingTop - mTextFontMetrics.top, mTextPaint);

    }

    public void resetLevelProgress(int start, int end, int current, String scoreInfo) {
        this.mStartScore = start;
        this.mTotalScore = end;
        this.mCurrentScore = current;
        this.mScoreInfo = scoreInfo;
        reCalculateTextSize();
        invalidate();
    }


    public int getScreenWidth() {
        WindowManager wm = (WindowManager) mContext
                .getSystemService(Context.WINDOW_SERVICE);

        int width = wm.getDefaultDisplay().getWidth();
        return width;
    }

    public int dip2px(float dpValue) {
        final float scale = mContext.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public int getScoreBackColor() {
        return mScoreBackColor;
    }

    public void setScoreBackColor(int mScoreBackColor) {
        this.mScoreBackColor = mScoreBackColor;
    }

    public int getScoreForeColor() {
        return mScoreForeColor;
    }

    public void setScoreForeColor(int mScoreForeColor) {
        this.mScoreForeColor = mScoreForeColor;
    }

    public int getInfoColor() {
        return mInfoColor;
    }

    public void setInfoColor(int mInfoColor) {
        this.mInfoColor = mInfoColor;
    }

    public int getInfoTextSize() {
        return mInfoTextSize;
    }

    public void setInfoTextSize(int mInfoTextSize) {
        this.mInfoTextSize = mInfoTextSize;
    }

    public int getCurrentScore() {
        return mCurrentScore;
    }

    public void setCurrentScore(int mCurrentScore) {
        this.mCurrentScore = mCurrentScore;
    }

    public int getStartScore() {
        return mStartScore;
    }

    public void setStartScore(int mStartScore) {
        this.mStartScore = mStartScore;
    }

    public int getTotalScore() {
        return mTotalScore;
    }

    public void setTotalScore(int mTotalScore) {
        this.mTotalScore = mTotalScore;
    }

    public int getScoreHeight() {
        return mScoreHeight;
    }

    public void setScoreHeight(int mScoreHeight) {
        this.mScoreHeight = mScoreHeight;
    }
}
