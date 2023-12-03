package miuix.core.util;

import java.io.ByteArrayOutputStream;
import java.io.CharArrayWriter;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.lang.ref.SoftReference;
import miuix.core.util.Pools;

/* loaded from: classes5.dex */
public class IOUtils {
    private static final String LINE_SEPARATOR;
    private static final Pools.Pool<StringWriter> STRING_WRITER_POOL;
    private static final ThreadLocal<SoftReference<byte[]>> THREAD_LOCAL_BYTE_BUFFER = new ThreadLocal<>();
    private static final ThreadLocal<SoftReference<char[]>> THREAD_LOCAL_CHAR_BUFFER = new ThreadLocal<>();
    private static final Pools.Pool<ByteArrayOutputStream> BYTE_ARRAY_OUTPUT_STREAM_POOL = Pools.createSoftReferencePool(new Pools.Manager<ByteArrayOutputStream>() { // from class: miuix.core.util.IOUtils.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // miuix.core.util.Pools.Manager
        public ByteArrayOutputStream createInstance() {
            return new ByteArrayOutputStream();
        }

        @Override // miuix.core.util.Pools.Manager
        public void onRelease(ByteArrayOutputStream byteArrayOutputStream) {
            byteArrayOutputStream.reset();
        }
    }, 2);
    private static final Pools.Pool<CharArrayWriter> CHAR_ARRAY_WRITER_POOL = Pools.createSoftReferencePool(new Pools.Manager<CharArrayWriter>() { // from class: miuix.core.util.IOUtils.2
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // miuix.core.util.Pools.Manager
        public CharArrayWriter createInstance() {
            return new CharArrayWriter();
        }

        @Override // miuix.core.util.Pools.Manager
        public void onRelease(CharArrayWriter charArrayWriter) {
            charArrayWriter.reset();
        }
    }, 2);

    static {
        Pools.SoftReferencePool createSoftReferencePool = Pools.createSoftReferencePool(new Pools.Manager<StringWriter>() { // from class: miuix.core.util.IOUtils.3
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // miuix.core.util.Pools.Manager
            public StringWriter createInstance() {
                return new StringWriter();
            }

            @Override // miuix.core.util.Pools.Manager
            public void onRelease(StringWriter stringWriter) {
                stringWriter.getBuffer().setLength(0);
            }
        }, 2);
        STRING_WRITER_POOL = createSoftReferencePool;
        StringWriter stringWriter = (StringWriter) createSoftReferencePool.acquire();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        printWriter.println();
        printWriter.flush();
        LINE_SEPARATOR = stringWriter.toString();
        printWriter.close();
        createSoftReferencePool.release(stringWriter);
    }

    public static void closeQuietly(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException unused) {
            }
        }
    }

    public static void closeQuietly(InputStream inputStream) {
        if (inputStream != null) {
            try {
                inputStream.close();
            } catch (IOException unused) {
            }
        }
    }

    public static void closeQuietly(OutputStream outputStream) {
        if (outputStream != null) {
            try {
                outputStream.flush();
            } catch (IOException unused) {
            }
            try {
                outputStream.close();
            } catch (IOException unused2) {
            }
        }
    }

    public static void closeQuietly(Reader reader) {
        if (reader != null) {
            try {
                reader.close();
            } catch (IOException unused) {
            }
        }
    }
}
