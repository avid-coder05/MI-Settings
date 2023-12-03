package com.miui.maml.elements.filament;

import android.util.Log;
import com.google.android.filament.Box;
import com.google.android.filament.Engine;
import com.google.android.filament.EntityManager;
import com.google.android.filament.IndexBuffer;
import com.google.android.filament.MaterialInstance;
import com.google.android.filament.RenderableManager;
import com.google.android.filament.VertexBuffer;
import com.miui.maml.ResourceManager;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import kotlin.io.CloseableKt;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

/* compiled from: MeshLoader.kt */
/* loaded from: classes2.dex */
public final class MeshLoaderKt {
    private static final IndexBuffer createIndexBuffer(Engine engine, Header header, ByteBuffer byteBuffer) {
        IndexBuffer build = new IndexBuffer.Builder().bufferType(header.getIndices16Bit() != 0 ? IndexBuffer.Builder.IndexType.USHORT : IndexBuffer.Builder.IndexType.UINT).indexCount((int) header.getTotalIndices()).build(engine);
        Intrinsics.checkNotNullExpressionValue(build, "IndexBuffer.Builder()\n  …           .build(engine)");
        build.setBuffer(engine, byteBuffer);
        return build;
    }

    private static final int createRenderable(Engine engine, Header header, IndexBuffer indexBuffer, VertexBuffer vertexBuffer, List<Part> list, List<String> list2, Map<String, ? extends MaterialInstance> map) {
        RenderableManager.Builder boundingBox = new RenderableManager.Builder((int) header.getParts()).boundingBox(header.getAabb());
        Intrinsics.checkNotNullExpressionValue(boundingBox, "RenderableManager.Builde….boundingBox(header.aabb)");
        int parts = (int) header.getParts();
        for (int i = 0; i < parts; i++) {
            boundingBox.geometry(i, RenderableManager.PrimitiveType.TRIANGLES, vertexBuffer, indexBuffer, (int) list.get(i).getOffset(), (int) list.get(i).getMinIndex(), (int) list.get(i).getMaxIndex(), (int) list.get(i).getIndexCount());
            MaterialInstance materialInstance = map.get(list2.get((int) list.get(i).getMaterialID()));
            if (materialInstance == null || boundingBox.material(i, materialInstance) == null) {
                MaterialInstance materialInstance2 = map.get("DefaultMaterial");
                Intrinsics.checkNotNull(materialInstance2);
                boundingBox.material(i, materialInstance2);
            }
        }
        int create = EntityManager.get().create();
        boundingBox.build(engine, create);
        return create;
    }

    private static final VertexBuffer createVertexBuffer(Engine engine, Header header, ByteBuffer byteBuffer) {
        VertexBuffer.AttributeType attributeType = !uvNormalized(header) ? VertexBuffer.AttributeType.HALF2 : VertexBuffer.AttributeType.SHORT2;
        VertexBuffer.Builder normalized = new VertexBuffer.Builder().bufferCount(1).vertexCount((int) header.getTotalVertices()).normalized(VertexBuffer.VertexAttribute.COLOR).normalized(VertexBuffer.VertexAttribute.TANGENTS).attribute(VertexBuffer.VertexAttribute.POSITION, 0, VertexBuffer.AttributeType.HALF4, (int) header.getPosOffset(), (int) header.getPositionStride()).attribute(VertexBuffer.VertexAttribute.TANGENTS, 0, VertexBuffer.AttributeType.SHORT4, (int) header.getTangentOffset(), (int) header.getTangentStride()).attribute(VertexBuffer.VertexAttribute.COLOR, 0, VertexBuffer.AttributeType.UBYTE4, (int) header.getColorOffset(), (int) header.getColorStride()).attribute(VertexBuffer.VertexAttribute.UV0, 0, attributeType, (int) header.getUv0Offset(), (int) header.getUv0Stride()).normalized(VertexBuffer.VertexAttribute.UV0, uvNormalized(header));
        Intrinsics.checkNotNullExpressionValue(normalized, "VertexBuffer.Builder()\n …V0, uvNormalized(header))");
        if (header.getUv1Offset() != 4294967295L && header.getUv1Stride() != 4294967295L) {
            normalized.attribute(VertexBuffer.VertexAttribute.UV1, 0, attributeType, (int) header.getUv1Offset(), (int) header.getUv1Stride()).normalized(VertexBuffer.VertexAttribute.UV1, uvNormalized(header));
        }
        VertexBuffer build = normalized.build(engine);
        Intrinsics.checkNotNullExpressionValue(build, "vertexBufferBuilder.build(engine)");
        build.setBufferAt(engine, 0, byteBuffer);
        return build;
    }

    public static final void destroyMesh(@NotNull Engine engine, @NotNull Mesh mesh) {
        Intrinsics.checkNotNullParameter(engine, "engine");
        Intrinsics.checkNotNullParameter(mesh, "mesh");
        engine.destroyEntity(mesh.getRenderable());
        engine.destroyIndexBuffer(mesh.getIndexBuffer());
        engine.destroyVertexBuffer(mesh.getVertexBuffer());
        EntityManager.get().destroy(mesh.getRenderable());
    }

    @NotNull
    public static final Mesh loadDefaultMesh(@NotNull MaterialInstance material, @NotNull Engine engine) {
        Intrinsics.checkNotNullParameter(material, "material");
        Intrinsics.checkNotNullParameter(engine, "engine");
        VertexBuffer build = new VertexBuffer.Builder().vertexCount(4).bufferCount(1).attribute(VertexBuffer.VertexAttribute.POSITION, 0, VertexBuffer.AttributeType.FLOAT2, 0, 16).attribute(VertexBuffer.VertexAttribute.UV0, 0, VertexBuffer.AttributeType.FLOAT2, 8, 16).build(engine);
        Intrinsics.checkNotNullExpressionValue(build, "VertexBuffer.Builder()\n …           .build(engine)");
        MeshLoaderKt$loadDefaultMesh$1 meshLoaderKt$loadDefaultMesh$1 = MeshLoaderKt$loadDefaultMesh$1.INSTANCE;
        ByteBuffer order = ByteBuffer.allocateDirect(64).order(ByteOrder.nativeOrder());
        Intrinsics.checkNotNullExpressionValue(order, "ByteBuffer.allocateDirec…(ByteOrder.nativeOrder())");
        build.setBufferAt(engine, 0, meshLoaderKt$loadDefaultMesh$1.invoke(meshLoaderKt$loadDefaultMesh$1.invoke(meshLoaderKt$loadDefaultMesh$1.invoke(meshLoaderKt$loadDefaultMesh$1.invoke(order, new MeshLoaderKt$loadDefaultMesh$Vertex(0.0f, 0.0f, 0.0f, 0.0f)), new MeshLoaderKt$loadDefaultMesh$Vertex(1.0f, 0.0f, 1.0f, 0.0f)), new MeshLoaderKt$loadDefaultMesh$Vertex(0.0f, 1.0f, 0.0f, 1.0f)), new MeshLoaderKt$loadDefaultMesh$Vertex(1.0f, 1.0f, 1.0f, 1.0f)).flip());
        IndexBuffer build2 = new IndexBuffer.Builder().indexCount(6).bufferType(IndexBuffer.Builder.IndexType.USHORT).build(engine);
        Intrinsics.checkNotNullExpressionValue(build2, "IndexBuffer.Builder()\n  …           .build(engine)");
        MeshLoaderKt$loadDefaultMesh$2 meshLoaderKt$loadDefaultMesh$2 = MeshLoaderKt$loadDefaultMesh$2.INSTANCE;
        ByteBuffer order2 = ByteBuffer.allocateDirect(12).order(ByteOrder.LITTLE_ENDIAN);
        Intrinsics.checkNotNullExpressionValue(order2, "ByteBuffer.allocateDirec…(ByteOrder.LITTLE_ENDIAN)");
        build2.setBuffer(engine, meshLoaderKt$loadDefaultMesh$2.invoke(meshLoaderKt$loadDefaultMesh$2.invoke(order2, new MeshLoaderKt$loadDefaultMesh$Triangle((short) 2, (short) 1, (short) 0)), new MeshLoaderKt$loadDefaultMesh$Triangle((short) 1, (short) 2, (short) 3)).flip());
        Box box = new Box(0.0f, 0.0f, 0.0f, 9000.0f, 9000.0f, 9000.0f);
        int create = EntityManager.get().create();
        new RenderableManager.Builder(1).boundingBox(box).geometry(0, RenderableManager.PrimitiveType.TRIANGLES, build, build2).material(0, material).build(engine, create);
        return new Mesh(create, build2, build, box);
    }

    @NotNull
    public static final Mesh loadMesh(@NotNull ResourceManager mgr, @NotNull String name, @NotNull Map<String, ? extends MaterialInstance> materials, @NotNull Engine engine) {
        Intrinsics.checkNotNullParameter(mgr, "mgr");
        Intrinsics.checkNotNullParameter(name, "name");
        Intrinsics.checkNotNullParameter(materials, "materials");
        Intrinsics.checkNotNullParameter(engine, "engine");
        InputStream input = mgr.getInputStream(name);
        try {
            Intrinsics.checkNotNullExpressionValue(input, "input");
            Header readHeader = readHeader(input);
            ReadableByteChannel channel = Channels.newChannel(input);
            Intrinsics.checkNotNullExpressionValue(channel, "channel");
            ByteBuffer readSizedData = readSizedData(channel, readHeader.getVerticesSizeInBytes());
            ByteBuffer readSizedData2 = readSizedData(channel, readHeader.getIndicesSizeInBytes());
            List<Part> readParts = readParts(readHeader, input);
            List<String> readMaterials = readMaterials(input);
            IndexBuffer createIndexBuffer = createIndexBuffer(engine, readHeader, readSizedData2);
            VertexBuffer createVertexBuffer = createVertexBuffer(engine, readHeader, readSizedData);
            Mesh mesh = new Mesh(createRenderable(engine, readHeader, createIndexBuffer, createVertexBuffer, readParts, readMaterials, materials), createIndexBuffer, createVertexBuffer, readHeader.getAabb());
            CloseableKt.closeFinally(input, null);
            return mesh;
        } finally {
        }
    }

    private static final float readFloat32LE(InputStream inputStream) {
        byte[] bArr = new byte[4];
        inputStream.read(bArr, 0, 4);
        ByteBuffer order = ByteBuffer.wrap(bArr).order(ByteOrder.LITTLE_ENDIAN);
        Intrinsics.checkNotNullExpressionValue(order, "ByteBuffer.wrap(bytes).o…(ByteOrder.LITTLE_ENDIAN)");
        return order.getFloat();
    }

    private static final Header readHeader(InputStream inputStream) {
        Header header = new Header();
        if (!readMagicNumber(inputStream)) {
            Log.e("Filament", "Invalid filamesh file.");
            return header;
        }
        header.setVersionNumber(readUIntLE(inputStream));
        header.setParts(readUIntLE(inputStream));
        header.setAabb(new Box(readFloat32LE(inputStream), readFloat32LE(inputStream), readFloat32LE(inputStream), readFloat32LE(inputStream), readFloat32LE(inputStream), readFloat32LE(inputStream)));
        header.setFlags(readUIntLE(inputStream));
        header.setPosOffset(readUIntLE(inputStream));
        header.setPositionStride(readUIntLE(inputStream));
        header.setTangentOffset(readUIntLE(inputStream));
        header.setTangentStride(readUIntLE(inputStream));
        header.setColorOffset(readUIntLE(inputStream));
        header.setColorStride(readUIntLE(inputStream));
        header.setUv0Offset(readUIntLE(inputStream));
        header.setUv0Stride(readUIntLE(inputStream));
        header.setUv1Offset(readUIntLE(inputStream));
        header.setUv1Stride(readUIntLE(inputStream));
        header.setTotalVertices(readUIntLE(inputStream));
        header.setVerticesSizeInBytes(readUIntLE(inputStream));
        header.setIndices16Bit(readUIntLE(inputStream));
        header.setTotalIndices(readUIntLE(inputStream));
        header.setIndicesSizeInBytes(readUIntLE(inputStream));
        header.setValid(true);
        return header;
    }

    private static final int readIntLE(InputStream inputStream) {
        return ((inputStream.read() & 255) << 24) | (inputStream.read() & 255) | ((inputStream.read() & 255) << 8) | ((inputStream.read() & 255) << 16);
    }

    private static final boolean readMagicNumber(InputStream inputStream) {
        byte[] bArr = new byte[8];
        inputStream.read(bArr);
        Charset forName = Charset.forName("UTF-8");
        Intrinsics.checkNotNullExpressionValue(forName, "Charset.forName(\"UTF-8\")");
        return Intrinsics.areEqual(new String(bArr, forName), "FILAMESH");
    }

    private static final List<String> readMaterials(InputStream inputStream) {
        int readUIntLE = (int) readUIntLE(inputStream);
        ArrayList arrayList = new ArrayList(readUIntLE);
        for (int i = 0; i < readUIntLE; i++) {
            byte[] bArr = new byte[(int) readUIntLE(inputStream)];
            inputStream.read(bArr);
            inputStream.skip(1L);
            Charset forName = Charset.forName("UTF-8");
            Intrinsics.checkNotNullExpressionValue(forName, "Charset.forName(\"UTF-8\")");
            arrayList.add(new String(bArr, forName));
        }
        return arrayList;
    }

    private static final List<Part> readParts(Header header, InputStream inputStream) {
        int parts = (int) header.getParts();
        ArrayList arrayList = new ArrayList(parts);
        for (int i = 0; i < parts; i++) {
            Part part = new Part();
            part.setOffset(readUIntLE(inputStream));
            part.setIndexCount(readUIntLE(inputStream));
            part.setMinIndex(readUIntLE(inputStream));
            part.setMaxIndex(readUIntLE(inputStream));
            part.setMaterialID(readUIntLE(inputStream));
            part.setAabb(new Box(readFloat32LE(inputStream), readFloat32LE(inputStream), readFloat32LE(inputStream), readFloat32LE(inputStream), readFloat32LE(inputStream), readFloat32LE(inputStream)));
            arrayList.add(part);
        }
        return arrayList;
    }

    private static final ByteBuffer readSizedData(ReadableByteChannel readableByteChannel, long j) {
        ByteBuffer buffer = ByteBuffer.allocateDirect((int) j);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        readableByteChannel.read(buffer);
        buffer.flip();
        Intrinsics.checkNotNullExpressionValue(buffer, "buffer");
        return buffer;
    }

    private static final long readUIntLE(InputStream inputStream) {
        return readIntLE(inputStream) & 4294967295L;
    }

    private static final boolean uvNormalized(Header header) {
        return (header.getFlags() & 2) != 0;
    }
}
