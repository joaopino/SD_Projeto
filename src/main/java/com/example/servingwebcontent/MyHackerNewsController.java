package com.example.servingwebcontent;

import com.example.servingwebcontent.HackerNewsItemRecord;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import meta1.SearchModule_I;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Controller
public class MyHackerNewsController {
    private static final Logger logger = LoggerFactory.getLogger(MyHackerNewsController.class);
    private SearchModule_I h;

    public List<HackerNewsItemRecord> hackerNewsTopStories(String searchTerms) throws Exception {
        h = (SearchModule_I) LocateRegistry.getRegistry(2000).lookup("Search_Module");
        String[] searchWords = searchTerms.split(" ");
        RestTemplate restTemplate = new RestTemplate();

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        String topStoriesUrl = "https://hacker-news.firebaseio.com/v0/topstories.json";
        int[] topStoryIds = restTemplate.getForObject(topStoriesUrl, int[].class);

        List<HackerNewsItemRecord> matchedStories = new ArrayList<>();


        for (int storyId : topStoryIds) {
            String storyUrl = "https://hacker-news.firebaseio.com/v0/item/" + storyId + ".json";
            ResponseEntity<String> responseEntity = restTemplate.getForEntity(storyUrl, String.class);

            if (responseEntity.getStatusCode().is2xxSuccessful()) {
                String responseBody = responseEntity.getBody();
                try {
                    HackerNewsItemRecord story = objectMapper.readValue(responseBody, HackerNewsItemRecord.class);
                    if (story != null && story.getText() != null) {
                        String[] storyWords = story.getText().split(" ");
                        boolean isMatched = true;
                        for (String searchWord : searchWords) {
                            boolean wordFound = false;
                            for (String storyWord : storyWords) {
                                if (storyWord.equalsIgnoreCase(searchWord)) {
                                    wordFound = true;
                                    break;
                                }
                            }
                            if (!wordFound) {
                                isMatched = false;
                                break;
                            }
                        }
                        if (isMatched) {
                            matchedStories.add(story);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        for(HackerNewsItemRecord s:matchedStories){
            if(s.getUrl() != null){
                h.addUrl(s.getUrl());
            }
            
        }

        return matchedStories;
    }


    public List<HackerNewsItemRecord> getStoriesFromUser(String username) throws Exception {
        RestTemplate restTemplate = new RestTemplate();

        // Get the submitted stories URL for the user
        String userUrl = "https://hacker-news.firebaseio.com/v0/user/" + username + ".json";
        String userJson = restTemplate.getForObject(userUrl, String.class);

        List<HackerNewsItemRecord> userStories = new ArrayList<>();
        h = (SearchModule_I) LocateRegistry.getRegistry(2000).lookup("Search_Module");

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

            JsonNode userNode = objectMapper.readTree(userJson);
            JsonNode submittedNode = userNode.get("submitted");

            if (submittedNode != null && submittedNode.isArray()) {
                for (JsonNode idNode : submittedNode) {
                    int storyId = idNode.asInt();

                    // Retrieve individual story using the story ID
                    String storyUrl = "https://hacker-news.firebaseio.com/v0/item/" + storyId + ".json";
                    ResponseEntity<String> responseEntity = restTemplate.getForEntity(storyUrl, String.class);

                    if (responseEntity.getStatusCode().is2xxSuccessful()) {
                        String storyJson = responseEntity.getBody();
                        HackerNewsItemRecord storyData = objectMapper.readValue(storyJson, HackerNewsItemRecord.class);

                        // Check if the storyData is not null and has necessary fields
                        if (storyData != null && storyData.getType() != null && storyData.getType().equals("story") && storyData.getText() != null && !storyData.getText().isEmpty()) {
                            userStories.add(storyData);
                        }
                    }
                }
            } else {
                // Handle the case where the user hasn't submitted anything
                logger.info("User {} hasn't submitted any stories.", username);
            }
        } catch (IOException e) {
            // Handle exception if JSON parsing fails
            logger.error("Error parsing user JSON: {}", e.getMessage());
        }
        for(HackerNewsItemRecord s:userStories){
            if(s.getUrl() != null){
                h.addUrl(s.getUrl());
            }
            
        }
        return userStories;
    }

    @GetMapping("/hackernews")
    public String goToHackerNewsView(){
        
        return("hackernews");
    }

    @PostMapping("/hackernews")
    public void choose_function(@RequestParam("query") String query,
    @RequestParam("searchType") String searchType, Model model){

        System.out.println(searchType);
        if(searchType.equals("stories")){
            topStories(query,model);
        }
        else{
            getUsers(query,model);
        }
    }
    

    public String topStories(String query, Model model) {
        System.out.println("[TOP STORIES] Queried: " + query);
    
        try {
            List<HackerNewsItemRecord> results = hackerNewsTopStories(query);
    
            for (HackerNewsItemRecord asd : results) {
                System.out.println(asd.getUrl());
            }
            model.addAttribute("searchResults", results);
        } catch (Exception e) {
            // Handle the exception appropriately (e.g., log the error, show an error message)
            e.printStackTrace();
        }
    
        return "hackernews";
    }

    public String getUsers(String query, Model model) {
        System.out.println("[USERS]Queried: " + query);
    
        try {
            List<HackerNewsItemRecord> results = getStoriesFromUser(query);
    
            for (HackerNewsItemRecord asd : results) {
                System.out.println(asd.getTitle());
            }

            model.addAttribute("searchResults", results);
        } catch (Exception e) {
            // Handle the exception appropriately (e.g., log the error, show an error message)
            e.printStackTrace();
        }

        
    
        return "hackernews";
    }


}
 