package com.demo.printingservice.config;

import javax.print.DocPrintJob;
import javax.print.event.PrintJobAdapter;
import javax.print.event.PrintJobEvent;

public class PrintRegisterListener {
    boolean done = false;

    public PrintRegisterListener(DocPrintJob job) {
        job.addPrintJobListener(new PrintJobAdapter() {
            @Override
            public void printDataTransferCompleted(PrintJobEvent pje) {
                allDone();
            }

            @Override
            public void printJobCompleted(PrintJobEvent pje) {
                allDone();
            }

            @Override
            public void printJobFailed(PrintJobEvent pje) {
                allDone();
            }

            @Override
            public void printJobCanceled(PrintJobEvent pje) {
                allDone();
            }

            @Override
            public void printJobNoMoreEvents(PrintJobEvent pje) {
                allDone();
            }

            @Override
            public void printJobRequiresAttention(PrintJobEvent pje) {
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
