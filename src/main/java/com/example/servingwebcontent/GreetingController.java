package com.example.servingwebcontent;

import java.rmi.registry.LocateRegistry;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import javax.sound.midi.Soundbank;

import com.example.servingwebcontent.beans.Number;
import com.example.servingwebcontent.forms.Project;
import com.example.servingwebcontent.thedata.Employee;

import meta1.SearchModule_I;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Controller
public class GreetingController {
    @Resource(name = "requestScopedNumberGenerator")
    private Number nRequest;

    @Resource(name = "sessionScopedNumberGenerator")
    private Number nSession;

    @Resource(name = "applicationScopedNumberGenerator")
    private Number nApplication;

    @Bean
    @Scope(value = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
    public Number requestScopedNumberGenerator() {
        return new Number();
    }

    @Bean
    @Scope(value = WebApplicationContext.SCOPE_SESSION, proxyMode = ScopedProxyMode.TARGET_CLASS)
    public Number sessionScopedNumberGenerator() {
        return new Number();
    }

    @Bean
    @Scope(value = WebApplicationContext.SCOPE_APPLICATION, proxyMode = ScopedProxyMode.TARGET_CLASS)
    public Number applicationScopedNumberGenerator() {
        return new Number();
    }

    @GetMapping("/")
    public String redirect() {
        return "redirect:/greeting";
    }

	@GetMapping("/greeting")
	public String greeting(@RequestParam(name="name", required=false, defaultValue="World") String name, Model model) {
		model.addAttribute("name", name);
		model.addAttribute("othername", "SD");
		return "greeting";
	}

    @PostMapping("/processUrl")
    public String processUrl(@RequestParam("url") String url, Model model) throws Exception {
        
        System.out.println("Received URL: " + url);
        SearchModule_I s = (SearchModule_I) LocateRegistry.getRegistry(2000).lookup("Search_Module");
        
        s.addUrl(url);
        model.addAttribute("successMessage", true);
        return "greeting";
    }

	@GetMapping("/search")
		public String goToSearch(Model model) throws Exception {
		return "search";
	}

    @GetMapping("/login")
		public String goToLogin(Model model) throws Exception {
		return "login";
	}
    @GetMapping("/searchurl")
		public String goToSearchUrl(Model model) throws Exception {
		return "searchUrl";
	}

    @GetMapping("/admin")
	public String goToAdmin(Model model) throws Exception {

    SearchModule_I h = (SearchModule_I) LocateRegistry.getRegistry(2000).lookup("Search_Module");
    String status = h.getDownloadersStatus();
    String[] parts = status.split(" ");
    ArrayList<Status> aa = new ArrayList<>();

    for (int i = 0; i < Integer.parseInt(parts[0]); i++) {
        aa.add(new Status(parts[1 + i * 4], parts[2 + i * 4], parts[3 + i * 4], parts[4 + i * 4]));
    }

    model.addAttribute("status", aa);

    return "table";
    }


    @PostMapping("/search")
    public String processSearchParam(@RequestParam("keyword") String keyword[],Model model) throws Exception {

        SearchModule_I s = (SearchModule_I) LocateRegistry.getRegistry(2000).lookup("Search_Module");
        ArrayList<String[]> searchResult = new ArrayList<>();
        searchResult = s.sendAnswer(keyword);
        

        for (String[] elementos : searchResult ){
            model.addAttribute("searchResults",new SearchResult(elementos[2], elementos[0], elementos[1]));
        }

        System.out.println("here");


        
        //model.addAttribute("successMessage", true);
        return "search";
    }
    @PostMapping("/searchurl")
    public String processSearchUrls(@RequestParam("urlinput") String url,Model model) throws Exception {

        SearchModule_I s = (SearchModule_I) LocateRegistry.getRegistry(2000).lookup("Search_Module");
        ArrayList<String> result = new ArrayList<>();
        result = s.sendUrlConnections(url);
        
        for( String urls : result){
            System.out.println(urls);
        }
        
        model.addAttribute("searchUrlResults", s.sendUrlConnections(url));


        
        //model.addAttribute("successMessage", true);
        return "searchurl";
    }
     

    

}

