/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package http_server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import static java.lang.System.out;
import java.net.Socket;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.*;
import sun.misc.IOUtils;

/**
 *
 * @author rnavarro
 */
public class HTTPService implements Runnable {

    private Socket clientSocket;
    private static final Logger LOG = Logger.getLogger(HTTPService.class.getName());
    PrintStream out;
    BufferedReader in = null;
    public HTTPService(Socket c) throws IOException {
        clientSocket = c; 
        out = new PrintStream(clientSocket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
    }

    @Override
    public void run() {
        try {
            String requestLine;
            String commandLine = null;
           
            // leer la solicitud del cliente
            while ((requestLine = in.readLine()) != null) {              
     
                if(requestLine.startsWith("GET")){
                    LOG.info(requestLine);
                    commandLine = requestLine;                   
                }               
                //Si recibimos linea en blanco, es fin del la solicitud
                if( requestLine.isEmpty() ) {
                    break;
                }
            }
            
            String fileName = null;
            if(commandLine == null){
                System.out.println("Comando de linea nulo");
            } else{
                String tokens[] = commandLine.split("\\s+");
            
                fileName = tokens[1].substring(tokens[1].lastIndexOf('/')+1);
                System.out.println(tokens[0]);
                System.out.println(tokens[1]);                
                System.out.println(tokens[2]);
            if(fileName.length() == 0){
                fileName = "index.html";
                
            }
            }
            
            System.out.println(fileName);
            if(!(fileName==null)){
            if(!(fileName.contains(".php?"))){                         
            Path fp = Paths.get(fileName);
            
            File filePointer = fp.toFile();
            
            if(!filePointer.exists()){
                System.out.println("Fallo");
            }else 
                if(fileName.endsWith(".ico")||fileName.endsWith(".png")||fileName.endsWith(".jpg")||fileName.endsWith(".gif")){
                    LOG.info("IMAGE");
                    sendIMGFile(filePointer);
                
                }else{                  
                    LOG.info("TEXT");
                    sendText(filePointer);
                }                      
            }else{
                String Arg[]= fileName.split("\\?+");
                String Arg2[]= Arg[1].split("\\&+");
                CrearTexto(Arg2);               
                
               
                Path fp = Paths.get("tabla.html");           
                File filePointer = fp.toFile();
                
                LOG.info("TEXT");               
                sendText(filePointer);
            }
            }
             clientSocket.close();
            //Final           
        } catch (IOException ex) {
            System.out.println("Error en la conexion");
        } 
        
        
    }
    private void sendIMGFile(File filePointer) throws IOException{
            
            out.println("HTTP/1.1 200 OK");
            out.println(lastModified(filePointer));
        
            System.out.println(filePointer.getName());
            
            String content = getExtension(filePointer.getName());

            if( content.equals("ico")){
            out.println("Content-Type_ image/ico");
            }
            if( content.equals("png")){
            out.println("Content-Type_ image/png");
            }
            if( content.equals("jpg")){
            out.println("Content-Type_ image/jpg");
            }
            if( content.equals("gif")){
            out.println("Content-Type_ image/gif");
            }

            out.println("Content-Length: " + filePointer.length());
            out.println();

            out.flush();
            LOG.log(Level.INFO,"Content-Length: {0}" + filePointer.length());
            
            FileInputStream file;
            try{
                file = new FileInputStream(filePointer);
                int data;
                while((data=file.read()) != -1){
                    out.write(data);
                }
                out.flush();
                file.close();
                LOG.info("IMG-DONE");
            } catch(FileNotFoundException ex){
                Logger.getLogger(HTTPService.class.getName()).log(Level.SEVERE,null,ex);
            } catch (IOException ex){
                Logger.getLogger(HTTPService.class.getName()).log(Level.SEVERE,null,ex);
            }
        
    }
    private void sendText(File f) throws FileNotFoundException, IOException{
            
            out.println("HTTP/1.1 200 OK");
            out.println(lastModified(f));
            out.println("Content-Type: text/html; charset=utf-8" );
            out.println("Content-Length: "+f.length());
            out.println();
            
            FileReader  file;
            try{
                file = new FileReader(f);
            int data;        
            while( (data = file.read()) != -1 ) {
                out.write(data);    
            }
            
            out.flush();
            file.close();
            } catch(FileNotFoundException ex){
                Logger.getLogger(HTTPService.class.getName()).log(Level.SEVERE,null,ex);
            } catch (IOException ex){
                Logger.getLogger(HTTPService.class.getName()).log(Level.SEVERE,null,ex);
            }
         
    }
    private String lastModified(File f){
        long d = f.lastModified();
        Date lastModified = new Date(d);
        
        return "Last-Modified: " + lastModified.toString();
    }
    private String getExtension(String f){
        int p = f.lastIndexOf('.');
        return f.substring(p + 1);
    }
    private void CrearTexto(String [] Cadena){
            String Datos="";
            for(int i=0;i<=Cadena.length-1;i++){
                Datos=Datos+" | "+(Cadena[i]);
            }   
            Datos=Datos+" |<br>";
            try{
            File f = new File("C:\\Users\\HP\\Downloads\\Desarrollo 3\\http_server\\tabla.html");
            
            if(!f.exists()){
                f.createNewFile();
            }
            FileWriter fw = new FileWriter(f,true);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(Datos);
            bw.close();
            } catch(Exception ex){
                System.out.println(ex);
            }
    }
}
