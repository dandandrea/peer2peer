import java.util.*;
import java.io.*;

public class ChokeMessage implements Message
{


        // Constructor for Choke_M
        public ChokeMessage(){
        }      
               

        // Deserialize Constructor
        public ChokeMessage(String message)
        {
                if ( Integer.parseInt(message.substring(4,5)) != 0)
                {
                        System.out.println(" ERROR: Invalid Message Type ");
                }

        }

        // To String
        public String toString()
        {
                return "00050";
        }

}