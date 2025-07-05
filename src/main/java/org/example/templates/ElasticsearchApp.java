package org.example.templates;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;

/**
 * Class - example from the notes for connecting to ElasticSearch
 * using dependency (elasticsearch-java)
 */
public class ElasticsearchApp {

    public static void main(String[] args) {
        RestClient restClient = RestClient.builder(
                new HttpHost("localhost", 9200, "http")
        ).build();

        ElasticsearchTransport transport = new RestClientTransport(
                restClient,
                new JacksonJsonpMapper()
        );

        ElasticsearchClient client = new ElasticsearchClient(transport);

        try {
            System.out.println("Elasticsearch version: " +
                    client.info().version());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                transport.close();
                restClient.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
