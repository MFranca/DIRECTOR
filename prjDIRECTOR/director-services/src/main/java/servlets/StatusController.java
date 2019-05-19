package servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mysqlDB.AbstractEntity;
import services.resources.PlatformResource;
import utils.LogUtils;

/**
 * Servlet implementation class PlatformController
 */
@WebServlet("/statusDirector")
public class StatusController extends DirectorAbstractController {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public StatusController() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String htmlResponse = ""; 
		
		try {			
			LogUtils.logTrace("doGet");
			htmlResponse = "<html><body>" + HTML_HEADER;
		
			htmlResponse += String.format("<p>Served at: <strong>%s</strong></p>", request.getRequestURI());
			htmlResponse += HTML_FOOTER;
			
			LogUtils.logTrace("PlatformResource pr = new PlatformResource();");
			PlatformResource pr = new PlatformResource();
			
			htmlResponse += "<spam style=\"font-family: Verdana, Geneva, Tahoma, sans-serif;font-size: 20px;\">";
			LogUtils.logTrace("htmlResponse += pr.getInformation(\"webApplication\");");
			htmlResponse += pr.getInformation("webApplication");
			htmlResponse += "</spam>";
			
		} catch (Exception ex) {
			ex.printStackTrace();			
			htmlResponse += String.format("<p>An error has occurred: %s</p>", ex.getMessage());			
			
		} finally {
			LogUtils.logTrace("finally");
			AbstractEntity.dispose();
		}
		
		response.getWriter().write(htmlResponse);		
		response.getWriter().append("</body></html>");		
	}
}
