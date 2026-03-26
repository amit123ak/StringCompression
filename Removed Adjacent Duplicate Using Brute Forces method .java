// Online Java Compiler
// Use this editor to write, compile and run your Java code online
import java.util.*;
class Main {
    public static void main(String[] args) {
      String str="abbacakpp";
      StringBuilder  sb =new StringBuilder(str);
      
     int  count =1;
     boolean flag =true;
      while(flag)
      {
            flag = false;   // reset each iteration
           
          
          for(int i=1;i<=sb.length();i++)
          {
              
              if(i<sb.length()&& sb.charAt(i)==sb.charAt(i-1))
              {
                  count++;
              }else{
                  
                  if(count>=2)
                  {
                      sb.delete(i-count,i);
                      count=1;
                      flag =true;
                 
                  }
                  
              }
          }
          
          
      }
      System.out.println(sb);
    
    }
}
