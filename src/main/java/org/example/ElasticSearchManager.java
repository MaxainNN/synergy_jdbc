package org.example;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.Result;
import co.elastic.clients.elasticsearch.core.IndexRequest;
import co.elastic.clients.elasticsearch.core.IndexResponse;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import org.apache.http.HttpHost;
import org.bson.Document;
import org.elasticsearch.client.RestClient;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Class for manage and implement ElasticSearch
 */
public class ElasticSearchManager {

    private final ElasticsearchClient client;
    private final RestClient restClient;
    private final ElasticsearchTransport transport;

    public ElasticSearchManager(String host, int port){
        this.restClient = RestClient.builder(new HttpHost(host,port)).build();
        this.transport = new RestClientTransport(restClient, new JacksonJsonpMapper());
        this.client = new ElasticsearchClient(transport);
    }

    /**
     * Indexing document in Elasticsearch
     * @param index index name
     * @param id document id
     * @param document document for indexation
     * @return result of operation
     */
    public Result indexDocument(String index, String id, Document document) throws IOException {
        Map<String, Object> source = new HashMap<>();
        document.forEach((key, value) -> {
            if (!"_id".equals(key)) {
                source.put(key, value);
            }
        });

        IndexRequest<Map<String, Object>> request = IndexRequest.of(b -> b
                .index(index)
                .id(id)
                .document(source)
        );

        IndexResponse response = client.index(request);
        return response.result();
    }

    /**
     * Search on index
     * @param index index name
     * @param field search field
     * @param query search query
     * @return list of founded documents
     */
    public List<Document> search(String index, String field, String query) throws IOException {
        SearchRequest request = SearchRequest.of(b -> b
                .index(index)
                .query(q -> q
                        .match(m -> m
                                .field(field)
                                .query(query)
                        )
                )
        );

        SearchResponse<Document> response = client.search(request, Document.class);
        return response.hits().hits().stream()
                .map(Hit::source)
                .collect(Collectors.toList());
    }

    /**
     * Close connection
     * @throws IOException if got an error while closing
     */
    public void close() throws IOException {
        transport.close();
        restClient.close();
    }
}
