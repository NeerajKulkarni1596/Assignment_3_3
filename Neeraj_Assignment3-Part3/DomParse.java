

import javax.xml.parsers.DocumentBuilder; 
import javax.xml.parsers.DocumentBuilderFactory;  
import javax.xml.parsers.ParserConfigurationException; 

import org.xml.sax.SAXException;  
import org.xml.sax.SAXParseException;  
import org.xml.sax.ErrorHandler;

import java.io.*;
import org.w3c.dom.Document;

public class DomParse {
    StringBuffer textBuffer;
    static boolean validateDTD = false;    
    static boolean validateXSD = false;     
    static boolean internal = false;        
                                           
    static boolean isPrettyPrint = false;   
    static Document document;               
    static String xmlFileName = null;
    static String dtdFileName = null;
    static String[] xsdFileNames = null;
    static boolean isValidating = false;   

    static final String JAXP_SCHEMA_LANGUAGE =
        "http://java.sun.com/xml/jaxp/properties/schemaLanguage";
    static final String W3C_XML_SCHEMA =
        "http://www.w3.org/2001/XMLSchema";
    static final String JAXP_SCHEMA_SOURCE =
        "http://java.sun.com/xml/jaxp/properties/schemaSource";
    public static void main(String args[]) {
        String[] xsdFileNameBuffer = null;
        if (args.length > 1)
  	    xsdFileNameBuffer = new String[args.length-1]; 
        int xsd_i = 0;
        for (int i = 0; i < args.length; i++) {
            String name = args[i];
            if (name.toLowerCase().endsWith(".xml")) {
                xmlFileName = name;
                continue;
        }
        if (name.toLowerCase().endsWith(".xsd")) {
            xsdFileNameBuffer[xsd_i++] = name;
            continue;
        }
        if (name.toLowerCase().endsWith(".dtd")) {
            dtdFileName = name;
            continue;
        }
        if (name.toLowerCase().equals("-print")) {
            isPrettyPrint = true;
        }
    }
    if (xmlFileName == null) {
        System.err.println("Usage: java DomParse XML-file [XSDs-or-DTD-file] [-print]");
        System.exit(1);
    }
    DomParse dp = new DomParse();
    if (xsd_i > 0) {
        xsdFileNames = new String[xsd_i];
        for (int i = 0; i < xsd_i; i++) xsdFileNames[i] = xsdFileNameBuffer[i];
    }
    dp.parse(xmlFileName, xsdFileNames, dtdFileName);
    if (isPrettyPrint) 
        System.out.println(TransformUtility.dom2string(document));
    }


    boolean parse(String xmlFileName, String xsdFileName, String dtdFileName) {
        String[] xsdFileNames = {xsdFileName};
        return parse(xmlFileName, xsdFileNames, dtdFileName);
    }

  
    boolean parse(String xmlFileName, String[] xsdFileNames, String dtdFileName) {
        isValidating = false;
        boolean hasError = false;
        DomParse.xmlFileName = xmlFileName;
        DomParse.dtdFileName = dtdFileName;
        if (dtdFileName != null) {
            isValidating = true;
        validateDTD = true;
        internal = false;
    }
    File xmlFile = new File(xmlFileName);
    DomParse.xsdFileNames = xsdFileNames;
    if ((xsdFileNames != null) && (xsdFileNames[0] != null) && xsdFileNames[0].toLowerCase().endsWith(".xsd")) {
        isValidating = true;
        validateXSD = true;
        internal = false;
    }
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    if (isValidating = (isValidating || isValidating(xmlFile))) { 
        factory.setValidating(true);
        System.out.println("<!------- Validation is on ----------->");
    }
    else
        System.out.println("<!------- Validation is off ----------->");
    if (isPrettyPrint) 
        System.out.println("<!------------- Output --------------->");

    factory.setNamespaceAware(true);
    try {
        if (validateXSD)
        factory.setAttribute(JAXP_SCHEMA_LANGUAGE, W3C_XML_SCHEMA);
        if (validateXSD && !internal) 
        factory.setAttribute(JAXP_SCHEMA_SOURCE, xsdFileNames);
        DocumentBuilder builder = factory.newDocumentBuilder();
        builder.setErrorHandler(new MyErrorHandler());
        if (dtdFileName != null) {
        byte[] b = TransformUtility.addDoctype(xmlFileName, dtdFileName).getBytes();
        ByteArrayInputStream inputStream = new ByteArrayInputStream(b, 0, b.length);
        document = builder.parse(inputStream);
        }
        else 
        document = builder.parse(xmlFile);
    } catch (SAXParseException spe) {
      
        hasError = true;
        System.out.println("\n** Parsing error" + ", line " + spe.getLineNumber() + ", uri " + spe.getSystemId());
        System.out.println("   " + spe.getMessage() );
    
  
        Exception  x = spe;
        if (spe.getException() != null)
        x = spe.getException();
        x.printStackTrace();

    } catch (SAXException sxe) {
     
   
        hasError = true;
        Exception  x = sxe;
        if (sxe.getException() != null)
        x = sxe.getException();
        x.printStackTrace();

    } catch (ParserConfigurationException pce) {
     
        hasError = true;
        pce.printStackTrace();

    } catch (IOException ioe) {
      // I/O error
        hasError = true;
        ioe.printStackTrace();

    } catch (Throwable t) {
        hasError = true;
        t.printStackTrace();
    }
    if (isValidating) {
        if (!hasError) {
        System.out.println("\n---The document is well-formed and its validation has succeeded---\n");
        return true;
        }
        else {
        System.out.println("\n---Validation failed---\n");
        return false;
        }
    }
    if (!isValidating) {
        if (!hasError) {
        System.out.println("\n---The document is well-formed---\n");
        return true;
        }
        else {
        System.out.println("\n---The document is not well-formed---\n");
        return false;
        }
    }
    return false;
    }


    static boolean isValidating(File xmlFile) { 
    boolean hasDOCTYPE = false; 
    internal = false; 
    try {
        BufferedReader br = new BufferedReader(new FileReader(xmlFile));
        String line = br.readLine().toLowerCase();
			for (int i = 0; (line != null) && (i < 20); i++) {
        if (line.indexOf("chemalocation=") != -1)
            validateXSD = true;
        if (line.indexOf("<!doctype") != -1)
            hasDOCTYPE = true;
        if (hasDOCTYPE && (line.indexOf("<!element") != -1))
            validateDTD = true;
        if (hasDOCTYPE && (line.indexOf("system") != -1))
            validateDTD = true;
        if (validateXSD || validateDTD) {
            internal = true;
        return true;
        }
        line = br.readLine().toLowerCase();
        }
    }
    catch (Exception e){} 
    return false;
    }

    public Document getDom() {
        return document;
    }
}

class MyErrorHandler implements ErrorHandler {
    public void fatalError(SAXParseException e) throws SAXParseException {
        throw e;
    }  

 
    public void error(SAXParseException e) throws SAXParseException {
        throw e;
    }

    
    public void warning(SAXParseException err) throws SAXParseException {
        System.out.println("** Warning"
            + ", line " + err.getLineNumber()
            + ", uri " + err.getSystemId());
        System.out.println("   " + err.getMessage());
    }
}
