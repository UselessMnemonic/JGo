package go.builtin;

public interface Error extends GoInterface {

    String Error();

    class PlainError implements Error {
        private final String errs;
        public PlainError(String errs) {
            this.errs = errs;
        }
        @Override
        public String Error() {
            return errs;
        }
    }
}
