package click.comeand.hopserver.web;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.stream.Collectors;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * HopServerWeb is a simple web version of HopServer.
 * 
 * HopServer gets list of servers (in the form server:port) where an instance of
 * HopServer is deployed and calls the first server on the list and send it the
 * remaining servers on the list. By this way, we can go through a series of
 * servers checking if the servers are available and accessible through those
 * ports.
 * 
 * This application is designed for educational purposes. Use it at your own risk.
 * 
 * @author fabianmartins
 * @see https://github.com/fabianmartins/hopserverweb
 */
@WebServlet("/hop")
public class HopServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public HopServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * GET just shows the details of the first server hitted
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.getWriter().append(this.getRequestDetails(request));
	}

	/**
	 * POST receives a list or servers in "serverlist", in the form server:port,
	 * calls the first one and send it the remaining servers from "serverlist".
	 * If there are no remaining servers in "serverlist", then it is finished.
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// Registering my details in the response
		String myDetails = this.getRequestDetails(request);
		response.getWriter().append(myDetails);
		// Calling the next servers
		String serverlist = request.getParameter("serverlist");
		if (serverlist == null || serverlist.trim().length() == 0) {
			// no server indicated. Present local.
			response.getWriter().append("\n\n*** All hops processed ***");
		} else
		// we have at least one server in the serverlist
		{
			String[] serverlistArray = serverlist.split("\n");
			String nextResponse = getNextLayerResponse(serverlistArray);
			response.getWriter().append(nextResponse);
		}
	}

	/**
	 * Describe client and server details
	 * 
	 * @param request
	 * @return
	 */
	protected String getRequestDetails(HttpServletRequest request) {
		StringBuffer buf = new StringBuffer();
		String client = "";
		client += "--------------------------------------------------------------------\n";
		client += "Requested by\n";
		client += "RemoteAddr:\t";
		client += request.getRemoteAddr() + "\n";
		client += "RemoteHost:\t" + request.getRemoteHost() + "\n";
		client += "RemotePort:\t" + request.getRemotePort() + "\n";
		client += "RemoteUser:\t" + request.getRemoteUser() + "\n\n";
		buf.append(client);
		String thisMachine = "";
		thisMachine += "And I am serving at\n" + "LocalAddr:\t" + request.getLocalAddr() + "\n";
		thisMachine += "LocalName:\t" + request.getLocalName() + "\n" + "LocalPort:\t" + request.getLocalPort();
		buf.append(thisMachine);
		return buf.toString();
	}

	/**
	 * Call the next server in the list.
	 * 
	 * @param serverlist
	 *            A list of servers. Must have at least one server.
	 * @return
	 */
	private String getNextLayerResponse(String[] serverlist) {
		StringBuffer result = new StringBuffer();
		String serverToCall = serverlist[0].trim();
		String urlToCall = "http://" + serverToCall + "/HopDescribeApp/hop";
		result.append("\n\n## NEXT LAYER : " + urlToCall + "\n");
		String newServerList;
		if (serverlist.length > 1) {
			String[] remainingServersArray = null;
			remainingServersArray = Arrays.copyOfRange(serverlist, 1, serverlist.length);
			newServerList = Arrays.stream(remainingServersArray).collect(Collectors.joining("\n"));
		} else
			newServerList = "";
		try {
			result.append(doRequest(urlToCall, new String[][] { { "serverlist", newServerList } })).append("\n");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result.toString();
	}

	/**
	 * 
	 * @param url
	 * @param parameters
	 * @return
	 * @throws IOException
	 */
	protected String doRequest(String url, String[][] parameters) throws IOException {
		StringBuffer result = new StringBuffer();
		StringBuffer encodedParameters = new StringBuffer();
		for (int i = 0; i < parameters.length; i++)
			encodedParameters.append(parameters[i][0]).append("=").append(URLEncoder.encode(parameters[i][1], "UTF-8"));
		URL u = new URL(url);
		URLConnection conn = u.openConnection();
		conn.setDoOutput(true);
		conn.connect();
		OutputStreamWriter os = new OutputStreamWriter(conn.getOutputStream());
		os.write(encodedParameters.toString());
		os.close();
		InputStreamReader is = new InputStreamReader(conn.getInputStream());
		BufferedReader reader = new BufferedReader(is);
		while (reader.ready()) {
			result.append(reader.readLine()).append("\n");
		}
		is.close();
		return result.toString();
	}

}
