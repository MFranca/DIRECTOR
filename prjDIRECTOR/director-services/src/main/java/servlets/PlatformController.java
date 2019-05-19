package servlets;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cloudant.dao.PlatformNoSqlDao;
import cloudant.dao.SummaryNoSqlDao;
import mysqlDB.AbstractEntity;
import mysqlDB.model.Platform;

/**
 * Servlet implementation class PlatformController
 */
@WebServlet("/listPlatforms")
public class PlatformController extends DirectorAbstractController {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public PlatformController() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String htmlResponse = "<html><body>" + HTML_HEADER;
		
		htmlResponse += String.format("<p>Served at: <strong>%s</strong></p>", request.getRequestURI());
		htmlResponse += HTML_FOOTER;
		
		try {			
			AbstractEntity.setupEntityManager(); // /director-dao/src/main/resources/META-INF/persistence.xml
			//Platform platformDao = new Platform();			
			
			//https://dzone.com/articles/java-string-format-examples
			System.out.println();
			htmlResponse += String.format("<table style=\"width:80%%\" border='1px' align='center'><tr>"
					+ "<th>%s</th>"
					+ "<th>%s</th>"
					+ "<th>%s</th>"
					+ "<th>%s</th>"
					+ "<th>%s</th>"
					+ "<th>%s</th>"
					+ "</tr>", "id", "Name", "Description", "Endpoint", "Status", "Days Being Monitored");
						
			List<Platform> platforms = new Platform().findAll();
			for (Platform p: platforms) {				
				//PlatformNoSqlDao noSqlDao = new PlatformNoSqlDao(p.getDbName());
				SummaryNoSqlDao noSqlDao = new SummaryNoSqlDao(p.getDbName()); // we started to save Platform documents later...
				long count = noSqlDao.getWorkingDays();						
				
				htmlResponse += String.format("<tr>"
						+ "<td>%d</td>"
						+ "<td>%s</td>"
						+ "<td>%s</td>"
						+ "<td>%s</td>"
						+ "<td>%s</td>"
						+ "<td>%d</td>"
						+ "</tr>", p.getId(), p.getName(), p.getDescription(), p.getEndpoint(), p.getStatus(), count);
			}			
			htmlResponse += "</table> "; 
			
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
