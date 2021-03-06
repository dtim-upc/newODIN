package edu.upc.essi.dtim.odin.models.mongo;

import edu.upc.essi.dtim.odin.config.vocabulary.SourceGraph;
import edu.upc.essi.dtim.odin.models.mongo.helpers.Attribute;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;

import java.util.Arrays;

@Getter @Setter
@NoArgsConstructor
public class Wrapper {
    @Id
    private String id;
    private String iri;
    private String name;
    private Attribute[] attributes;
    private String dataSourcesId;
    private String lavMappingId;
    private String dataSourcesLabel;

    public Wrapper(String name, Attribute[] attributes, String dataSourcesId) {
        this.name = name;
        this.attributes = attributes;
        this.dataSourcesId = dataSourcesId;
        this.lavMappingId = "";
    }

    public String getIri() {
        return SourceGraph.WRAPPER.val() + '/' + name;
    }

    @Override
    public String toString() {
        return "Wrapper{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", attributes=" + Arrays.toString(attributes) +
                ", dataSourcesId='" + dataSourcesId + '\'' +
                ", lavMappingId='" + lavMappingId + '\'' +
                ", dataSourcesLabel='" + dataSourcesLabel + '\'' +
                '}';
    }
}