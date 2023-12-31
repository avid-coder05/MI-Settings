package androidx.slice.core;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import androidx.core.graphics.drawable.IconCompat;
import androidx.slice.Slice;
import androidx.slice.SliceItem;
import miui.provider.Weather;
import miui.telephony.MiuiHeDuoHaoUtil;
import miui.yellowpage.YellowPageContract;

/* loaded from: classes.dex */
public class SliceActionImpl implements SliceAction {
    private PendingIntent mAction;
    private SliceItem mActionItem;
    private String mActionKey;
    private ActionType mActionType;
    private CharSequence mContentDescription;
    private long mDateTimeMillis;
    private IconCompat mIcon;
    private int mImageMode;
    private boolean mIsActivity;
    private boolean mIsChecked;
    private int mPriority;
    private SliceItem mSliceItem;
    private CharSequence mTitle;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: androidx.slice.core.SliceActionImpl$1  reason: invalid class name */
    /* loaded from: classes.dex */
    public static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$androidx$slice$core$SliceActionImpl$ActionType;

        static {
            int[] iArr = new int[ActionType.values().length];
            $SwitchMap$androidx$slice$core$SliceActionImpl$ActionType = iArr;
            try {
                iArr[ActionType.TOGGLE.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$androidx$slice$core$SliceActionImpl$ActionType[ActionType.DATE_PICKER.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                $SwitchMap$androidx$slice$core$SliceActionImpl$ActionType[ActionType.TIME_PICKER.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public enum ActionType {
        DEFAULT,
        TOGGLE,
        DATE_PICKER,
        TIME_PICKER
    }

    public SliceActionImpl(PendingIntent action, IconCompat actionIcon, int imageMode, CharSequence actionTitle) {
        this.mImageMode = 5;
        this.mActionType = ActionType.DEFAULT;
        this.mPriority = -1;
        this.mDateTimeMillis = -1L;
        this.mAction = action;
        this.mIcon = actionIcon;
        this.mTitle = actionTitle;
        this.mImageMode = imageMode;
    }

    public SliceActionImpl(PendingIntent action, IconCompat actionIcon, CharSequence actionTitle, boolean isChecked) {
        this(action, actionIcon, 0, actionTitle);
        this.mIsChecked = isChecked;
        this.mActionType = ActionType.TOGGLE;
    }

    public SliceActionImpl(PendingIntent action, CharSequence actionTitle, boolean isChecked) {
        this.mImageMode = 5;
        this.mActionType = ActionType.DEFAULT;
        this.mPriority = -1;
        this.mDateTimeMillis = -1L;
        this.mAction = action;
        this.mTitle = actionTitle;
        this.mActionType = ActionType.TOGGLE;
        this.mIsChecked = isChecked;
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    @SuppressLint({"InlinedApi"})
    public SliceActionImpl(SliceItem slice) {
        char c;
        this.mImageMode = 5;
        ActionType actionType = ActionType.DEFAULT;
        this.mActionType = actionType;
        this.mPriority = -1;
        this.mDateTimeMillis = -1L;
        this.mSliceItem = slice;
        SliceItem find = SliceQuery.find(slice, "action");
        if (find == null) {
            return;
        }
        this.mActionItem = find;
        this.mAction = find.getAction();
        SliceItem find2 = SliceQuery.find(find.getSlice(), YellowPageContract.ImageLookup.DIRECTORY_IMAGE);
        if (find2 != null) {
            this.mIcon = find2.getIcon();
            this.mImageMode = parseImageMode(find2);
        }
        SliceItem find3 = SliceQuery.find(find.getSlice(), "text", "title", (String) null);
        if (find3 != null) {
            this.mTitle = find3.getSanitizedText();
        }
        SliceItem findSubtype = SliceQuery.findSubtype(find.getSlice(), "text", "content_description");
        if (findSubtype != null) {
            this.mContentDescription = findSubtype.getText();
        }
        if (find.getSubType() != null) {
            String subType = find.getSubType();
            subType.hashCode();
            switch (subType.hashCode()) {
                case -868304044:
                    if (subType.equals(MiuiHeDuoHaoUtil.TOGGLE)) {
                        c = 0;
                        break;
                    }
                    c = 65535;
                    break;
                case 759128640:
                    if (subType.equals("time_picker")) {
                        c = 1;
                        break;
                    }
                    c = 65535;
                    break;
                case 1250407999:
                    if (subType.equals("date_picker")) {
                        c = 2;
                        break;
                    }
                    c = 65535;
                    break;
                default:
                    c = 65535;
                    break;
            }
            switch (c) {
                case 0:
                    this.mActionType = ActionType.TOGGLE;
                    this.mIsChecked = find.hasHint("selected");
                    break;
                case 1:
                    this.mActionType = ActionType.TIME_PICKER;
                    SliceItem findSubtype2 = SliceQuery.findSubtype(find, "long", "millis");
                    if (findSubtype2 != null) {
                        this.mDateTimeMillis = findSubtype2.getLong();
                        break;
                    }
                    break;
                case 2:
                    this.mActionType = ActionType.DATE_PICKER;
                    SliceItem findSubtype3 = SliceQuery.findSubtype(find, "long", "millis");
                    if (findSubtype3 != null) {
                        this.mDateTimeMillis = findSubtype3.getLong();
                        break;
                    }
                    break;
                default:
                    this.mActionType = actionType;
                    break;
            }
        } else {
            this.mActionType = actionType;
        }
        this.mIsActivity = this.mSliceItem.hasHint("activity");
        SliceItem findSubtype4 = SliceQuery.findSubtype(find.getSlice(), "int", "priority");
        this.mPriority = findSubtype4 != null ? findSubtype4.getInt() : -1;
        SliceItem findSubtype5 = SliceQuery.findSubtype(find.getSlice(), "text", "action_key");
        if (findSubtype5 != null) {
            this.mActionKey = findSubtype5.getText().toString();
        }
    }

    private Slice.Builder buildSliceContent(Slice.Builder builder) {
        Slice.Builder builder2 = new Slice.Builder(builder);
        IconCompat iconCompat = this.mIcon;
        if (iconCompat != null) {
            int i = this.mImageMode;
            builder2.addIcon(iconCompat, (String) null, i == 6 ? new String[]{"show_label"} : i == 0 ? new String[0] : new String[]{"no_tint"});
        }
        CharSequence charSequence = this.mTitle;
        if (charSequence != null) {
            builder2.addText(charSequence, (String) null, "title");
        }
        CharSequence charSequence2 = this.mContentDescription;
        if (charSequence2 != null) {
            builder2.addText(charSequence2, "content_description", new String[0]);
        }
        long j = this.mDateTimeMillis;
        if (j != -1) {
            builder2.addLong(j, "millis", new String[0]);
        }
        if (this.mActionType == ActionType.TOGGLE && this.mIsChecked) {
            builder2.addHints("selected");
        }
        int i2 = this.mPriority;
        if (i2 != -1) {
            builder2.addInt(i2, "priority", new String[0]);
        }
        String str = this.mActionKey;
        if (str != null) {
            builder2.addText(str, "action_key", new String[0]);
        }
        if (this.mIsActivity) {
            builder.addHints("activity");
        }
        return builder2;
    }

    public static int parseImageMode(SliceItem iconItem) {
        if (iconItem.hasHint("show_label")) {
            return 6;
        }
        if (iconItem.hasHint("no_tint")) {
            return iconItem.hasHint(Weather.RawInfo.PARAM) ? iconItem.hasHint("large") ? 4 : 3 : iconItem.hasHint("large") ? 2 : 1;
        }
        return 0;
    }

    public Slice buildPrimaryActionSlice(Slice.Builder builder) {
        return buildSliceContent(builder).addHints("shortcut", "title").build();
    }

    public Slice buildSlice(Slice.Builder builder) {
        return builder.addHints("shortcut").addAction(this.mAction, buildSliceContent(builder).build(), getSubtype()).build();
    }

    @Override // androidx.slice.core.SliceAction
    public PendingIntent getAction() {
        PendingIntent pendingIntent = this.mAction;
        return pendingIntent != null ? pendingIntent : this.mActionItem.getAction();
    }

    public SliceItem getActionItem() {
        return this.mActionItem;
    }

    public CharSequence getContentDescription() {
        return this.mContentDescription;
    }

    @Override // androidx.slice.core.SliceAction
    public IconCompat getIcon() {
        return this.mIcon;
    }

    @Override // androidx.slice.core.SliceAction
    public int getImageMode() {
        return this.mImageMode;
    }

    public String getKey() {
        return this.mActionKey;
    }

    @Override // androidx.slice.core.SliceAction
    public int getPriority() {
        return this.mPriority;
    }

    public SliceItem getSliceItem() {
        return this.mSliceItem;
    }

    public String getSubtype() {
        int i = AnonymousClass1.$SwitchMap$androidx$slice$core$SliceActionImpl$ActionType[this.mActionType.ordinal()];
        if (i != 1) {
            if (i != 2) {
                if (i != 3) {
                    return null;
                }
                return "time_picker";
            }
            return "date_picker";
        }
        return MiuiHeDuoHaoUtil.TOGGLE;
    }

    @Override // androidx.slice.core.SliceAction
    public CharSequence getTitle() {
        return this.mTitle;
    }

    public boolean isChecked() {
        return this.mIsChecked;
    }

    public boolean isDefaultToggle() {
        return this.mActionType == ActionType.TOGGLE && this.mIcon == null;
    }

    @Override // androidx.slice.core.SliceAction
    public boolean isToggle() {
        return this.mActionType == ActionType.TOGGLE;
    }

    public void setActivity(boolean isActivity) {
        this.mIsActivity = isActivity;
    }
}
