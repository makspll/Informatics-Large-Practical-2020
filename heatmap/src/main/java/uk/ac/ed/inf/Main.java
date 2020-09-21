package uk.ac.ed.inf;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class Main{


    public static void main(String[] args) {
        // if program is launched incorrectly print usage information
        if(args.length != 1){
            displayUsageInformation();
            System.exit(-1);
        } else {

            // argument 1 is input file name
            File inputFile = new File(args[1]);
            BufferedReader fileReader = null;
                
            // set up file reader
            try {
                fileReader = new BufferedReader(new FileReader(inputFile));
            } catch (FileNotFoundException e) {
                // display error
                displayInvalidInputFileNotFoundInformation(inputFile.getName());
                System.exit(-1);
            }

            try{
                for(String line = ""; fileReader.ready(); line = fileReader.readLine()){
                    
                }
            } catch(IOException e){

            }

            
        }




    }

    /** The output printed out when the user launches the program incorrectly */    
    private static void displayUsageInformation() {
        System.out.println("Usage: java "+Main.class.getSimpleName()+" predictions.txt");
        System.out.println("where  predictions.txt is a file containing 10 lines of text with 10 comma separated values");
    }

    /** The output printed out when the input file cannot be found */    
    private static void displayInvalidInputFileNotFoundInformation(String filename){
        System.err.println("Error: input file" +filename+ "file cannot be found.");
    }


        
}
