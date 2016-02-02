package li.winston.cateserver.transport.net;

/**
 * Created by winston on 20/01/2016.
 */
public class UncheckedUnauthorizedException extends RuntimeException {

    private final UnauthorizedException cause;

    public UncheckedUnauthorizedException(UnauthorizedException cause) {
        this.cause = cause;
    }

    @Override
    public UnauthorizedException getCause() {
        return cause;
    }

}
