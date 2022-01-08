package cz.kfkl.mstruct.gui.utils.validation;

/**
 * Error which is caused by wrong input (user or file).
 */
public class PopupErrorException extends UserMessageException {
	private static final long serialVersionUID = 7562772374784743170L;

	public PopupErrorException(Throwable e, String message, Object... messageArguments) {
		super(e, message, messageArguments);
	}

	public PopupErrorException(Throwable e, String message) {
		super(e, message);
	}

	public PopupErrorException(Throwable e) {
		super(e);
	}

	public PopupErrorException(String message, Object... messageArguments) {
		super(message, messageArguments);
	}

	public PopupErrorException(String message) {
		super(message);
	}

	@Override
	protected String getDefaultHeaderText() {
		return "Input Exception";
	}
}
