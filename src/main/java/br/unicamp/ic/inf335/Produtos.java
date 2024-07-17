package br.unicamp.ic.inf335;

import com.mongodb.MongoException;
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

import java.util.Arrays;

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

    public void alteraValorProduto(MongoClient conn, String idProduto, String valor){

    }

    public void apagaProduto(MongoClient conn, String idProduto){

    }

    public static void main(String[] args){
        Produtos produto = new Produtos();

        MongoClient conn = produto.conectar("mongoAdmin","INF335UNICAMP","localhost:27017");

        produto.listaProdutos(conn);

        produto.insereProduto(conn,"Prod6","Celular antigo","200.0","Produto antigo");

        produto.listaProdutos(conn);
    }

}

