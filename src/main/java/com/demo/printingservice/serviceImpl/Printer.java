package com.demo.printingservice.serviceImpl;

import com.demo.printingservice.config.PrintRegisterListener;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.JPEGFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.printing.PDFPageable;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;

import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import java.awt.image.BufferedImage;
import java.awt.print.PrinterJob;
import java.io.FileInputStream;
import java.io.IOException;

@Slf4j
public class Printer {
    private static PrintService service;
    private static DocFlavor FLAVOR= DocFlavor.INPUT_STREAM.AUTOSENSE;
    public void printPDFDoc(FileInputStream inputbyte, String printers, int numCopies) {
        try {
            PDDocument pdfDoc = getPdDocument(inputbyte, PDRectangle.A4);
            PrintService service = getPrintService(printers);
            if(service!=null){
                DocPrintJob job=service.createPrintJob();
//                PrintRegisterListener plistener= new PrintRegisterListener(job);

                PrinterJob pjob = PrinterJob.getPrinterJob();
                pjob.setPrintService(job.getPrintService());
                pjob.setCopies(numCopies);
                pjob.setPageable(new PDFPageable(pdfDoc));
                pjob.print();
//                Doc doc=new SimpleDoc(inputbyte,FLAVOR,null );
//                PrintRequestAttributeSet pras = new HashPrintRequestAttributeSet();
//                pras.add(MediaSizeName.ISO_A4);
//                pras.add(new PageRanges(startPage+1,endPage+1));
//                pras.add(new Copies(numCopies));
//                job.print(doc,pras);

//                plistener.waitForDone();



            }else{
                log.error("No proper printer");
            }
            pdfDoc.close();
            inputbyte.close();
        } catch (Exception ex) {
            log.error(ex.getMessage());
        }

    }

    /**
     * Change to auto fit
     * @param inputbyte
     * @return
     * @throws IOException
     */
    private PDDocument getPdDocument(FileInputStream inputbyte, PDRectangle pdrectangle) throws IOException {
        PDDocument pdDocument = new PDDocument();
        PDDocument oDocument = PDDocument.load(inputbyte);
        PDFRenderer pdfRenderer = new PDFRenderer(oDocument);
        int numberOfPages = oDocument.getNumberOfPages();
        PDPage page = null;

        for (int i = 0; i < numberOfPages; i++) {
            page = new PDPage(pdrectangle);
            BufferedImage bim = pdfRenderer.renderImageWithDPI(i, 600, ImageType.RGB);
            PDImageXObject pdImage = JPEGFactory.createFromImage(pdDocument, bim);
            PDPageContentStream contentStream = new PDPageContentStream(pdDocument, page);
            float newHeight = pdrectangle.getHeight();
            float newWidth = pdrectangle.getWidth();
            contentStream.drawImage(pdImage, 0, 0, newWidth, newHeight);
            contentStream.close();

            pdDocument.addPage(page);
        }
        return pdDocument;
    }

    private PrintService getPrintService(String printers) {

        PrintService services[] = PrintServiceLookup.lookupPrintServices(FLAVOR, null); //PrintRequestAttributeSet is kept null
        for(int i=0;i<services.length;i++){
            String name=services[i].toString();
            log.info("Printer: "+name);
            if(name.equals(printers)){
                service=services[i];
                break;
            }
        }
        return service;
    }
}
