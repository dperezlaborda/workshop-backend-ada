package ar.com.manflack.ada.domain.util.log;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_EMPTY)
@JsonPropertyOrder({ "type", "id", "address", "method", "responseCode", "responseText", "headers", "payload" })
public class ClientLogEntry
{
	private static final String FILED_SEPARATOR = "|";

	private String type = "Outbound Message";
	private String id;
	private String address;
	private String method;
	private String responseCode;
	private String responseText;
	private String headers;
	private String payload;

	public ClientLogEntry()
	{
		super();
	}

	public String getType()
	{
		return type;
	}

	public void setType(String type)
	{
		this.type = type;
	}

	public String getId()
	{
		return id;
	}

	public void setId(String id)
	{
		this.id = id;
	}

	public String getAddress()
	{
		return address;
	}

	public void setAddress(String address)
	{
		this.address = address;
	}

	public String getMethod()
	{
		return method;
	}

	public void setMethod(String method)
	{
		this.method = method;
	}

	public String getResponseCode()
	{
		return responseCode;
	}

	public void setResponseCode(String responseCode)
	{
		this.responseCode = responseCode;
	}

	public String getResponseText()
	{
		return responseText;
	}

	public void setResponseText(String responseText)
	{
		this.responseText = responseText;
	}

	public String getHeaders()
	{
		return headers;
	}

	public void setHeaders(String headers)
	{
		this.headers = headers;
	}

	public String getPayload()
	{
		return payload;
	}

	public void setPayload(String payload)
	{
		this.payload = payload;
	}

	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder(type);
		sb.append(FILED_SEPARATOR);

		sb.append("---------------------------");
		sb.append(FILED_SEPARATOR);

		sb.append("ID: ");
		sb.append(id);
		sb.append(FILED_SEPARATOR);

		sb.append("Address: ");
		sb.append(address);
		sb.append(FILED_SEPARATOR);

		sb.append("Method: ");
		sb.append(method);
		sb.append(FILED_SEPARATOR);

		sb.append("Response-Code: ");
		sb.append(responseCode);
		sb.append(FILED_SEPARATOR);

		sb.append("Response-Text: ");
		sb.append(responseText);
		sb.append(FILED_SEPARATOR);

		sb.append("Headers: ");
		sb.append(headers);
		sb.append(FILED_SEPARATOR);

		sb.append("Payload: ");
		sb.append(payload);
		sb.append(FILED_SEPARATOR);

		sb.append("---------------------------");

		return sb.toString();
	}

}
