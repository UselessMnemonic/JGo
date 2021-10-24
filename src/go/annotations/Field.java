package go.annotations;

/**
 * An annotation for struct types that contains information about the
 * field to which it is attached.
 * Field annotations are generated from struct types for reflective operations.
 * These are used to determine assignability between declared struct types.
 */
public @interface Field {
    String annotation() default "";
}
