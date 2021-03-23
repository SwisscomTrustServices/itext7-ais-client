package com.swisscom.ais.itext.client;

import com.swisscom.ais.itext.client.model.ArgumentsContext;
import com.swisscom.ais.itext.client.service.AisRequestService;
import com.swisscom.ais.itext.client.service.CliService;

import java.util.Optional;

public class SignPdfCli {

    public static void main(String[] args) {
        CliService cliService = new CliService(new AisRequestService());
        try {
            Optional<ArgumentsContext> argumentsContext = cliService.buildArgumentsContext(args);
            if (argumentsContext.isPresent()) {
                cliService.prepareForSignings(argumentsContext.get());
                cliService.performSignings(argumentsContext.get());
            }
        } catch (Exception e) {
            System.out.println("Error encountered. Use -help argument to see the detailed options.");
            if (cliService.isVerboseLevelActive()) {
                e.printStackTrace();
            }
            System.exit(1);
        }
    }
}
