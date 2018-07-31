package com.example.indexview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;


/**
 * 创建时间:2018-7-31
 */

public class IndexView extends LinearLayout{
    public final static String TAG = "IndexView";

    /**
     *  A - Z 及 * 的字符
     */
    public static final char[] ALPHABETS = new char[]{
        'A','B','C','D','E',
        'F','G','H','I','J',
        'K','L','M','N','O',
        'P','Q','R','S','T',
        'U','V','W','X','Y',
        'Z','*'
    };

    private Context mContext;

    private int mColor;
    private int mSelectedColor;
    private float mTextSize;
    private int mCurrentPosition = -1;

    private IndexViewListener mIndexViewListener;

    public IndexView(Context context) {
        this(context,null);
    }

    public IndexView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        TypedArray typedArray = mContext.obtainStyledAttributes(attrs,R.styleable.IndexView);
        mColor = typedArray.getColor(R.styleable.IndexView_color, Color.LTGRAY);
        mSelectedColor = typedArray.getColor(R.styleable.IndexView_selectedColor,Color.WHITE);
        mTextSize = typedArray.getDimension(R.styleable.IndexView_textSize,10);
        typedArray.recycle();

        setOrientation(VERTICAL);

        //虽然IndexView继承于LinearLayout,但仍视为非控件组
        //因此删去xml上带的子控件
        if(getChildCount() > 0){
            removeAllViews();
        }

        //各个字符通过TextView来显示
        for(int i = 0;i < ALPHABETS.length;i++){
            TextView textView = new TextView(mContext);
            textView.setText(ALPHABETS[i] + "");
            textView.setTextColor(mColor);
            textView.measure(MeasureSpec.UNSPECIFIED,MeasureSpec.UNSPECIFIED);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_PX,mTextSize);
            textView.setGravity(Gravity.CENTER_HORIZONTAL);
            addView(textView);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        final int childHeigh = (b - t) / ALPHABETS.length;
        final int paddingLeft = getPaddingLeft();
        final int paddingRight = getPaddingRight();

        //支持paddingLeft 和 paddingRight,但不支持paddingTop 和 paddingBottom
        for(int i = 0;i < ALPHABETS.length;i++){
            final View child = getChildAt(i);
            child.layout(paddingLeft,childHeigh * i,r - paddingRight,childHeigh * (i + 1));
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float eventY = event.getY();
        final int childHeight = getChildAt(0).getHeight();
        int position = (int) (eventY / childHeight);
        switch(event.getAction()){
            case MotionEvent.ACTION_DOWN:
                if(mIndexViewListener != null){
                    mIndexViewListener.onDown(position,ALPHABETS[position]);
                }
                return true;

            case MotionEvent.ACTION_MOVE:
                if(mCurrentPosition != position){
                    setSelected(position);
                    if(mIndexViewListener != null){
                        mIndexViewListener.onSelectedPositionChanged(mCurrentPosition,ALPHABETS[mCurrentPosition]);
                    }
                }
                return true;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if(mIndexViewListener != null){
                    mIndexViewListener.onUp();
                }
                setAsUnselected(mCurrentPosition);
                mCurrentPosition = -1;
                return true;
        }
        return super.onTouchEvent(event);
    }

    private void setSelected(int position){
        if(position < 0 || position >= ALPHABETS.length){
            throw new IndexOutOfBoundsException("position < 0 || position >= ALPHABETS.length?!");
        }

        mCurrentPosition = position;
        for(int i = 0;i < ALPHABETS.length;i++){
            final TextView child = (TextView) getChildAt(i);
            if(i == position){
                child.setTextColor(mSelectedColor);
                continue;
            }
            child.setTextColor(mColor);
        }
    }

    private void setAsUnselected(int position){
        if(position < 0 || position >= ALPHABETS.length){
            throw new IndexOutOfBoundsException("position < 0 || position >= ALPHABETS.length?!");
        }

        final TextView child = (TextView) getChildAt(position);
        child.setTextColor(mColor);
    }

    /**
     * 获取未选字符的颜色
     * @return
     */
    public int getColor() {
        return mColor;
    }

    /**
     * 设置未选字符的颜色
     * @return
     */
    public void setUnselectedColor(int mColor) {
        this.mColor = mColor;
        for(int i = 0;i < ALPHABETS.length;i++){
            final TextView child = (TextView) getChildAt(i);
            child.setTextColor(mColor);
        }
    }

    /**
     * 获取被选中字符的颜色
     * @return
     */
    public int getSelectedColor() {
        return mSelectedColor;
    }

    /**
     * 设置被选中字符的颜色
     * @return
     */
    public void setSelectedColor(int mSelectedColor) {
        this.mSelectedColor = mSelectedColor;
    }

    /**
     * 设置索引监听器
     * @param mIndexViewListener
     */
    public void setIndexViewListener(IndexViewListener mIndexViewListener) {
        this.mIndexViewListener = mIndexViewListener;
    }

    /**
     * 索引指示器监听器,用于监听索引指示器的触摸事件
     */
    public interface IndexViewListener{
        /**
         * 手指落在控件上时触发
         * @param position
         * @param alphabet
         */
        void onDown(int position, char alphabet);

        /**
         * 当手指滑动且移动到了其他字符上时触发
         * @param position
         * @param alphabet
         */
        void onSelectedPositionChanged(int position, char alphabet);

        /**
         * 手指抬起或移到此空间外时触发
         */
        void onUp();
    }
}
