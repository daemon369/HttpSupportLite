package lite.httpsupport.codec;

public interface ICodec {
    byte[] encode(final Object data) throws CodecException;

    <RESP> RESP decode(final byte[] data, final Class<?> clz)
            throws CodecException;

    enum Type {
        ENCODE, DECODE
    }

    public class CodecException extends Exception {
        private static final long serialVersionUID = 3388036999482910487L;

        private Type type;

        public CodecException() {
            super();
        }

        public CodecException(String detailMessage, Throwable throwable) {
            super(detailMessage, throwable);
        }

        public CodecException(String detailMessage) {
            super(detailMessage);
        }

        public CodecException(Throwable throwable) {
            super(throwable);
        }

        public Type getType() {
            return type;
        }

        public CodecException setType(Type type) {
            this.type = type;
            return this;
        }
    }
}
