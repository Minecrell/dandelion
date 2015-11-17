/*
 * Copyright (c) 2015, Minecrell <https://github.com/Minecrell>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package net.minecrell.dandelion.decompiler;

import com.google.common.io.ByteStreams;
import org.jetbrains.java.decompiler.main.Fernflower;
import org.jetbrains.java.decompiler.main.decompiler.PrintStreamLogger;
import org.jetbrains.java.decompiler.main.extern.IBytecodeProvider;
import org.jetbrains.java.decompiler.struct.StructClass;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class DandelionDecompiler implements IBytecodeProvider, AutoCloseable {

    private final Fernflower fernflower;

    public DandelionDecompiler(Path source) {
        this.fernflower = new Fernflower(this, NullResourceSaver.INSTANCE, null, new PrintStreamLogger(System.out));
        this.fernflower.getStructContext().addSpace(source.toFile(), true);
        this.fernflower.decompileContext(false);
    }

    @Override
    public byte[] getBytecode(String externalPath, String internalPath) throws IOException {
        Path path = Paths.get(externalPath);
        if (internalPath != null) {
            try (ZipFile zip = new ZipFile(path.toFile())) {
                ZipEntry entry = zip.getEntry(internalPath);
                try (InputStream in = zip.getInputStream(entry)) {
                    return ByteStreams.toByteArray(in);
                }
            }
        } else {
            return Files.readAllBytes(path);
        }
    }

    public String decompile(String name) {
        StructClass structClass = this.fernflower.getStructContext().getClass(name);
        return this.fernflower.getClassContent(structClass);
    }

    @Override
    public void close() {
        this.fernflower.clearContext();
    }

}
