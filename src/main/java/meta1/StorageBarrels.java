package meta1;
import java.rmi.*;
import java.rmi.registry.*;
import java.rmi.server.*;
import java.util.*;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.io.File;

public class StorageBarrels extends UnicastRemoteObject implements StorageBarrels_I, Serializable{


    static SearchModule_I sm;
	String name;
	Thread t;
	int flag;
    String[] palavras;


	StorageBarrels() throws RemoteException {
        super();
	}
    
 public static HashMap<String, HashSet<String[]>> lerArquivoObj(String nomeArquivo) throws Exception {
        
        File file = new File(nomeArquivo);
        if(file.length() == 0 ){
            System.out.println("Was empty!!");
            return new HashMap<String, HashSet<String[]>>();
        } 
        
        // Open the object file for reading
        FileInputStream fileIn = new FileInputStream(nomeArquivo);
        ObjectInputStream objIn = new ObjectInputStream(fileIn);
        
        // Read the HashMap object from the file
        HashMap<String, HashSet<String[]>> data = (HashMap<String, HashSet<String[]>>) objIn.readObject();
        
        // Close the input streams
        objIn.close();
        fileIn.close();
        
        // Use the data object as needed
        return data;

    }

	public static HashMap<String, HashSet<String>> lerArquivoUrls(String nomeArquivo) throws Exception{
        
        File file = new File(nomeArquivo);
        if(file.length() == 0 ){
            System.out.println("Was empty!!");
            return new HashMap<String, HashSet<String>>();
        } 
        
        // Open the object file for reading
        FileInputStream fileIn = new FileInputStream(nomeArquivo);
        ObjectInputStream objIn = new ObjectInputStream(fileIn);
        
        // Read the HashMap object from the file
        HashMap<String, HashSet<String>> data = (HashMap<String, HashSet<String>>) objIn.readObject();
        
        // Close the input streams
        objIn.close();
        fileIn.close();
        
        // Use the data object as needed
        
        return data;

    }


	public static HashMap<String, HashSet<String[]>> buscarHashMap( String[] argumentos,String nomeArquivo) throws Exception {
		
		HashMap<String, HashSet<String[]>> objetos= lerArquivoObj(nomeArquivo);
		HashMap<String, HashSet<String[]>> results = new HashMap<String, HashSet<String[]>>();

      

            
        for (String arg : argumentos) {
            if (objetos.containsKey(arg )){
                    results.put(arg, objetos.get(arg));
            }
        }

        File file = new File("top_10_palavras.obj");
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        for(String key : results.keySet()){
        FileInputStream fileInputStream = new FileInputStream(file);
        ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);

        HashMap<String,Integer> objectHashMap = null;
        try {
            objectHashMap = (HashMap<String,Integer>) objectInputStream.readObject();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        if (objectHashMap == null) {
            objectHashMap = new HashMap<>();
        }

        // busca a chave no HashMap do arquivo de objetos
        if (objectHashMap.containsKey(key)) {
            // incrementa o valor Integer correspondente à chave
            int value = objectHashMap.get(key);
            value++;
            objectHashMap.put(key, value);
        } else {
            // cria um novo valor Integer correspondente à chave
            objectHashMap.put(key, 0);
        }

        FileOutputStream fileOutputStream = new FileOutputStream(file);
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
        objectOutputStream.writeObject(objectHashMap);

        objectInputStream.close();
        objectOutputStream.close();

}

        return results;
    }


    public ArrayList<String> lista_10_maiores() throws Exception {
        File file = new File("top_10_palavras.obj");
        HashMap<String, Integer> palavras_top = new HashMap<>();
        ArrayList<String> lista_10_ordenada= new ArrayList<>();
        
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            System.out.println(fileInputStream);
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);

            try {
                palavras_top = (HashMap<String, Integer>) objectInputStream.readObject();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

            objectInputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (palavras_top != null) {
            Map<String, Integer> sortedMap = new LinkedHashMap<>();
            palavras_top.entrySet().stream().sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                    .forEachOrdered(entry -> sortedMap.put(entry.getKey(), entry.getValue()));

            int count = 0;
            for (String key : sortedMap.keySet()) {
                if (sortedMap.keySet().size() < 10) {
                    lista_10_ordenada.add(key);
                } else {
                    lista_10_ordenada.add(key);
                    count++;
                    if (count == 10) {
                        break;
                    }
                }
            }
        }
        else{
            System.out.println("Nunca foi efetuada uma pesquisa");
        }
        return lista_10_ordenada;
    }
   

    public HashMap<String, HashSet<String[]>> organizaLista(String[] argumentos,String nomeArquivo,String urlArquivo) throws Exception{
		HashMap<String, HashSet<String>> urlsList= lerArquivoUrls(urlArquivo);
		HashMap<String, HashSet<String[]>> results = buscarHashMap(  argumentos, nomeArquivo);
        HashMap<String, HashSet<String[]>> novoMapa = new HashMap<>();
			for (String chave : results.keySet()) {
                HashSet<String[]> valores = results.get(chave);
                for (String[] valor : valores) {
                        for (String key : urlsList.keySet()) {
                            if (valor[0].equals(key)) {
                                String[] novoValor = new String[valor.length + 1];
                                System.arraycopy(valor, 0, novoValor, 0, valor.length);
                                novoValor[3] = Integer.toString(urlsList.get(key).size());
                                valores.remove(valor);
                                valores.add(novoValor);
                            }
                        }
                    }
                    novoMapa.put(chave, valores);

                }



        return novoMapa;
            
        }


    public static void escreverHashMap(String nomeDoArquivo, String arquivoUrl) throws Exception {

        try {
            
            
            //Ir buscar os arquivos

            String url = "";
            String title = "";
            String citacao = "";
            ArrayList<String> words = new ArrayList<String>();
            ArrayList<String> links = new ArrayList<String>();

            boolean foundFirstLink = false;
            String input = readMulticast();
            
            String[] parts = input.split(";");

            for (String part : parts) {
                if (part.trim().isEmpty()) {
                    continue; // passa para a próxima iteração
                }
                String[] keyValue = part.split("\\|");
                String key = keyValue[0].trim();
                String value = keyValue[1].trim();
                

                if (key.equals("URL")) {
                    url = value;
                } else if (key.equals("title")) {
                    title = value;
                } else if (key.equals("citacao")) {
                    citacao = value;
                } else if (key.equals("words")) {
                    String[] wordParts = value.split(" ");
                    words.addAll(Arrays.asList(wordParts));
                } else if (key.equals("LINK")) {
                    if (!foundFirstLink) {
                        foundFirstLink = true;
                    } else {
                        links.add(value);
                    }
                }
            }
            /*System.out.println("URL: " + url);
            System.out.println("Title: " + title );
            System.out.println("Citacao: " + citacao );
            System.out.println("Words : " + words);
            System.out.println("Links : " + links );*/



            
            HashMap<String, HashSet<String[]>> wordMap = lerArquivoObj(nomeDoArquivo);
            HashMap<String, HashSet<String>> urlMap = lerArquivoUrls(arquivoUrl);


            FileOutputStream arquivo = new FileOutputStream(nomeDoArquivo);
            ObjectOutputStream escritor = new ObjectOutputStream(arquivo);

            FileOutputStream urls = new FileOutputStream(arquivoUrl);
            ObjectOutputStream escritorUrl= new ObjectOutputStream(urls);




            
            for (String word : words) {
                wordMap.put(word, new HashSet<String[]>());
            }

            String[] info = { url, title, citacao };
            
            for (String word : words) {                
                    wordMap.get(word).add(info);

            }
            

            /* DEBUGING ->>>
            for (String key : wordMap.keySet()) {
                HashSet<String[]> valueSet = wordMap.get(key);
                System.out.println(key + " -> ");
                for (String[] arr : valueSet) {
                    System.out.print(Arrays.toString(arr) + " ");
                }
            }
             */

            
            
            //HashMap<String, HashSet<String>> urlMap = new HashMap<String, HashSet<String>>();
            for (String link:links){
                if(urlMap.get(link) == null)
                    urlMap.put(link,new HashSet<String>());
                urlMap.get(link).add(url);
            };
            
            /* DEBUGING ->>>
            for (String key : urlMap.keySet()) {
                HashSet<String> valueSet = urlMap.get(key);
                System.out.println(key + " -> " + valueSet);
            }
            */
            
            

        
            escritorUrl.writeObject(urlMap);
            escritor.writeObject(wordMap);
            escritorUrl.close();
            escritor.close();
            urls.close();
            arquivo.close();
            System.out.println("Objeto gravado com sucesso!");


            /* 
            HashMap<String, HashSet<String>> teste = lerArquivoUrls(arquivoUrl);
            for (String key : teste.keySet()) {
                HashSet<String> valueSet = teste.get(key);
                System.out.println(key + " -> " + valueSet);
            }
            
            HashMap<String, HashSet<String[]>> teste = lerArquivoObj(nomeDoArquivo);
            for (String key : teste.keySet()) {
                HashSet<String[]> valueSet = teste.get(key);
                System.out.println(key + " -> ");
                for (String[] arr : valueSet) {
                    System.out.print(Arrays.toString(arr) + " ");
                }
                System.out.println("\n");
            }
             */

        } catch (IOException e) {
            System.out.println("Erro ao gravar objeto: " + e.getMessage());
            e.printStackTrace();
        }
    }


    public ArrayList<String[]> listaResultados(String[] argumentos,String nomeArquivo,String arquivoUrl) throws Exception {
        HashMap<String, HashSet<String[]>> dicio= organizaLista(argumentos,nomeArquivo,arquivoUrl);
        ArrayList<String[]> resultado = new ArrayList<>();


        // percorrendo os dados e adicionando as strings no ArrayList
        for (HashSet<String[]> set : dicio.values()) {
            resultado.addAll(set);
        }

        Comparator<String[]> decrescente = new Comparator<String[]>() {
            @Override
            public int compare(String[] o1, String[] o2) {
                return Integer.parseInt(o2[3]) - Integer.parseInt(o1[3]);
            }
        };

        // ordenando o ArrayList em ordem decrescente com base no índice 3
        resultado.sort(decrescente);
        return resultado;
    }

//https://www.uc.pt/international-applicants/oportunidades/linguas
    public ArrayList<String> urlsLigados(String url) throws Exception{
        
        System.out.println("User URL: "+url);

        HashMap<String, HashSet<String>> urlsList= lerArquivoUrls("urlList.obj");
        
        for (String key : urlsList.keySet()) {
            HashSet<String> valueSet = urlsList.get(key);
            System.out.println(key + " -> " + valueSet);
        }

        
        ArrayList<String> result = new ArrayList<String>();


        System.out.println("True: "+urlsList.containsKey(url));
        if (urlsList.containsKey(url)) {
            result.addAll(urlsList.get(url));
        }
        else{

            return null;
        }
        return result;

    }

	public static String readMulticast(){
		int port = 5000; 
        String group = "225.4.5.6";
        try (MulticastSocket s = new MulticastSocket(port)) {
            // join the multicast group 
            s.joinGroup(InetAddress.getByName(group));
            
            byte[] buf = new byte[2048];
            DatagramPacket pack = new DatagramPacket(buf, buf.length); s.receive(pack);
            
            //Store the data sent
            String data = new String(pack.getData());
            //System.out.println(data);
            

            s.close();

			return data;
        } catch (IOException e) {
            e.printStackTrace();}

        return group;
    }
	
    public static void main(String args[]) throws RemoteException {

    try {
        StorageBarrels barrel = new StorageBarrels();
        LocateRegistry.createRegistry(1099).rebind("Storage_Barrel", barrel);


        BarrelThread b_t1 = new BarrelThread("reader1"); // create threads
        //BarrelThread b_t2 = new BarrelThread("reader2");

        Thread t1 = new Thread(b_t1);
        //Thread t2 = new Thread(b_t2);
        t1.start();
        //t2.start();


	
		

		
    } catch (Exception e) {
        throw new RuntimeException(e);
    }
    }

    private static class BarrelThread implements Runnable{
        private BarrelThread(String name) {
            System.out.println("New Thread: " + name);
        }

        @Override
        public void run() {
            while(true){
                try {
                    escreverHashMap("Pages.obj", "urlList.obj");
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            


        }
    }
}
