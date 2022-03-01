/*
 *  MIT License
 *
 *  Copyright (c) 2021 Gael Rial Costas
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */

package net.jamsimulator.jams.utils;

import java.io.*;
import java.nio.file.*;
import java.util.Optional;
import java.util.function.BiConsumer;

public class FileUtils {

    public static boolean isChild(File child, File parent) {
        return isChild(child.toPath().toAbsolutePath(), parent.toPath().toAbsolutePath());
    }

    public static boolean isChild(Path child, Path parent) {
        return child.startsWith(parent);
    }

    public static boolean deleteDirectory(File directory) {
        Validate.notNull(directory, "Directory cannot be null!");
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (!Files.isSymbolicLink(file.toPath())) {
                    if (!deleteDirectory(file)) return false;
                }
            }
        }
        return directory.delete();
    }

    public static String readAll(File file) throws IOException {
        Validate.notNull(file, "File cannot be null!");
        Validate.isTrue(file.exists(), "File must exist!");
        Validate.isTrue(file.isFile(), "File must be a file!");

        StringBuilder builder = new StringBuilder();
        Reader reader = new FileReader(file);

        int c;
        while ((c = reader.read()) != -1) {
            builder.append((char) c);
        }

        reader.close();

        return builder.toString();
    }

    public static Optional<String> readAllSafe(File file) {
        if (file == null || !file.exists() || !file.isFile()) return Optional.empty();
        try {
            return Optional.of(readAll(file));
        } catch (IOException e) {
            return Optional.empty();
        }
    }

    public static String readAll(InputStream stream) throws IOException {
        Validate.notNull(stream, "Stream cannot be null!");

        StringBuilder builder = new StringBuilder();
        Reader reader = new InputStreamReader(stream);

        int c;
        while ((c = reader.read()) != -1) {
            builder.append((char) c);
        }

        reader.close();

        return builder.toString();
    }

    public static String readAll(Reader r) throws IOException {
        BufferedReader reader = new BufferedReader(r);
        //Loads the string first. This allows us to check if the file is empty.
        StringBuilder builder = new StringBuilder();
        boolean first = true;
        String line;
        while ((line = reader.readLine()) != null) {
            if (!first) {
                builder.append('\n');
            } else first = false;
            builder.append(line);
        }
        return builder.toString();
    }

    public static void writeAll(File file, String text) throws IOException {
        Validate.notNull(file, "File cannot be null!");
        Validate.isTrue(!file.exists() || file.isFile(), "File must not exist or be a file!");
        Writer writer = new FileWriter(file);
        writer.write(text);
        writer.close();
    }

    public static boolean copyFileToFolder(File folder, File target) {
        try {
            Path from = target.toPath();
            Path to = new File(folder, target.getName()).toPath();
            if (target.isDirectory()) {
                Files.walkFileTree(from, new CopyFileVisitor(from, to, false, null));
            } else {
                Files.copy(from, to, StandardCopyOption.COPY_ATTRIBUTES, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static boolean copyFile(File from, File to) {
        try {
            if (from.isDirectory()) {
                Files.walkFileTree(from.toPath(), new CopyFileVisitor(from.toPath(), to.toPath(), false, null));
            } else {
                Files.copy(from.toPath(), to.toPath(), StandardCopyOption.COPY_ATTRIBUTES, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }


    public static boolean moveFileToFolder(File folder, File target, BiConsumer<File, File> moveAction) {
        try {
            File toFile = new File(folder, target.getName());
            Path from = target.toPath();
            Path to = toFile.toPath();
            if (target.isDirectory()) {
                Files.walkFileTree(from, new CopyFileVisitor(from, to, true, moveAction));
            } else {
                moveAction.accept(target, toFile);
                Files.move(from, to, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }


    public static boolean isValidPath(String path) {
        try {
            Paths.get(path);
        } catch (InvalidPathException | NullPointerException ex) {
            return false;
        }
        return true;
    }
}

