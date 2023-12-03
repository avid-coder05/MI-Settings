package com.android.settings.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Outline;
import android.graphics.Rect;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.widget.VideoView;
import com.android.settings.R;
import com.android.settings.R$styleable;

/* loaded from: classes2.dex */
public class CornerVideoView extends VideoView {

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public class VideoViewOutlineProvider extends ViewOutlineProvider {
        private float mRadius;

        public VideoViewOutlineProvider(float f) {
            this.mRadius = f;
        }

        @Override // android.view.ViewOutlineProvider
        public void getOutline(View view, Outline outline) {
            outline.setRoundRect(new Rect(0, 0, view.getWidth(), view.getHeight()), this.mRadius);
        }
    }

    public CornerVideoView(Context context) {
        super(context);
    }

    public CornerVideoView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        setCornerRadiusFromAttrs(context, attributeSet);
    }

    public CornerVideoView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        setCornerRadiusFromAttrs(context, attributeSet);
    }

    public CornerVideoView(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        setCornerRadiusFromAttrs(context, attributeSet);
    }

    private void setCornerRadiusFromAttrs(Context context, AttributeSet attributeSet) {
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R$styleable.CornerVideoView);
        float dimensionPixelSize = obtainStyledAttributes.getDimensionPixelSize(R$styleable.CornerVideoView_cornerRadius, 0);
        obtainStyledAttributes.recycle();
        setCornerRadius(dimensionPixelSize);
    }

    public void play(int i, int i2) {
        if (i2 != 0) {
            setBackground(getContext().getDrawable(i2));
        }
        if (isPlaying() || i == 0) {
            return;
        }
        setVideoURI(Uri.parse("android.resource://" + getContext().getPackageName() + "/" + i));
        setOnPreparedListener(new MediaPlayer.OnPreparedListener() { // from class: com.android.settings.view.CornerVideoView.1
            @Override // android.media.MediaPlayer.OnPreparedListener
            public void onPrepared(MediaPlayer mediaPlayer) {
                mediaPlayer.setLooping(true);
                mediaPlayer.setOnInfoListener(new MediaPlayer.OnInfoListener() { // from class: com.android.settings.view.CornerVideoView.1.1
                    @Override // android.media.MediaPlayer.OnInfoListener
                    public boolean onInfo(MediaPlayer mediaPlayer2, int i3, int i4) {
                        if (i3 == 3) {
                            CornerVideoView cornerVideoView = CornerVideoView.this;
                            cornerVideoView.setBackgroundColor(cornerVideoView.getContext().getColor(R.color.miuix_sbl_transparent));
                            return true;
                        }
                        return true;
                    }
                });
                mediaPlayer.start();
            }
        });
    }

    public void setCornerRadius(float f) {
        setOutlineProvider(new VideoViewOutlineProvider(f));
        setClipToOutline(true);
    }
}
