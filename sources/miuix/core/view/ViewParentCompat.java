package miuix.core.view;

import android.os.Build;
import android.util.Log;
import android.view.View;
import android.view.ViewParent;
import androidx.core.view.NestedScrollingParent;
import miui.core.view.NestedScrollingParent2;
import miui.core.view.NestedScrollingParent3;

/* loaded from: classes5.dex */
public final class ViewParentCompat {
    public static boolean onNestedFling(ViewParent viewParent, View view, float f, float f2, boolean z) {
        if (Build.VERSION.SDK_INT < 21) {
            if (viewParent instanceof NestedScrollingParent) {
                return ((NestedScrollingParent) viewParent).onNestedFling(view, f, f2, z);
            }
            if (viewParent instanceof miui.core.view.NestedScrollingParent) {
                return ((miui.core.view.NestedScrollingParent) viewParent).onNestedFling(view, f, f2, z);
            }
            return false;
        }
        try {
            return viewParent.onNestedFling(view, f, f2, z);
        } catch (AbstractMethodError e) {
            Log.e("ViewParentCompat", "ViewParent " + viewParent + " does not implement interface method onNestedFling", e);
            return false;
        }
    }

    public static boolean onNestedPreFling(ViewParent viewParent, View view, float f, float f2) {
        if (Build.VERSION.SDK_INT < 21) {
            if (viewParent instanceof NestedScrollingParent) {
                return ((NestedScrollingParent) viewParent).onNestedPreFling(view, f, f2);
            }
            if (viewParent instanceof miui.core.view.NestedScrollingParent) {
                return ((miui.core.view.NestedScrollingParent) viewParent).onNestedPreFling(view, f, f2);
            }
            return false;
        }
        try {
            return viewParent.onNestedPreFling(view, f, f2);
        } catch (AbstractMethodError e) {
            Log.e("ViewParentCompat", "ViewParent " + viewParent + " does not implement interface method onNestedPreFling", e);
            return false;
        }
    }

    public static void onNestedPreScroll(ViewParent viewParent, View view, int i, int i2, int[] iArr, int i3) {
        if (viewParent instanceof NestedScrollingParent2) {
            ((NestedScrollingParent2) viewParent).onNestedPreScroll(view, i, i2, iArr, i3);
        } else if (viewParent instanceof androidx.core.view.NestedScrollingParent2) {
            ((androidx.core.view.NestedScrollingParent2) viewParent).onNestedPreScroll(view, i, i2, iArr, i3);
        } else if (i3 == 0) {
            if (Build.VERSION.SDK_INT < 21) {
                if (viewParent instanceof NestedScrollingParent) {
                    ((NestedScrollingParent) viewParent).onNestedPreScroll(view, i, i2, iArr);
                    return;
                } else if (viewParent instanceof miui.core.view.NestedScrollingParent) {
                    ((miui.core.view.NestedScrollingParent) viewParent).onNestedPreScroll(view, i, i2, iArr);
                    return;
                } else {
                    return;
                }
            }
            try {
                viewParent.onNestedPreScroll(view, i, i2, iArr);
            } catch (AbstractMethodError e) {
                Log.e("ViewParentCompat", "ViewParent " + viewParent + " does not implement interface method onNestedPreScroll", e);
            }
        }
    }

    public static void onNestedScroll(ViewParent viewParent, View view, int i, int i2, int i3, int i4, int i5, int[] iArr) {
        if (viewParent instanceof NestedScrollingParent3) {
            ((NestedScrollingParent3) viewParent).onNestedScroll(view, i, i2, i3, i4, i5, iArr);
        } else if (viewParent instanceof androidx.core.view.NestedScrollingParent3) {
            ((androidx.core.view.NestedScrollingParent3) viewParent).onNestedScroll(view, i, i2, i3, i4, i5, iArr);
        } else {
            iArr[0] = iArr[0] + i3;
            iArr[1] = iArr[1] + i4;
            if (viewParent instanceof NestedScrollingParent2) {
                ((NestedScrollingParent2) viewParent).onNestedScroll(view, i, i2, i3, i4, i5);
            } else if (viewParent instanceof androidx.core.view.NestedScrollingParent2) {
                ((androidx.core.view.NestedScrollingParent2) viewParent).onNestedScroll(view, i, i2, i3, i4, i5);
            } else if (i5 == 0) {
                if (Build.VERSION.SDK_INT < 21) {
                    if (viewParent instanceof NestedScrollingParent) {
                        ((NestedScrollingParent) viewParent).onNestedScroll(view, i, i2, i3, i4);
                        return;
                    } else if (viewParent instanceof miui.core.view.NestedScrollingParent) {
                        ((miui.core.view.NestedScrollingParent) viewParent).onNestedScroll(view, i, i2, i3, i4);
                        return;
                    } else {
                        return;
                    }
                }
                try {
                    viewParent.onNestedScroll(view, i, i2, i3, i4);
                } catch (AbstractMethodError e) {
                    Log.e("ViewParentCompat", "ViewParent " + viewParent + " does not implement interface method onNestedScroll", e);
                }
            }
        }
    }

    public static void onNestedScrollAccepted(ViewParent viewParent, View view, View view2, int i, int i2) {
        if (viewParent instanceof NestedScrollingParent2) {
            ((NestedScrollingParent2) viewParent).onNestedScrollAccepted(view, view2, i, i2);
        } else if (viewParent instanceof androidx.core.view.NestedScrollingParent2) {
            ((androidx.core.view.NestedScrollingParent2) viewParent).onNestedScrollAccepted(view, view2, i, i2);
        } else if (i2 == 0) {
            if (Build.VERSION.SDK_INT < 21) {
                if (viewParent instanceof NestedScrollingParent) {
                    ((NestedScrollingParent) viewParent).onNestedScrollAccepted(view, view2, i);
                    return;
                } else if (viewParent instanceof miui.core.view.NestedScrollingParent) {
                    ((miui.core.view.NestedScrollingParent) viewParent).onNestedScrollAccepted(view, view2, i);
                    return;
                } else {
                    return;
                }
            }
            try {
                viewParent.onNestedScrollAccepted(view, view2, i);
            } catch (AbstractMethodError e) {
                Log.e("ViewParentCompat", "ViewParent " + viewParent + " does not implement interface method onNestedScrollAccepted", e);
            }
        }
    }

    public static boolean onStartNestedScroll(ViewParent viewParent, View view, View view2, int i, int i2) {
        if (viewParent instanceof NestedScrollingParent2) {
            return ((NestedScrollingParent2) viewParent).onStartNestedScroll(view, view2, i, i2);
        }
        if (viewParent instanceof androidx.core.view.NestedScrollingParent2) {
            return ((androidx.core.view.NestedScrollingParent2) viewParent).onStartNestedScroll(view, view2, i, i2);
        }
        if (i2 == 0) {
            if (Build.VERSION.SDK_INT < 21) {
                if (viewParent instanceof NestedScrollingParent) {
                    return ((NestedScrollingParent) viewParent).onStartNestedScroll(view, view2, i);
                }
                if (viewParent instanceof miui.core.view.NestedScrollingParent) {
                    return ((miui.core.view.NestedScrollingParent) viewParent).onStartNestedScroll(view, view2, i);
                }
                return false;
            }
            try {
                return viewParent.onStartNestedScroll(view, view2, i);
            } catch (AbstractMethodError e) {
                Log.e("ViewParentCompat", "ViewParent " + viewParent + " does not implement interface method onStartNestedScroll", e);
                return false;
            }
        }
        return false;
    }

    public static void onStopNestedScroll(ViewParent viewParent, View view, int i) {
        if (viewParent instanceof NestedScrollingParent2) {
            ((NestedScrollingParent2) viewParent).onStopNestedScroll(view, i);
        } else if (viewParent instanceof androidx.core.view.NestedScrollingParent2) {
            ((androidx.core.view.NestedScrollingParent2) viewParent).onStopNestedScroll(view, i);
        } else if (i == 0) {
            if (Build.VERSION.SDK_INT < 21) {
                if (viewParent instanceof NestedScrollingParent) {
                    ((NestedScrollingParent) viewParent).onStopNestedScroll(view);
                    return;
                } else if (viewParent instanceof miui.core.view.NestedScrollingParent) {
                    ((miui.core.view.NestedScrollingParent) viewParent).onStopNestedScroll(view);
                    return;
                } else {
                    return;
                }
            }
            try {
                viewParent.onStopNestedScroll(view);
            } catch (AbstractMethodError e) {
                Log.e("ViewParentCompat", "ViewParent " + viewParent + " does not implement interface method onStopNestedScroll", e);
            }
        }
    }
}
