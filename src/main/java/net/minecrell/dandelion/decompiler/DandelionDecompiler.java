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

import org.jetbrains.java.decompiler.main.Fernflower;
import org.jetbrains.java.decompiler.main.decompiler.PrintStreamLogger;
import org.jetbrains.java.decompiler.struct.FileStructContext;
import org.jetbrains.java.decompiler.struct.StructClass;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Set;
import java.util.stream.Stream;

public class DandelionDecompiler implements AutoCloseable {

    private final Fernflower fernflower;

    public DandelionDecompiler(Path source) throws IOException {
        FileStructContext context = new FileStructContext();
        this.fernflower = new Fernflower(context, null, new PrintStreamLogger(System.out));
        context.scan(source, true);
        this.fernflower.decompileContext();
    }

    public Stream<String> getClasses() {
        return this.fernflower.getStructContext().getClasses().values().stream()
                .map(structClass -> structClass.qualifiedName);
    }


    public Set<String> getResources() {
        return this.fernflower.getStructContext().getResources();
    }

    public String decompile(String name) {
        StructClass structClass = this.fernflower.getStructContext().getClass(name);
        return this.fernflower.getClassContent(structClass);
    }

    public byte[] getResource(String path) throws IOException {
        return this.fernflower.getStructContext().readResource(path);
    }

    @Override
    public void close() throws IOException {
        this.fernflower.close();
    }

}
