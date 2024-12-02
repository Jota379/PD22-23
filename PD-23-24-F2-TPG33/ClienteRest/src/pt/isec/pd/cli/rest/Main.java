package pt.isec.pd.cli.rest;

import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Scanner;

public class Main {

    public static String sendRequestAndShowResponse(String uri, String verb, String authorizationValue, String body) throws MalformedURLException, IOException {

        String responseBody = null;
        URL url = new URL(uri);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setRequestMethod(verb);
        connection.setRequestProperty("Accept", "application/xml, */*");

        if(authorizationValue!=null) {
            connection.setRequestProperty("Authorization", authorizationValue);
        }

        if(body!=null){
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "Application/Json");
            connection.getOutputStream().write(body.getBytes());
        }

        connection.connect();

        int responseCode = connection.getResponseCode();
        System.out.println("Response code: " +  responseCode + " (" + connection.getResponseMessage() + ")");

        Scanner s;

        if(connection.getErrorStream()!=null) {
            s = new Scanner(connection.getErrorStream()).useDelimiter("\\A");
            responseBody = s.hasNext() ? s.next() : null;
        }

        try {
            s = new Scanner(connection.getInputStream()).useDelimiter("\\A");
            responseBody = s.hasNext() ? s.next() : null;
        } catch (IOException e){}

        connection.disconnect();

        System.out.println(verb + " " + uri + (body==null?"":" with body: "+body) + " ==> " + responseBody);
        System.out.println();

        return responseBody;
    }

    public static void main(String[] args) {
	// write your code here;
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        try {
            while(true) {
                System.out.println("1-Login\n2-Register\n3-sair");
                switch (Integer.parseInt(in.readLine())) {
                    case 1/*login*/:
                        System.out.println("\n==========Login========\n");
                        login();
                        break;
                    case 2/*register*/:
                        System.out.println("\n========Register==========\n");
                        register();
                        break;
                    case 3/*exit*/:
                        System.out.println("Adeus o/");
                        return;
                    default:
                        System.out.println("Registe um valor valido!!!!");
                        continue;
                }
            }
        }catch (IOException e) {
            e.printStackTrace();
        }
        }

    private static void login() {
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        try{
            System.out.println("========LOGIN========");
            System.out.println("email");
            String email = in.readLine();
            System.out.println("password");
            String pass = in.readLine();

            String credentials = Base64.getEncoder().encodeToString((email+":"+pass).getBytes());
            String token = sendRequestAndShowResponse("http://localhost:8080/login", "POST","basic "+ credentials, null);


            //Deserializa a resposta recebida em socket
            if( token == null || token.isEmpty()){
                System.out.println("Utilizador não existe ou as credencias erradas");
                return;
            }/*
            if(resposta.getAdmin() == 1) {
                menuAdmin(socket, resposta, oin, oout);
            }else{
                menuPrincipal(socket,resposta,oin,oout);
            }*/
            System.out.println("Login com sucesso ");
            String[] chunks = token.split("\\.");
            Base64.Decoder decoder = Base64.getUrlDecoder();

            String header = new String(decoder.decode(chunks[0]));
            String payload = new String(decoder.decode(chunks[1]));


            JSONObject jsonObject = new JSONObject(payload);
            if(jsonObject.getString("scope").equals("ADMIN")){
                menuAdm(token);
            }else{
                menuPrinc(token);
            }
            return;

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void menuPrinc(String token) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        String[] chunks = token.split("\\.");
        Base64.Decoder decoder = Base64.getUrlDecoder();
        String header = new String(decoder.decode(chunks[0]));
        String payload = new String(decoder.decode(chunks[1]));
        JSONObject jsonObject = new JSONObject(payload);
        System.out.println("Bem vindo ao menu principal");
        System.out.println(jsonObject.getString("sub"));
        while(true) {
            System.out.println("1-Submeter codigo\n2-Ver Presencas\n0-Logout");
            switch (Integer.parseInt(in.readLine())) {
                case 1/*Sub codigo*/:
                    System.out.println("Submeter Codigo\n");
                    System.out.println("Coloque o codigo abaixo:");
                    int codigo = Integer.parseInt(in.readLine());
                    sendRequestAndShowResponse("http://localhost:8080/entrar","POST","bearer " + token,"{\"email\":\""+jsonObject.getString("sub")+"\",\"codigo\":"+codigo+"}");
                    break;
                case 2/*ver presencas*/:
                    System.out.println("As tuas presencas:");
                    System.out.println("Designacao | local | data | inicio | fim"+sendRequestAndShowResponse("http://localhost:8080/presencas?email="+jsonObject.getString("sub"),"GET","bearer " + token,null));
                    break;
                case 0/*exit*/:
                    System.out.println("Logout");
                    return;
                default:
                    System.out.println("Registe um valor valido!!!!");
                    continue;
            }
        }
    }

    private static void menuAdm(String token) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        String[] chunks = token.split("\\.");
        Base64.Decoder decoder = Base64.getUrlDecoder();
        String header = new String(decoder.decode(chunks[0]));
        String payload = new String(decoder.decode(chunks[1]));
        JSONObject jsonObject = new JSONObject(payload);

        System.out.println(jsonObject.getString("sub"));
        while(true) {
            System.out.println("Bem vindo ao menu principal\n");
            System.out.println("1-Submeter codigo\n2-Ver Presencas\n3-CriarEvento(ADMIN)\n4-EliminarEvento(ADMIN)\n5-Gerar Codigo(ADMIN)\n6-Ver Presentes em ...(ADMIN)\n7-Listar Eventos\n0-Logout");
            switch (Integer.parseInt(in.readLine())) {
                case 1/*Sub codigo*/:
                    System.out.println("Submeter Codigo\n");
                    System.out.println("Coloque o codigo abaixo:");
                    int codigo = Integer.parseInt(in.readLine());
                    System.out.println(sendRequestAndShowResponse("http://localhost:8080/entrar","POST","bearer " + token,"{\"email\":\""+jsonObject.getString("sub")+"\",\"codigo\":"+codigo+"}"));
                    break;
                case 2/*ver presencas*/:
                    System.out.println("As tuas presencas:");
                    System.out.println("Designacao | local | data | inicio | fim"+sendRequestAndShowResponse("http://localhost:8080/presencas?email="+jsonObject.getString("sub"),"GET","bearer " + token,null));
                    break;
                case 3/*Criar Evento*/:
                    System.out.println("\n========CRIAR EVENTO=======\n");
                    System.out.println("Designacao");
                    String designacao = in.readLine();
                    System.out.println("Local");
                    String local = in.readLine();
                    System.out.println("Data dd/mm/aaaa");
                    String data = in.readLine();
                    System.out.println("Hora inicio hh:mm");
                    String hora_ini = in.readLine();
                    System.out.println("Hora do termino hh:mm");
                    String hora_fim = in.readLine();
                    System.out.println(sendRequestAndShowResponse("http://localhost:8080/criarevento","POST","bearer " + token,"{\"designacao\":\""+designacao+"\",\"local\":\""+local+"\",\"data\":\""+data+"\",\"hora_ini\":\""+hora_ini+"\",\"hora_fim\":\""+hora_fim+"\"}"));
                    break;
                case 4/*elimina evento*/:
                    System.out.println("\n===========Elimina evento=======\n");
                    System.out.println("Designacao do evento?");
                    String desig_ev = in.readLine();
                    System.out.println(sendRequestAndShowResponse("http://localhost:8080/delevento?evento="+desig_ev,"POST","bearer " + token,null));
                    break;
                case 5/*Gerar codigo*/:
                    System.out.println("\n==========Gerar Codigo============\n");
                    System.out.println("escreva a designacao do evento");
                    desig_ev = in.readLine();
                    System.out.println("Coloque o codigo abaixo:");
                    codigo = Integer.parseInt(in.readLine());
                    System.out.println("Validade em mins:");
                    int validade = Integer.parseInt(in.readLine());
                    sendRequestAndShowResponse("http://localhost:8080/gerar","POST","bearer " + token,"{\"id\":"+codigo+",\"evento\":\""+desig_ev+"\",\"validade\":"+validade+",\"t\":\""+ LocalDateTime.now() +"\"}");
                    break;
                case 6/*Ve presentes em*/:
                    System.out.println("\n===========Ver Presentes em=======\n");
                    System.out.println("Designacao do evento?");
                    desig_ev = in.readLine();
                    System.out.println("EMAIL | NOME | IDENTIFICACAO"+sendRequestAndShowResponse("http://localhost:8080/presentes?evento="+desig_ev,"GET","bearer " + token,null));
                    break;
                case 7/*lista eventos*/:
                    System.out.println("\n======Eventos=========\n");
                    System.out.println("Designacao | local | data | inicio | fim"+sendRequestAndShowResponse("http://localhost:8080/eventos","GET","bearer " + token,null));
                    break;
                case 0/*exit*/:
                    System.out.println("Logout");
                    return;
                default:
                    System.out.println("Registe um valor valido!!!!");
                    continue;
            }
        }
    }

    private static void register() {
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        try {
            String response;
            System.out.println("email");
            String email = in.readLine();
            System.out.println("password");
            String pass = in.readLine();
            System.out.println("nome");
            String name = in.readLine();
            System.out.println("Identificacao");
            int id = Integer.parseInt(in.readLine());

            //Serializa a string TIME_REQUEST para o OutputStream associado a socket
            response = sendRequestAndShowResponse("http://localhost:8080/registo", "POST", null, "{\"nome\":\""+name+"\",\"id\":"+id+",\"email\":\""+email+"\",\"password\":\""+pass+"\",\"admin\":0}");

            if(response.equals("USER CRIADO COM SUCESSO")){
                System.out.println("Registo bem sucedido");
                login();
                return;}
                System.out.println("Registro Mal sucedido");
                return;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

