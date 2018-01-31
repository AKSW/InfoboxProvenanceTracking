package org.dbpedia.infoboxprov.webinterface;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class EchoServlet
 */
public class Servlet extends HttpServlet {
	 
	   @Override
	   public void doGet(HttpServletRequest request, HttpServletResponse response)
	               throws IOException, ServletException {
		   // Set the response message's MIME type
		      response.setContentType("text/html;charset=UTF-8");
		      // Allocate a output writer to write the response message into the network socket
		      PrintWriter out = response.getWriter();
		 
		      // Write the response message, in an HTML page
		      try {
		         out.println("<!DOCTYPE html>");
		         out.println("<html><head>");
		         out.println("<meta http-equiv='Content-Type' content='text/html; charset=UTF-8'>");
		         out.println("<title>Hello, World</title></head>");
		         out.println("<body>");
		         out.println("<h1>Hello, world!</h1>");  // says Hello
		         
		         // Hyperlink "BACK" to input page
		         out.println("<a href='index.html'>BACK</a>");
		         
		         out.println("</body>");
		         out.println("</html>");
		      } finally {
		         out.close();  // Always close the output writer
		      }
	}
}