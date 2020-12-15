package ar.com.manflack.ada.domain.exception;

public abstract class GenericException extends Exception
{
	private static final long serialVersionUID = -5448077999646695145L;

	private String errorCode;

	public GenericException(String errorCode, String message)
	{
		super(message);
		this.errorCode = errorCode;
	}

	public GenericException(String errorCode, String message, Throwable ex)
	{
		super(message, ex);
		this.errorCode = errorCode;
	}

	public String getErrorCode()
	{
		return errorCode;
	}
}
