import go.builtin.Bool;
import go.builtin.GoClass;
import go.builtin.GoObject;
import go.builtin.MutableString;
import go.builtin.annotations.Field;

import java.util.Objects;

public class TestStruct extends GoObject {

    @Field
    public MutableString Field1;

    @Field
    public Bool Field2;

    public TestStruct() {
        super(GoClass.forStruct(TestStruct.class));
        Field1 = new MutableString("");
        Field2 = new Bool(false);
    }

    private TestStruct(TestStruct other) {
        super(GoClass.forStruct(TestStruct.class));
        Field1 = other.Field1.clone();
        Field2 = other.Field2.clone();
    }

    @Override
    public void assign(GoObject other) {
        if (other instanceof TestStruct) {
            assign((TestStruct) other);
        }
        throw new IllegalArgumentException();
    }

    public void assign(TestStruct other) {
        this.Field1.assign(other.Field1);
        this.Field2.assign(other.Field2);
    }

    @Override
    public GoObject clone() {
        return new TestStruct(this);
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof TestStruct) {
            return equals((TestStruct) other);
        }
        throw new IllegalArgumentException();
    }

    public boolean equals(TestStruct other) {
        return this.Field1.equals(other.Field1)
                && this.Field2.equals(other.Field2);
    }

    @Override
    public int hashCode() {
        return Objects.hash(Field1, Field2);
    }

    @Override
    public String toString() {
        return "";
    }
}
