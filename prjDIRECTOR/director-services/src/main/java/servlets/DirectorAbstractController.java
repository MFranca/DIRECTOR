package servlets;

import javax.servlet.http.HttpServlet;

public abstract class DirectorAbstractController extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	
	// confidencialidade em papers
	//protected static final String HTML_HEADER = "<h1>COPPE/UFRJ - Rio de Janeiro, Brazil</h1><h2>director-services</h2><h3>A Ph.D research by Marcelo França and Claudia Werner.</h3><hr />";
	protected static final String HTML_HEADER = "<h1>OUR UNIVERISTY - Our City, Our Country</h1><h2>director-services</h2><h3>A Ph.D research by a student and an advisor.</h3><hr />";
	protected static final String HTML_FOOTER =
			"<p><a href=\"https://jsonformatter.org/\" target=\"_blank\">JSON formatter</a> to better see the results...</p>" + 
			"<p><a href=\"javascript:history.back()\">Back</a> to the main page...</p><hr />";
}