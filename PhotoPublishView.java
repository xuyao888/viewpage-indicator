package com.zhenai.phone.mycenter.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.tools.DateUtils;
import com.luck.picture.lib.tools.StringUtils;
import com.zhenai.base.callback.ClickCallBack;
import com.zhenai.base.callback.SelectCallBack;
import com.zhenai.base.utils.log.ZALog;
import com.zhenai.base.view.DividerItemDecoration;
import com.zhenai.phone.mycenter.R;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/*
 * 卡牌自定义View
 * */
public class PhotoPublishView extends LinearLayout {

    private Context mContext;
    private RecyclerView recyclerView;
    List<LocalMedia> mPhotos = new ArrayList<>();

    private int ADD_VIEW = 100;
    private PhotoAdapter photoAdapter;
    private int itemHeight = 0;

    private int limitNum = 0;

    public PhotoPublishView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        initViews();
    }

    private void initViews() {
        recyclerView = new RecyclerView(mContext);
        addView(recyclerView);
        LinearLayout.LayoutParams lp =
                (LayoutParams) recyclerView.getLayoutParams();
        lp.width = LayoutParams.MATCH_PARENT;
        lp.height = LayoutParams.WRAP_CONTENT;
        recyclerView.setLayoutParams(lp);

    }

    public void setDataList(final List<LocalMedia> mPhotos, int itemHeight, int orientation, int spanCount, int dividerHeight, int limitNum) {
        if (mPhotos == null) return;
        this.mPhotos = mPhotos;
        this.itemHeight = itemHeight;
        this.limitNum = limitNum;
        GridLayoutManager gridLayoutManager = new GridLayoutManager(mContext, spanCount);
        recyclerView.setLayoutManager(gridLayoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mContext, orientation, dividerHeight);
        recyclerView.addItemDecoration(dividerItemDecoration);
        photoAdapter = new PhotoAdapter();
        recyclerView.setAdapter(photoAdapter);

    }

    public void setChanged() {

        recyclerView.getAdapter().notifyDataSetChanged();

    }

    public class PhotoAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


        @Override
        public int getItemCount() {
            if (mPhotos.size() < limitNum) {
                return mPhotos.size() + 1;
            }
            return mPhotos.size();
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            RecyclerView.ViewHolder viewHolder;

            ZALog.e("TAG", "--ADD_VIEW--" + viewType);
            if (viewType == ADD_VIEW) {
                View addView = LayoutInflater.from(mContext).inflate(R.layout.view_add_photo, parent, false);
                ImageView iv = addView.findViewById(R.id.iv_default_photo);
                FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) iv.getLayoutParams();
                lp.height = itemHeight;
                viewHolder = new AddViewHolder(addView);

            } else {
                View photoView = LayoutInflater.from(mContext).inflate(R.layout.item_photo_publish, parent, false);
                ImageView iv = photoView.findViewById(R.id.iv_add_photo);
                FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) iv.getLayoutParams();
                lp.height = itemHeight;
                viewHolder = new PhotoViewHolder(photoView);

            }
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {

            if (getItemViewType(position)
                    != ADD_VIEW) {
                PhotoViewHolder photoViewHolder = (PhotoViewHolder) holder;
                final LocalMedia media = mPhotos.get(position);
                int mimeType = media.getMimeType();
                String path = "";
                if (media.isCut() && !media.isCompressed()) {
                    // 裁剪过
                    path = media.getCutPath();
                } else if (media.isCompressed() || (media.isCut() && media.isCompressed())) {
                    // 压缩过,或者裁剪同时压缩过,以最终压缩过图片为准
                    path = media.getCompressPath();
                } else {
                    // 原图
                    path = media.getPath();
                }
                int pictureType = PictureMimeType.isPictureType(media.getPictureType());
                photoViewHolder.tv_duration.setVisibility(pictureType == PictureConfig.TYPE_VIDEO
                        ? View.VISIBLE : View.GONE);

                if (mimeType == PictureMimeType.ofAudio()) {
                    photoViewHolder.tv_duration.setVisibility(View.VISIBLE);
                    Drawable drawable = ContextCompat.getDrawable(mContext, R.drawable.picture_audio);
                    StringUtils.modifyTextViewDrawable(photoViewHolder.tv_duration, drawable, 0);
                } else {
                    Drawable drawable = ContextCompat.getDrawable(mContext, R.drawable.video_icon);
                    StringUtils.modifyTextViewDrawable(photoViewHolder.tv_duration, drawable, 0);
                }
                photoViewHolder.tv_duration.setText(DateUtils.timeParse(media.getDuration()));
                RequestOptions options = new RequestOptions()
                        .centerCrop()
                        .diskCacheStrategy(DiskCacheStrategy.ALL);
                Glide.with(holder.itemView.getContext())
                        .load(path)
                        .apply(options)
                        .into(photoViewHolder.iv_add_photo);
                photoViewHolder.iv_delete_photo.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        selectCallBack.callBack(v, mPhotos.get(position), true);
                        mPhotos.remove(position);
                        setChanged();
                    }
                });

                photoViewHolder.iv_add_photo.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String pictureType = mPhotos.get(position).getPictureType();
                        int mediaType = PictureMimeType.pictureToVideo(pictureType);
                        switch (mediaType) {
                            case 1:
                                // 预览图片 可自定长按保存路径
                                //PictureSelector.create(MainActivity.this).themeStyle(themeId).externalPicturePreview(position, "/custom_file", selectList);
                                PictureSelector.create((Activity) mContext).themeStyle(R.style.picture_default_style).openExternalPreview(position, mPhotos);
                                break;
                            case 2:
                                // 预览视频
                                PictureSelector.create((Activity)mContext).externalPictureVideo(media.getPath());
                                break;
                            case 3:
                                // 预览音频
                                PictureSelector.create((Activity)mContext).externalPictureAudio(media.getPath());
                                break;
                        }
                    }
                });



            }
        }

        @Override
        public int getItemViewType(int position) {
            if (position == mPhotos.size()) {//最后一个add按钮
                return ADD_VIEW;
            }
            return super.getItemViewType(position);
        }

    }

    ClickCallBack clickCallBack;

    public void setCallBack(ClickCallBack clickCallBack) {
        this.clickCallBack = clickCallBack;
    }

    SelectCallBack selectCallBack;

    public void setSelectCallBack(SelectCallBack selectCallBack) {
        this.selectCallBack = selectCallBack;
    }

    class PhotoViewHolder extends RecyclerView.ViewHolder {
        View itemView;
        ImageView iv_add_photo;
        ImageView iv_delete_photo;
        TextView tv_duration;
        public PhotoViewHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;
            iv_add_photo = itemView.findViewById(R.id.iv_add_photo);
            iv_delete_photo = itemView.findViewById(R.id.iv_delete_photo);
            tv_duration = itemView.findViewById(R.id.tv_duration);
        }
    }

    class AddViewHolder extends RecyclerView.ViewHolder {
        View itemView;
        ImageView iv_default_photo;

        public AddViewHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;
            iv_default_photo = itemView.findViewById(R.id.iv_default_photo);
            iv_default_photo.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickCallBack.callBack(v);

                }
            });
        }
    }
}