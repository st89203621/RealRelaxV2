package com.ehear.aiot.cloud.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class comand_convert_util {

	
	
    public static void main(String[] args) throws IOException  
    {  
    	comand_convert_util ccu = new comand_convert_util();
       ccu.test();
    }  
    
    
    public void test() throws IOException {
    	 try  
         {  
             StringBuffer sb = new StringBuffer("");  
   
             FileReader reader = new FileReader("auto4.txt");  
             BufferedReader br = new BufferedReader(reader);  
   
             String str = null;  
   
             String last_line = "";
             while ((str = br.readLine()) != null)  
             {  
            	 str =str.toUpperCase();
            	  if(!"".equals(last_line))
            	  {
            		  String last_time = last_line.split("0100")[0];
            		  String now_time = str.split("0100")[0];
            		  sb.append("{"+(Integer.parseInt(now_time)-Integer.parseInt(last_time)));
            		  String data = last_line.substring(last_time.length());
            		  sb.append(","+"0x"+data.substring(0,2));
            		  sb.append(","+"0x"+data.substring(2,4));
            		  sb.append(","+"0x"+data.substring(4,6));
            		  sb.append(","+"0x"+data.substring(6,8));
            		  sb.append(","+"0x"+data.substring(8,10));
            		  sb.append(","+"0x"+data.substring(10,12));
            		  sb.append(","+"0x"+data.substring(12,14));
            		  sb.append(","+"0x"+data.substring(14,16));
            		  sb.append(","+"0x"+data.substring(16,18));
            		  sb.append(","+"0x"+data.substring(18,20));
            		  
            		  sb.append(","+"0x"+data.substring(20,22));
            		  sb.append(","+"0x"+data.substring(22,24));
            		  sb.append(","+"0x"+data.substring(24,26));
            		  sb.append(","+"0x"+data.substring(26,28));
            		  sb.append(","+"0x"+data.substring(28,30));
            		  sb.append(","+"0x"+data.substring(30,32));
            		  sb.append(","+"0x"+data.substring(32,34));
            		  sb.append(","+"0x"+data.substring(34,36));
            		  sb.append(","+"0x"+data.substring(36,38));
            		  sb.append(","+"0x"+data.substring(38,40));

            		  sb.append("},"+"\n");

            	  }
                 last_line = str;
             }  
   
             br.close();  
             reader.close();  
   
             // write string to file  
             FileWriter writer = new FileWriter("auto4_handled.txt");  
             BufferedWriter bw = new BufferedWriter(writer);  
             bw.write(sb.toString());  
   
             bw.close();  
             writer.close();  
         } catch (FileNotFoundException e)  
         {  
             e.printStackTrace();  
         } catch (IOException e)  
         {  
             e.printStackTrace();  
         }  
    }
}
