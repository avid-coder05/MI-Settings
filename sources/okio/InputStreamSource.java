package okio;

import java.io.IOException;
import java.io.InputStream;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

/* compiled from: JvmOkio.kt */
/* loaded from: classes5.dex */
final class InputStreamSource implements Source {
    @NotNull
    private final InputStream input;
    @NotNull
    private final Timeout timeout;

    public InputStreamSource(@NotNull InputStream input, @NotNull Timeout timeout) {
        Intrinsics.checkNotNullParameter(input, "input");
        Intrinsics.checkNotNullParameter(timeout, "timeout");
        this.input = input;
        this.timeout = timeout;
    }

    @Override // okio.Source, java.io.Closeable, java.lang.AutoCloseable, java.nio.channels.Channel
    public void close() {
        this.input.close();
    }

    @Override // okio.Source
    public long read(@NotNull Buffer sink, long j) {
        Intrinsics.checkNotNullParameter(sink, "sink");
        if (j == 0) {
            return 0L;
        }
        if (j >= 0) {
            try {
                this.timeout.throwIfReached();
                Segment writableSegment$external__okio__android_common__okio_lib = sink.writableSegment$external__okio__android_common__okio_lib(1);
                int read = this.input.read(writableSegment$external__okio__android_common__okio_lib.data, writableSegment$external__okio__android_common__okio_lib.limit, (int) Math.min(j, 8192 - writableSegment$external__okio__android_common__okio_lib.limit));
                if (read != -1) {
                    writableSegment$external__okio__android_common__okio_lib.limit += read;
                    long j2 = read;
                    sink.setSize$external__okio__android_common__okio_lib(sink.size() + j2);
                    return j2;
                } else if (writableSegment$external__okio__android_common__okio_lib.pos == writableSegment$external__okio__android_common__okio_lib.limit) {
                    sink.head = writableSegment$external__okio__android_common__okio_lib.pop();
                    SegmentPool segmentPool = SegmentPool.INSTANCE;
                    SegmentPool.recycle(writableSegment$external__okio__android_common__okio_lib);
                    return -1L;
                } else {
                    return -1L;
                }
            } catch (AssertionError e) {
                if (Okio.isAndroidGetsocknameError(e)) {
                    throw new IOException(e);
                }
                throw e;
            }
        }
        throw new IllegalArgumentException(Intrinsics.stringPlus("byteCount < 0: ", Long.valueOf(j)).toString());
    }

    @NotNull
    public String toString() {
        return "source(" + this.input + ')';
    }
}
