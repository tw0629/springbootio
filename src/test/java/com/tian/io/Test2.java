package com.tian.io;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author David Tian
 * @desc
 * @since 2020-04-14 09:57
 */
public class Test2 {

    public static void main(String[] args) {
        List<String> seq = new ArrayList<String>(Arrays.asList("abc","abc","abc","abc","abc","abc","abc","abc","abc"));
        ngram(seq,3);
    }

    public static void ngram(List<String> seq, int n){
        StringBuilder sb = new StringBuilder("{");
        for (int i = 0;i<seq.size();i++){
            if(i==0){
                sb.append("(");
            }
            sb.append(seq.get(i)+",");
            if(i==seq.size()-1){
                sb.deleteCharAt(sb.length()-1);
                sb.append(")}");
            }else if((i+1)%n==0){
                sb.deleteCharAt(sb.length()-1);
                sb.append("),(");
            }
        }
        System.out.println(sb.toString());
    }

}
