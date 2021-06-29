// 
// Decompiled by Procyon v0.5.36
// 

package rp.com.google.common.io;

import rp.com.google.common.collect.ImmutableSet;
import rp.com.google.common.base.Optional;
import rp.com.google.common.base.Predicate;
import java.util.Collections;
import java.util.Arrays;
import rp.com.google.common.graph.Traverser;
import java.util.Iterator;
import rp.com.google.common.base.Joiner;
import java.util.ArrayList;
import rp.com.google.common.base.Splitter;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.MappedByteBuffer;
import rp.com.google.common.hash.HashCode;
import rp.com.google.common.hash.HashFunction;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import rp.com.google.common.collect.Lists;
import java.util.List;
import java.io.IOException;
import java.io.Writer;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.FileOutputStream;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.Reader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.FileInputStream;
import rp.com.google.common.base.Preconditions;
import java.io.BufferedReader;
import java.nio.charset.Charset;
import rp.com.google.common.graph.SuccessorsFunction;
import java.io.File;
import rp.com.google.common.collect.TreeTraverser;
import rp.com.google.common.annotations.GwtIncompatible;
import rp.com.google.common.annotations.Beta;

@Beta
@GwtIncompatible
public final class Files
{
    private static final int TEMP_DIR_ATTEMPTS = 10000;
    private static final TreeTraverser<File> FILE_TREE_TRAVERSER;
    private static final SuccessorsFunction<File> FILE_TREE;
    
    private Files() {
    }
    
    public static BufferedReader newReader(final File file, final Charset charset) throws FileNotFoundException {
        Preconditions.checkNotNull(file);
        Preconditions.checkNotNull(charset);
        return new BufferedReader(new InputStreamReader(new FileInputStream(file), charset));
    }
    
    public static BufferedWriter newWriter(final File file, final Charset charset) throws FileNotFoundException {
        Preconditions.checkNotNull(file);
        Preconditions.checkNotNull(charset);
        return new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), charset));
    }
    
    public static ByteSource asByteSource(final File file) {
        return new FileByteSource(file);
    }
    
    public static ByteSink asByteSink(final File file, final FileWriteMode... modes) {
        return new FileByteSink(file, modes);
    }
    
    public static CharSource asCharSource(final File file, final Charset charset) {
        return asByteSource(file).asCharSource(charset);
    }
    
    public static CharSink asCharSink(final File file, final Charset charset, final FileWriteMode... modes) {
        return asByteSink(file, modes).asCharSink(charset);
    }
    
    public static byte[] toByteArray(final File file) throws IOException {
        return asByteSource(file).read();
    }
    
    @Deprecated
    public static String toString(final File file, final Charset charset) throws IOException {
        return asCharSource(file, charset).read();
    }
    
    public static void write(final byte[] from, final File to) throws IOException {
        asByteSink(to, new FileWriteMode[0]).write(from);
    }
    
    @Deprecated
    public static void write(final CharSequence from, final File to, final Charset charset) throws IOException {
        asCharSink(to, charset, new FileWriteMode[0]).write(from);
    }
    
    public static void copy(final File from, final OutputStream to) throws IOException {
        asByteSource(from).copyTo(to);
    }
    
    public static void copy(final File from, final File to) throws IOException {
        Preconditions.checkArgument(!from.equals(to), "Source %s and destination %s must be different", from, to);
        asByteSource(from).copyTo(asByteSink(to, new FileWriteMode[0]));
    }
    
    @Deprecated
    public static void copy(final File from, final Charset charset, final Appendable to) throws IOException {
        asCharSource(from, charset).copyTo(to);
    }
    
    @Deprecated
    public static void append(final CharSequence from, final File to, final Charset charset) throws IOException {
        asCharSink(to, charset, FileWriteMode.APPEND).write(from);
    }
    
    public static boolean equal(final File file1, final File file2) throws IOException {
        Preconditions.checkNotNull(file1);
        Preconditions.checkNotNull(file2);
        if (file1 == file2 || file1.equals(file2)) {
            return true;
        }
        final long len1 = file1.length();
        final long len2 = file2.length();
        return (len1 == 0L || len2 == 0L || len1 == len2) && asByteSource(file1).contentEquals(asByteSource(file2));
    }
    
    public static File createTempDir() {
        final File baseDir = new File(System.getProperty("java.io.tmpdir"));
        final String baseName = System.currentTimeMillis() + "-";
        for (int counter = 0; counter < 10000; ++counter) {
            final File tempDir = new File(baseDir, baseName + counter);
            if (tempDir.mkdir()) {
                return tempDir;
            }
        }
        throw new IllegalStateException("Failed to create directory within 10000 attempts (tried " + baseName + "0 to " + baseName + 9999 + ')');
    }
    
    public static void touch(final File file) throws IOException {
        Preconditions.checkNotNull(file);
        if (!file.createNewFile() && !file.setLastModified(System.currentTimeMillis())) {
            throw new IOException("Unable to update modification time of " + file);
        }
    }
    
    public static void createParentDirs(final File file) throws IOException {
        Preconditions.checkNotNull(file);
        final File parent = file.getCanonicalFile().getParentFile();
        if (parent == null) {
            return;
        }
        parent.mkdirs();
        if (!parent.isDirectory()) {
            throw new IOException("Unable to create parent directories of " + file);
        }
    }
    
    public static void move(final File from, final File to) throws IOException {
        Preconditions.checkNotNull(from);
        Preconditions.checkNotNull(to);
        Preconditions.checkArgument(!from.equals(to), "Source %s and destination %s must be different", from, to);
        if (!from.renameTo(to)) {
            copy(from, to);
            if (!from.delete()) {
                if (!to.delete()) {
                    throw new IOException("Unable to delete " + to);
                }
                throw new IOException("Unable to delete " + from);
            }
        }
    }
    
    @Deprecated
    public static String readFirstLine(final File file, final Charset charset) throws IOException {
        return asCharSource(file, charset).readFirstLine();
    }
    
    public static List<String> readLines(final File file, final Charset charset) throws IOException {
        return asCharSource(file, charset).readLines((LineProcessor<List<String>>)new LineProcessor<List<String>>() {
            final List<String> result = Lists.newArrayList();
            
            @Override
            public boolean processLine(final String line) {
                this.result.add(line);
                return true;
            }
            
            @Override
            public List<String> getResult() {
                return this.result;
            }
        });
    }
    
    @Deprecated
    @CanIgnoreReturnValue
    public static <T> T readLines(final File file, final Charset charset, final LineProcessor<T> callback) throws IOException {
        return asCharSource(file, charset).readLines(callback);
    }
    
    @Deprecated
    @CanIgnoreReturnValue
    public static <T> T readBytes(final File file, final ByteProcessor<T> processor) throws IOException {
        return asByteSource(file).read(processor);
    }
    
    @Deprecated
    public static HashCode hash(final File file, final HashFunction hashFunction) throws IOException {
        return asByteSource(file).hash(hashFunction);
    }
    
    public static MappedByteBuffer map(final File file) throws IOException {
        Preconditions.checkNotNull(file);
        return map(file, FileChannel.MapMode.READ_ONLY);
    }
    
    public static MappedByteBuffer map(final File file, final FileChannel.MapMode mode) throws IOException {
        return mapInternal(file, mode, -1L);
    }
    
    public static MappedByteBuffer map(final File file, final FileChannel.MapMode mode, final long size) throws IOException {
        Preconditions.checkArgument(size >= 0L, "size (%s) may not be negative", size);
        return mapInternal(file, mode, size);
    }
    
    private static MappedByteBuffer mapInternal(final File file, final FileChannel.MapMode mode, final long size) throws IOException {
        Preconditions.checkNotNull(file);
        Preconditions.checkNotNull(mode);
        final Closer closer = Closer.create();
        try {
            final RandomAccessFile raf = closer.register(new RandomAccessFile(file, (mode == FileChannel.MapMode.READ_ONLY) ? "r" : "rw"));
            final FileChannel channel = closer.register(raf.getChannel());
            return channel.map(mode, 0L, (size == -1L) ? channel.size() : size);
        }
        catch (Throwable e) {
            throw closer.rethrow(e);
        }
        finally {
            closer.close();
        }
    }
    
    public static String simplifyPath(final String pathname) {
        Preconditions.checkNotNull(pathname);
        if (pathname.length() == 0) {
            return ".";
        }
        final Iterable<String> components = Splitter.on('/').omitEmptyStrings().split(pathname);
        final List<String> path = new ArrayList<String>();
        for (final String s : components) {
            final String component = s;
            switch (s) {
                case ".": {
                    continue;
                }
                case "..": {
                    if (path.size() > 0 && !path.get(path.size() - 1).equals("..")) {
                        path.remove(path.size() - 1);
                        continue;
                    }
                    path.add("..");
                    continue;
                }
                default: {
                    path.add(component);
                    continue;
                }
            }
        }
        String result = Joiner.on('/').join(path);
        if (pathname.charAt(0) == '/') {
            result = "/" + result;
        }
        while (result.startsWith("/../")) {
            result = result.substring(3);
        }
        if (result.equals("/..")) {
            result = "/";
        }
        else if ("".equals(result)) {
            result = ".";
        }
        return result;
    }
    
    public static String getFileExtension(final String fullName) {
        Preconditions.checkNotNull(fullName);
        final String fileName = new File(fullName).getName();
        final int dotIndex = fileName.lastIndexOf(46);
        return (dotIndex == -1) ? "" : fileName.substring(dotIndex + 1);
    }
    
    public static String getNameWithoutExtension(final String file) {
        Preconditions.checkNotNull(file);
        final String fileName = new File(file).getName();
        final int dotIndex = fileName.lastIndexOf(46);
        return (dotIndex == -1) ? fileName : fileName.substring(0, dotIndex);
    }
    
    @Deprecated
    static TreeTraverser<File> fileTreeTraverser() {
        return Files.FILE_TREE_TRAVERSER;
    }
    
    public static Traverser<File> fileTraverser() {
        return Traverser.forTree(Files.FILE_TREE);
    }
    
    private static Iterable<File> fileTreeChildren(final File file) {
        if (file.isDirectory()) {
            final File[] files = file.listFiles();
            if (files != null) {
                return (Iterable<File>)Collections.unmodifiableList((List<?>)Arrays.asList((T[])files));
            }
        }
        return (Iterable<File>)Collections.emptyList();
    }
    
    public static Predicate<File> isDirectory() {
        return FilePredicate.IS_DIRECTORY;
    }
    
    public static Predicate<File> isFile() {
        return FilePredicate.IS_FILE;
    }
    
    static {
        FILE_TREE_TRAVERSER = new TreeTraverser<File>() {
            @Override
            public Iterable<File> children(final File file) {
                return fileTreeChildren(file);
            }
            
            @Override
            public String toString() {
                return "Files.fileTreeTraverser()";
            }
        };
        FILE_TREE = new SuccessorsFunction<File>() {
            @Override
            public Iterable<File> successors(final File file) {
                return fileTreeChildren(file);
            }
        };
    }
    
    private static final class FileByteSource extends ByteSource
    {
        private final File file;
        
        private FileByteSource(final File file) {
            this.file = Preconditions.checkNotNull(file);
        }
        
        @Override
        public FileInputStream openStream() throws IOException {
            return new FileInputStream(this.file);
        }
        
        @Override
        public Optional<Long> sizeIfKnown() {
            if (this.file.isFile()) {
                return Optional.of(this.file.length());
            }
            return Optional.absent();
        }
        
        @Override
        public long size() throws IOException {
            if (!this.file.isFile()) {
                throw new FileNotFoundException(this.file.toString());
            }
            return this.file.length();
        }
        
        @Override
        public byte[] read() throws IOException {
            final Closer closer = Closer.create();
            try {
                final FileInputStream in = closer.register(this.openStream());
                return ByteStreams.toByteArray(in, in.getChannel().size());
            }
            catch (Throwable e) {
                throw closer.rethrow(e);
            }
            finally {
                closer.close();
            }
        }
        
        @Override
        public String toString() {
            return "Files.asByteSource(" + this.file + ")";
        }
    }
    
    private static final class FileByteSink extends ByteSink
    {
        private final File file;
        private final ImmutableSet<FileWriteMode> modes;
        
        private FileByteSink(final File file, final FileWriteMode... modes) {
            this.file = Preconditions.checkNotNull(file);
            this.modes = ImmutableSet.copyOf(modes);
        }
        
        @Override
        public FileOutputStream openStream() throws IOException {
            return new FileOutputStream(this.file, this.modes.contains(FileWriteMode.APPEND));
        }
        
        @Override
        public String toString() {
            return "Files.asByteSink(" + this.file + ", " + this.modes + ")";
        }
    }
    
    private enum FilePredicate implements Predicate<File>
    {
        IS_DIRECTORY {
            @Override
            public boolean apply(final File file) {
                return file.isDirectory();
            }
            
            @Override
            public String toString() {
                return "Files.isDirectory()";
            }
        }, 
        IS_FILE {
            @Override
            public boolean apply(final File file) {
                return file.isFile();
            }
            
            @Override
            public String toString() {
                return "Files.isFile()";
            }
        };
    }
}
