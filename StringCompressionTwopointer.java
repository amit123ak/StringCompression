// Online Java Compiler
// Use this editor to write, compile and run your Java code online
import java.util.*;
class Main {
    public static void main(String[] args) {
 
    
   //   Input:  "aaabbbcc"
     //Output: "aabbcc"
     
        char ch[]= {'a','a','a','b','b','b','c','c'};
        
  //  
      int index=0;
      int count=1;
     
      for (int i = 1; i <= ch.length; i++)

      {
          
          if(i<ch.length&& ch[i]==ch[i-1])
          {
              count++;
          }
          else 
             {
                 int times= Math.min(count,2);
                 for(int j=0;j<times;j++)
                 {
                     ch[index++]= ch[i-1];
                 }
                count=1;
           }
          
      }
     
     
     
               
         
     
     for(int i=0;i<index;i++)
     {
         System.out.print(ch[i]+" ");
     }
    }
     
    }
