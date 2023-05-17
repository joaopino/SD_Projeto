package meta1;

import java.rmi.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;


public interface StorageBarrels_I extends Remote {
    public ArrayList<String[]> listaResultados(String[] argumentos, String nomeArquivo, String arquivoUrl) throws Exception;
    public ArrayList<String> lista_10_maiores() throws Exception;

    public ArrayList<String> urlsLigados(String url) throws Exception;
}