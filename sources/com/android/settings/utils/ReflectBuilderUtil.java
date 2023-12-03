package com.android.settings.utils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/* loaded from: classes2.dex */
public class ReflectBuilderUtil {

    /* loaded from: classes2.dex */
    public static class ReflAgent {
        private Class mClass;
        private Object mObject;
        private Object mResult;

        private ReflAgent() {
        }

        public static ReflAgent getObject(Object obj) {
            ReflAgent reflAgent = new ReflAgent();
            if (obj != null) {
                reflAgent.mObject = obj;
                reflAgent.mClass = obj.getClass();
            }
            return reflAgent;
        }

        public ReflAgent call(String str, Class<?>[] clsArr, Object... objArr) {
            Object obj = this.mObject;
            if (obj != null) {
                try {
                    this.mResult = ReflectBuilderUtil.callObjectMethod(obj, str, clsArr, objArr);
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

        public ReflAgent getObjectFiled(String str) {
            Object obj = this.mObject;
            if (obj != null) {
                try {
                    this.mResult = ReflectBuilderUtil.getObjectField(obj, str);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (NoSuchFieldException e2) {
                    e2.printStackTrace();
                }
            }
            return this;
        }

        public ReflAgent setResultToSelf() {
            this.mObject = this.mResult;
            this.mResult = null;
            return this;
        }
    }

    public static Object callObjectMethod(Object obj, String str, Class<?>[] clsArr, Object... objArr) throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        Method declaredMethod = obj.getClass().getDeclaredMethod(str, clsArr);
        declaredMethod.setAccessible(true);
        return declaredMethod.invoke(obj, objArr);
    }

    public static Object getObjectField(Object obj, String str) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
        Field declaredField = obj.getClass().getDeclaredField(str);
        declaredField.setAccessible(true);
        return declaredField.get(obj);
    }
}
