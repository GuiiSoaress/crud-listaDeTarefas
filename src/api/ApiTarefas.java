package api;

import static spark.Spark.after;
import static spark.Spark.delete;
import static spark.Spark.get;
import static spark.Spark.port;
import static spark.Spark.post;
import static spark.Spark.put;

import com.google.gson.Gson;

import dao.TarefaDAO;
import model.Tarefa;
import spark.Filter;
import spark.Request;
import spark.Response;
import spark.Route;

public class ApiTarefas {
    
    private static final TarefaDAO dao = new TarefaDAO();
    private static final Gson gson = new Gson();

    private static final String APPLICATION_JSON = "application/json";

    public static void main(String[] args) {
        port(4567);

        after(new Filter(){
            @Override
            public void handle(Request request, Response response){
                response.type(APPLICATION_JSON);

            }
        });

        // GET /tarefas - Buscar Todos
        get("/tarefas", new Route(){
            @Override
            public Object handle(Request request, Response response){
                return gson.toJson(dao.buscarTodos());
            }
        });

        //GET /tarefas/id
        get("/tarefas/:id", new Route(){
            @Override
            public Object handle(Request request, Response response){
                try {
                    Long id = Long.parseLong(request.params(":id"));

                    Tarefa tarefa = dao.buscarPorId(id);

                    if(tarefa != null){
                        return gson.toJson(tarefa);
                    }else{
                        response.status(404);
                        return "{\"mensagem\": \"Tarefa com ID " + id + "  Não encontrado\"}";
                    }
                } catch (NumberFormatException e) {
                    response.status(400);
                    return "{\"mensagem\": \"Id Invalido\"}";
                }
            }
        });

        // POST /tarefas - Criar nova tarefa
        post("/tarefas", new Route() {
            @Override
            public Object handle(Request request, Response response){
                try {
                    Tarefa novaTarefa = gson.fromJson(request.body(), Tarefa.class);
                    dao.inserir(novaTarefa);

                    response.status(201);
                    return gson.toJson(novaTarefa);

                } catch (Exception e) {
                    response.status(500);
                    System.out.println("Erro ao processar requisição post");
                    System.out.println(e.getMessage());
                    e.printStackTrace();
                    return "{\"mensagem\": \"Erro ao cadastrar a tarefa\"}";
                }
                
            }
        });

        // PUT /tarefas/:id - Atualizar produto existente
        put("/tarefas/:id", new Route() {
            @Override
            public Object handle(Request request, Response response) {
                try {
                    Long id = Long.parseLong(request.params(":id")); // Usa Long

                    if (dao.buscarPorId(id) == null) {
                        response.status(404);
                        return "{\"mensagem\": \"Produto não encontrado para atualização.\"}";
                    }

                    Tarefa tarefaParaAtualizar = gson.fromJson(request.body(), Tarefa.class);
                    tarefaParaAtualizar.setId(id); // garante que o ID da URL seja usado

                    dao.atualizar(tarefaParaAtualizar);

                    response.status(200); // OK
                    return gson.toJson(tarefaParaAtualizar);

                } catch (NumberFormatException e) {
                    response.status(400); // Bad Request
                    return "{\"mensagem\": \"Formato de ID inválido.\"}";
                } catch (Exception e) {
                    response.status(500);
                    System.err.println("Erro ao processar requisição PUT: " + e.getMessage());
                    e.printStackTrace();
                    return "{\"mensagem\": \"Erro ao atualizar a tarefa.\"}";
                }
            }
        });

        // DELETE /tarefa/:id - Deletar um produto
        delete("/tarefas/:id", new Route() {
            @Override
            public Object handle(Request request, Response response) {
                try {
                    Long id = Long.parseLong(request.params(":id")); // Usa Long

                    if (dao.buscarPorId(id) == null) {
                        response.status(404);
                        return "{\"mensagem\": \"Tarefa não encontrado para exclusão.\"}";
                    }

                    dao.deletar(id); // Usa o Long ID

                    response.status(204); // No Content
                    return ""; // Corpo vazio

                } catch (NumberFormatException e) {
                    response.status(400);
                    return "{\"mensagem\": \"Formato de ID inválido.\"}";
                }
            }
        });

        System.out.println("API de tarefas iniciada na porta 4567. Acesse: http://localhost:4567/tarefas");

    }

}
