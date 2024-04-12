/* COMPSCI 424 Program 3
 * Name:
 * 
 * This is a template. Program3.java *must* contain the main class
 * for this program. 
 * 
 * You will need to add other classes to complete the program, but
 * there's more than one way to do this. Create a class structure
 * that works for you. Add any classes, methods, and data structures
 * that you need to solve the problem and display your solution in the
 * correct format.
 */

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import scala.util.CommandLineParser.FromString;
 import scala.util.CommandLineParser

 /**
 * Main class for this program. To help you get started, the major
 * steps for the main program are shown as comments in the main
 * method. Feel free to add more comments to help you understand
 * your code, or for any reason. Also feel free to edit this
 * comment to be more helpful.
 */

object Program3{
    // Declare any class/instance variables that you need here.

    /**
     * @param args Command-line arguments. 
     * 
     * args[0] should be a string, either "manual" or "auto". 
     * 
     * args[1] should be another string: the path to the setup file
     * that will be used to initialize your program's data structures. 
     * To avoid having to use full paths, put your setup files in the
     * top-level directory of this repository.
     * - For Test Case 1, use "424-p3-test1.txt".
     * - For Test Case 2, use "424-p3-test2.txt".
     */
    

    
    def main(args: Array[String]): Unit={
        // Code to test command-line argument processing.
        // You can keep, modify, or remove this. It's not required.
        if(args.length < 2){
            println("Not enough command-line arguments provided, exiting.");
            return 0;
        }
        println("Selected mode: " + args(0));
        println("Setup file location: " + args(1));
        // 1. Open the setup file using the path in args[1]
        var currentLine: String = "";
        var setupFileReader: BufferedReader = null;
        try{
            setupFileReader = BufferedReader(FileReader(args(1)))
        }
        catch{
            case e: FileNotFoundException => {
                println("Cannot find set-up file at " + args(1) + " exiting...");
                return ();
            }
        }
        
        // 2. Get the number of resources and processes from the setup
        // file, and use this info to create the Banker's Algorithm
        // data structures
        var numResources: Int = -1;
        var numProcesses: Int = -1;
        // For simplicity's sake, we'll use one try block to handle
        // possible exceptions for all code that reads the setup file.

        try{
            // Get number of resources
            currentLine = setupFileReader.readLine();
            if (currentLine == null){
                println("Cannot find number of resources, exiting.");
                setupFileReader.close();
                return ();
            }
            else{
                numResources = Integer.parseInt(currentLine.split(" ")(0))
                println(numResources + " resources");
            }
            // Get number of processes
            currentLine = setupFileReader.readLine();
            if(currentLine==null){
                println("Cannot find number of processes, exiting.");
                setupFileReader.close();
                return ();
            }
            else{
                numProcesses = Integer.parseInt(currentLine.split(" ")(0));
                println(numProcesses + " processes");
            }
            // Create the Banker's Algorithm data structures, in any
            // way you like as long as they have the correct size


            // 3. Use the rest of the setup file to initialize the
            // data structures


            setupFileReader.close(); // done reading the file, so close it
        }
        catch{
            case e: IOException =>{
                println("Something went wrong while reading setup file " + args(1) + ". Stack trace follows. Exiting.");
                e.printStackTrace(System.err);
                System.err.println("");
                return ();
            }
        }
        return ();
    }
}

