package ar.com.manflack.ada.domain.util.filter;

import java.io.IOException;

import javax.annotation.Resource;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Component;

import ar.com.manflack.ada.domain.util.filter.api.HeadersApi;

@Component
public class HeadersFilterScopeRequest implements Filter
{

	@Resource(name = HeadersApi.RESOURCE_SCOPE_REQUEST)
	HeadersApi headersApiScopeRequest;

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException
	{
		HttpServletRequest httpServletRequest = (HttpServletRequest) request;

		if (!httpServletRequest.getRequestURI().contains("swagger")
				&& !httpServletRequest.getRequestURI().contains("api-docs")
				&& !httpServletRequest.getRequestURI().contains("actuator"))
		{
			this.headersApiScopeRequest.parse(httpServletRequest);
		}

		chain.doFilter(httpServletRequest, response);
	}

}
