package cn.com.vortexa.irys_onchain_bot.exception;


/**
 * @author helei
 * @since 2025-09-19
 */
public class OnChainException extends Exception {
    public OnChainException() {
        super("On chain error.");
    }

    public OnChainException(String message) {
        super(message);
    }

    public OnChainException(String message, Throwable cause) {
        super(message, cause);
    }

    public OnChainException(Throwable cause) {
        super(cause);
    }
}
