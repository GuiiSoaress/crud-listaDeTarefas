package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import model.Tarefa;
import util.ConnectionFactory;

public class TarefaDAO {
    
    //-------------------------------------
    // GET /tarefas (todas as tarefas)
    //-------------------------------------

    public List<Tarefa> buscarTodos(){
        List<Tarefa> tarefas = new ArrayList<>();

        //Query sql armazenada numa string
        String sql = "SELECT * FROM tarefas";

        try (
            Connection conn = ConnectionFactory.getConnection(); 
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery()       
        ) {
            //mapeaia um registro em um objeto tarefa (enquanto houver registros)
            while(rs.next()){
                Tarefa tarefa = new Tarefa(
                    rs.getLong("id"),
                    rs.getString("descricao"),
                    rs.getBoolean("concluida") 
                );
                tarefas.add(tarefa);
            }
            

        } catch (SQLException e) {
            System.out.println("Erro ao buscar tarefas: " + e.getMessage());
            e.printStackTrace();
        }

        return tarefas;
    }

    // --------------------------------------------
    // GET /tarefas/id 
    // -------------------------------------------

    public Tarefa buscarPorId(Long id){
        
        Tarefa tarefa = null;
        
        String sql = "SELECT id, descricao, concluida FROM tarefas WHERE id = ?";

        try (
            Connection conn = ConnectionFactory.getConnection(); 
            PreparedStatement stmt = conn.prepareStatement(sql);
        ) {
            stmt.setLong(1, id);
            
            try(ResultSet rs = stmt.executeQuery()){
                // mapeia um registro em objeto tarefa (Somente um registro)
                if(rs.next()){
                    tarefa = new Tarefa(
                    rs.getLong("id"),
                    rs.getString("descricao"),
                    rs.getBoolean("concluida") 
                    );
                }
            }
        } catch (SQLException e) {
           System.out.println("Erro ao buscar a tarefa. ID: " + id);
           System.out.println(e.getMessage());
           e.printStackTrace();
        }

        return tarefa;
    }

    // ------------------------------------
    // CREATE
    // ------------------------------------

    public void inserir(Tarefa tarefa){

        String sql = "INSERT INTO tarefas(descricao, concluida) VALUES (?,?)"; // ? = placeholder

        try (
            Connection conn = ConnectionFactory.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)
        ) {
            //adiciona valores aos placeholders
            stmt.setString(1, tarefa.getDescricao());
            stmt.setBoolean(2, tarefa.isConcluida());

            stmt.executeUpdate();

            try(ResultSet rs = stmt.getGeneratedKeys()){
                if(rs.next()){
                    tarefa.setId(rs.getLong(1));
                }
            }

        } catch (SQLException e) {
            System.out.println("Erro ao inserir a tarefa: " + tarefa.getDescricao());
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

     // ------------------------------------
    // Update
    // ------------------------------------

    public void atualizar(Tarefa tarefa){

        String sql = "UPDATE tarefas SET descricao = ?, concluida = ? WHERE id = ?";

        try (
            Connection conn = ConnectionFactory.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)
        ) {
            stmt.setString(1, tarefa.getDescricao());
            stmt.setBoolean(2, tarefa.isConcluida());
            stmt.setLong(3, tarefa.getId());
            
            int linhasAfetadas = stmt.executeUpdate();
            System.out.println("Tarefa iD: " + tarefa.getId() + " Atualizado");
            System.out.println("Linhas afetadas: " + linhasAfetadas);

        } catch (SQLException e) {
            System.out.println("Erro ao atualizar a tarefa: " + tarefa.getDescricao());
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    // -----------------------------------------------------------
    // DELETE 
    // -----------------------------------------------------------

    public void deletar(Long id){
        String sql = "DELETE FROM tarefas WHERE id = ?";

        try (
            Connection conn = ConnectionFactory.getConnection(); 
            PreparedStatement stmt = conn.prepareStatement(sql)  
        ) {
           stmt.setLong(1, id);
            
           int linhasAfetadas = stmt.executeUpdate();
           System.out.println("Produto Excluido");
           System.out.println("Linhas afetadas: " + linhasAfetadas);
        
        } catch (SQLException e) {
            System.out.println("Erro ao excluir a tarefa ID: " + id);
            System.out.println(e.getMessage());
            e.printStackTrace();
        }

    }



}
