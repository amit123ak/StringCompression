// Online Java Compiler
// Use this editor to write, compile and run your Java code online

class Main {
    public static void main(String[] args) {
        System.out.println("Try programiz.pro");
        
       //  Input: aaabb → Output: [(a,3,0), (b,2,3)]
       
       String str="aaabb";
      int count=1;
       for(int i=1;i<=str.length();i++)
       {
           
           if(i<str.length()&& str.charAt(i)==str.charAt(i-1))
           {
               count++;
           }else{
               
               System.out.println("("+str.charAt(i-1)+","+count +","+(i-count)+")");
               count =1;
           }
       }
    }
}
