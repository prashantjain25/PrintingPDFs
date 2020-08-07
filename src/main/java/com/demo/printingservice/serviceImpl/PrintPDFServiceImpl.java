package com.demo.printingservice.serviceImpl;
import com.demo.printingservice.config.PrintRegisterListener;

import javax.print.*;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.Copies;
import javax.print.attribute.standard.MediaSizeName;
import javax.print.attribute.standard.PageRanges;
import javax.print.attribute.standard.PrintQuality;
import java.io.ByteArrayInputStream;

public class PrintPDFServiceImpl {
    MediaSizeName mediaSizeName;
    PrintQuality quality;
    byte[] bytes;
    PrintService service;

    public PrintPDFServiceImpl(byte[] bytes,MediaSizeName name, PrintQuality quality) {
        mediaSizeName=name;
        this.quality=quality;
        this.bytes=bytes;
    }

    /**
     *
     * @param service
     * @param startPage
     * @param endPage
     * @param copies
     * @param shrinktoPrintableArea
     */
   public void setupPrintService(PrintService service, int startPage, int endPage, int copies, boolean shrinktoPrintableArea){
       PrintRequestAttributeSet pras = new HashPrintRequestAttributeSet();
       pras.add(MediaSizeName.ISO_A4);
//       pras.add(new PageRanges(startPage+1,endPage+1));
       pras.add(new Copies(copies));
       this.service=service;
    }
    public void print(ByteArrayInputStream bytestream){


    }
}

