package com.demo.printingservice.serviceImpl;

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
import org.apache.pdfbox.util.Matrix;

import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.awt.print.PrinterJob;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;

@Slf4j
public class Printer {
    private static PrintService service;
    private static DocFlavor FLAVOR= DocFlavor.INPUT_STREAM.AUTOSENSE;
    public void printPDFDoc(FileInputStream inputbyte, String printers, int numCopies) throws IOException {
        HashMap<Integer, PDPageContentStream> mPageContentStreamMap = new HashMap<>();
        PDDocument oDocument = PDDocument.load(inputbyte);
        PDDocument pdfDoc = getPdDocument(oDocument, PDRectangle.A4,mPageContentStreamMap);
        try {
            PrintService service = getPrintService(printers);
            if(service!=null){
                DocPrintJob job=service.createPrintJob();
//                PrintRegisterListener plistener= new PrintRegisterListener(job);

                PrinterJob pjob = PrinterJob.getPrinterJob();
                pjob.setPrintService(job.getPrintService());
                pjob.setCopies(numCopies);
                pjob.setPageable(new PDFPageable(pdfDoc));
                for (int i : mPageContentStreamMap.keySet()) {
                    mPageContentStreamMap.get(i).close();
                }
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


        } catch (Exception ex) {
            log.error(ex.getMessage());
        }finally{
            oDocument.close();
            pdfDoc.close();
        }
//        inputbyte.close();
    }

    /**
     * Change to auto fit
     * @return
     * @throws IOException
     */
    private PDDocument getPdDocument(PDDocument oDocument, PDRectangle pdrectangle,HashMap<Integer, PDPageContentStream> mPageContentStreamMap) throws IOException {
        PDDocument pdDocument = new PDDocument();

        PDFRenderer pdfRenderer = new PDFRenderer(oDocument);
        int numberOfPages = oDocument.getDocumentCatalog().getPages().getCount();


        for (int i = 0; i < numberOfPages; i++) {
//            BufferedImage bim = pdfRenderer.renderImageWithDPI(i, 600, ImageType.RGB);
//            PDImageXObject pdImage = JPEGFactory.createFromImage(pdDocument, bim);

            PDPage page =  oDocument.getDocumentCatalog().getPages().get(i);
            PDPageContentStream cs = new PDPageContentStream(oDocument, page, PDPageContentStream.AppendMode.PREPEND, false, false);
            Matrix matrix = null;
            int isRotated=findLandscape(oDocument.getPage(i));
            if(isRotated==-1) {
                matrix= Matrix.getRotateInstance(Math.toRadians(90), 0, 0);
            }else if(isRotated==1){
                matrix= Matrix.getRotateInstance(Math.toRadians(270), 0, 0);
            }
            else{
                matrix=Matrix.getRotateInstance(Math.toRadians(0), 0, 0);
            }

            cs.transform(matrix);
            PDRectangle cropBox = page.getCropBox();
            float cx = (cropBox.getLowerLeftX() + cropBox.getUpperRightX()) / 2;
            float cy = (cropBox.getLowerLeftY() + cropBox.getUpperRightY()) / 2;
            Point2D.Float newC = matrix.transformPoint(cx, cy);
            float tx = (float)newC.getX() - cx;
            float ty = (float)newC.getY() - cy;
            page.setCropBox(new PDRectangle(cropBox.getLowerLeftX() + tx, cropBox.getLowerLeftY() + ty, cropBox.getWidth(), cropBox.getHeight()));
            PDRectangle mediaBox = page.getMediaBox();
            page.setMediaBox(new PDRectangle(mediaBox.getLowerLeftX() + tx, mediaBox.getLowerLeftY() + ty, mediaBox.getWidth(), mediaBox.getHeight()));



//            PDPageContentStream contentStream = new PDPageContentStream(pdDocument, page);
//            float newHeight = pdrectangle.getHeight();
//            float newWidth = pdrectangle.getWidth();
//
//            cs.drawImage(pdImage, 0, 0, newWidth, newHeight);

            mPageContentStreamMap.put(i, cs);
            pdDocument.addPage(page);

        }


//        oDocument.close();

        return pdDocument;
    }

    /**
     * Check orientation
     * @param page
     */
    private int findLandscape(PDPage page) {
        int flag=0;
        PDRectangle mediaBox= page.getMediaBox();
        boolean isLandscape = mediaBox.getWidth() > mediaBox.getHeight();
        int rotation = page.getRotation();
        if (rotation == 90 && isLandscape) {
           return flag = -1;
        }
        else if(rotation == 270) {
                return flag = 1;
            }

        return flag;
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
