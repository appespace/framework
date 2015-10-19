package com.appe.server.internal;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.appe.util.Objects;
/**
 * Map a given servlet to a JSP page, by default the page will be located under WEB-INF
 * 
 * @author ho
 *
 */
public class JspViewRender extends HttpServlet {
	private static final long serialVersionUID = 1336457408255780274L;
	
	//keep a map of request URI to views if any specified
	private static final Map<String, String> mapper = new HashMap<>();
	
	@Override
	public void init() throws ServletException {
		super.init();
		
		//requestURI:jsp,requestURI:jsp
		String views = getInitParameter("mapper");
		if(views != null) {
			for(String view: Objects.toArray(views, ",", true)) {
				String[] pair = Objects.toArray(view, ":", true);
				if(pair.length == 2) {
					mapper.put(pair[0], pair[1]);
				}
			}
		}
	}

	/**
	 * 
	 */
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		req.getRequestDispatcher(resolveView(req)).forward(req, resp);
	}
	
	/**
	 * return jsp page to render
	 * 
	 * @param req
	 * @return
	 * @throws ServletException
	 */
	protected String resolveView(HttpServletRequest req) throws ServletException {
		String path = req.getPathInfo();
		String requestURI = Objects.isEmpty(path)? req.getServletPath() : (req.getServletPath() + path);
		String tview = mapper.get(requestURI);
		
		//ASSUMING JSP VIEW IF NOT FOUND
		if(tview == null) {
			tview = "/WEB-INF" + requestURI + ".jsp";
		}
		return tview;
	}
}
