package net.jamsimulator.jams.gui.mips.simulator.memory;

import net.jamsimulator.jams.mips.interrupt.RuntimeAddressException;
import net.jamsimulator.jams.mips.memory.Memory;
import net.jamsimulator.jams.mips.memory.cache.CacheBlock;
import net.jamsimulator.jams.utils.NumericUtils;
import net.jamsimulator.jams.utils.StringUtils;

import java.util.function.BiFunction;

public enum MemoryRepresentation {

    HEXADECIMAL(false, (o1, o2) -> "0x" + StringUtils.addZeros(Integer.toHexString(o1), 8)),
    DECIMAL(false, (o1, o2) -> String.valueOf(o1)),
    OCTAL(false, (o1, o2) -> "0" + Integer.toOctalString(o1)),
    BINARY(false, (o1, o2) -> "0b" + StringUtils.addZeros(Integer.toBinaryString(o1), 32)),
    LONG(true, (o1, o2) -> String.valueOf(NumericUtils.intsToLong(o1, o2))),
    FLOAT(false, (o1, o2) -> String.valueOf(Float.intBitsToFloat(o1))),
    DOUBLE(true, (o1, o2) -> String.valueOf(NumericUtils.intsToDouble(o1, o2))),
    CHAR(false, (o1, o2) -> {
        char[] array = new char[4];
        for (int i = 0; i < 4; i++) {
            array[i] = (char) ((o1 >> i * 8) & 0xFF);
        }
        return new String(array);
    }),
    RGB(false, (o1, o2) -> getRGBAsString(o1)),
    RGBA(false, (o1, o2) -> getRGBAAsString(o1)),
    ENGLISH(false, (o1, o2) -> NumericUtils.toEnglish(o1)),
    ROMAN(false, (o1, o2) -> NumericUtils.toRoman(o1));

    private final BiFunction<Integer, Integer, String> transformer;
    private final boolean requiresNextWord;

    MemoryRepresentation(boolean requiresNextWord, BiFunction<Integer, Integer, String> transformer) {
        this.requiresNextWord = requiresNextWord;
        this.transformer = transformer;
    }

    public String getLanguageNode() {
        return "NUMBER_FORMAT_" + name();
    }

    public boolean isColor() {
        return this == RGB || this == RGBA;
    }

    public boolean isRequiresNextWord() {
        return requiresNextWord;
    }

    public String represent(Memory memory, int address) {
        try {
            int o1 = memory.getWord(address, false, true);
            int o2 = 0;
            if (requiresNextWord) {
                o2 = memory.getWord(address + 4, false, true);
            }

            return transformer.apply(o1, o2);
        } catch (RuntimeAddressException ex) {
            System.err.println("Bad address " + StringUtils.addZeros(Integer.toHexString(ex.getBadAddress()), 8));
            return "0'";
        }
    }

    public String represent(int firstWord, int secondWord) {
        return transformer.apply(firstWord, secondWord);
    }

    public String represent(CacheBlock block, int address) {
        int o1 = block.getWord(address, false);
        int o2 = 0;

        if (requiresNextWord && block.getData().length > address + 4) {
            o2 = block.getWord(address + 4, false);
        }

        return transformer.apply(o1, o2);
    }

    private static String getRGBAsString(int value) {
        String val = StringUtils.addZeros(Integer.toHexString(value), 6);
        if (val.length() > 6) val = val.substring(val.length() - 6);
        return "#" + val;
    }

    private static String getRGBAAsString(int value) {
        return "#" + StringUtils.addZeros(Integer.toHexString(value), 8);
    }

}
