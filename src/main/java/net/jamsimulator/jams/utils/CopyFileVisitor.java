package net.jamsimulator.jams.utils;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

import static java.nio.file.FileVisitResult.CONTINUE;
import static java.nio.file.FileVisitResult.SKIP_SUBTREE;


public class CopyFileVisitor extends SimpleFileVisitor<Path> {

	final Path source;
	final Path target;

	public CopyFileVisitor(Path source, Path target) {
		this.source = source;
		this.target = target;
	}


	@Override
	public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
		Path newDirectory = target.resolve(source.relativize(dir));
		try {
			Files.copy(dir, newDirectory);
		} catch (FileAlreadyExistsException ioException) {
			return CONTINUE;
		}

		return FileVisitResult.CONTINUE;
	}

	@Override
	public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
		Path newFile = target.resolve(source.relativize(file));
		Files.copy(file, newFile);
		return FileVisitResult.CONTINUE;

	}

	@Override
	public FileVisitResult postVisitDirectory(Path dir, IOException exc) {
		return FileVisitResult.CONTINUE;
	}

	@Override
	public FileVisitResult visitFileFailed(Path file, IOException exc) {
		exc.printStackTrace();
		return CONTINUE;
	}
}
