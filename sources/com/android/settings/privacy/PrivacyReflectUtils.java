package com.android.settings.privacy;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/* loaded from: classes2.dex */
public class PrivacyReflectUtils {

    /* loaded from: classes2.dex */
    public static class ReflAgent {
        private Class mClass;
        private Object mObject;
        private Object mResult;

        private ReflAgent() {
        }

        public static ReflAgent getClass(String str) {
            ReflAgent reflAgent = new ReflAgent();
            try {
                reflAgent.mClass = Class.forName(str);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            return reflAgent;
        }

        public boolean booleanResult() {
            Object obj = this.mResult;
            if (obj == null) {
                return false;
            }
            return ((Boolean) obj).booleanValue();
        }

        public ReflAgent call(String str, Class<?>[] clsArr, Object... objArr) {
            Object obj = this.mObject;
            if (obj != null) {
                try {
                    this.mResult = PrivacyReflectUtils.callObjectMethod(obj, str, clsArr, objArr);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (NoSuchMethodException e2) {
                    e2.printStackTrace();
                } catch (InvocationTargetException e3) {
                    e3.printStackTrace();
                }
            }
            return this;
        }

        public ReflAgent callStatic(String str, Class<?>[] clsArr, Object... objArr) {
            Class cls = this.mClass;
            if (cls != null) {
                try {
                    this.mResult = PrivacyReflectUtils.callStaticObjectMethod(cls, str, clsArr, objArr);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (NoSuchMethodException e2) {
                    e2.printStackTrace();
                } catch (InvocationTargetException e3) {
                    e3.printStackTrace();
                }
            }
            return this;
        }

        public ReflAgent setResultToSelf() {
            this.mObject = this.mResult;
            this.mResult = null;
            return this;
        }

        public String stringResult() {
            Object obj = this.mResult;
            if (obj == null) {
                return null;
            }
            return obj.toString();
        }
    }

    public static Object callObjectMethod(Object obj, String str, Class<?>[] clsArr, Object... objArr) throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        Method declaredMethod = obj.getClass().getDeclaredMethod(str, clsArr);
        declaredMethod.setAccessible(true);
        return declaredMethod.invoke(obj, objArr);
    }

    public static Object callStaticObjectMethod(Class<?> cls, String str, Class<?>[] clsArr, Object... objArr) throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        Method declaredMethod = cls.getDeclaredMethod(str, clsArr);
        declaredMethod.setAccessible(true);
        return declaredMethod.invoke(null, objArr);
    }
}
