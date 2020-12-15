package ar.com.manflack.ada.app.handler;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import ar.com.manflack.ada.app.api.ApiError;

@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler
{
	@Override
	protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request)
	{
		String error = "Malformed JSON request";
		return buildResponseEntity(new ApiError(HttpStatus.BAD_REQUEST, "", error, ex));
	}

	/*@ExceptionHandler(UtilityException.class)
	protected ResponseEntity<Object> handleExampleError(UtilityException ex)
	{
		return buildResponseEntity(new ApiError(HttpStatus.NOT_FOUND, ex.getErrorCode(), ex.getMessage(), ex));
	}*/

	@ExceptionHandler(Exception.class)
	protected ResponseEntity<Object> handleInternalError(Exception ex)
	{
		logger.error("Unknown exception: ", ex);
		return buildResponseEntity(new ApiError(HttpStatus.INTERNAL_SERVER_ERROR, "", ex.getMessage(), ex));
	}

	private ResponseEntity<Object> buildResponseEntity(ApiError apiError)
	{
		return new ResponseEntity<>(apiError, apiError.getStatus());
	}
}