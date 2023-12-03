package com.iqiyi.android.qigsaw.core.splitload.compat;

import android.content.Context;

/* loaded from: classes2.dex */
class PathMapperAbove21 implements NativePathMapper {
    private final Context context;

    /* JADX INFO: Access modifiers changed from: package-private */
    public PathMapperAbove21(Context context) {
        this.context = context;
    }

    @Override // com.iqiyi.android.qigsaw.core.splitload.compat.NativePathMapper
    public String map(String str, String str2) {
        return str2;
    }
}
