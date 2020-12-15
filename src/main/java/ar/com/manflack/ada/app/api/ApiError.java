package ar.com.manflack.ada.app.api;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

import io.swagger.v3.oas.annotations.media.Schema;

@JsonRootName(value = "ApiError")
@Schema(description = "ApiError data information.")
public class ApiError
{
	@JsonProperty
	private HttpStatus status;

	@JsonProperty
	private LocalDateTime timestamp;

	@JsonProperty
	private String code;

	@JsonProperty
	private String message;

	@JsonProperty
	private String debugMessage;

	private ApiError()
	{
		timestamp = LocalDateTime.now();
	}

	public ApiError(HttpStatus status)
	{
		this();
		this.status = status;
	}

	public ApiError(HttpStatus status, Throwable ex)
	{
		this();
		this.status = status;
		this.message = "Unexpected error";
		this.debugMessage = ex.getLocalizedMessage();
	}

	public ApiError(HttpStatus status, String code, String message)
	{
		this();
		this.status = status;
		this.code = code;
		this.message = message;
	}

	public ApiError(HttpStatus status, String code, String message, Throwable ex)
	{
		this();
		this.status = status;
		this.code = code;
		this.message = message;
		this.debugMessage = ex.getMessage();
	}

	public HttpStatus getStatus()
	{
		return status;
	}

	public void setStatus(HttpStatus status)
	{
		this.status = status;
	}

	public LocalDateTime getTimestamp()
	{
		return timestamp;
	}

	public void setTimestamp(LocalDateTime timestamp)
	{
		this.timestamp = timestamp;
	}

	public String getMessage()
	{
		return message;
	}

	public void setMessage(String message)
	{
		this.message = message;
	}

	public String getDebugMessage()
	{
		return debugMessage;
	}

	public void setDebugMessage(String debugMessage)
	{
		this.debugMessage = debugMessage;
	}

	public String getCode()
	{
		return code;
	}

	public void setCode(String code)
	{
		this.code = code;
	}
}