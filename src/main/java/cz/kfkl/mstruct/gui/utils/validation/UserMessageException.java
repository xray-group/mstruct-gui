package cz.kfkl.mstruct.gui.utils.validation;

public class UserMessageException extends RuntimeException {
	public static final String DEFAULT_UNEXPECTED_EXCEPTION_TITLE = "Unexpected Exception";

	private String headerText;

	private static final long serialVersionUID = -4580951179436282862L;

	public UserMessageException(Throwable e, String message, Object... messageArguments) {
		super(String.format(message, messageArguments), e);
	}

	public UserMessageException(Throwable e, String message) {
		super(message, e);
	}

	public UserMessageException(Throwable e) {
		super(e);
	}

	public UserMessageException(String message, Object... messageArguments) {
		super(String.format(message, messageArguments));
	}

	public UserMessageException(String message) {
		super(message);
	}

	public String getHeaderText() {
		return headerText == null ? getDefaultHeaderText() : headerText;
	}

	public void setHeaderText(String headerText) {
		this.headerText = headerText;
	}

	protected String getDefaultHeaderText() {
		return DEFAULT_UNEXPECTED_EXCEPTION_TITLE;
	}

}
