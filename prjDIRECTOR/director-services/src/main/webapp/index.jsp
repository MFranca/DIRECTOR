<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>DIRECTOR - a clouD mIcroseRvice selECTion framewORk</title>
    </head>
    
    <body>
    	<h1>DIRECTOR - a clouD mIcroseRvice selECTion framewORk</h1>
    	<h2>director-services</h2>
        <h3>Our University - Our City, Our Country</h3>        
        <h4>A Ph.D research by a student and his advisor.</h4>
        <!-- v0.AA.MM.DD -->
        <h5>v0.18.12.27</h5>
        <hr />
    
    	<h2>Direct Query Links</h2>
        <ol>
        	<li><a href="listPlatforms">List registered PaaS...</a></li>
        	<li><a href="statusDirector">Get a raw JSON of DIRECTOR's overall status...</a></li>
        	<li><a href="listServices">Get a raw JSON of DIRECTOR's services...</a></li>
        	<li><a href="listFeatures">Get a raw JSON of the available features...</a></li>        	
        </ol>
        <hr />
        
        <h2>1) (Discovery) Are you searching for a cloud microservice?</h2>        
        <form method = "get"  action = "listCandidates">
                Comma-separated list of desired features: 
                <input type = "text" name = "txtPositiveFilter" size="120" value ="dbaas, nosql, data store, data stores, dba" required /><br />
                Comma-separated list of features to exclude: 
                <input type = "text" name = "txtNegativeFilter" size="120" value="ibm_deprecated, key-value, caching" /><br />
                <br />
                <input type = "submit" value = "Show Me Some Candidates"/>
        </form>       
        
        <!-- Technical Perspective -->
        <br />
        <hr />
        <h2>2) Technical Perspective</h2>        
        <form method = "get"  action = "evaluateCandidatesTechnically">
			Comma-separated list of candidates (serviceIds from previous form / 'Show Me Some Candidates'): 
	        <input type = "text" name = "txtCandidates" size="120" value ="1341, 1781, 1451, 2001, 1571, 1241, 2011, 1791, 1161, 121, 1, 311, 1401, 1641, 621, 931, 1291" required /><br />
	        <br />	         
	        
	        <h3>QoS Priorities (Importance)</h3>	         
	        <table border="1">
				<tr>
					<th>Agility</th>
				    <th>Assurance</th>
				    <th>Financial</th>
				    <th>Performance</th>
				    <th>Security & Privacy</th>
				    <th>Usability</th>				    
				</tr>
				
				<tr>
					<td>
						<select name="selAgility" size="5">
							<option value="5">5 (higher)</option>
						  	<option value="4">4</option>						  
						  	<option value="3">3</option>
						  	<option value="2">2</option>
						  	<option value="1" selected>1 (lower)</option>
						</select>
					</td>
				    <td>
						<select name="selAssurance" size="5">
							<option value="5">5 (higher)</option>
						  	<option value="4">4</option>						  
						  	<option value="3">3</option>
						  	<option value="2">2</option>
						  	<option value="1" selected>1 (lower)</option>
						</select>
					</td>
					<td>
						<select name="selFinancial" size="5">
							<option value="5">5 (higher)</option>
						  	<option value="4">4</option>						  
						  	<option value="3">3</option>
						  	<option value="2">2</option>
						  	<option value="1" selected>1 (lower)</option>
						</select>
					</td>
					<td>
						<select name="selPerformance" size="5">
							<option value="5">5 (higher)</option>
						  	<option value="4">4</option>						  
						  	<option value="3">3</option>
						  	<option value="2">2</option>
						  	<option value="1" selected>1 (lower)</option>
						</select>
					</td>
					<td>
						<select name="selSecurity" size="5">
							<option value="5">5 (higher)</option>
						  	<option value="4">4</option>						  
						  	<option value="3">3</option>
						  	<option value="2">2</option>
						  	<option value="1" selected>1 (lower)</option>
						</select>
					</td>
					<td>
						<select name="selUsability" size="5">
							<option value="5">5 (higher)</option>
						  	<option value="4">4</option>						  
						  	<option value="3">3</option>
						  	<option value="2">2</option>
						  	<option value="1" selected>1 (lower)</option>
						</select>
					</td>
				</tr>								  
			</table> 
	
			<br />
			<input type = "submit" value = "Rank These Candidates"/>
        </form>        
        
        <!-- Social Perspective -->
        <br />
        <hr />
        <h2>3) Social Perspective</h2>
         <form method = "get"  action = "evaluateCandidatesSocially">
			Comma-separated list of candidates (serviceIds from previous form / 'Show Me Some Candidates'): 
	        <input type = "text" name = "txtCandidates" size="120" value ="1341, 1781, 1451, 2001, 1571, 1241, 2011, 1791, 1161, 121, 1, 311, 1401, 1641, 621, 931, 1291" required /><br />
	        <br />	         
	        
			<input type = "submit" value = "Rank These Candidates"/>
        </form>
        
        <!-- Semantical Perspective -->
        <br />
        <hr />
        <h2>4) Semantical Perspective</h2>
         <form method = "get"  action = "evaluateCandidatesSemantically">
         	Classifier ID: 
			<input type = "text" name = "txtClassifierId" size="30" value="122608x455-nlc-2922" required /><br />
			Brief description (free-text): 
	        <input type = "text" name = "txtDescription" size="190" value ="I need to store json objects in a nosql database." required /><br />
	        <br />	         
	        
			<input type = "submit" value = "Rank The Candidates"/>
        </form>                
        
        <!-- Footer -->        
        <br />
        <em>Running since 2017 on IBM Bluemix (IBM Cloud).</em>
    </body>
</html>
