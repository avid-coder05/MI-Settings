package com.split.signature;

import java.security.cert.X509Certificate;

/* loaded from: classes2.dex */
final class X509CertificateEx extends X509CertificateWrapper {
    private byte[] a;

    /* JADX INFO: Access modifiers changed from: package-private */
    public X509CertificateEx(X509Certificate x509Certificate, byte[] bArr) {
        super(x509Certificate);
        this.a = bArr;
    }

    @Override // com.split.signature.X509CertificateWrapper, java.security.cert.Certificate
    public byte[] getEncoded() {
        return this.a;
    }
}
