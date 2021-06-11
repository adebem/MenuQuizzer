// Test.java
// Anthony de Bem 2021

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;

// this class keeps track of the information for the quiz that the user takes
class Test{

    Menu test_menu;
    List<Item> correct = new ArrayList<>();
    List<Item> incorrect = new ArrayList<>();
    int question_number  = 1;
    int correct_count = 0;
    boolean instructions_given = false;
    boolean quit = false;
    boolean restart  = false;
    boolean elimination = false;
    Scanner user_input = new Scanner(System.in);
    String format_line  = "_______________________________________";
    String key;
    int question_type;

    Test() {
        startGame();
    }


    void startGame() {
        String game_category = "All";
        String main_menu = format_line + "\n\nenter: Elimination\nf: Free Play\np: Pick Test Category\nq: Quit\n";

        System.out.print(main_menu);
        String input = getInput();


        while (!input.equals("q")) {
            while ( !( input.isEmpty() || input.equals("f") ||  input.equals("p")) ) {
                System.out.print("Invalid input. Try again.\n");
                input = getInput();
            }

            if (input.equals("p")) {
                game_category = getGameCategory();
                System.out.print(main_menu);
                input = getInput();
            } else {
                if (input.isEmpty()) {
                    elimination = true;
                }
                test_menu = new Menu(game_category);
                break;
            }
        }
    }


    public String getInput() {
        return user_input.nextLine().toLowerCase().strip();
    }


    // if the user wishes to be quizzed on a certain category,
    // they must indicate which category that they want to be tested on
    public String getGameCategory() {
        String prompt = "\nSelect Test Category\n   0. Acai Bowl\n   1. Cheese Foam Top\n   2. Coffee\n   3. Frappe\n   4. Frosty Milk\n   5. Iced Fruit Tea\n   6. Hot Beverage\n   7. Iced Tea\n   8. Milk Tea\n   9. Shake\n   10. Shaved Ice\n   11. Slush\n   12. Smoothie\n   13. All\n\n";
        String[] categories = {"Acai Bowl", "Cheese Foam", "Coffee", "Frappe", "Frosty Milk", "Fruit Tea", "Hot Beverage", "Iced Tea", "Milk Tea", "Shake", "Shaved Ice", "Slush", "Smoothie", "All"};

        System.out.print(prompt);
        System.out.print("Category Number: ");

        String input = getInput();

        System.out.print("\n");

        while (!"012345678910111213".contains(input)) {
            System.out.print("Invalid Number. Try again.\n\n"+ prompt);
            input = getInput();
        }

        int i = Integer.parseInt(input);

        return categories[i];
    }


    public void runTest() {
        if (!quit ) {
            while ( !quit & (test_menu.items.size() > 0) ) {
                if (restart) {
                    restartGame();
                }

                if (!(instructions_given)) {
                    System.out.print("At any time, enter \"q\" to quit or \"r\" to restart\n" + format_line + "\n\n");
                    instructions_given = true;
                }

                askQuestion();
            }

            if ( (test_menu.items.size() == 0) & (question_number > 1) ) {
                endGame();
            }

        } else {
            user_input.close();
        }
    }


    public void restartGame() {
        System.out.print(format_line + "\nAre you sure you want to restart?\ny: yes, n: no\n\n");
        question_number -= 1;
        String user_answer = getInput();

        while ( !(user_answer.equals("y") || user_answer.equals("n")) ) {
            user_answer = getInput();
        }

        if (user_answer.equals("y")) {
            System.out.print("\nRestarting Game...\n" + format_line + "\n\n");

            correct = new ArrayList<>();
            incorrect = new ArrayList<>();
            question_number = 1;
            correct_count = 0;
            restart = false;

            startGame();
        }

        System.out.print(format_line + "\n");
    }

    // randomly picks a type of question to ask the user. These types include:
    //     type 0: name-based question
    //     type 1: category-based question
    //     type 2: keyword-based question
    public void askQuestion() {
        boolean no_keywords = test_menu.keywords.isEmpty();
        Random r = new Random();

        if (no_keywords){
            question_type = r.nextInt(2);
        } else {
            question_type = r.nextInt(3);
        }

        printQuestionHeader();

        if (question_type == 0) {
            nameQuestion();
        } else {
            multipleChoice();
        }

     /* } else if (question_type == 1) {
        categoryQuestion();
     } else if (question_type == 2) {
        keywordQuestion();
     } */

        if (elimination & !restart) {
            String stats =  "%d Correct\n%d Incorrect\n%d To Go\n" + format_line + "\n";
            System.out.printf(stats, correct.size(), incorrect.size(), test_menu.items.size());
        }

        System.out.print("\n");
    }


    public void printQuestionHeader() {
        String question_header =  "\n%d. ";

        if (question_type == 0) {
            question_header += "Name That Item\n\n";
        }

        System.out.printf(question_header, question_number);
        question_number += 1;
    }


    public void nameQuestion() {
        boolean right = false;
        Item correct_answer = findRandomItem(test_menu.items);
        String prompt_str = promptString(correct_answer);
        System.out.print(prompt_str);

        String user_answer = getInput();

        if (continueGame(user_answer)) {
            while ( !user_answer.equals("g") ) {
           /* System.out.print("correct_answer.getName().toLowerCase(): " + correct_answer.getName().toLowerCase());
           System.out.print("\n user_answer: " + user_answer);
           System.out.print("\n perfect_match: " + perfect_match + "\n");*/
                if (checkForMatch(correct_answer, user_answer)) {
                    System.out.print("\nCORRECT!\n");
                    correctAnswer(correct_answer);
                    right = true;
                    correct_count += 1;
                    break;
                } else {
                    System.out.print( wrongAnswerPrompt(user_answer, prompt_str));
                    user_answer = getInput();
                }
            }

            if (!right) {
                incorrectAnswer(correct_answer);
                System.out.printf("\nCorrect Answer: %s\n\n", correct_answer.getName() );
            }
        }
    }


    public Item findRandomItem(List<Item> item_list) {
        Random r = new Random();
        int index = r.nextInt(item_list.size());

        return item_list.get(index);
    }


    String promptString(Item correct_answer) {
        String prompt_str = String.format("    Category: %s\n\n",
                correct_answer.getCategory().replace("_", " "));
        String new_hints =  displayHints(correct_answer);
        if (!prompt_str.contains(new_hints)) {
            prompt_str += new_hints;
        }
        return prompt_str +  "Answer: ";
    }


    public String displayHints(Item answer) {
        String hint_string = "";

        if (question_type == 0) {
            hint_string += String.format("    Starts with: \"%s\"\n", answer.getName().charAt(0));
        }

        hint_string += getKeywords(answer);

        if (question_type == 0) {
            hint_string += "\n";
        }

        return hint_string;
    }


    public String getKeywords(Item answer) {

        List<String> keyword_list = new ArrayList<>(answer.getKeywords());

        if ( (question_type == 2) & (keyword_list.contains(key)) ) {
            keyword_list.remove(key);
        }

        if (keyword_list.isEmpty()){
            return "";
        }

        StringBuilder keywords = new StringBuilder();

        if (question_type == 0) {
            keywords.append("\n    ");
        }
        else if (question_type == 2) {
            String category_str = String.format("Category: %s\n", answer.getCategory().replace("_", " "));
            keywords.append(category_str);
        }

        keywords.append("Keyword(s):");

        for (String keyword : keyword_list) {
            String keyword_str = String.format(" %s,", keyword.replace("_", " "));
            keywords.append(keyword_str);
        }

        return ( keywords.substring(0, (keywords.length() - 1) )  + "\n" );
    }


    // makes sure that the user has not  indicated that they no longer want to continue the test
    public boolean continueGame(String input) {
        boolean continue_game = true;

        if ( (input.equals("q")) || (input.equals("r")) ) {
            continue_game = false;

            if (input.equals("q")) {
                quit = true;
            } else {
                restart = true;
            }
        }

        return continue_game;
    }

    public boolean checkForMatch(Item given_answer, String user_answer) {
        boolean perfect_match = given_answer.getName().toLowerCase().strip().equals(user_answer);
        boolean near_match = (checkNearMatch(given_answer, user_answer) );

        return (perfect_match || near_match);
    }


    public boolean checkNearMatch(Item i, String user_answer) {
        if (question_type == 2) {
            return false;
        }

        String item_name = i.getName().toLowerCase();
        int category_index = item_name.length();
        String category = i.getCategory().toLowerCase().strip().replace("_", " ");
        int category_length = category.length();

        /*
        System.out.print("item_name: [" + item_name + "] \n");
        System.out.print("category: [" + category + "] \n\n");
        System.out.print("item_name.contains(category): [" + item_name.contains(category) + "] \n\n");
        */

        if (item_name.contains(category)) {
            int potential_index = (item_name.length() - category_length);

            if (!item_name.substring(0, potential_index).contains(i.getCategory().toLowerCase())) {
                category_index = potential_index;
            }
        }

        String w_o_category = item_name.substring(0, category_index).strip();

        /*
        if (!(correct.contains(i))) {
            System.out.print("w_o_category: [" + w_o_category + "] \n");
            System.out.print("user_answer: [" + user_answer + "] \n\n");
        }
        */

        return (w_o_category.equals(user_answer));

    }


    public void correctAnswer(Item correct_answer) {
        if (elimination)
        {
            updateStats(true, correct_answer);
            test_menu.deleteItem(correct_answer);
        }
    }

    public void incorrectAnswer(Item correct_answer) {
        if (elimination)
        {
            updateStats(false, correct_answer);
        }
    }


    public void updateStats(boolean right, Item correct_answer) {
        if (right) {
            correct.add(correct_answer);

            if (incorrect.contains(correct_answer)) {
                incorrect.remove(correct_answer);
            }
        } else {
            incorrect.add(correct_answer);

        }
    }


    // helps the user narrow down their answer if they've already gotten an answer correct
    // or if the user guesses a wrong answer that still fits the prompt
    public String wrongAnswerPrompt(String user_answer, String prompt_str) {

        for ( Item given_answer : correct ) {
        /*
        System.out.print("\n given_answer.getName().toLowerCase(): " + given_answer.getName().toLowerCase());
        System.out.printf("\n boolean perfect_match: %b\n" + perfect_match);
        System.out.printf("\n near_match: %b\n" + near_match + "\n\n"); */

            if (checkForMatch(given_answer, user_answer)) {
                return "You already got that one correct! Think of something else!\nAnswer (Give up: g): ";
            }
        }

        if (!prompt_str.equals("")) {
            for (Item potential_match : test_menu.items) {

                if ( checkForMatch(potential_match, user_answer) & prompt_str.equals(promptString(potential_match)) ) {
                    return "That's not the one I'm thinking of.\nTry again (Give up: g): ";
                }
            }
        }

        return "Wrong Answer. Try Again (Give up: g): ";
    }


    public void multipleChoice() {
        Map< String, List<Item> > map;

        if (question_type == 1) {
            map = test_menu.categories;
        } else {
            map = test_menu.keywords;
        }

        key = getRandomKey(map);
        int num_questions = multipleChoicePrompt(map);

        getAnswers(num_questions, map);
    }


    public String getRandomKey( Map< String, List<Item> > map) {
        List<String> keys = new ArrayList<>( map.keySet() );
        Random r = new Random();
        int index = r.nextInt( keys.size() );

        return keys.get(index);
    }


    public int multipleChoicePrompt(Map< String, List<Item> > map) {
        /* System.out.print("\nCategory Question!\n"); */

        int quantity = getQuestionQuantity(map);

        key = key.replace("_", " ");

        if (question_type == 1) {
            if (quantity == 1) {
                System.out.printf("Name 1 %s\n\n", key);
            } else {
                String temp_key = key;
                int key_len = temp_key.length();

                if ( (key_len > 2) & (temp_key.substring(key_len-2).equals("sh")) ) {
                    temp_key += "e";
                }
                else if (temp_key.substring(key_len-1).equals("y")) {
                    temp_key = temp_key.substring(0, temp_key.length()-1) + "ies";
                }

                System.out.printf("Name %d %ss\n\n", quantity, temp_key);
            }
        } else {
            if (quantity == 1) {
                System.out.printf("Name 1 item with the keyword \"%s\"\n\n", key);
            } else {
                System.out.printf("Name %d items with the keyword \"%s\"\n\n", quantity, key);
            }
        }

        return quantity;
    }


    public int getQuestionQuantity(Map< String, List<Item> > map) {
        List<Item> possible_entries = map.get(key);
        Random r = new Random();
        int quantity = 1;

        if (possible_entries.size() >= 3) {
            quantity += r.nextInt(3);
        } else if (possible_entries.size() == 2) {
            quantity += r.nextInt(2);
        }

        return quantity;
    }


    public void getAnswers(int num_answers, Map< String, List<Item> > map) {

        List<Item> correct_answers = map.get(key.replace(" ", "_"));

        if (java.util.Objects.isNull(correct_answers)) {
            System.out.print("correct_answers IS NULL\n");
            System.out.print("map:\n");
            for (String s: map.keySet() ) {
                System.out.printf("    %s\n", s);
            }
            System.out.printf("key: %s\n\n", key);

        }

        int counter = 1;
        answerPrompt(counter, num_answers);
        String user_answer = getInput().strip();
        List<String> hints_given = new ArrayList<>();
        List<Item> answer_list = new ArrayList<>();
        answer_list.addAll(correct_answers);

        while ( continueGame(user_answer) & (counter <= num_answers) & !user_answer.equals("g")) {
            if ( user_answer.equals("h") ) { // the user needs a hint
                boolean no_hints = true;

                while(answer_list.size() != 0) {
                    Item random_item = findRandomItem(answer_list);
                    String hints = displayHints(random_item);
                    answer_list.remove(random_item);

                    if (!hints.equals("") & (!hints_given.contains(hints))) {
                        hints_given.add(hints);
                        System.out.print(hints);
                        no_hints = false;
                        answerPrompt(counter, num_answers);
                        break;
                    }
                }

                if (no_hints) {
                    if (hints_given.size() >= 1) {
                        System.out.print("No more hints!\n");
                    } else {
                        System.out.print("No hints available.\n");
                    }
                    answerPrompt(counter, num_answers);
                }

                user_answer = getInput();

            } else {
                boolean correct = false;

                    /*
                    boolean contains_key = user_answer.toLowerCase().contains(key.toLowerCase());

                    System.out.print("question_type: " + question_type + "\n");

                    if (question_type == 2) {
                        System.out.print("user_answer.toLowerCase(): " + user_answer.toLowerCase() + "\n");
                        System.out.print("key.toLowerCase(): " + key.toLowerCase() + "\n");
                        System.out.print("contains_key: " + contains_key + "\n");
                        System.out.print("user_answer.toLowerCase(): " + user_answer.toLowerCase() + "\n");
                        System.out.print("user_answer.toLowerCase(): " + user_answer.toLowerCase() + "\n");
                    }



                    if ( contains_key & (question_type == 2) ) { // if the drink entered contains the keyword
                        correct = true;
                    } else {

                    }
                    */

                for (Item correct_answer : correct_answers) {
                    String correct_key = key.replace(" ", "_");
                    boolean same_category = (correct_key.equals(correct_answer.getCategory()));
                    boolean category_condition = ( (question_type == 2) || same_category );

                    if (category_condition & checkForMatch(correct_answer, user_answer)) {
                        correctAnswer(correct_answer);
                        answer_list.remove(correct_answer);
                        correct = true;
                        break;
                    }
                }

                if (correct) {
                    counter += 1;

                    if (counter <= num_answers) {
                        answerPrompt(counter, num_answers);
                        user_answer = getInput();
                    } else {
                        System.out.print("\nCORRECT!\n");
                        correct_count += 1;
                    }

                } else {
                    System.out.print( wrongAnswerPrompt(user_answer, ""));
                    user_answer = getInput();
                }
            }
        }

        if ( user_answer.equals("g") ) { // the user has given up
            System.out.print("\nCorrect answer(s):\n");

            for (Item correct_answer : correct_answers) {
                if ( elimination & (correct_answers.size() <= 3) ) {
                    updateStats(false, correct_answer);
                }
                System.out.printf("%s\n", correct_answer.getName());
            }
            System.out.print("\n");
        }
    }


    public void answerPrompt(int counter, int num_answers) {
        if (num_answers == 1) {
            System.out.print("Answer: ");
        } else {
            System.out.printf("Answer %d of %d: ", counter, num_answers);
        }
    }

    public boolean keyInName(String Name) {
        return true;
    }

    public void endGame() {
        question_number -= 1;
        double score = ((new Double(correct_count)/ new Double(question_number)) * 100);

        System.out.print("Congratulations! You've won!\n");
        System.out.printf("You answered %d questions correctly out of %d.\n", correct_count, question_number);

        if ( (score % 1) > 0 ) {
            System.out.printf("Score: %.2f\n", score);
        }
        else {
            System.out.printf("Score: %.0f\n", score);
        }

        System.out.print("Thanks for playing!\n");
    }

}

