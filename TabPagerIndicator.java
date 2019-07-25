package com.zhenai.phone.mycenter.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zhenai.base.fragment.BaseFragment;
import com.zhenai.base.utils.log.ZALog;
import com.zhenai.phone.mycenter.R;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

public class TabPagerIndicator extends LinearLayout {

    private Context mContext;

    private int lineWidth = 0;
    private int lineHeight = 0;
    private List<String> mTiles;

    private ViewPager vp;
    Paint paint;
    private int startX;
    List<BaseFragment> baseFragments;
    int pos = 0;
    private int weight;
    int rightMargin;
    private int total;

    private int DEFAULT_COLOR= Color.parseColor("#5C5E66");
    private int SELECT_COLOR= Color.parseColor("#2E2F33");
    private int endWidth;

    public TabPagerIndicator(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        setOrientation(HORIZONTAL);
        lineWidth = getResources().getDimensionPixelOffset(R.dimen.dp12);
        lineHeight = getResources().getDimensionPixelOffset(R.dimen.dp3);
        paint = new Paint();
        paint.setColor(getResources().getColor(R.color.color_7F57DB));
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeWidth(lineHeight);
        initView();
    }


    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        canvas.drawLine(startX, getHeight() - lineHeight / 2, endWidth, getHeight() - lineHeight / 2, paint);


    }

    private void initView() {


    }

    public void setTitils(List<String> datas, int rightMargin, FragmentManager supportFragmentManager, ViewPager vp, final List<BaseFragment> baseFragments, int weight) {
        if (datas != null && datas.size() > 0) {
            this.mTiles = datas;
            this.vp = vp;
            this.baseFragments = baseFragments;
            this.weight = weight;
            this.rightMargin = rightMargin;
            initVp(supportFragmentManager);
            addViews();
            addClick();
        }
    }

    private void initVp(FragmentManager supportFragmentManager) {
        vp.setAdapter(new FragmentPagerAdapter(supportFragmentManager) {
            @NonNull
            @Override
            public Fragment getItem(int position) {
                return baseFragments.get(position);
            }

            @Override
            public int getCount() {
                return baseFragments.size();
            }
        });
        vp.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                ZALog.e("TAG", "---onPageScrolled---");
                int tvWidth = 0;
                int tvWidth1 = 0;
                int itemWidth = 0;
                if (position < mTiles.size() -1){
                    TextView tv = (TextView) getChildAt(position);
                    TextView tv1 = (TextView) getChildAt(position +1);
//                    tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18 - positionOffset*2);
//                    tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16 +  positionOffset*2);
                    tvWidth = tv.getWidth();
                    tvWidth1 = tv1.getWidth();
                    itemWidth = tvWidth/2 + tvWidth1/2+rightMargin ;
                if (positionOffset > 0){
                    tv.setTextColor((Integer) evaluate( 1- positionOffset,DEFAULT_COLOR,SELECT_COLOR));
                    tv1.setTextColor((Integer) evaluate(positionOffset,DEFAULT_COLOR,SELECT_COLOR));
                }
                }else {
                    TextView tv1 = (TextView) getChildAt(position);
                    tvWidth = tv1.getWidth();
                }
                int total = 0;
                if (position == 0) {
                    total = tvWidth + rightMargin;
                } else {
                    for (int i = 0; i <= position; i++) {
                        total += getChildAt(i).getWidth() + rightMargin;
                    }
                }
                startX = (int) (total - rightMargin - tvWidth / 2 - lineWidth / 2 + positionOffset * itemWidth);
                if (positionOffset <= 0.4){
                    endWidth = (int) (startX + lineWidth + 5*lineWidth*positionOffset);
                }else if (positionOffset >= 0.6){
                    endWidth = (int) (startX + lineWidth + 5*lineWidth* (1-positionOffset));
                }else {
                    endWidth = (startX + lineWidth +2*lineWidth);
                }


                invalidate();
            }

            @Override
            public void onPageSelected(int position) {
                ZALog.e("TAG", "---onPageSelected---");
                TextView tv = (TextView) getChildAt(position);
                setBackground();
                tv.setTextColor(SELECT_COLOR);
                tv.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));//加粗
                tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
//                tv.setTextSize();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        vp.setCurrentItem(0);
    }
    public Object evaluate(float fraction, Object startValue, Object endValue) {
        int startInt = (Integer) startValue;
        float startA = ((startInt >> 24) & 0xff) / 255.0f;
        float startR = ((startInt >> 16) & 0xff) / 255.0f;
        float startG = ((startInt >>  8) & 0xff) / 255.0f;
        float startB = ( startInt        & 0xff) / 255.0f;

        int endInt = (Integer) endValue;
        float endA = ((endInt >> 24) & 0xff) / 255.0f;
        float endR = ((endInt >> 16) & 0xff) / 255.0f;
        float endG = ((endInt >>  8) & 0xff) / 255.0f;
        float endB = ( endInt        & 0xff) / 255.0f;

        // convert from sRGB to linear
        startR = (float) Math.pow(startR, 2.2);
        startG = (float) Math.pow(startG, 2.2);
        startB = (float) Math.pow(startB, 2.2);

        endR = (float) Math.pow(endR, 2.2);
        endG = (float) Math.pow(endG, 2.2);
        endB = (float) Math.pow(endB, 2.2);

        // compute the interpolated color in linear space
        float a = startA + fraction * (endA - startA);
        float r = startR + fraction * (endR - startR);
        float g = startG + fraction * (endG - startG);
        float b = startB + fraction * (endB - startB);

        // convert back to sRGB in the [0..255] range
        a = a * 255.0f;
        r = (float) Math.pow(r, 1.0 / 2.2) * 255.0f;
        g = (float) Math.pow(g, 1.0 / 2.2) * 255.0f;
        b = (float) Math.pow(b, 1.0 / 2.2) * 255.0f;

        return Math.round(a) << 24 | Math.round(r) << 16 | Math.round(g) << 8 | Math.round(b);
    }
    private void setBackground() {
        for (int i = 0; i < getChildCount(); i++) {
            TextView tv = (TextView) getChildAt(i);
            tv.setTextColor(DEFAULT_COLOR);
            tv.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
            tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        }
    }

    private void addClick() {
        for (int i = 0; i < getChildCount(); i++) {
            final int index = i;
            getChildAt(i).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    vp.setCurrentItem(index);
                }
            });
        }
    }

    private void addViews() {

        for (int i = 0; i < mTiles.size(); i++) {
            TextView tv = new TextView(mContext);
            LayoutParams lp = null;
            if (weight != 0) {
                lp = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT);
                lp.weight = 1;

            }
            if (rightMargin != 0) {
                lp = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
//                if (i != mTiles.size() -1)
                lp.rightMargin = rightMargin;

            }

            tv.setLayoutParams(lp);
            tv.setPadding(0, 0, 0, getResources().getDimensionPixelOffset(R.dimen.dp5));
            tv.setGravity(Gravity.CENTER);
            tv.setText(mTiles.get(i));
            addView(tv);
            if (i == 0) {
                tv.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));//加粗
                tv.setTextColor(SELECT_COLOR);
                tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
            } else {
                tv.setTextColor(DEFAULT_COLOR);
                tv.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
                tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            }
        }

    }

}
