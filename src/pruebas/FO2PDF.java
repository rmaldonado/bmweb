package pruebas;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringBufferInputStream;

import org.apache.avalon.framework.ExceptionUtil;
import org.apache.avalon.framework.logger.ConsoleLogger;
import org.apache.avalon.framework.logger.Logger;
import org.apache.fop.apps.Driver;
import org.apache.fop.apps.FOPException;
import org.apache.fop.messaging.MessageHandler;
import org.xml.sax.InputSource;

/**
 * This class demonstrates the conversion of an FO file to PDF using FOP.
 */
public class FO2PDF {

    public void convertFO2PDF(String pdfFile) throws IOException, FOPException {

    	StringBuffer sb = new StringBuffer();
    	
    	// Leo el archivo
    	String linea;
    	BufferedReader br = new BufferedReader(new FileReader("WEB-INF/bono_barcode.fo.xml"));
    	while ( (linea = br.readLine()) != null){ sb.append(linea); }
        
    	//Construct driver
        Driver driver = new Driver();
        
        //Setup logger
        Logger logger = new ConsoleLogger(ConsoleLogger.LEVEL_DISABLED);
        driver.setLogger(logger);
        MessageHandler.setScreenLogger(logger);

        //Setup Renderer (output format)        
        driver.setRenderer(Driver.RENDER_PDF);
        
        /**
         * No deberia requerir encriptar, ya que el PDF que se genera es bastante feo
         * (binario)
         * 
        HashMap rendererOptions = new HashMap();
        rendererOptions.put("ownerPassword", "mypassword");
        //rendererOptions.put("allowCopyContent", "FALSE");
        rendererOptions.put("allowEditContent", "FALSE");
        //rendererOptions.put("allowPrint", "FALSE");
        driver.getRenderer().setOptions(rendererOptions);
        */
        
    	BufferedOutputStream out = new BufferedOutputStream( new FileOutputStream(pdfFile) );
    	
    	StringBufferInputStream in = new StringBufferInputStream(sb.toString());

    	try {
            driver.setOutputStream(out);

            try {
                driver.setInputSource(new InputSource(in));
            
                //Process FO
                driver.run();
            } finally {
                in.close();
            }
        } finally {
            out.close();
        }
    }


    public static void main(String[] args) {
        try {

            FO2PDF app = new FO2PDF();
            app.convertFO2PDF("salida.pdf");
            
            System.out.println("Success!");
        } catch (Exception e) {
            System.err.println(ExceptionUtil.printStackTrace(e));
            System.exit(-1);
        }
    }
}
