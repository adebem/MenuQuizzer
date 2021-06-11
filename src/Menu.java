// Menu.java
// Anthony de Bem 2021

import java.io.*;
import java.util.*;

// this class holds the information for the menu that the user is quizzed on
class Menu {
    List<Item> items = new ArrayList<>();
    Map< String, List<Item> > categories = new HashMap<>();
    Map< String, List<Item> > keywords = new HashMap<>();

    public Menu(String game_category) {
        try {
            Scanner menu_text = new Scanner(new File("src/drinks.txt" ) );

            String line = findFirstLine(game_category.toLowerCase(), menu_text);

            String category_keywords = "";
            String current_category = game_category;

            // parse each line: (retrieve item information, add item to appropriate keyword and category maps)
            while( (game_category.equals("All")) || !line.isEmpty() ){
                int delimiter_index;

                if (line.contains("Category: ")) {
                    category_keywords = "";
                    delimiter_index = line.indexOf("|");
                    current_category = line.substring(10, delimiter_index);

                    if (delimiter_index < line.length()){
                        category_keywords = line.substring(delimiter_index + 1);
                    }
                } else if ( !line.isEmpty() ) {
                    String name;
                    String keywords = "";

                    if (line.contains("|")) {
                        delimiter_index = line.indexOf("|");
                        name = line.substring(0, delimiter_index).strip();

                        if ( delimiter_index < line.length()) {
                            keywords = category_keywords + line.substring(delimiter_index + 1) ;
                        }

                        addItem(name, current_category, keywords);
                    }
                }

                if (!menu_text.hasNextLine()) {
                    break;
                }

                line = menu_text.nextLine();
            }

            menu_text.close();

            // if an item's name or category contains a keyword:
            //     add it to that keyword's entry in the keyword map
            for (Item i : items) {
                String name = i.getName().strip().toLowerCase();
                String category = i.getCategory().strip().toLowerCase().replace("_", " ");

                Set<String> key_set = keywords.keySet();

                for (String keyword : key_set) {
                    String temp_keyword = keyword.toLowerCase().replace("_", " ");
                    boolean name_contains = name.contains(temp_keyword);
                    boolean category_contains = category.contains(temp_keyword);

                    if (name_contains || category_contains) {
                        keywords.get(keyword).add(i);
                    }
                }
            }
        }
        catch (FileNotFoundException e) {
            System.out.print(e.getMessage());
        }
    }


    // finds the first line of the menu file  that needs to be read
    // this line depends on the game mode
    String findFirstLine(String game_mode, Scanner menu_text){
        if (!game_mode.equals("all")) { // find the line that reads Category: [category name]
            String line = "";
            boolean found_line = false;

            while ( (!found_line) & menu_text.hasNextLine()) {
                line = menu_text.nextLine();

                if ((line.toLowerCase()).contains("category: " + game_mode)) {
                    found_line = true;
                }
            }

            return line;
        } else {
            return menu_text.nextLine();
        }
    }


    void addItem(String name, String category, String k){
        List<String> keywords_list;
        category = category.strip().replace(" ", "_");

        if (k.isEmpty()) {
            keywords_list = new ArrayList<>();
        } else {
            String[] keywords = k.strip().split(" ");
            keywords_list = Arrays.asList(keywords);
        }

        Item new_item = new Item(name, category, keywords_list);

        addToCategory(new_item);
        addToKeywordsMap(new_item);
        items.add(new_item);
    }


    void deleteItem(Item d){
        if ( items.contains(d) ){
            items.remove(d);

            String category = d.getCategory();

            categories.get( category ).remove(d);

            if ( categories.get( category ).isEmpty() ){
                System.out.printf("\"%s\" category cleared!\n", category.replace("_", " "));
                categories.remove( category );
            }

            for (String keyword : d.getKeywords() ) {
                if (keywords.containsKey(keyword)){
                    keywords.get( keyword).remove(d);

                    if ( keywords.get(keyword).isEmpty() ) {
                        keywords.remove(keyword);
                    }
                }
            }

            String d_name = d.getName().strip().toLowerCase();
            String d_category = d.getCategory().strip().toLowerCase().replace("_", " ");

            Set<String> key_set = new HashSet<>(keywords.keySet());

            for (String keyword : key_set) {
                String temp_keyword = keyword.toLowerCase().replace("_", " ");
                boolean name_contains = d_name.contains(temp_keyword);
                boolean category_contains = d_category.contains(temp_keyword);

                if (name_contains || category_contains) {
                    keywords.get(keyword).remove(d);

                    if ( keywords.get(keyword).isEmpty() ) {
                        keywords.remove(keyword);
                    }
                }
            }
        }
        else
        {
            System.out.print("That item does not exist\n");
        }
    }


    void addToCategory(Item new_item){
        String new_category = new_item.getCategory();

        if (!categories.containsKey( new_category )){
            List<Item> item_list = new ArrayList<>();
            categories.put( new_item.getCategory(), item_list );
        }

        categories.get(new_category).add(new_item);
    }


    void addToKeywordsMap(Item new_item){
        for(String keyword : new_item.getKeywords() ){
            boolean starts_with =  ( ( keyword.length() >= 11) & keyword.contains("Starts with") );

            if ( (keyword.length() > 0) & !starts_with) {
                if ( !(keywords.containsKey( keyword )) ){
                    List<Item> item_list = new ArrayList<>();

                    keywords.put( keyword, item_list );
                }

                if ( !(keywords.get(keyword).contains(new_item) ) ) {
                    keywords.get(keyword).add(new_item);
                }
            }
        }


    }
}
