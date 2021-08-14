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

package net.jamsimulator.jams.mips.memory.cache;

import java.util.Random;
import java.util.function.Function;

public enum CacheReplacementPolicy {

    RANDOM,
    FIFO,
    LRU,
    MRU;

    private static final Random RANDOM_INSTANCE = new Random();

    public int getBlockToReplaceIndex(CacheBlock[] blocks) {
        Function<CacheBlock, Long> comparingFunction;
        switch (this) {
            case RANDOM:
                return RANDOM_INSTANCE.nextInt(blocks.length);
            case FIFO:
                comparingFunction = CacheBlock::getCreationTime;
                break;
            case MRU:
                comparingFunction = cacheBlock -> -cacheBlock.getModificationTime();
                break;
            default:
            case LRU:
                comparingFunction = CacheBlock::getModificationTime;
                break;
        }

        long min = Long.MAX_VALUE;
        int minIndex = 0;

        CacheBlock b;
        long v;
        for (int i = 0; i < blocks.length; i++) {
            b = blocks[i];
            if (b == null) return i;
            v = comparingFunction.apply(b);
            if (v < min) {
                min = v;
                minIndex = i;
            }
        }

        return minIndex;
    }

}
