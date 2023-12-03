package com.android.settings.display;

import android.content.Context;
import android.graphics.Typeface;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.android.settings.R;
import com.android.settings.display.font.FontWeightUtils;
import com.android.settings.usagestats.utils.FileUtils;
import com.android.settingslib.utils.ThreadUtils;
import java.io.File;
import java.lang.ref.WeakReference;
import java.util.List;
import miuix.visual.check.VisualCheckBox;

/* loaded from: classes.dex */
public class FontAdapter extends RecyclerView.Adapter<FontViewHolder> {
    public static int EMPTY = 0;
    public static int FONT = 1;
    private static String TAG = "FontAdapter";
    private WeakReference<Context> contextWeakReference;
    private long downIndex = 0;
    private long mCurrentIndex = 0;
    private List<LocalFontModel> mList;
    FontSelectListener mListener;

    /* loaded from: classes.dex */
    public interface FontSelectListener {
        void fontSelected(int i, boolean z);
    }

    /* loaded from: classes.dex */
    public static class FontViewHolder extends RecyclerView.ViewHolder {
        VisualCheckBox checkBox;
        VisualCheckBox checkbox3points;
        private OnItemTouchListener mListener;
        TextView textView;

        /* loaded from: classes.dex */
        public interface OnItemTouchListener {
            boolean onItemTouch(MotionEvent motionEvent, int i);
        }

        public FontViewHolder(View view) {
            super(view);
            VisualCheckBox visualCheckBox = (VisualCheckBox) view.findViewById(R.id.checkbox);
            this.checkBox = visualCheckBox;
            visualCheckBox.setFocusable(true);
            this.checkBox.setAccessibilityDelegate(new View.AccessibilityDelegate() { // from class: com.android.settings.display.FontAdapter.FontViewHolder.1
                @Override // android.view.View.AccessibilityDelegate
                public void onInitializeAccessibilityNodeInfo(View view2, AccessibilityNodeInfo accessibilityNodeInfo) {
                    super.onInitializeAccessibilityNodeInfo(view2, accessibilityNodeInfo);
                    accessibilityNodeInfo.setClickable(true);
                }
            });
            VisualCheckBox visualCheckBox2 = (VisualCheckBox) view.findViewById(R.id.checkbox_3points);
            this.checkbox3points = visualCheckBox2;
            visualCheckBox2.setFocusable(true);
            this.checkbox3points.setAccessibilityDelegate(new View.AccessibilityDelegate() { // from class: com.android.settings.display.FontAdapter.FontViewHolder.2
                @Override // android.view.View.AccessibilityDelegate
                public void onInitializeAccessibilityNodeInfo(View view2, AccessibilityNodeInfo accessibilityNodeInfo) {
                    super.onInitializeAccessibilityNodeInfo(view2, accessibilityNodeInfo);
                    accessibilityNodeInfo.setClickable(true);
                }
            });
            this.textView = (TextView) view.findViewById(R.id.check_text);
        }

        public void setItemTouchListener(OnItemTouchListener onItemTouchListener) {
            this.mListener = onItemTouchListener;
            if (onItemTouchListener != null) {
                this.checkBox.setOnTouchListener(new View.OnTouchListener() { // from class: com.android.settings.display.FontAdapter.FontViewHolder.3
                    @Override // android.view.View.OnTouchListener
                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        return FontViewHolder.this.mListener.onItemTouch(motionEvent, FontViewHolder.this.getPosition());
                    }
                });
                this.checkbox3points.setOnTouchListener(new View.OnTouchListener() { // from class: com.android.settings.display.FontAdapter.FontViewHolder.4
                    @Override // android.view.View.OnTouchListener
                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        return FontViewHolder.this.mListener.onItemTouch(motionEvent, FontViewHolder.this.getPosition());
                    }
                });
            }
        }
    }

    private int getMiddleScaleFromWeightList(LocalFontModel localFontModel) {
        int i = PageLayoutFragment.MIUI_WGHT[5];
        List<Integer> fontWeight = localFontModel.getFontWeight();
        if (fontWeight == null) {
            return i;
        }
        int size = fontWeight.size();
        if (size % 2 == 1) {
            return fontWeight.get(size / 2).intValue();
        }
        int i2 = size / 2;
        return (localFontModel.getFontWeight().get(i2).intValue() + localFontModel.getFontWeight().get(i2 - 1).intValue()) / 2;
    }

    public static Typeface getVarTypeface(LocalFontModel localFontModel, int i) {
        return TextUtils.equals(localFontModel.getId(), "b004d74e-5c49-430c-bb6a-18ed5d2d33e4") ? FontWeightUtils.getVarTypeface(i, 3) : FontWeightUtils.createTypefaceWithWeight(localFontModel.getBuilder(), i);
    }

    private File tryGetFontAssertFile(LocalFontModel localFontModel) {
        try {
            File file = new File(this.contextWeakReference.get().getCacheDir(), localFontModel.getId() + ".ttf");
            if (file.exists()) {
                return file;
            }
            FileUtils.InputStreamToFile(this.contextWeakReference.get().getContentResolver().openInputStream(Uri.parse(localFontModel.getContentUri())), file);
            return file;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public int getItemCount() {
        List<LocalFontModel> list = this.mList;
        if (list != null) {
            return list.size();
        }
        return 0;
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public long getItemId(int i) {
        return i;
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public int getItemViewType(int i) {
        return i == getItemCount() + (-1) ? EMPTY : FONT;
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public void onBindViewHolder(FontViewHolder fontViewHolder, int i) {
        List<LocalFontModel> list = this.mList;
        if (list != null) {
            if (i == list.size() - 1) {
                fontViewHolder.checkBox.setVisibility(8);
                fontViewHolder.checkbox3points.setVisibility(0);
                fontViewHolder.checkbox3points.setChecked(this.mCurrentIndex == ((long) i));
                return;
            }
            LocalFontModel localFontModel = this.mList.get(i);
            fontViewHolder.textView.setText(localFontModel.getTitle());
            setFontFamily(fontViewHolder.textView, localFontModel);
            fontViewHolder.checkBox.setChecked(this.mCurrentIndex == ((long) i));
        }
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public FontViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        FontViewHolder fontViewHolder = new FontViewHolder(this.mList.size() >= 3 ? LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.font_item_normal, viewGroup, false) : LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.font_item_big, viewGroup, false));
        fontViewHolder.setItemTouchListener(new FontViewHolder.OnItemTouchListener() { // from class: com.android.settings.display.FontAdapter.1
            @Override // com.android.settings.display.FontAdapter.FontViewHolder.OnItemTouchListener
            public boolean onItemTouch(MotionEvent motionEvent, int i2) {
                int action = motionEvent.getAction();
                if (action == 0) {
                    FontAdapter.this.downIndex = i2;
                } else if (action == 1) {
                    long j = i2;
                    if (j == FontAdapter.this.downIndex) {
                        FontAdapter.this.setCurrentIndex(j);
                        FontAdapter fontAdapter = FontAdapter.this;
                        FontSelectListener fontSelectListener = fontAdapter.mListener;
                        if (fontSelectListener != null) {
                            fontSelectListener.fontSelected(i2, i2 == fontAdapter.mList.size() - 1);
                        }
                    }
                }
                return true;
            }
        });
        return fontViewHolder;
    }

    public void setContext(Context context) {
        this.contextWeakReference = new WeakReference<>(context);
    }

    public void setCurrentFontId(String str) {
        for (int i = 0; i < this.mList.size(); i++) {
            if (str != null && str.equals(this.mList.get(i).getId())) {
                setCurrentIndex(getItemId(i));
                return;
            }
        }
    }

    public void setCurrentIndex(long j) {
        int i = (int) this.mCurrentIndex;
        this.mCurrentIndex = j;
        notifyItemChanged(i);
        notifyItemChanged((int) this.mCurrentIndex);
    }

    public void setDataList(List<LocalFontModel> list) {
        this.mList = list;
        for (int i = 0; i < this.mList.size() - 1; i++) {
            LocalFontModel localFontModel = this.mList.get(i);
            if (localFontModel.getId() != null && !localFontModel.getId().equals("10") && !localFontModel.getId().equals("b004d74e-5c49-430c-bb6a-18ed5d2d33e4") && !localFontModel.getId().equals("-100") && localFontModel.getFontAssert() == null) {
                try {
                    File tryGetFontAssertFile = tryGetFontAssertFile(localFontModel);
                    localFontModel.setFontAssert(tryGetFontAssertFile);
                    if (tryGetFontAssertFile != null) {
                        Typeface.Builder builder = new Typeface.Builder(tryGetFontAssertFile);
                        localFontModel.setTypeface(builder.build());
                        localFontModel.setBuilder(builder);
                        this.mList.set(i, localFontModel);
                    }
                } catch (Exception e) {
                    Log.e(TAG, "setDataList file io error, font is " + localFontModel.toString() + ",");
                    e.printStackTrace();
                }
            }
        }
        notifyDataSetChanged();
    }

    public void setFont(final TextView textView, final Typeface typeface) {
        ThreadUtils.postOnMainThread(new Runnable() { // from class: com.android.settings.display.FontAdapter$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                textView.setTypeface(typeface);
            }
        });
    }

    public void setFontFamily(TextView textView, LocalFontModel localFontModel) {
        try {
            Typeface typeface = localFontModel.getTypeface();
            if (localFontModel.getId().equals("10")) {
                typeface = FontWeightUtils.getVarTypeface(PageLayoutFragment.MIUI_WGHT[5], 0);
            } else if (localFontModel.isVariable()) {
                typeface = getVarTypeface(localFontModel, getMiddleScaleFromWeightList(localFontModel));
            }
            if (typeface != null) {
                setFont(textView, typeface);
            } else {
                Log.e(TAG, "setFontFamily error fontTypeFace, content uri is null! ");
            }
        } catch (Exception e) {
            Log.e(TAG, "set FontFamily fail, " + e.getMessage());
        }
    }

    public void setFontSelectListener(FontSelectListener fontSelectListener) {
        this.mListener = fontSelectListener;
    }
}
