package src.com.android.settings.emergency.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/* loaded from: classes5.dex */
public class WaveHeader {
    public int AvgBytesPerSec;
    public short BitsPerSample;
    public short BlockAlign;
    public short Channels;
    public int DataHdrLeth;
    public int FmtHdrLeth;
    public short FormatTag;
    public int SamplesPerSec;
    public int fileLength;
    public final char[] fileID = {'R', 'I', 'F', 'F'};
    public char[] wavTag = {'W', 'A', 'V', 'E'};
    public char[] FmtHdrID = {'f', 'm', 't', ' '};
    public char[] DataHdrID = {'d', 'a', 't', 'a'};

    private void WriteChar(ByteArrayOutputStream byteArrayOutputStream, char[] cArr) {
        for (char c : cArr) {
            byteArrayOutputStream.write(c);
        }
    }

    private void WriteInt(ByteArrayOutputStream byteArrayOutputStream, int i) throws IOException {
        byteArrayOutputStream.write(new byte[]{(byte) ((i << 24) >> 24), (byte) ((i << 16) >> 24), (byte) ((i << 8) >> 24), (byte) (i >> 24)});
    }

    private void WriteShort(ByteArrayOutputStream byteArrayOutputStream, int i) throws IOException {
        byteArrayOutputStream.write(new byte[]{(byte) ((i << 24) >> 24), (byte) ((i << 16) >> 24)});
    }

    public byte[] getHeader() throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        WriteChar(byteArrayOutputStream, this.fileID);
        WriteInt(byteArrayOutputStream, this.fileLength);
        WriteChar(byteArrayOutputStream, this.wavTag);
        WriteChar(byteArrayOutputStream, this.FmtHdrID);
        WriteInt(byteArrayOutputStream, this.FmtHdrLeth);
        WriteShort(byteArrayOutputStream, this.FormatTag);
        WriteShort(byteArrayOutputStream, this.Channels);
        WriteInt(byteArrayOutputStream, this.SamplesPerSec);
        WriteInt(byteArrayOutputStream, this.AvgBytesPerSec);
        WriteShort(byteArrayOutputStream, this.BlockAlign);
        WriteShort(byteArrayOutputStream, this.BitsPerSample);
        WriteChar(byteArrayOutputStream, this.DataHdrID);
        WriteInt(byteArrayOutputStream, this.DataHdrLeth);
        byteArrayOutputStream.flush();
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        byteArrayOutputStream.close();
        return byteArray;
    }
}
