package com.yf833;
import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;


public class Main {

    public static int num_entries;
    public static final String STOPWORDS_FILE = "./stopwords.txt";
    public static HashSet<String> stopwords;
    public static ArrayList<Biography> training_bios;
    public static ArrayList<Biography> test_bios;


    public static void main(String[] args) throws FileNotFoundException {

        getInput(args[0], args[1]);

        System.out.println();


    }



    // return the number of bios of category C in that contain word W
    // (doesn't matter the number exact count of W as long is it's at least 1)
    public static int occ_WC(String w, String c, ArrayList<Biography> bioslist){
        int count = 0;
        for(Biography b : bioslist){
            if(b.description.contains(w) && b.category.equals(c)){
                count++;
            }
        }
        return count;
    }

    // return the number of bios of category C in a list of bios
    public static int occ_C(String c, ArrayList<Biography> bioslist){
        int count = 0;
        for(Biography b : bioslist){
            if(b.category.equals(c)){
                count++;
            }
        }
        return count;
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

        for(int i=0; i<num_entries; i++){
            String person = corpus_scan.nextLine().toLowerCase();
            String field = corpus_scan.nextLine().toLowerCase();
            String description = corpus_scan.nextLine().toLowerCase();

            training_bios.add(new Biography(person, field, description));
            //skip empty line
            if(corpus_scan.hasNextLine()){
                corpus_scan.nextLine();
            }
        }

        //read in the next entries into test data
        while(corpus_scan.hasNextLine()){
            String person = corpus_scan.nextLine().toLowerCase();
            String field = corpus_scan.nextLine().toLowerCase();
            String description = corpus_scan.nextLine().toLowerCase();

            test_bios.add(new Biography(person, field, description));

            //skip empty line
            if(corpus_scan.hasNextLine()){
                corpus_scan.nextLine();
            }
        }

    }



}
