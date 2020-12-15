package ar.com.manflack.ada.domain.util.log;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_EMPTY)
public class LogEntry
{
	private static final String FILED_SEPARATOR = "|";

	private String className;
	private String method;
	private List<String> parameters;
	private String errorCode;
	private long duration;

	public LogEntry()
	{
		super();
		parameters = new ArrayList<>();
	}

	public String getClassName()
	{
		return className;
	}

	public void setClassName(String className)
	{
		this.className = className;
	}

	public String getMethod()
	{
		return method;
	}

	public void setMethod(String method)
	{
		this.method = method;
	}

	public List<String> getParameters()
	{
		return parameters;
	}

	public void setParameters(List<String> parameters)
	{
		this.parameters = parameters;
	}

	public String getErrorCode()
	{
		return errorCode;
	}

	public void setErrorCode(String errorCode)
	{
		this.errorCode = errorCode;
	}

	public long getDuration()
	{
		return duration;
	}

	public void setDuration(long duration)
	{
		this.duration = duration;
	}

	@Override
	public String toString()
	{
		String separator;
		if (System.getProperties().containsKey("FIELD_SEPARATOR"))
			separator = System.getProperty("FIELD_SEPARATOR");
		else
			separator = FILED_SEPARATOR;

		StringBuilder sb = new StringBuilder("LogEntry");

		sb.append(separator);
		sb.append("---------------------------");
		sb.append(separator);

		sb.append("ClassName: ");
		sb.append(className);
		sb.append(separator);

		sb.append("Method: ");
		sb.append(method);
		sb.append(separator);

		sb.append("Parameters: ");
		for (String string : parameters)
		{
			sb.append(string);
			sb.append(",");
		}
		sb.append(separator);

		sb.append("ErrorCode: ");
		sb.append(errorCode);
		sb.append(separator);

		sb.append("Duration: ");
		sb.append(duration);
		sb.append("ms");
		sb.append(separator);

		sb.append("---------------------------");

		return sb.toString();
	}

}
