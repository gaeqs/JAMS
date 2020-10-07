package net.jamsimulator.jams.mips.memory.cache.builder;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import net.jamsimulator.jams.mips.memory.Memory;
import net.jamsimulator.jams.mips.memory.cache.Cache;
import net.jamsimulator.jams.mips.memory.cache.CacheBuilder;
import net.jamsimulator.jams.mips.memory.cache.CacheReplacementPolicy;
import net.jamsimulator.jams.mips.memory.cache.writeback.WriteBackSetAssociativeCache;
import net.jamsimulator.jams.mips.memory.cache.writethrough.WriteThroughSetAssociativeCache;
import net.jamsimulator.jams.utils.NumericUtils;

import java.util.LinkedList;

public class SetAssociativeCacheBuilder extends CacheBuilder<Cache> {

	public static final String NAME = "SET_ASSOCIATIVE";

	private final SimpleBooleanProperty writeBack;
	private final SimpleIntegerProperty blockSize, blocksAmount, setSize;
	private final SimpleObjectProperty<CacheReplacementPolicy> replacementPolicy;

	public SetAssociativeCacheBuilder() {
		super(NAME, new LinkedList<>());
		properties.add(writeBack = new SimpleBooleanProperty(null, "WRITE_BACK", false));
		properties.add(blockSize = new SimpleIntegerProperty(null, "BLOCK_SIZE", 4));
		properties.add(blocksAmount = new SimpleIntegerProperty(null, "BLOCKS_AMOUNT", 4));
		properties.add(setSize = new SimpleIntegerProperty(null, "SET_SIZE", 2));
		properties.add(replacementPolicy = new SimpleObjectProperty<>(null, "REPLACEMENT_POLICY", CacheReplacementPolicy.LRU));
	}

	@Override
	public int getSizeInBytes() {
		return  blocksAmount.get() * (blockSize.get() << 2);
	}

	@Override
	public Cache build(Memory parent) {
		int logSetSize = NumericUtils.log2(setSize.get());

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

		if (logAmount < logSetSize) {
			logSetSize = logAmount;
		}

		return writeBack.get()
				? new WriteBackSetAssociativeCache(parent, 1 << logSize, 1 << logAmount, 1 << logSetSize, replacementPolicy.get())
				: new WriteThroughSetAssociativeCache(parent, 1 << logSize, 1 << logAmount, 1 << logSetSize, replacementPolicy.get());
	}

	@Override
	public CacheBuilder<Cache> makeNewInstance() {
		return new SetAssociativeCacheBuilder();
	}

	@Override
	public CacheBuilder<Cache> copy() {
		var builder = new SetAssociativeCacheBuilder();
		builder.writeBack.setValue(writeBack.getValue());
		builder.blockSize.setValue(blockSize.getValue());
		builder.blocksAmount.setValue(blocksAmount.getValue());
		builder.setSize.setValue(setSize.getValue());
		builder.replacementPolicy.setValue(replacementPolicy.getValue());
		return builder;
	}
}
