
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
 
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.w3c.dom.Document;
import org.w3c.dom.DOMException;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.transform.stream.StreamResult;

import java.io.*;

public class Transform
{
    public static void main (String argv[])
    {
        if (argv.length != 2) {
            System.err.println ("Usage: java Transform stylesheet-file xml-file");
            System.exit (1);
        }

        DocumentBuilderFactory factory =
            DocumentBuilderFactory.newInstance();
       
 
        try {
            File stylesheet = new File(argv[0]);
            File datafile   = new File(argv[1]);
 
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(datafile);
 
       
            TransformerFactory tFactory =
                TransformerFactory.newInstance();
            StreamSource stylesource = new StreamSource(stylesheet);
            Transformer transformer = tFactory.newTransformer(stylesource);
 
            DOMSource source = new DOMSource(document);
            StreamResult result = new StreamResult(System.out);
            transformer.transform(source, result);
           
        } catch (TransformerConfigurationException tce) {
       
           System.out.println ("\n** Transformer Factory error");
           System.out.println("   " + tce.getMessage() );

          
           Throwable x = tce;
           if (tce.getException() != null)
               x = tce.getException();
           x.printStackTrace();
      
        } catch (TransformerException te) {
          
           System.out.println ("\n** Transformation error");
           System.out.println("   " + te.getMessage() );

           
           Throwable x = te;
           if (te.getException() != null)
               x = te.getException();
           x.printStackTrace();
           
         } catch (SAXException sxe) {
           
           Exception  x = sxe;
           if (sxe.getException() != null)
               x = sxe.getException();
           x.printStackTrace();

        } catch (ParserConfigurationException pce) {
         
            pce.printStackTrace();

        } catch (IOException ioe) {
      
           ioe.printStackTrace();
        }

    } 

}
