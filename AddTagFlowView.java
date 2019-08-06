package com.zhenai.phone.mycenter.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zhenai.base.callback.ClickCallBack;
import com.zhenai.base.utils.log.ZALog;
import com.zhenai.phone.mycenter.R;
import com.zhenai.phone.mycenter.bean.TagBean;
import com.zhenai.widget.flowlayout.FlowLayout;
import com.zhenai.widget.flowlayout.TagAdapter;
import com.zhenai.widget.flowlayout.TagFlowLayout;
import com.zhenai.widget.flowlayout.TagView;

import java.sql.SQLTransactionRollbackException;
import java.util.List;

import androidx.annotation.Nullable;

public class AddTagFlowView extends LinearLayout {

    private Context mContext;
    private TagBean tagBean;

    public TagFlowLayout getTagFlowLayout() {
        return tagFlowLayout;
    }

    private TagFlowLayout tagFlowLayout;
    List<TagBean> mLabelDatas;

    private int limitNum = 0;
    private int itemTypeView = 0;
    private String addLabelText = "";
    private boolean isAdd_label = true;

    public AddTagFlowView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        TypedArray ta = mContext.obtainStyledAttributes(attrs, R.styleable.AddTagFlowView);
        addLabelText = ta.getString(R.styleable.AddTagFlowView_addLabelText);
        ta.recycle();
        setOrientation(VERTICAL);
        initView();
    }

    private void initView() {
        tagBean = new TagBean();
        tagBean.setTagId(-1);
        tagFlowLayout = new TagFlowLayout(mContext);
        addView(tagFlowLayout);
        LinearLayout.LayoutParams lp = (LayoutParams) tagFlowLayout.getLayoutParams();
        lp.width = LayoutParams.MATCH_PARENT;
        lp.height = LayoutParams.WRAP_CONTENT;
        tagFlowLayout.setLayoutParams(lp);
    }


    public void setListDatas(final List<TagBean> labelDatas, final int limitNum, int selectMax, final int typeView, final int itemTypeView) {
        if (labelDatas == null) return;
        this.mLabelDatas = labelDatas;
        this.limitNum = limitNum;
        this.itemTypeView = itemTypeView;
        tagFlowLayout.setMaxSelectCount(selectMax);
        if (mLabelDatas.size() < limitNum) {
            mLabelDatas.add(mLabelDatas.size(), tagBean);
        }
        tagFlowLayout.setAdapter(new TagAdapter<TagBean>(mLabelDatas) {
            @Override
            public View getView(FlowLayout parent, final int position, TagBean s) {

                if (isShowAddTag(position, s)) { //全部删除了
                    FrameLayout frameLayout = null;
                    //显示添加按钮
                    if (typeView == 1) { //灰色添加按钮
                        frameLayout = (FrameLayout) LayoutInflater.from(mContext).inflate(R.layout.view_add_tag, null);
                    } else if (typeView == 2) {//蓝色添加按钮
                        frameLayout = (FrameLayout) LayoutInflater.from(mContext).inflate(R.layout.view_blue_add_tag, null);
                    }
                    TextView tv = frameLayout.findViewById(R.id.tv_custom_add_lebel);
                    tv.setText(addLabelText);
                    return frameLayout;
                } else {
                    FrameLayout frameLayout = null;

                    if (itemTypeView == 2) { //带删除的item

                        frameLayout = (FrameLayout) LayoutInflater.from(mContext).inflate(R.layout.item_label_publish, null);
                        TextView tv_add_lebel = frameLayout.findViewById(R.id.tv_add_lebel);
                        tv_add_lebel.setText(s.getTagName());
                        ImageView iv_delete = frameLayout.findViewById(R.id.iv_delete);
                        iv_delete.setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mLabelDatas.remove(position);
                                if (!mLabelDatas.contains(tagBean)) {
                                    mLabelDatas.add(mLabelDatas.size(), tagBean);
                                }
                                tagFlowLayout.onChanged();
                            }
                        });

                    } else if (itemTypeView == 1) { //不带删除的item
                        frameLayout = (FrameLayout) LayoutInflater.from(mContext).inflate(R.layout.item_nodel_label_publish, null);
                        TextView tv_add_lebel = frameLayout.findViewById(R.id.tv_add_lebel);
                        tv_add_lebel.setText(s.getTagName());
                    }
                    return frameLayout;
                }
            }

            @Override
            public void onSelected(int position, View view) {
                super.onSelected(position, view);
                FrameLayout frameLayout = (FrameLayout) view;
                TextView tv = frameLayout.findViewById(R.id.tv_custom_add_lebel);

                if (itemTypeView == 1 && position < mLabelDatas.size() - 1) {
                    TextView addLabel = frameLayout.findViewById(R.id.tv_add_lebel);
                    addLabel.setTextColor(getResources().getColor(R.color.color_7F57DB));
                    addLabel.setBackground(getResources().getDrawable(R.drawable.blue_corners));
                    clickCallBack.callBack(mLabelDatas.get(position));
                }

            }

            @Override
            public void unSelected(int position, View view) {
                super.onSelected(position, view);
                FrameLayout frameLayout = (FrameLayout) view;
                if (itemTypeView == 1 && position < mLabelDatas.size() - 1) {
                    TextView addLabel = frameLayout.findViewById(R.id.tv_add_lebel);
                    addLabel.setTextColor(getResources().getColor(R.color.color_8A8D99));
                    addLabel.setBackground(getResources().getDrawable(R.drawable.gray_corber));
                }
            }

        });


    }

    private boolean isShowAddTag(int position, TagBean s) {

        if (position == mLabelDatas.size() - 1 && s.getTagId() == -1) {
            return true;
        }
        return false;

    }

    ClickCallBack clickCallBack;

    public void setClickCallBack(ClickCallBack clickCallBack) {
        this.clickCallBack = clickCallBack;
    }
}
