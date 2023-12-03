package miuix.appcompat.internal.app.widget;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.PagerAdapter;
import java.util.ArrayList;

/* JADX INFO: Access modifiers changed from: package-private */
/* compiled from: ActionBarViewPagerController.java */
/* loaded from: classes5.dex */
public class DynamicFragmentPagerAdapter extends PagerAdapter {
    private Context mContext;
    private FragmentManager mFragmentManager;
    private ArrayList<FragmentInfo> mFragmentInfos = new ArrayList<>();
    private FragmentTransaction mCurTransaction = null;
    private Fragment mCurrentPrimaryItem = null;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* compiled from: ActionBarViewPagerController.java */
    /* loaded from: classes5.dex */
    public class FragmentInfo {
        Bundle args;
        Class<? extends Fragment> clazz;
        Fragment fragment = null;
        boolean hasActionMenu;
        ActionBar.Tab tab;
        String tag;

        FragmentInfo(String str, Class<? extends Fragment> cls, Bundle bundle, ActionBar.Tab tab, boolean z) {
            this.tag = str;
            this.clazz = cls;
            this.args = bundle;
            this.tab = tab;
            this.hasActionMenu = z;
        }
    }

    public DynamicFragmentPagerAdapter(Context context, FragmentManager fragmentManager) {
        this.mContext = context;
        this.mFragmentManager = fragmentManager;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int addFragment(String str, int i, Class<? extends Fragment> cls, Bundle bundle, ActionBar.Tab tab, boolean z) {
        FragmentInfo fragmentInfo = new FragmentInfo(str, cls, bundle, tab, z);
        if (!isRTL()) {
            this.mFragmentInfos.add(i, fragmentInfo);
        } else if (i >= this.mFragmentInfos.size()) {
            this.mFragmentInfos.add(0, fragmentInfo);
        } else {
            this.mFragmentInfos.add(toIndexForRTL(i) + 1, fragmentInfo);
        }
        notifyDataSetChanged();
        return i;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int addFragment(String str, Class<? extends Fragment> cls, Bundle bundle, ActionBar.Tab tab, boolean z) {
        if (isRTL()) {
            this.mFragmentInfos.add(0, new FragmentInfo(str, cls, bundle, tab, z));
        } else {
            this.mFragmentInfos.add(new FragmentInfo(str, cls, bundle, tab, z));
        }
        notifyDataSetChanged();
        return this.mFragmentInfos.size() - 1;
    }

    @Override // androidx.viewpager.widget.PagerAdapter
    public void destroyItem(ViewGroup viewGroup, int i, Object obj) {
        if (this.mCurTransaction == null) {
            this.mCurTransaction = this.mFragmentManager.beginTransaction();
        }
        this.mCurTransaction.detach((Fragment) obj);
    }

    @Override // androidx.viewpager.widget.PagerAdapter
    public void finishUpdate(ViewGroup viewGroup) {
        FragmentTransaction fragmentTransaction = this.mCurTransaction;
        if (fragmentTransaction != null) {
            fragmentTransaction.commitAllowingStateLoss();
            this.mCurTransaction = null;
            this.mFragmentManager.executePendingTransactions();
        }
    }

    @Override // androidx.viewpager.widget.PagerAdapter
    public int getCount() {
        return this.mFragmentInfos.size();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Fragment getFragment(int i, boolean z) {
        return getFragment(i, z, true);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Fragment getFragment(int i, boolean z, boolean z2) {
        Class<? extends Fragment> cls;
        if (this.mFragmentInfos.isEmpty()) {
            return null;
        }
        ArrayList<FragmentInfo> arrayList = this.mFragmentInfos;
        if (z2) {
            i = toIndexForRTL(i);
        }
        FragmentInfo fragmentInfo = arrayList.get(i);
        if (fragmentInfo.fragment == null) {
            Fragment findFragmentByTag = this.mFragmentManager.findFragmentByTag(fragmentInfo.tag);
            fragmentInfo.fragment = findFragmentByTag;
            if (findFragmentByTag == null && z && (cls = fragmentInfo.clazz) != null) {
                fragmentInfo.fragment = Fragment.instantiate(this.mContext, cls.getName(), fragmentInfo.args);
                fragmentInfo.clazz = null;
                fragmentInfo.args = null;
            }
        }
        return fragmentInfo.fragment;
    }

    @Override // androidx.viewpager.widget.PagerAdapter
    public int getItemPosition(Object obj) {
        int size = this.mFragmentInfos.size();
        for (int i = 0; i < size; i++) {
            if (obj == this.mFragmentInfos.get(i).fragment) {
                return i;
            }
        }
        return -2;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ActionBar.Tab getTabAt(int i) {
        return this.mFragmentInfos.get(i).tab;
    }

    public boolean hasActionMenu(int i) {
        if (i < 0 || i >= this.mFragmentInfos.size()) {
            return false;
        }
        return this.mFragmentInfos.get(i).hasActionMenu;
    }

    @Override // androidx.viewpager.widget.PagerAdapter
    public Object instantiateItem(ViewGroup viewGroup, int i) {
        if (this.mCurTransaction == null) {
            this.mCurTransaction = this.mFragmentManager.beginTransaction();
        }
        Fragment fragment = getFragment(i, true, false);
        if (fragment.getFragmentManager() != null) {
            this.mCurTransaction.attach(fragment);
        } else {
            this.mCurTransaction.add(viewGroup.getId(), fragment, this.mFragmentInfos.get(i).tag);
        }
        if (fragment != this.mCurrentPrimaryItem) {
            fragment.setMenuVisibility(false);
            fragment.setUserVisibleHint(false);
        }
        return fragment;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean isRTL() {
        return this.mContext.getResources().getConfiguration().getLayoutDirection() == 1;
    }

    @Override // androidx.viewpager.widget.PagerAdapter
    public boolean isViewFromObject(View view, Object obj) {
        return ((Fragment) obj).getView() == view;
    }

    @Override // androidx.viewpager.widget.PagerAdapter
    public void setPrimaryItem(ViewGroup viewGroup, int i, Object obj) {
        Fragment fragment = (Fragment) obj;
        Fragment fragment2 = this.mCurrentPrimaryItem;
        if (fragment != fragment2) {
            if (fragment2 != null) {
                fragment2.setMenuVisibility(false);
                this.mCurrentPrimaryItem.setUserVisibleHint(false);
            }
            if (fragment != null) {
                fragment.setMenuVisibility(true);
                fragment.setUserVisibleHint(true);
            }
            this.mCurrentPrimaryItem = fragment;
        }
    }

    @Override // androidx.viewpager.widget.PagerAdapter
    public void startUpdate(ViewGroup viewGroup) {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int toIndexForRTL(int i) {
        if (isRTL()) {
            int size = this.mFragmentInfos.size() - 1;
            if (size > i) {
                return size - i;
            }
            return 0;
        }
        return i;
    }
}
