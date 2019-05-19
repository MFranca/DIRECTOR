package servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mysqlDB.AbstractEntity;
import services.resources.ServiceResource;

/**
 * Servlet implementation class PlatformController
 */
@WebServlet("/listFeatures")
public class TagController extends DirectorAbstractController {
	private static final long serialVersionUID = 1L;
		
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String htmlResponse = "<html><body>" + HTML_HEADER;
		
		htmlResponse += String.format("<p>Served at: <strong>%s</strong></p>", request.getRequestURI());
		htmlResponse += HTML_FOOTER;
		
		try {			
			ServiceResource resource = new ServiceResource();
			
			htmlResponse += "<spam style=\"font-family: Verdana, Geneva, Tahoma, sans-serif;font-size: 20px;\">";
			htmlResponse += resource.getTagInformation("webApplication");
			htmlResponse += "</spam>";
			
		} catch (Exception ex) {
			ex.printStackTrace();			
			htmlResponse += String.format("<p>An error has occurred: %s</p>", ex.getMessage());			
			
		} finally {
			AbstractEntity.dispose();
		}
		
		response.getWriter().write(htmlResponse);		
		response.getWriter().append("</body></html>");		
	}
}
