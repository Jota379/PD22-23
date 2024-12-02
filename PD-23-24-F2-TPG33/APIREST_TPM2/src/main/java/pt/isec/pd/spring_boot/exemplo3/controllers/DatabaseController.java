package pt.isec.pd.spring_boot.exemplo3.controllers;

import Utils.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@RestController
public class DatabaseController {
    Connection conn = DriverManager.getConnection("jdbc:sqlite:C:/databaseTrabalho/trabalho.db");

    public DatabaseController() throws SQLException {
    }


    public synchronized Utilizador getUser(String utilizador) {
        Statement s = null;
        try {
            s = conn.createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
        try {
            ResultSet rs = s.executeQuery("SELECT * FROM UTILIZADORES WHERE email LIKE'"+utilizador+"';");
            if(rs.getString("email") == null){
                System.out.println("O email esta a null");
                s.close();
                return null;}

            Utilizador utilizador1 = new Utilizador(rs.getString("nome"), Integer.parseInt(rs.getString("id")), rs.getString("email"),
                    rs.getString("password"), Integer.parseInt(rs.getString("administrador")));
            s.close();
            return utilizador1;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @PostMapping("registo")
    public synchronized ResponseEntity registo(@RequestBody Utilizador u) {
        Statement s = null;
        try {
            s = conn.createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Erro no acesso a Base de Dados");
        }
        System.out.println(u.getEmail());
        String query = String.format("INSERT INTO UTILIZADORES (nome, id, email, password, administrador) VALUES ('%s',%d,'%s','%s',%d);",u.getNome(),u.getId(),u.getEmail(),u.getPassword(),u.getAdmin());
        if (u.getEmail() == null)
            return ResponseEntity.badRequest().body("Preencha todos os parametros.");
        try {
            if((s.executeUpdate(query))<1)
                return ResponseEntity.badRequest().body("USER NAO CRIADO");
            return ResponseEntity.ok("USER CRIADO COM SUCESSO");
        } catch (SQLException e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("USER NAO CRIADO || EMAIL JA UTILIZADO");
        }
        //return ResponseEntity.badRequest().body("USER NAO CRIADO");
    }

    @PostMapping("entrar")
    public synchronized ResponseEntity entrarNoEvento(@RequestBody UserCode uc) {
        Statement s = null;
        try {
            s = conn.createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Erro no acesso a Base de Dados");
        }
        System.out.println("A dar entrada");
        try {
            ResultSet rs = s.executeQuery("SELECT * FROM CODIGOS WHERE id LIKE "+uc.codigo+ ";");
            String e = rs.getString("evento");
            if(e == null){
                return ResponseEntity.badRequest().body("CODIGO NAO EXISTE");
            }
            System.out.println(e);
            s.close();
            try {
                s = conn.createStatement();
            } catch (SQLException ee) {
                ee.printStackTrace();
                return ResponseEntity.internalServerError().body("Erro no acesso a Base de Dados");
            }
            String query = String.format("INSERT INTO PRESENCAS (email,evento) VALUES ('%s','%s');",uc.email,e);
            if((s.executeUpdate(query))<1)
                return ResponseEntity.badRequest().body("ERRO AO INSERIR PRESENCA");
            Utilsdb.incrementVersionDB(s);
            return ResponseEntity.ok("PRESENCA REGISTADA EM "+e.toUpperCase());
        } catch (SQLException e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("ERRO AO INSERIR PRESENCA");
        }

    }

    @GetMapping("presencas")
    public synchronized ResponseEntity PresencasUtilizador(@RequestParam(value="email", required=true)String email) {
        Statement s = null;
        try {
            s = conn.createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("ERRO NO ACESSO A BASE DE DADOS");
        }
        ResultSet rs;
        System.out.println("Email = "+email+"fim");
        try {
            rs = s.executeQuery("SELECT * FROM PRESENCAS WHERE email LIKE '"+email+"';");
            List<String> les = new ArrayList<>();
            List<Evento> le = new ArrayList<>();
            while(rs.next()){
                System.out.println("Pelo nome"+rs.getString("evento"));
                String aux = rs.getString("evento");
                if(aux == null)
                    break;
                les.add(aux);
            }
            for (String ss:les) {
                System.out.println(ss);
                le.add(getEvento(ss));
            }
            String listaEv="";
            for (Evento e: le) {
                listaEv += "\n"+e.designacao+" | "+e.local+" | "+e.data+" | "+e.hora_ini+" | "+e.hora_fim;
            }
            return ResponseEntity.ok(listaEv);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Evento getEvento(String evento) {
        Statement s = null;
        try {
            s = conn.createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
        try {
            ResultSet rs = s.executeQuery("SELECT * FROM EVENTOS WHERE designacao LIKE'"+evento+"';");
            System.out.println("aqui "+rs.getString("designacao"));
            if(rs.getString("designacao") == null){
                System.out.println("A designacao esta a null");
                s.close();
                return null;}
            System.out.println("Criou evento");
            return new Evento(rs.getString("designacao"),rs.getString("local"),rs.getString("data"),
                    rs.getString("hora_ini"),rs.getString("hora_fim"));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @PostMapping("criarevento")
    public synchronized ResponseEntity registoEvento(@RequestBody Evento ev) {
        Statement s = null;
        try {
            s = conn.createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("ERRO NO ACESSO A BASE DE DADOS");
        }
        String query = String.format("INSERT INTO EVENTOS (designacao, local, data, hora_ini, hora_fim) VALUES ('%s','%s','%s','%s','%s');",ev.designacao,ev.local,ev.data,ev.hora_ini,ev.hora_fim);
        try {
            if((s.executeUpdate(query))<1) {
                s.close();
                return ResponseEntity.badRequest().body("EVENTO NAO CRIADO");
            }
            s.close();
            return ResponseEntity.ok("EVENTO CRIADO");
        } catch (SQLException e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("EVENTO NAO CRIADO || Erro na query");
        }
    }

    @PostMapping("delevento")
    public synchronized ResponseEntity eliminaEvento(@RequestParam(value="evento", required=true)String ev) {
        System.out.println("EVENTO A DELETAR= "+ev);
        Statement s = null;
        try {
            s = conn.createStatement();
        } catch (SQLException ee) {
            ee.printStackTrace();
            return ResponseEntity.internalServerError().body("ERRO NO ACESSO A BASE DE DADOS");
        }
        try {
            String query ="DELETE FROM CODIGOS WHERE evento = '"+ev+"';";
            s.executeUpdate(query);
            s.close();
            try {
                s = conn.createStatement();
            } catch (SQLException ee) {
                ee.printStackTrace();
                return ResponseEntity.internalServerError().body("ERRO NO ACESSO A BASE DE DADOS");
            }
            query ="DELETE FROM PRESENCAS WHERE evento = '"+ev+"';";
            s.executeUpdate(query);
            s.close();
            try {
                s = conn.createStatement();
            } catch (SQLException ee) {
                ee.printStackTrace();
                return ResponseEntity.internalServerError().body("ERRO NO ACESSO A BASE DE DADOS");
            }
            query ="DELETE FROM EVENTOS WHERE designacao = '"+ev+"';";
            s.executeUpdate(query);
            s.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
            return ResponseEntity.badRequest().body("Evento NAO DELETADO");
        }
        return ResponseEntity.ok().body("Evento Deletado com SUCESSO");
    }

    @GetMapping("eventos")
    public synchronized ResponseEntity listaEventos() {
        Statement s = null;
        try {
            s = conn.createStatement();
        } catch (SQLException ee) {
            ee.printStackTrace();
            return ResponseEntity.internalServerError().body("ERRO NO ACESSO A BASE DE DADOS");
        }
        ResultSet rs;
        try {
            rs = s.executeQuery("SELECT * FROM EVENTOS;");
            if(rs == null){
                return ResponseEntity.accepted().body("Nao Existe Eventos");
            }
            List<String> les = new ArrayList<>();
            List<Evento> le = new ArrayList<>();
            while(rs.next()){
                System.out.println("Pelo nome"+rs.getString("designacao"));
                String aux = rs.getString("designacao");
                if(aux == null)
                    break;
                les.add(aux);
            }
            for (String ss:les) {
                le.add(getEvento(ss));
            }
            s.close();
            String listaEv="";
            for (Evento e: le) {
                listaEv += "\n"+e.designacao+" | "+e.local+" | "+e.data+" | "+e.hora_ini+" | "+e.hora_fim;
            }
            return ResponseEntity.ok().body(listaEv);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ResponseEntity.internalServerError().body("Excepcao Na querry");
    }

    @GetMapping("presentes")
    public synchronized ResponseEntity presentesEvento(@RequestParam(value="evento", required=true)String ev) {
        Statement s = null;
        try {
            s = conn.createStatement();
        } catch (SQLException ee) {
            ee.printStackTrace();
            return ResponseEntity.internalServerError().body("ERRO NO ACESSO A BASE DE DADOS");
        }
        ResultSet rs;
        System.out.println(ev);
        try {
            rs = s.executeQuery("SELECT * FROM PRESENCAS WHERE evento LIKE '"+ev+ "';");
            if(rs == null){
                return null;
            }
            List<String> les = new ArrayList<>();
            List<Utilizador> lu = new ArrayList<>();
            while(rs.next()){
                //System.out.println("Pelo nome"+rs.getString("email"));
                String aux = rs.getString("email");
                if(aux == null)
                    break;
                les.add(aux);
            }
            for (String ss:les) {
                lu.add(getUser(ss));
            }
            String listaUser = "";
            for (Utilizador u:lu) {
                listaUser+="\n"+u.getEmail()+" | "+u.getNome()+" | "+u.getId();
            }
            return ResponseEntity.ok().body(listaUser);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ResponseEntity.badRequest().body("Erro na base de dados");
    }

    @PostMapping("gerar")
    public ResponseEntity registoCodigo(@RequestBody Codigo c) {
        Statement s = null;
        try {
            s = conn.createStatement();
        } catch (SQLException ee) {
            ee.printStackTrace();
            return ResponseEntity.internalServerError().body("ERRO NO ACESSO A BASE DE DADOS");
        }
        String query = String.format("INSERT INTO CODIGOS (id, evento, time_inicio, time_validacao) VALUES (%d,'%s','%s','%s');",c.id,c.evento,c.t,c.validade);
        try {
            if((s.executeUpdate(query))<1)
                return ResponseEntity.accepted().body("Codigo não aceite");
            return ResponseEntity.ok().body("Codigo gerado para o evento "+c.evento);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ResponseEntity.badRequest().body("Pedido não aceite | Excepsao");
    }
}
