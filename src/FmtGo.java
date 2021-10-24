import go.builtin.Error;
import go.builtin.GoObject;
import go.builtin.MutableString;

public class FmtGo {

    public static Error Errorf(MutableString string, GoObject... a) {
        String fmt = String.format(string.toString(), (Object[]) a);
        return new Error.PlainError(fmt);
    }

    public interface Stringer {
        String String();
    }
}
