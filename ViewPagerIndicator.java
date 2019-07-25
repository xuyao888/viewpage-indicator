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
/**
 *增加滑动时tab线长度变化
 */

public class ViewPagerIndicator extends LinearLayout {

    private Context mContext;

    private int lineWidth = 0;
    private int lineHeight = 0;
    private List<String> mTiles;

    private ViewPager vp;
    Paint paint;
    private int startX;

    private  int endWidth;
    List<BaseFragment> baseFragments;
    int pos = 0;
    private int weight;
    int rightMargin;

    public ViewPagerIndicator(Context context, @Nullable AttributeSet attrs) {
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
        canvas.drawLine(startX, getHeight() - lineHeight / 2 - getResources().getDimensionPixelOffset(R.dimen.dp12), endWidth, getHeight() - lineHeight / 2 - getResources().getDimensionPixelOffset(R.dimen.dp12), paint);
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
                startX = (int) (getWidth() / mTiles.size() / 2 - lineWidth / 2 + position * getWidth() / mTiles.size() + positionOffset * getWidth() / mTiles.size());
                if (positionOffset <= 0.4){
                    endWidth = (int) (startX + lineWidth + 5*lineWidth*positionOffset);
                }else if (positionOffset >= 0.6){
                    endWidth = (int) (startX + lineWidth + 5*lineWidth* (1-positionOffset));
                }else {
                    endWidth = (int) (startX + lineWidth +2*lineWidth);
                }
                invalidate();
            }

            @Override
            public void onPageSelected(int position) {
                ZALog.e("TAG", "---onPageSelected---");
                TextView tv = (TextView) getChildAt(position);
                setBackground();
                tv.setTextColor(getResources().getColor(R.color.color_2E2F33));
                tv.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));//加粗
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        vp.setCurrentItem(0);
    }

    private void setBackground() {
        for (int i = 0; i < getChildCount(); i++) {
            TextView tv = (TextView) getChildAt(i);
            tv.setTextColor(getResources().getColor(R.color.color_5C5E66));
            tv.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
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
                lp.rightMargin = rightMargin;

            }
            tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            tv.setLayoutParams(lp);

            tv.setGravity(Gravity.CENTER);
            tv.setText(mTiles.get(i));
            addView(tv);
            if (i == 0) {
                tv.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));//加粗
                tv.setTextColor(getResources().getColor(R.color.color_2E2F33));
            } else {
                tv.setTextColor(getResources().getColor(R.color.color_5C5E66));
                tv.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
            }
        }

    }

}
