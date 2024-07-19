package br.unicamp.ic.inf335;

import com.mongodb.MongoException;
import com.mongodb.client.result.DeleteResult;
import org.bson.Document;
import org.bson.types.ObjectId;

import com.mongodb.client.model.Projections;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.conversions.Bson;
import com.mongodb.client.model.Sorts;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.result.InsertOneResult;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.UpdateResult;

import java.util.Arrays;

import static com.mongodb.client.model.Filters.eq;

public class Produtos
{
    private MongoClient conectar(String usuario, String senha, String url){

        MongoClient client = MongoClients.create("mongodb://"+ usuario +":"+ senha + "@" + url);

        return client;
    }
    public void listaProdutos(MongoClient conn){

        MongoDatabase database = conn.getDatabase("mongoAdmin");

        MongoCollection<Document> collection = database.getCollection("produtos");

        Bson projectionFields = Projections.fields(
                Projections.include("_id", "nome", "descricao","valor", "estado"),
                Projections.excludeId());

        try (MongoCursor<Document> cursor = collection.find()
                .projection(projectionFields)
                .sort(Sorts.ascending("nome")).iterator()) {
            while (cursor.hasNext()) {
                System.out.println(cursor.next().toJson());
            }
        }
    }

    public void insereProduto(MongoClient conn, String nomeProduto, String descricaoProduto, String valor, String descricao){
        MongoDatabase database = conn.getDatabase("mongoAdmin");

        MongoCollection<Document> collection = database.getCollection("produtos");

        try {
            InsertOneResult result = collection.insertOne(new Document()
                    .append("_id", new ObjectId())
                    .append("nome", nomeProduto)
                    .append("descricao",descricaoProduto)
                    .append("valor",valor)
                    .append("estado",descricao));
            System.out.println("Successo! Id do item inserido: " + result.getInsertedId());
        } catch (MongoException me) {
            System.err.println("Impossível inserir devido a erro: " + me);
        }

    }

    public void alteraValorProduto(MongoClient conn, String nome, String valor){

        MongoDatabase database = conn.getDatabase("mongoAdmin");

        MongoCollection<Document> collection = database.getCollection("produtos");

        Document query = new Document().append("nome", nome);

        Bson updates = Updates.combine(
                Updates.set("valor", valor));

        UpdateOptions options = new UpdateOptions().upsert(true);
        try{
            UpdateResult result = collection.updateOne(query, updates, options);
            System.out.println("Documentos alterados: " + result.getUpsertedId());
        } catch (MongoException me) {
            System.err.println("Erro ao alterar valor de item devido a erro:" + me);
        }
    }

    public void apagaProduto(MongoClient conn, String nome){
        MongoDatabase database = conn.getDatabase("mongoAdmin");

        MongoCollection<Document> collection = database.getCollection("produtos");
        Bson query = eq("nome", nome);
        try{
            DeleteResult result = collection.deleteOne(query);
            System.out.println("Documentos deletados: " + result.getDeletedCount());
        } catch (MongoException me) {
            System.err.println("Erro ao apagar item devido a erro:" + me);
        }
    }

    public static void main(String[] args){
        Produtos produto = new Produtos();

        MongoClient conn = produto.conectar("mongoAdmin","INF335UNICAMP","localhost:27017");
        System.out.println("=== Lista inicial de produtos. ===");
        produto.listaProdutos(conn);

        System.out.println("\n=== Inserção de um item. ===");
        produto.insereProduto(conn,"Prod6","Celular antigo","200.0","Produto antigo");

        produto.listaProdutos(conn);

        System.out.println("\n=== Alteração de valor de um item. ===");
        produto.alteraValorProduto(conn,"Prod6","500");

        produto.listaProdutos(conn);

        System.out.println("\n=== Remoção de um item. ===");
        produto.apagaProduto(conn,"Prod6");

        produto.listaProdutos(conn);
    }

}

