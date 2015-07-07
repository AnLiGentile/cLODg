package it.istc.cnr.stlab.clodg.util;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Set;

import javax.xml.xpath.XPathExpressionException;

import org.apache.commons.io.FileUtils;




/**
 * @author annalisa
 *
 */
public class CheckURIs {

	// Using Java IO
	 public static void openFileFromUrlWithJavaIO(String fileName, String fileUrl)
	 throws MalformedURLException, IOException {
	 BufferedInputStream in = null;
	 FileOutputStream fout = null;
	 try {
	 in = new BufferedInputStream(new URL(fileUrl).openStream());
	 fout = new FileOutputStream(fileName);
	 
	byte data[] = new byte[1024];
	 int count;
	 while ((count = in.read(data, 0, 1024)) != -1) {
	 fout.write(data, 0, count);
	 }
	 } finally {
	 if (in != null)
	 in.close();
	 if (fout != null)
	 fout.close();
	 }
	 }
	 
	// Using Commons IO library
	 // Available at http://commons.apache.org/io/download_io.cgi
	 public static void saveFileFromUrlWithCommonsIO(String fileName,
	 String fileUrl) throws MalformedURLException, IOException {
	 FileUtils.copyURLToFile(new URL(fileUrl), new File(fileName));
	 }
	 
	/**
	 * @param args
	 * @throws XPathExpressionException 
	 * @throws IOException 
	 */
	public static void main(String[] args) throws XPathExpressionException, IOException {
	
		 
		 
		Set<URL> people = People.people;
		
		new File("./query/").mkdirs();
		int i =0;
    	for (URL p:people){
    		i++;
		HttpURLConnection con = (HttpURLConnection)(p.openConnection());
		con.setInstanceFollowRedirects( false );
		con.connect();
		int responseCode = con.getResponseCode();
		String location = con.getHeaderField( "Location" );
		System.out.println( responseCode +"\t"+p+"\t"+location);
		if (responseCode!=404) {saveFileFromUrlWithJavaIO("./query/"+i, location);};
		
		
    	}
		
		
	}
	
	// Using Java IO
	 public static void saveFileFromUrlWithJavaIO(String fileName, String fileUrl)
	 throws MalformedURLException, IOException {
	 BufferedInputStream in = null;
	 FileOutputStream fout = null;
	 try {
	 in = new BufferedInputStream(new URL(fileUrl).openStream());
	 fout = new FileOutputStream(fileName);
	 
	byte data[] = new byte[1024];
	 int count;
	 while ((count = in.read(data, 0, 1024)) != -1) {
	 fout.write(data, 0, count);
	 }
	 } finally {
	 if (in != null)
	 in.close();
	 if (fout != null)
	 fout.close();
	 }
	 }

	
    static void printString(String s, String filename){
		PrintWriter out;
		try {
			out = new PrintWriter(new FileWriter(filename));
						out.println(s);
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		    }
}
