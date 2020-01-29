package com.joseluisestevez.msa.zuul.filters;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;

@Component
public class PreElapsedTimeFilter extends ZuulFilter {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(PreElapsedTimeFilter.class);

	@Override
	public boolean shouldFilter() {
		// se ejeuta si cumple esta condicion
		return true;
	}

	@Override
	public Object run() throws ZuulException {

		RequestContext ctx = RequestContext.getCurrentContext();
		HttpServletRequest request = ctx.getRequest();
		Long startTime = System.currentTimeMillis();
		request.setAttribute("startTime", startTime);

		LOGGER.info("[{}] request routed to [{}]", request.getMethod(),
				request.getRequestURL());

		return null;
	}

	@Override
	public String filterType() {
		return "pre"; // post or error
	}

	@Override
	public int filterOrder() {
		return 1;
	}

}
