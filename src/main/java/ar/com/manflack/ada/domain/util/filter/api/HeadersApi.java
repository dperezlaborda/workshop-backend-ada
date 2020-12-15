package ar.com.manflack.ada.domain.util.filter.api;

import javax.servlet.http.HttpServletRequest;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.web.context.WebApplicationContext;

@Configuration
public class HeadersApi
{

	private String requestId = "";
	private String channel = "";
	private String origin = "";
	private String terminal = "";

	public static final String RESOURCE_SCOPE_REQUEST = "getHeadersScopeRequest";

	public String getRequestId()
	{
		return requestId;
	}

	public void setRequestId(String requestId)
	{
		this.requestId = requestId;
	}

	public String getChannel()
	{
		return channel;
	}

	public void setChannel(String channel)
	{
		this.channel = channel;
	}

	public String getOrigin()
	{
		return origin;
	}

	public void setOrigin(String origin)
	{
		this.origin = origin;
	}

	public String getTerminal()
	{
		return terminal;
	}

	public void setTerminal(String terminal)
	{
		this.terminal = terminal;
	}

	public HeadersApi()
	{
	}

	public void parse(HttpServletRequest httpServletRequest)
	{

		this.requestId = httpServletRequest.getHeader("request-id");
		this.channel = httpServletRequest.getHeader("channel");
		this.origin = httpServletRequest.getHeader("origin");
		this.terminal = httpServletRequest.getHeader("terminal");

	}

	/**
	 * Creates a new instance per request of HeadersApi Usage:
	 * 
	 * 
	 * @return HeadersApi
	 */
	@Bean
	@Scope(value = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
	public static HeadersApi getHeadersScopeRequest()
	{
		return new HeadersApi();
	}
}