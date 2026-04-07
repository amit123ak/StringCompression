// Online Java Compiler
// Use this editor to write, compile and run your Java code online

import java.util.*;

class Main {
    public static void main(String[] args) {
        
        //K-Group Removal Compression Example: Input: deeedbbcccbdaa, k=3 → Output: aa
        
        String str="deeedbbcccbdaa";
        int k=3;
        Stack <int[]> stack=new Stack<>();
        
        for(char c:str.toCharArray())
        {
            
            if(!stack.isEmpty()&& stack.peek()[0]==c)
            {
                stack.peek()[1]++;
            }else{
                
                 stack.push(new int[] {c,1});
            }
            
            if(stack.pop()[1]==k)
            {
                stack.pop();
            }
        }
        
       // StringBuillder sb=new StringBuilder();
        System.out.println(stack);
        
        
}
}
