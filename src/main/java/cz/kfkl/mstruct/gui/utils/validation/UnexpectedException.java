package cz.kfkl.mstruct.gui.utils.validation;

/**
 * Error which is probably a coding exception.
 */
public class UnexpectedException extends UserMessageException {

	private static final long serialVersionUID = 4945388222061527422L;

	public UnexpectedException(Throwable e, String message, Object... messageArguments) {
		super(e, message, messageArguments);
	}

	public UnexpectedException(Throwable e, String message) {
		super(e, message);
	}

	public UnexpectedException(Throwable e) {
		super(e);
	}

	public UnexpectedException(String message, Object... messageArguments) {
		super(message, messageArguments);
	}

	public UnexpectedException(String message) {
		super(message);
	}

	@Override
	protected String getDefaultHeaderText() {
		return "Application Exception";
	}
}
