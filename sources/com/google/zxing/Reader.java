package com.google.zxing;

import java.util.Map;

/* loaded from: classes2.dex */
public interface Reader {
    Result decode(BinaryBitmap binaryBitmap, Map<DecodeHintType, ?> map) throws NotFoundException, ChecksumException, FormatException;

    void reset();
}
