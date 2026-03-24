// Online Java Compiler
// Use this editor to write, compile and run your Java code online
import java.util.*;

class Main {
    public static void main(String[] args) {
        System.out.println("Try programiz.pro");
        
      
      // abbaca → Output: ca
      String str="abbaca";
      Stack<Character>stack=new Stack<>();
      
      for(int i=0;i<str.length();i++)
      {
          if(!stack.isEmpty()&& stack.peek()==str.charAt(i))
          {
              
             stack.pop();
          }else{
              stack.push(str.charAt(i));
          }
      }
      
      StringBuilder sb =new StringBuilder();
      for(Character ch:stack)
      {
          sb.append(ch);
      }
      System.out.println(sb);
      
    }
}
