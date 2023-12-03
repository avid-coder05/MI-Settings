package miuix.preference;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.TextView;
import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/* loaded from: classes5.dex */
public class TextPreference extends Preference {
    private CharSequence mText;
    private TextProvider mTextProvider;
    private int mTextRes;

    /* loaded from: classes5.dex */
    public interface TextProvider<T extends TextPreference> {
        CharSequence provideText(T t);
    }

    public TextPreference(Context context) {
        this(context, null);
    }

    public TextPreference(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, R$attr.textPreferenceStyle);
    }

    public TextPreference(Context context, AttributeSet attributeSet, int i) {
        this(context, attributeSet, i, R$style.Miuix_Preference_TextPreference);
    }

    public TextPreference(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R$styleable.TextPreference, i, i2);
        CharSequence text = obtainStyledAttributes.getText(R$styleable.TextPreference_android_text);
        String string = obtainStyledAttributes.getString(R$styleable.TextPreference_textProvider);
        obtainStyledAttributes.recycle();
        if (!TextUtils.isEmpty(text)) {
            setText(text.toString());
        }
        setTextProvider(createTextProvider(context, string));
    }

    private TextProvider createTextProvider(Context context, String str) {
        if (TextUtils.isEmpty(str)) {
            return null;
        }
        try {
            Constructor constructor = context.getClassLoader().loadClass(str).asSubclass(TextProvider.class).getConstructor(new Class[0]);
            constructor.setAccessible(true);
            return (TextProvider) constructor.newInstance(new Object[0]);
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("Can't find provider: " + str, e);
        } catch (IllegalAccessException e2) {
            throw new IllegalStateException("Can't access non-public constructor " + str, e2);
        } catch (InstantiationException e3) {
            throw new IllegalStateException("Could not instantiate the TextProvider: " + str, e3);
        } catch (NoSuchMethodException e4) {
            throw new IllegalStateException("Error creating TextProvider " + str, e4);
        } catch (InvocationTargetException e5) {
            throw new IllegalStateException("Could not instantiate the TextProvider: " + str, e5);
        }
    }

    public CharSequence getText() {
        return getTextProvider() != null ? getTextProvider().provideText(this) : this.mText;
    }

    public final TextProvider getTextProvider() {
        return this.mTextProvider;
    }

    @Override // androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        TextView textView = (TextView) preferenceViewHolder.itemView.findViewById(R$id.text_right);
        if (textView != null) {
            CharSequence text = getText();
            if (TextUtils.isEmpty(text)) {
                textView.setVisibility(8);
                return;
            }
            textView.setText(text);
            textView.setVisibility(0);
        }
    }

    public void setText(int i) {
        setText(getContext().getString(i));
        this.mTextRes = i;
    }

    public void setText(String str) {
        if (getTextProvider() != null) {
            throw new IllegalStateException("Preference already has a TextProvider set.");
        }
        if (TextUtils.equals(str, this.mText)) {
            return;
        }
        this.mTextRes = 0;
        this.mText = str;
        notifyChanged();
    }

    public final void setTextProvider(TextProvider textProvider) {
        this.mTextProvider = textProvider;
        notifyChanged();
    }
}
