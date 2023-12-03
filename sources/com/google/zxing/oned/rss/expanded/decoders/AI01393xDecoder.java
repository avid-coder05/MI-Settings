package com.google.zxing.oned.rss.expanded.decoders;

import com.google.zxing.NotFoundException;
import com.google.zxing.common.BitArray;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes2.dex */
public final class AI01393xDecoder extends AI01decoder {
    /* JADX INFO: Access modifiers changed from: package-private */
    public AI01393xDecoder(BitArray bitArray) {
        super(bitArray);
    }

    @Override // com.google.zxing.oned.rss.expanded.decoders.AbstractExpandedDecoder
    public String parseInformation() throws NotFoundException {
        if (getInformation().getSize() >= 48) {
            StringBuilder sb = new StringBuilder();
            encodeCompressedGtin(sb, 8);
            int extractNumericValueFromBitArray = getGeneralDecoder().extractNumericValueFromBitArray(48, 2);
            sb.append("(393");
            sb.append(extractNumericValueFromBitArray);
            sb.append(')');
            int extractNumericValueFromBitArray2 = getGeneralDecoder().extractNumericValueFromBitArray(50, 10);
            if (extractNumericValueFromBitArray2 / 100 == 0) {
                sb.append('0');
            }
            if (extractNumericValueFromBitArray2 / 10 == 0) {
                sb.append('0');
            }
            sb.append(extractNumericValueFromBitArray2);
            sb.append(getGeneralDecoder().decodeGeneralPurposeField(60, null).getNewString());
            return sb.toString();
        }
        throw NotFoundException.getNotFoundInstance();
    }
}
