package net.jamsimulator.jams.project.exception;

public class MIPSTemplateBuildException extends Exception {

    public MIPSTemplateBuildException() {
    }

    public MIPSTemplateBuildException(String message) {
        super(message);
    }

    public MIPSTemplateBuildException(String message, Throwable cause) {
        super(message, cause);
    }

    public MIPSTemplateBuildException(Throwable cause) {
        super(cause);
    }

    public MIPSTemplateBuildException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
