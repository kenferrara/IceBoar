package com.roche.iceboar.runner;

import com.roche.iceboar.progressevent.ProgressEventFactory;
import com.roche.iceboar.progressevent.ProgressEventQueue;
import com.roche.iceboar.settings.GlobalSettings;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * TODO write JAVADOC!!!
 * User: koziolek
 */
public abstract class AbstractJVMRunner implements JVMRunner {
    protected final ProgressEventFactory progressEventFactory;
    protected GlobalSettings settings;
    protected ExecutableCommandFactory executableCommandFactory;
    protected ProgressEventQueue progressEventQueue;

    public AbstractJVMRunner(GlobalSettings settings, ProgressEventQueue progressEventQueue, ProgressEventFactory progressEventFactory, ExecutableCommandFactory executableCommandFactory) {
        this.settings = settings;
        this.progressEventQueue = progressEventQueue;
        this.progressEventFactory = progressEventFactory;
        this.executableCommandFactory = executableCommandFactory;
    }

    protected abstract void runMainClass();

    protected void redirectProcessOutputsToDebugWindow(final Process process) {
        startOutputReaderThread(process);
        startErrorReaderThread(process);
    }

    private void startOutputReaderThread(final Process process) {
        Thread thread1 = new Thread(new Runnable() {
            public void run() {
                BufferedReader inputReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String line;
                while (true) {
                    try {
                        if (inputReader.ready()) {
                            line = inputReader.readLine();
                            if (line != null) {
                                System.out.println("Process input: " + line);
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        thread1.start();
    }

    private void startErrorReaderThread(final Process process) {
        Thread thread2 = new Thread(new Runnable() {
            public void run() {
                BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
                String line;
                while (true) {
                    try {
                        if (errorReader.ready()) {
                            line = errorReader.readLine();
                            if (line != null) {
                                System.out.println("Process error: " + line);
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        thread2.start();
    }
}
