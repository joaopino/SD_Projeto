package com.example.servingwebcontent;

import java.rmi.registry.LocateRegistry;
import java.util.ArrayList;

import org.apache.coyote.ajp.AjpAprProtocol;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import meta1.SearchModule_I;

@Component
public class StatusUpdater {

    private final SimpMessagingTemplate messagingTemplate;
    SearchModule_I h;
    public StatusUpdater(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @Scheduled(fixedDelay = 1000) // Adjust the delay as per your requirements
    public void generateStatus() throws Exception{
        // Generate or retrieve the information you want to send
        h = (SearchModule_I) LocateRegistry.getRegistry(2000).lookup("Search_Module");
        String status = h.getDownloadersStatus();
        String[] parts = status.split(" ");
        ArrayList<Status> aa = new ArrayList<>();
        for (int i = 0; i < Integer.parseInt(parts[0]); i++) {
            aa.add(new Status(parts[1 + i * 4], parts[2 + i * 4], parts[3 + i * 4], parts[4 + i * 4]));
    }  
        // Send the status information to the '/topic/status' endpoint
        messagingTemplate.convertAndSend("/topic/status", aa);
    }

    



    
}
