package go.builtin;

import go.builtin.channel.Channel;
import util.TupleMap;
import java.util.HashMap;

/**
 * Details the full type information of a GoObject.
 *
 * Java generics do not completely preserve type arguments, so this class aims to preserve all type
 * information that may be needed in reflective operations.
 *
 * All types have unique Go classes, even between similar types. For example, the slices []int and
 * []string (rather, Slice<Int32> and Slice<String>) may have the same Java class, but have
 * unique GoClasses.
 */
public final class GoClass {

    private static final HashMap<Class<?>, GoClass> BUILTIN_TYPES;
    private static final HashMap<Class<?>, GoClass> STRUCT_TYPES;
    private static final HashMap<GoClass, GoClass> ARRAY_TYPES;
    private static final HashMap<GoClass, GoClass> SLICE_TYPES;
    private static final HashMap<GoClass, GoClass> CHANNEL_TYPES;
    private static final HashMap<GoClass, GoClass> POINTER_TYPES;
    private static final TupleMap<GoClass, GoClass, GoClass> MAP_TYPES;
    private static final HashMap<Class<? extends GoInterface>, GoClass> IF_TYPES;

    static {
        BUILTIN_TYPES = new HashMap<>();
        STRUCT_TYPES = new HashMap<>();
        ARRAY_TYPES = new HashMap<>();
        SLICE_TYPES = new HashMap<>();
        CHANNEL_TYPES = new HashMap<>();
        POINTER_TYPES = new HashMap<>();
        MAP_TYPES = new TupleMap<>();
        IF_TYPES = new HashMap<>();
    }

    public final Class<?> javaClass;
    private final GoClass elementType;
    private final GoClass keyType;
    private final int numElements;

    private GoClass(Class<?> javaClass) {
        this.javaClass = javaClass;
        this.elementType = null;
        this.keyType = null;
        this.numElements = -1;
    }

    private GoClass(Class<?> javaClass, GoClass elementType) {
        this.javaClass = javaClass;
        this.elementType = elementType;
        this.keyType = null;
        this.numElements = -1;
    }

    private GoClass(Class<?> javaClass, GoClass elementType, int numElements) {
        this.javaClass = javaClass;
        this.elementType = elementType;
        this.keyType = null;
        this.numElements = numElements;
    }

    private GoClass(Class<?> javaClass, GoClass elementType, GoClass keyType) {
        this.javaClass = javaClass;
        this.elementType = elementType;
        this.keyType = keyType;
        this.numElements = -1;
    }

    /**
     * Gets the Go class for this class's element type.
     * Objects with element types include arrays, slices, maps, and pointers. The element type for
     * any of these is the type of data that is contained by them.
     * @return The element type of this class; null if this GoClass has no element type.
     */
    public GoClass getElementType() {
        return elementType;
    }

    /**
     * Gets the key type for this class.
     * The key type for a map is the type of data used to index the map.
     * @return The key type; null if this GoClass does not represent a map.
     */
    public GoClass getKeyType() {
        return keyType;
    }

    /**
     * Gets the number of elements used by this class, if this class represents an array.
     * @return The number of elements allocated by this array type; -1 if this GoClass does not
     * represent an array.
     */
    public int getNumElements() {
        return numElements;
    }

    /**
     * Generates a default value for this GoClass. Any arbitrary user-defined type must have a
     * no-args constructor that creates a default value.
     * @return The default value for this GoClass
     * @throws UnsupportedOperationException If the type represented by this GoClass does not have
     * a no-args constructor.
     */
    public GoObject newDefaultValue() throws UnsupportedOperationException {
        if (javaClass == Array.class) {
            return new Array<>(elementType, numElements);
        }
        else if (javaClass == Slice.class) {
            return new Slice<>(elementType);
        }
        else if (javaClass == Pointer.class) {
            return new Pointer<>(elementType);
        }
        else if (javaClass == Map.class) {
            return new Map<>(elementType, keyType);
        }
        else try {
            return (GoObject) javaClass.getDeclaredConstructor().newInstance();
        } catch (ReflectiveOperationException e) {
            throw new UnsupportedOperationException(e);
        }
    }

    static GoClass forBuiltin(Class<?> primitiveClass) {
        // If the basic type does not have a GoClass rep, create one
        if (!BUILTIN_TYPES.containsKey(primitiveClass)) {
            GoClass type = new GoClass(primitiveClass);
            BUILTIN_TYPES.put(primitiveClass, type);
        }
        return BUILTIN_TYPES.get(primitiveClass);
    }

    public static GoClass forStruct(Class<?> structClass) {
        // If the basic type does not have a GoClass rep, create one
        if (!STRUCT_TYPES.containsKey(structClass)) {
            GoClass type = new GoClass(structClass);
            STRUCT_TYPES.put(structClass, type);
        }
        return STRUCT_TYPES.get(structClass);
    }

    public static GoClass forArray(GoClass elementType, int numElements) {
        // If the type does not have an array type, create one
        if (!ARRAY_TYPES.containsKey(elementType)) {
            GoClass type = new GoClass(Array.class, elementType, numElements);
            ARRAY_TYPES.put(elementType, type);
        }
        return ARRAY_TYPES.get(elementType);
    }

    public static GoClass forSlice(GoClass elementType) {
        // If the type does not have a slice type, create one
        if (!SLICE_TYPES.containsKey(elementType)) {
            GoClass type = new GoClass(Slice.class, elementType);
            SLICE_TYPES.put(elementType, type);
        }
        return SLICE_TYPES.get(elementType);
    }

    public static GoClass forChannel(GoClass elementType) {
        // If the type does not have a channel type, create one
        if (!CHANNEL_TYPES.containsKey(elementType)) {
            GoClass type = new GoClass(Channel.class, elementType);
            CHANNEL_TYPES.put(elementType, type);
        }
        return CHANNEL_TYPES.get(elementType);
    }

    public static GoClass forPointer(GoClass referentType) {
        // If the type does not have a pointer type, create one
        if (!POINTER_TYPES.containsKey(referentType)) {
            GoClass type = new GoClass(Pointer.class, referentType);
            POINTER_TYPES.put(referentType, type);
        }
        return POINTER_TYPES.get(referentType);
    }

    public static GoClass forMap(GoClass keyType, GoClass valueType) {
        // If the types do not have a map type, create one
        if (!MAP_TYPES.containsKeys(keyType, valueType)) {
            GoClass type = new GoClass(Map.class, keyType, valueType);
            MAP_TYPES.put(keyType, valueType, type);
        }
        return MAP_TYPES.get(keyType, valueType);
    }

    public static GoClass forInterface(Class<? extends GoInterface> itype) {
        if (!IF_TYPES.containsKey(itype)) {
            GoClass type = new GoClass(GoInterface.class);
            IF_TYPES.put(itype, type);
        }
        return IF_TYPES.get(itype);
    }
}
