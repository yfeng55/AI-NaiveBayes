import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;


public class Main {

    public static final String STOPWORDS_FILE = "./stopwords.txt";
    public static double MIN_PROB = 1000.0;

    public static int num_entries;

    public static HashSet<String> categories;
    public static HashSet<String> trainingwordset;
    public static HashSet<String> stopwords;

    public static ArrayList<Biography> training_bios;
    public static ArrayList<Biography> test_bios;



    public static void main(String[] args) throws FileNotFoundException {

        getInput(args[0], args[1]);

        ///// LEARNING PHASE /////
        HashMap<String, Double> category_probs = new HashMap<>();
        HashMap<String, Double> wordcategory_probs = new HashMap<>();

        //for each category, compute P(C) and compute P(W|C) for all word-category combinations
        for(String c : categories){
            double prob_c = Util.P_C(c, categories.size(), training_bios);
            category_probs.put(c, Util.L_C(prob_c));

            //for each word in the set (and not in the stopwords set)
            for(String w : trainingwordset){
                if(!stopwords.contains(w)){
                    double prob_wc = Util.P_WC(w, c, training_bios);
                    wordcategory_probs.put(w+","+c, Util.L_WC(prob_wc));
                }
            }
        }



        ///// CLASSIFICATION PHASE /////

        //track the number of correct predictions
        int num_correct = 0;

        //for each biography B in the test set
        for(Biography b : test_bios){

            System.out.print("\n\n" + b.person + ".");

            double lowest_prob = MIN_PROB;
            String predicted_category = "";
            HashMap<String, Double> cb_probs = new HashMap<>();

            // 1. for each category, compute L(C|B) - sum of all L(W|C) probabilities for all W in B
            for(String c : categories){
                HashSet<String> viewedwords = new HashSet<>();
                //get L(C)
                double lc = category_probs.get(c);
                //get sum of all L(W|C) values
                double lwc_sum = 0.0;
                for(String w : b.description.split("\\s+")){
                    if(trainingwordset.contains(w.replaceAll("[^\\w\\s]","")) && !stopwords.contains(w.replaceAll("[^\\w\\s]","")) && !viewedwords.contains(w.replaceAll("[^\\w\\s]",""))){
                        String key = w.replaceAll("[^\\w\\s]","") + "," + c;
                        lwc_sum += wordcategory_probs.get(key);
                    }
                    viewedwords.add(w);
                }
                double lcb = lc + lwc_sum;
                cb_probs.put(c, lcb);
                //System.out.println(lcb);

                //update the lowest probability (used in recovering the actual probability)
                if(lcb < lowest_prob){
                    lowest_prob = lcb;
                    predicted_category = c;
                }
            }

            // 2. get the prediction for this bio
            System.out.print("\tPrediction: " + predicted_category + ".");
            if(predicted_category.replaceAll("[^\\w\\s]","").equals(b.category.replaceAll("[^\\w\\s]",""))){
                System.out.println("\tRight.");
                num_correct++;
            }else{
                System.out.println("\tWrong.");
            }


            // 3. recover and print actual probabilities
            double sval = 0.0;
            for(String c : categories){
                if(category_probs.get(c) < 7.0){
                    sval += Math.pow(2.0, (lowest_prob - cb_probs.get(c)));
                }
            }
            for(String c : categories){
                double actual_prob = Math.pow(2.0, (lowest_prob - cb_probs.get(c))) / sval;
                System.out.print(c + ": " + String.format("%.2f", actual_prob) + "\t\t");
            }


        }

        //print the overall accuracy of the classification phase
        double overall_accuracy = num_correct / (double) test_bios.size();
        System.out.println("\n\nOverall accuracy: " + num_correct + " out of " + test_bios.size() + " = " + overall_accuracy + ".");


    }


    // read stopwords, training corpus entries, and test corpus entries
    public static void getInput(String corpuspath, String N) throws FileNotFoundException {

        //read in the number of entries in the corpus
        num_entries = Integer.parseInt(N);

        //read in the set of stopwords
        stopwords = new HashSet<>();
        Scanner stopword_scan = new Scanner(new File(STOPWORDS_FILE), "UTF-8");
        String stopwords_str = stopword_scan.useDelimiter("\\A").next();
        stopword_scan.close();

        for(String s : stopwords_str.split("\\s+")){
            stopwords.add(s.toLowerCase());
        }


        //read in the first N entries as training data
        Scanner corpus_scan = new Scanner(new File(corpuspath), "UTF-8");
        training_bios = new ArrayList<>();
        test_bios = new ArrayList<>();


        //read in first N entries in the input file
        for(int i=0; i<num_entries; i++){
            String person = corpus_scan.nextLine().toLowerCase();
            String field = corpus_scan.nextLine().toLowerCase();
            String description = "";

            while(corpus_scan.hasNextLine()){
                String nextline = corpus_scan.nextLine().toLowerCase();
                if(nextline.isEmpty()){
                    break;
                }else{
                    description += nextline;
                }
            }

            System.out.println("person: " + person);
            System.out.println("field: " + field);
            System.out.println("description: " + description);

            training_bios.add(new Biography(person, field, description));

        }

        //read in the next entries into test data
        while(corpus_scan.hasNextLine()){
            String person = corpus_scan.nextLine();
            String field = corpus_scan.nextLine().toLowerCase();
            String description = "";
            while(corpus_scan.hasNextLine()){
                String nextline = corpus_scan.nextLine().toLowerCase();
                if(nextline.isEmpty()){
                    break;
                }else{
                    description += nextline;
                }
            }
            test_bios.add(new Biography(person, field, description));

        }



        //initialize a set of all unique categories
        categories = new HashSet<>();
        for(Biography b : training_bios){
            if(!categories.contains(b.category)){
                categories.add(b.category);
            }
        }

        //initialize a set of all unique words found in the training text
        trainingwordset = new HashSet<>();
        for(Biography b : training_bios){
            for(String s : b.description.split("\\s+")){
                if(!trainingwordset.contains(s)){ trainingwordset.add(s.replaceAll("[^\\w\\s]","")); }
            }
//            for(String s : b.person.split("\\s+")){
//                if(!trainingwordset.contains(s)){ trainingwordset.add(s); }
//            }
        }

    }



}
