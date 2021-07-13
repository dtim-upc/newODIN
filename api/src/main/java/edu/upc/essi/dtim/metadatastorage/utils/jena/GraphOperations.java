package edu.upc.essi.dtim.metadatastorage.utils.jena;

import edu.upc.essi.dtim.metadatastorage.config.db.JenaConnection;
import org.apache.jena.query.Dataset;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.system.Txn;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.ByteArrayInputStream;

@Component
public class GraphOperations {


    @Autowired
    private JenaConnection jenaConnection;
    private Dataset ds;

    @PostConstruct
    public void init(){
        ds = jenaConnection.getTDBDataset();
    }

    public void loadTTL(String namedGraph, String contentTTL) {
        Txn.executeWrite(ds, ()-> {
            Model graph = ds.getNamedModel(namedGraph);
            graph.read(new ByteArrayInputStream(contentTTL.getBytes()), null, "TTL");
        });

    }
}
