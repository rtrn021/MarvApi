package Pojo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Marvel {
//    int id;
    String name;
//    String description;
//    String modified;
    String available;

//    String resourceURI;

    @JsonProperty("comics")
    public void setAvailable(Map<String,Object> comics) {
        this.available = String.valueOf(comics.get("available"));
    }

//    @JsonProperty("comics.items[0]")
//    public void setResourceURI(Map<String,Object> items) {
//        this.resourceURI = String.valueOf(items.get("resourceURI"));
//    }

//    @JsonProperty("comics.items")
//    public void setResourceURI(Map<String,Object>[] items) {
//        this.resourceURI = String.valueOf(items[0].get("resourceURI"));
//    }





}
