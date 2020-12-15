package ar.com.manflack.ada.domain.util.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ar.com.manflack.ada.domain.util.filter.api.HeadersApi;

@Component
public class HeadersFilter implements Filter
{
	@Autowired
	HeadersApi headersApi;

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException
	{
		HttpServletRequest httpServletRequest = (HttpServletRequest) request;

		if (!httpServletRequest.getRequestURI().contains("swagger")
				&& !httpServletRequest.getRequestURI().contains("api-docs")
				&& !httpServletRequest.getRequestURI().contains("actuator"))
		{
			this.headersApi.parse(httpServletRequest);
		}

		chain.doFilter(httpServletRequest, response);
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException
	{
	}

	@Override
	public void destroy()
	{
	}
}
