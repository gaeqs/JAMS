package net.jamsimulator.jams.mips.memory.cache.builder;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import net.jamsimulator.jams.mips.memory.Memory;
import net.jamsimulator.jams.mips.memory.cache.Cache;
import net.jamsimulator.jams.mips.memory.cache.CacheBuilder;
import net.jamsimulator.jams.mips.memory.cache.CacheReplacementPolicy;
import net.jamsimulator.jams.mips.memory.cache.writeback.WriteBackAssociativeCache;
import net.jamsimulator.jams.mips.memory.cache.writethrough.WriteThroughAssociativeCache;
import net.jamsimulator.jams.utils.NumericUtils;

import java.util.LinkedList;

public class AssociativeCacheBuilder extends CacheBuilder<Cache> {

	public static final String NAME = "ASSOCIATIVE";

	private final SimpleBooleanProperty writeBack;
	private final SimpleIntegerProperty blockSize, blocksAmount;
	private final SimpleObjectProperty<CacheReplacementPolicy> replacementPolicy;

	public AssociativeCacheBuilder() {
		super(NAME, new LinkedList<>());
		properties.add(writeBack = new SimpleBooleanProperty(null, "WRITE_BACK", false));
		properties.add(blockSize = new SimpleIntegerProperty(null, "BLOCK_SIZE", 4));
		properties.add(blocksAmount = new SimpleIntegerProperty(null, "BLOCKS_AMOUNT", 4));
		properties.add(replacementPolicy = new SimpleObjectProperty<>(null, "REPLACEMENT_POLICY", CacheReplacementPolicy.LRU));
	}

	@Override
	public Cache build(Memory parent) {
		int logSize = NumericUtils.log2(blockSize.get());
		int logAmount = NumericUtils.log2(blocksAmount.get());

		if (logSize + logAmount > 32) {
			if (logSize > 32) {
				logSize = 32;
				logAmount = 0;
			} else {
				logAmount = 32 - logSize;
			}
		}

		return writeBack.get()
				? new WriteBackAssociativeCache(parent, 1 << logSize, 1 << logAmount, replacementPolicy.get())
				: new WriteThroughAssociativeCache(parent, 1 << logSize, 1 << logAmount, replacementPolicy.get());
	}

	@Override
	public CacheBuilder<Cache> makeNewInstance() {
		return new AssociativeCacheBuilder();
	}
}
