package com.yf833;
import java.util.ArrayList;


public class Util {

    // L(W|C) - negative log probability
    public static double L_WC(double P_WC){
        return -1.0 * (Math.log(P_WC) / Math.log(2));
    }

    // L(C) - negative log probability
    public static double L_C(double P_C){
        return -1.0 * (Math.log(P_C) / Math.log(2));
    }

    // P(W|C) - probability of a word given category C
    public static double P_WC(String w, String c, double epsilon, ArrayList<Biography> bioslist){
        return (freq_WC(w, c, bioslist) + epsilon) / (1.0 + 2.0 * epsilon);
    }

    // P(C) - probability of category C
    public static double P_C(String c, double epsilon, int num_categories, ArrayList<Biography> bioslist){
        return (freq_C(c, bioslist) + epsilon) / (1.0 + num_categories * epsilon);
    }


    // return the fraction of biographies that are of category C
    public static double freq_C(String c, ArrayList<Biography> bioslist){
        return occ_C(c, bioslist) / (double) bioslist.size();
    }

    // return the fraction of biographies that contain the word W
    // out of the number of biographies of category C
    public static double freq_WC(String w, String c, ArrayList<Biography> bioslist){
        return occ_WC(w, c, bioslist) / (double) occ_C(c, bioslist);
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




}
