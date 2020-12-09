package com.efactura.service;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.ResourceBundle;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.soap.client.core.SoapActionCallback;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import com.efactura.cl.fe.prod.APIFACTURAELECTRONICACHILEBindingStub;
import com.efactura.cl.fe.prod.APIFACTURAELECTRONICACHILEPortTypeProxy;
import com.efactura.cl.fe.prod.ObtenerToken;
import com.efactura.dominio.FECAESolicitarRespCodBarraProd;
import com.efactura.dominio.Factura;
import com.google.common.base.Charsets;
import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;

import jdk.internal.org.xml.sax.InputSource;
import jdk.internal.org.xml.sax.SAXException;


/**
 * <pre>This piece of code was created to consume a Web Service made with Soap using directly with http call</pre>
 * @author Miguel Schwindt - miguel@mstechnology.com.ar
 *
 */
public class HttpSoapCall4Java{

	protected final static Logger LOGGER = Logger.getLogger(HttpSoapCall4Java.class);

	/**
	 * @param args
	 * @throws SAXException 
	 * @throws MalformedURLException 
	 * @throws DecoderException 
	 * @throws IOException 
	 * @throws ClassNotFoundException 
	 * @throws DocumentException 
	 */
	public static void main(String[] args) throws MalformedURLException, IOException, SAXException{
		
		HttpSoapCall4Java call = new HttpSoapCall4Java();
		call.doHttpSoapCallObtenerToken();
		
	}
	
	public String doHttpSoapCallObtenerToken() throws MalformedURLException, IOException, org.xml.sax.SAXException {

		//Code to make a webservice HTTP request
		String responseString = "";
		String outputString = "";
		String wsURL = "http://www.example.cl/ws/WebService.php";
		URL url = new URL(wsURL);
		URLConnection connection = url.openConnection();
		HttpURLConnection httpConn = (HttpURLConnection)connection;
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		
		//hardcode credentials
		String RUTACCESOAPI = "miguel";
		String PASSWORDACCESOAPI = "pass";
		
		//TODO Improve: create method to create boiler plate xml code 
		String xmlInput =
		"<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:app=\"http://www.example.cl\">"+
		"   <soapenv:Header/>"+
		"   <soapenv:Body>"+
		"      <app:ObtenerToken>"+
		"         <RUTACCESOAPI>"+RUTACCESOAPI+"</RUTACCESOAPI>"+
		"         <PASSWORDACCESOAPI>"+PASSWORDACCESOAPI+"</PASSWORDACCESOAPI>"+
		"      </app:ObtenerToken>"+
		"   </soapenv:Body>"+
		"</soapenv:Envelope>";
		
		byte[] buffer = new byte[xmlInput.length()];
		buffer = xmlInput.getBytes();
		bout.write(buffer);
		byte[] b = bout.toByteArray();
		String SOAPAction = "http://www.example.cl/ObtenerToken";
		
		// Set the appropriate HTTP parameters.
		httpConn.setRequestProperty("Content-Length",String.valueOf(b.length));
		httpConn.setRequestProperty("Content-Type", "text/xml; charset=utf-8");
		httpConn.setRequestProperty("SOAPAction", SOAPAction);
		httpConn.setRequestMethod("POST");
		httpConn.setDoOutput(true);
		httpConn.setDoInput(true);
		OutputStream out = httpConn.getOutputStream();
		
		//Write the content of the request to the outputstream of the HTTP Connection.
		out.write(b);
		//out.close();
		//Ready with sending the request.
		
		//Read the response.
		InputStreamReader isr = new InputStreamReader(httpConn.getInputStream());
		BufferedReader in = new BufferedReader(isr);
		
		//Write the SOAP message response to a String.
		while ((responseString = in.readLine()) != null) {
			outputString = outputString + responseString;
		}
		//Parse the String output to a org.w3c.dom.Document and be able to reach every node with the org.w3c.dom API.
		Document document = parseXmlFile(outputString);
		NodeList nodeLst = document.getElementsByTagName("Token");
		String res = nodeLst.item(0).getFirstChild().getNodeValue();
		LOGGER.info(String.format("Token: %s", res));
		
		//Write the SOAP message formatted to Logger.
		String formattedSOAPResponse = formatXML(outputString);
		LOGGER.info(String.format("Response XML: \n %s", formattedSOAPResponse));
		return res;
	}
	
	//format the XML in your String
	public String formatXML(String unformattedXml) throws org.xml.sax.SAXException {
		try {
			Document document = parseXmlFile(unformattedXml);
			OutputFormat format = new OutputFormat(document);
			format.setIndenting(true);
			format.setIndent(3);
			format.setOmitXMLDeclaration(true);
			Writer out = new StringWriter();
			XMLSerializer serializer = new XMLSerializer(out, format);
			serializer.serialize(document);
			return out.toString();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	private Document parseXmlFile(String in) throws org.xml.sax.SAXException {
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			InputSource is = new InputSource(new StringReader(in));
			Reader stream = is.getCharacterStream();
			InputStream inputStream = 
		      IOUtils.toInputStream(IOUtils.toString(stream));

			stream.close();
			inputStream.close();
		    
			return db.parse(inputStream);
		} catch (ParserConfigurationException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	

}
