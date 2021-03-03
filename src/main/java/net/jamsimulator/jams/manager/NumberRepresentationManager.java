package net.jamsimulator.jams.manager;

import net.jamsimulator.jams.mips.memory.cache.CacheBuilder;
import net.jamsimulator.jams.utils.NumberRepresentation;
import net.jamsimulator.jams.utils.NumericUtils;
import net.jamsimulator.jams.utils.StringUtils;
import net.jamsimulator.jams.utils.representation.event.NumberRepresentationRegisterEvent;
import net.jamsimulator.jams.utils.representation.event.NumberRepresentationUnregisterEvent;

/**
 * This singleton stores all {@link NumberRepresentation}s that JAMs may use.
 * <p>
 * To register an {@link NumberRepresentation} use {@link #add(NumberRepresentation)}.
 * To unregister an {@link NumberRepresentation} use {@link #remove(Object)}.
 * A {@link NumberRepresentation}'s removal from the manager doesn't make projects
 * to stop using it if they're already using it.
 */
public class NumberRepresentationManager extends Manager<NumberRepresentation> {

    public static final NumberRepresentation HEXADECIMAL = new NumberRepresentation("HEXADECIMAL",
            false, false,
            (o1, o2) -> "0x" + StringUtils.addZeros(Integer.toHexString(o1), 8));
    public static final NumberRepresentation DECIMAL = new NumberRepresentation("DECIMAL",
            false, false,
            (o1, o2) -> String.valueOf(o1));
    public static final NumberRepresentation OCTAL = new NumberRepresentation("OCTAL",
            false, false,
            (o1, o2) -> "0" + Integer.toOctalString(o1));
    public static final NumberRepresentation BINARY = new NumberRepresentation("BINARY",
            false, false,
            (o1, o2) -> "0b" + StringUtils.addZeros(Integer.toBinaryString(o1), 32));
    public static final NumberRepresentation LONG = new NumberRepresentation("LONG",
            true, false,
            (o1, o2) -> String.valueOf(NumericUtils.intsToLong(o1, o2)));
    public static final NumberRepresentation FLOAT = new NumberRepresentation("FLOAT",
            false, false,
            (o1, o2) -> String.valueOf(Float.intBitsToFloat(o1)));
    public static final NumberRepresentation DOUBLE = new NumberRepresentation("DOUBLE",
            true, false,
            (o1, o2) -> String.valueOf(NumericUtils.intsToDouble(o1, o2)));
    public static final NumberRepresentation CHAR = new NumberRepresentation("CHAR",
            false, false,
            (o1, o2) -> {
                char[] array = new char[4];
                for (int i = 0; i < 4; i++) {
                    array[i] = (char) ((o1 >> i * 8) & 0xFF);
                }
                return new String(array);
            });
    public static final NumberRepresentation RGB = new NumberRepresentation("RGB",
            false, true,
            (o1, o2) -> getRGBAsString(o1));
    public static final NumberRepresentation RGBA = new NumberRepresentation("RGBA",
            false, true,
            (o1, o2) -> getRGBAAsString(o1));
    public static final NumberRepresentation ENGLISH = new NumberRepresentation("ENGLISH",
            false, false,
            (o1, o2) -> NumericUtils.toEnglish(o1));
    public static final NumberRepresentation ROMAN = new NumberRepresentation("ROMAN",
            false, false,
            (o1, o2) -> NumericUtils.toRoman(o1));

    public static final NumberRepresentationManager INSTANCE = new NumberRepresentationManager();

    private NumberRepresentationManager() {
        super(NumberRepresentationRegisterEvent.Before::new,
                NumberRepresentationRegisterEvent.After::new,
                NumberRepresentationUnregisterEvent.Before::new,
                NumberRepresentationUnregisterEvent.After::new);
    }

    @Override
    protected void loadDefaultElements() {
        add(HEXADECIMAL);
        add(DECIMAL);
        add(OCTAL);
        add(BINARY);
        add(LONG);
        add(FLOAT);
        add(DOUBLE);
        add(CHAR);
        add(RGB);
        add(RGBA);
        add(ENGLISH);
        add(ROMAN);
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
