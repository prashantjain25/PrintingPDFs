package com.demo.printingservice.config;

import lombok.extern.slf4j.Slf4j;
import sun.print.Win32PrintJob;

import javax.print.DocPrintJob;
import javax.print.event.PrintJobAdapter;
import javax.print.event.PrintJobEvent;
import java.util.UUID;

@Slf4j
public class PrintRegisterListener {
    boolean done = false;
    UUID printId=null;
    public PrintRegisterListener(DocPrintJob job) {
        job.addPrintJobListener(new PrintJobAdapter() {
            @Override
            public void printDataTransferCompleted(PrintJobEvent pje) {
                log.info("printDataTransferCompleted");
                allDone();
            }

            @Override
            public void printJobCompleted(PrintJobEvent pje) {
                log.info("printJobCompleted");
                allDone();
            }

            @Override
            public void printJobFailed(PrintJobEvent pje) {
                log.info("printJobFailed");
                allDone();
            }

            @Override
            public void printJobCanceled(PrintJobEvent pje) {
                log.info("printJobCanceled");
                allDone();
            }

            @Override
            public void printJobNoMoreEvents(PrintJobEvent pje) {
                log.info("printJobNoMoreEvents");
                allDone();
            }

            @Override
            public void printJobRequiresAttention(PrintJobEvent pje) {
                log.info("printJobRequiresAttention");
                allDone();
            }

            void allDone() {
                synchronized (PrintRegisterListener.this) {
                    done = true;
                    PrintRegisterListener.this.notify();
                }
            }


        });
    }

    public synchronized void waitForDone() {
        try {
            while (!done) {
                wait();
            }
        } catch (InterruptedException ex) {

        }
    }
}
