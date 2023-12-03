package com.iqiyi.android.qigsaw.core.splitload.compat;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import com.iqiyi.android.qigsaw.core.common.AbiUtil;

/* loaded from: classes2.dex */
public class NativePathMapperImpl implements NativePathMapper {
    private final NativePathMapper mapper;

    public NativePathMapperImpl(Context context) {
        if (needUseCommonSoDir(context)) {
            this.mapper = new PathMapperV21(context);
        } else {
            this.mapper = new PathMapperAbove21(context);
        }
    }

    private boolean needUseCommonSoDir(Context context) {
        int i = Build.VERSION.SDK_INT;
        return i >= 21 && i < 23 && AbiUtil.isArm64(context);
    }

    @Override // com.iqiyi.android.qigsaw.core.splitload.compat.NativePathMapper
    public String map(String str, String str2) {
        String map;
        if (TextUtils.isEmpty(str) || TextUtils.isEmpty(str2)) {
            return str2;
        }
        synchronized (Runtime.getRuntime()) {
            map = this.mapper.map(str, str2);
        }
        return map;
    }
}
