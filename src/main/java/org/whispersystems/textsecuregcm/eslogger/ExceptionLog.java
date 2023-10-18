package org.whispersystems.textsecuregcm.eslogger;

import com.github.difftim.eslogger.ESLogger;

import java.io.PrintWriter;
import java.io.StringWriter;

public class ExceptionLog {
    public static void exception(Throwable ex,Integer code) {
        ESLogger log = new ESLogger("exceptionlogs");
        StringWriter errors = new StringWriter();
        ex.printStackTrace(new PrintWriter(errors));
        log.withCustom("exception",errors.toString()).withResponseCode(code).send();
    }
}
