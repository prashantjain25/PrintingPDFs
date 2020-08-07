package com.demo.printingservice;

import com.demo.printingservice.serviceImpl.Printer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;

@SpringBootApplication
public class PrintingserviceApplication {

    public static void main(String[] args) throws IOException {
        SpringApplication.run(PrintingserviceApplication.class, args);
        Printer test=new Printer();
        FileInputStream file=new FileInputStream("C:\\pdf\\test.pdf");
//        byte[] fileContent = Files.readAllBytes(file.toPath());
        test.printPDFDoc(file,"Win32 Printer : EPSON L3150 Series",1);
    }

}
