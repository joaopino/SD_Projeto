package meta1;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.*;

import java.net.*;

/* TODO
 * Adicionar o TCP para receber os URLS para indexar
 */

/* Protocolo UDP: 

URL | str ; title | str ; citacao | str ; words | str ; LINK | str ; LINK | str ; LINK | str ; [...]\0

 */

public class Downloader{

    static String[] stop_words;

    public static void main(String[] args) throws IOException {
        //reads the stopwords file and adds it to the array
        readFileToStringArray("src/main/java/meta1/lib/stopwords.txt");

        FIFO fifo = new FIFO();
        //fifo.addUrl("https://en.wikipedia.org/wiki/Portugal");
        fifo.addUrl("https://inforestudante.uc.pt/nonio/security/login.do");
        //fifo.addUrl("https://inforestudante.uc.pt");

        new DownloaderThread("Downloader-1",fifo);
        new DownloaderThread("Downloader-2",fifo);
        //new DownloaderThread("Downloader-3",fifo);

        //new DownloaderThread("Downloader-3",fifo);
        
        ServerSocket serverSocket = new ServerSocket(433);


        while (true) {
            Socket clientSocket = serverSocket.accept();
            
            //Receives input
            BufferedReader inFromClient = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter outFromServer = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream()), true);
            
            String message = inFromClient.readLine();
            System.out.println("Received message: " + message);

            if(message.equals("SHOW")){
                fifo.printFIFO();
            }
            
            else if(message.equals("STATUS")){
                Set<Thread> threads = Thread.getAllStackTraces().keySet();
                System.out.printf("%-15s \t %-15s \t %-15s \t %s\n", "Name", "State", "Priority", "isDaemon");
                //Gets information about the Downloader Threads
                String msg = new String();
                String msg_aux = new String();
                int count = 0;
                for (Thread t : threads) {
                    if(t.getName().equals("Downloader-1")  || t.getName().equals("Downloader-3") || t.getName().equals("Downloader-2")){
                        count++;
                        msg_aux = msg_aux +  t.getName() + " " + t.getState() + " " + t.getPriority() +" " +t.isDaemon() + " ";
                    }
                    
                }
                msg = count +" " + msg_aux;
                msg = msg.replaceAll("[^a-zA-Z0-9\\s]", "");
                outFromServer.println(msg);

            }
            else{
                fifo.addFirstUrl(message);
                System.out.println("Added URL: " + message);

            }
            
        }
        

    }
    public static void readFileToStringArray(String filePath) throws IOException {
        ArrayList<String> lines = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        String line;
        while ((line = reader.readLine()) != null) lines.add(line.strip());

        reader.close();
        String[] result = new String[lines.size()];
        stop_words =  lines.toArray(result);
    }

    public static String removeStopWords(String texto) {
        texto = texto.replace(";|", "");
        String[] words = texto.split("\\s+");
        StringBuilder fixed_text = new StringBuilder();
        for(String word_to_test : words){
            if((!Arrays.asList(stop_words).contains(word_to_test)))
                fixed_text.append(word_to_test);
                fixed_text.append(" ");
        }
        return fixed_text.toString().trim();
    }
 


    static class DownloaderThread extends Thread{
    
        String currentURL;
        String ThreadNumber;
        FIFO fifo;

        public DownloaderThread() {
            
        }
        public DownloaderThread(String string,FIFO fifo) {
            //Criação e inicio da thread
            this.ThreadNumber = string;
            this.fifo = fifo;
            new Thread(this,string).start();
        }
    
        @Override
        public void run (){
           System.out.println("Thread " + Thread.currentThread().getName() + " started!");
           while(true){

                String currentURL = new String();
                currentURL = fifo.removeURL();
            
                String all_text = new String();
                String citacao = new String();
                String words = new String();
            
                try {
                    
                Document doc = Jsoup.connect(currentURL).get();
                    StringTokenizer tokens = new StringTokenizer(doc.text());
                    
                    all_text = "URL | " + currentURL + " ; title | " + doc.title() + " ; citacao | ";
                    words = "";
                    
                    //Incluir todos os tokens do doc.text()
                    while (tokens.hasMoreElements() ){
                        words = words + tokens.nextToken().toLowerCase().replaceAll("[^a-zA-Z0-9\\s]", "") + " ";
                    }
                    
                    //Associa à citação os primeiros 10 characters das words todas
                    if(words.length() < 10) {citacao = words;}
                    else{citacao = words.substring(0,10);}
                    all_text = all_text + citacao + " ; words | ";
                    //Filtra as words para não incluir as stopwords
                    words = removeStopWords(words);
                    all_text = all_text + words + " ;";

                    
                    
                    //Guarda todos os links e adiciona ao texto para enviar pelo protocolo do UDP
                    //Todos os links que são encontrados no website são enviados para a Queue
                    Elements links = doc.select("a[href]");
                    for (Element link : links) {
                        String absHref = link.attr("abs:href");
                        if (absHref.length() >= 4 && absHref.substring(0, 4).equals("http")) {
                            all_text = all_text + " LINK | "  + absHref + " ;";
                            fifo.addUrl(absHref);
                        }
                    }



                    //Add the final character to end the protocol
                    all_text = all_text + "\0";
        
                } catch (IOException e ){
                    //e.printStackTrace();
                }
                
                int port = 5000;
                String group = "225.4.5.6"; int ttl = 1;
                
                try (MulticastSocket s = new MulticastSocket()) {
                    //System.out.println(all_text);
                    System.out.println("Thread "+Thread.currentThread().getName()+ " Scanned: "+currentURL);
                    
                    DatagramPacket pack = new DatagramPacket(all_text.getBytes(), all_text.length(),InetAddress.getByName(group), port);
                    
                    s.send(pack,(byte)ttl);
                    s.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                


           }
           
            
        }
        
    }

    static class FIFO {
        private LinkedList<String> urls;
    
        public FIFO() {
    
            urls = new LinkedList<String>();
        }
        
        //Used for a Downloader request. The URL is added as the last in the FIFO
        public synchronized void addUrl(String url) {
            //Verifica que é o URL correto para o Jsoup
            if(url.substring(0,4).equals("http")){
                urls.addLast(url);
            }
            notifyAll();
        }
        //Used for a client request. The URL is added as the first in the FIFO

        public synchronized void addFirstUrl(String url) {
            //Verifica que é o URL correto para o Jsoup
            if(url.substring(0,4).equals("http")){
                urls.addFirst(url);
            }
            notifyAll();
        }
    
        public String getNextUrl() {
            return urls.pollFirst();
        }
    
        public boolean isEmpty() {
            return urls.isEmpty();
        }
    
        public synchronized String removeURL(){
            
            //Se estiver vazio inicia uma espera
            while(urls.isEmpty()){
                try {
                    wait();
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            String URL = urls.removeFirst();
            notifyAll();
            return URL;
        }
    
        public void printFIFO(){
            //Apenas usada para testes de implementação
            for(int i = 0; i < urls.size();i++ ){
                System.out.println("URL"+i+" : "+urls.get(i));
            }
        }
    }



} 