// Item.java
// Anthony de Bem 2021

import java.util.*;

// this class represents an item within a restaurant's menu
public class Item {
    String name = "";
    String category = "";
    List<String> keywords;

    Item(String n, String c, List<String> k){
        name = n;
        category = c;
        keywords = k;
    }

    String getName(){
        return name;
    }

    String getCategory(){
        return category;
    }

    List<String> getKeywords(){
        return keywords;
    }
}
