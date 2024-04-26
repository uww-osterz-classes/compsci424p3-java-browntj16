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
import java.util.concurrent.Semaphore;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import scala.util.CommandLineParser.FromString;
import scala.util.CommandLineParser
import java.util.Scanner;
import scala.compiletime.ops.int
//import scala.compiletime.ops.int

/** Main class for this program. To help you get started, the major steps for
  * the main program are shown as comments in the main method. Feel free to add
  * more comments to help you understand your code, or for any reason. Also feel
  * free to edit this comment to be more helpful.
  */

object Program3 {
  // Declare any class/instance variables that you need here.

  /** @param args
    *   Command-line arguments.
    *
    * args[0] should be a string, either "manual" or "auto". args[1] should be
    * another string: the path to the setup file that will be used to initialize
    * your program's data structures. To avoid having to use full paths, put
    * your setup files in the top-level directory of this repository.
    *   - For Test Case 1, use "424-p3-test1.txt".
    *   - For Test Case 2, use "424-p3-test2.txt".
    */

  def main(args: Array[String]): Unit = {
    // Code to test command-line argument processing.
    // You can keep, modify, or remove this. It's not required.
    if (args.length < 2) {
      println("Not enough command-line arguments provided, exiting.");
      return ();
    }
    val mode = args(0);
    val fileLocation = args(1);
    println("Selected mode: " + mode);
    println("Setup file location: " + fileLocation);
    // 1. Open the setup file using the path in args[1]
    var currentLine: String = "";
    var setupFileReader: BufferedReader = null;
    try {
      setupFileReader = BufferedReader(FileReader(fileLocation))
    } catch {
      case e: FileNotFoundException => {
        println("Cannot find set-up file at " + fileLocation + " exiting...");
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

    try {
      // Get number of resources
      currentLine = setupFileReader.readLine();
      if (currentLine == null) {
        println("Cannot find number of resources, exiting.");
        setupFileReader.close();
        return ();
      } else {
        numResources = Integer.parseInt(currentLine.split(" ")(0))
        println(numResources + " resources");
      }
      // Get number of processes
      currentLine = setupFileReader.readLine();
      if (currentLine == null) {
        println("Cannot find number of processes, exiting.");
        setupFileReader.close();
        return ();
      } else {
        numProcesses = Integer.parseInt(currentLine.split(" ")(0));
        println(numProcesses + " processes");
      }

      // Create the Banker's Algorithm data structures, in any
      // way you like as long as they have the correct size

      // 3. Use the rest of the setup file to initialize the
      // data structures

      var available = Array[Int](0); // create array of available
      println(
        setupFileReader.readLine() + ":"
      ); // read then print the next line
      currentLine = setupFileReader.readLine(); // move to next line
      if (currentLine == null) { // if currentline is ever null we quit
        println("Cannot find number of available resources, exiting");
        setupFileReader.close();
        return ();
      } else { // otherwise we make available essentially just equal to the parsed int of the line in the file
        val splitLine = currentLine.split(" ");
        available = splitLine.map(Integer.parseInt);
        print(available.mkString(" ") + "\n");
      }

      println(setupFileReader.readLine() + ":");

      val max = fill2dArray(
        rows = numProcesses,
        line = setupFileReader.readLine(),
        fileReader = setupFileReader,
        columns = numResources
      );
      if (max.length == 0) {
        println("Cannot find maximum claims, exiting")
        return ();
      }

      // creating 2d array of allocation
      println(setupFileReader.readLine() + ":");
      val allocation = fill2dArray(
        rows = numProcesses,
        line = setupFileReader.readLine(),
        fileReader = setupFileReader,
        columns = numResources
      );
      if (allocation.length == 0) {
        println("Cannot find allocations, exiting")
        return ();
      }

      setupFileReader.close(); // done reading the file, so close it

      val requests = Array.ofDim[Int](numProcesses, numResources)

      if (condition1(allocation = allocation, maximum = max)) {
        println("Condition 1 passed.")
      }

      val total = condition2(allocation = allocation, available = available);
      println("Total: \n" + total.mkString(" "));

      if (mode.equals("manual")) {
        manualMode(
          row = numProcesses,
          col = numResources,
          maxClaims = max,
          currAllocations = allocation,
          availableResources = available
        );
      } else if (mode.equals("automatic")) {
        automaticMode(
          row = numProcesses,
          col = numResources,
          maxClaims = max,
          currAllocations = allocation,
          availableResources = available
        );
      } else {
        println("Invalid mode entered.")
      }

    } catch {
      case e: IOException => {
        println(
          "Something went wrong while reading setup file " + args(
            1
          ) + ". Stack trace follows. Exiting."
        );
        e.printStackTrace(System.err);
        System.err.println("");
        return ();
      }
    }

    return ();
  }

  /** condition that checks to see if every process is resourcing less than or
    * equal to their maximum
    *
    * @param allocation
    * @param maximum
    * @return
    */
  def condition1(
      allocation: Array[Array[Int]],
      maximum: Array[Array[Int]]
  ): Boolean = {
    for (i <- 0 to allocation.length - 1) {
      for (j <- 0 to allocation(0).length - 1) {
        if (allocation(i)(j) > maximum(i)(j)) {
          return false;
        }
      }
    }
    return true;
  }

  /** not even a condition. actually just makes our array of total units(?) in
    * each resource.
    *
    * @param allocation
    * @param available
    * @return
    */
  def condition2(
      allocation: Array[Array[Int]],
      available: Array[Int]
  ): Array[Int] = {
    var totalAllocation = new Array[Int](available.length);
    // basically, what these for loops do is add every member of a column with each other and then the available resources of the same column
    for (i <- 0 to available.length - 1) {
      var currAvail = available(i);
      for (j <- 0 to allocation.length - 1) {
        currAvail += allocation(j)(i);
      }
      totalAllocation(i) = currAvail;
    }
    return totalAllocation;
  }

  /** I don't think I ever fully figured out this condition and its probably why
    * my program doesnt work. The zybook describes the unsafe state as when the
    * potential requests of all processes exceed the available units, but even
    * in that example they have (zybook 5.3.5) the potential request for p2 is |
    * 1 | 2 | and the available units are | 1 | 0 |, and here 1 does not exceed
    * 1 and doesn't fulfill its own conditions. i've spent probably too much
    * time banging my head against the keyboard trying to figure this out
    *
    * @param maxClaims
    * @param currAllocations
    * @param availableResources
    * @param rows
    * @param columns
    * @return
    */
  def condition3(
      maxClaims: Array[Array[Int]],
      currAllocations: Array[Array[Int]],
      availableResources: Array[Int],
      rows: Int,
      columns: Int
  ): Boolean = {
    for (i <- 0 to rows - 1) {
      // println("Resources: " + availableResources.mkString(" "))
      for (j <- 0 to columns - 1) {
        val potentialRequest = maxClaims(i)(j) - currAllocations(i)(j);
        // println("Current allocations: " + currAllocations(i).mkString(" "));
        // println(potentialRequest + " <= " + availableResources(j))
        if (potentialRequest < availableResources(j)) {
          return false;
        }
      }
    }
    return true;
  }

  /** checks to see if more than available is being requested
    *
    * @param unitsToRequest
    * @param resourceAvailable
    * @return
    */
  def condition4(unitsToRequest: Int, resourceAvailable: Int): Boolean = {
    if (unitsToRequest > resourceAvailable) {
      return false;
    } else {
      return true;
    }
  }

  /** function that just helps read the formatted numbers in the test files.
    * think the list of numbers in the file, but placed into a 2d array.
    *
    * @param rows
    * @param line
    * @param fileReader
    * @return
    *   2d array that represents a table
    */
  def fill2dArray(
      rows: Int,
      columns: Int,
      line: String,
      fileReader: BufferedReader
  ): Array[Array[Int]] = {
    var currentLine = line;
    if (currentLine != null) {
      var splitLine = currentLine.split(" ");
      val returnArray = Array.ofDim[Int](rows, columns);
      for (i <- 0 to rows - 1) {
        if (currentLine == null) {
          fileReader.close();
          return Array.ofDim(0, 0);
        } else {
          println(splitLine.mkString(" "));
          returnArray(i) = splitLine.map(Integer.parseInt);
          // stops us from prematurely reading next line when unecessary
          if (i + 1 != rows) {
            currentLine = fileReader.readLine();
            splitLine = currentLine.split(" ");
          }
        }
      }
      return returnArray;
    }
    // returning this empty 2d array is our way of saying we failed
    return Array.ofDim(0, 0);
  }

  /** traps user in while loop until invalid command is entered. while valid
    * commands are entered we execute them.
    *
    * @param row
    * @param col
    * @param maxClaims
    * @param currAllocations
    * @param availableResources
    */
  def manualMode(
      row: Int,
      col: Int,
      maxClaims: Array[Array[Int]],
      currAllocations: Array[Array[Int]],
      availableResources: Array[Int]
  ) = {
    val currentRequests = Array.ofDim[Int](row, col);
    var cond = true;
    while (cond) {
      val sc = Scanner(System.in);
      println("Enter a command: ");
      val command = sc.nextLine();
      cond = parseCommand(
        command = command,
        rows = row,
        cols = col,
        maxClaims = maxClaims,
        currAllocations = currAllocations,
        availableResources = availableResources,
        manual = true
      );
      println(
        "Current resources: " +
          availableResources.mkString(" ")
      );
    }
  }

  def automaticMode(
      row: Int,
      col: Int,
      maxClaims: Array[Array[Int]],
      currAllocations: Array[Array[Int]],
      availableResources: Array[Int]
  ) = {
    val semaphore = new Semaphore(1);
    println("before the loop");
    for (i <- 0 to row - 1) {
      val proc = process(
        rowPlacement = i,
        rows = row,
        cols = col,
        maxClaims = maxClaims,
        currAllocations = currAllocations,
        availableResources = availableResources,
        semaphore = semaphore
      );
      println("About to run");
      proc.start();
    }
  }
  class process(
      rowPlacement: Int,
      rows: Int,
      cols: Int,
      maxClaims: Array[Array[Int]],
      currAllocations: Array[Array[Int]],
      availableResources: Array[Int],
      semaphore: Semaphore
  ) extends Thread {

    def genRandCommands(): Array[String] = {
      var commands = Array[String]();
      for (i <- 0 to 2) {
        val col1 = genRandInt(0, cols);
        val col2 = genRandInt(0, cols);
        commands = commands :+ ("request " + genRandInt(
          0,
          maxClaims(rowPlacement)(col1)
        ) + " of " + col1 + " for " + rowPlacement)

        commands = commands :+ ("release " + genRandInt(
          0,
          currAllocations(rowPlacement)(col2)
        ) + " of " + col2 + " for " + rowPlacement)
      }
      return commands;
    }

    override def run() = {
      genRandInt(0, 0);
      val commands = genRandCommands();
      for (command <- commands) {
        acquireSemaphore(semaphore = semaphore);
        println("Process " + rowPlacement + " has acquired the semaphore.")
        var cond = false;
        while (!cond) {
          cond = parseCommand(
            command = command,
            rows = rows,
            cols = cols,
            maxClaims = maxClaims,
            currAllocations = currAllocations,
            availableResources = availableResources,
            manual = false
          )
          if (!cond) { // if the command fails, we release the semaphore and attempt to acquire it again to try the command again
            println(
              "Process " + rowPlacement + " will release the semaphore. Command failed and will try again later."
            )
            semaphore.release();
            Thread.sleep(2000);
            acquireSemaphore(semaphore = semaphore);
          } else { // if the command succeeds, we release the semaphore
            println(
              "Process " + rowPlacement + " will release the semaphore. Command succeeded."
            )
            semaphore.release();
          }
        }
      }
      println(
        "\nProcess " + rowPlacement + " finished all commands. Exiting.\n"
      )

    }
    // override def run() = {
    //   val commands = genRandCommands();
    //   for (command <- commands) {
    //     acquireSemaphore(semaphore = semaphore);
    //     println("Process " + rowPlacement + " has acquired the semaphore.")

    //     var cond = parseCommand(
    //       command = command,
    //       rows = rows,
    //       cols = cols,
    //       maxClaims = maxClaims,
    //       currAllocations = currAllocations,
    //       availableResources = availableResources,
    //       manual = false
    //     )
    //     if (!cond) { // if the command fails, we release the semaphore and attempt to acquire it again to try the command again
    //       println(
    //         "Process " + rowPlacement + " will release the semaphore. Command failed."
    //       )
    //     } else { // if the command succeeds, we release the semaphore
    //       println(
    //         "Process " + rowPlacement + " will release the semaphore. Command succeeded."
    //       )
    //       semaphore.release();
    //     }

    //   }

    // }
  }

  /** kinda lazy with this one but whatever. the command checking just checks to
    * see if the first word in the command is release/request. if it is then we
    * just assume the rest of the command is valid. not a good idea outside the
    * purposes of this assignment. if an invalid command is entered then we
    * quit. returns false for invalid commands. returns true for valid ones.
    *
    * @param command
    * @param rows
    * @param cols
    * @param maxClaims
    * @param currAllocations
    * @param availableResources
    * @return
    */
  def parseCommand(
      command: String,
      rows: Int,
      cols: Int,
      maxClaims: Array[Array[Int]],
      currAllocations: Array[Array[Int]],
      availableResources: Array[Int],
      manual: Boolean
  ): Boolean = {
    val splitString = command.split(" ");
    if (splitString(0).toLowerCase().equals("request")) {
      val requestStatus = request(
        unitsToRequest = Integer.parseInt(splitString(1)),
        resourceColumn = Integer.parseInt(splitString(3)),
        processRequesting = Integer.parseInt(splitString(5)),
        maxClaims = maxClaims,
        currAllocations = currAllocations,
        availableResources = availableResources,
        rows = rows,
        columns = cols
      )
      if (manual) {
        return true;
      } else {
        return requestStatus;
      }

    } else if (splitString(0).toLowerCase().equals("release")) {
      val releaseStatus = release(
        unitsToRelease = Integer.parseInt(splitString(1)),
        resourceColumn = Integer.parseInt(splitString(3)),
        processReleasing = Integer.parseInt(splitString(5)),
        availableResources = availableResources,
        allocations = currAllocations
      )
      if (manual) {
        return true;
      } else {
        return releaseStatus;
      }
    } else {
      println("Probably invalid command entered or something. Exiting.");
      return false;
    }
  }

  /** function that handles requests.
    *
    * @param unitsToRequest
    * @param resourceColumn
    * @param processRequesting
    * @param maxClaims
    * @param currAllocations
    * @param availableResources
    * @param rows
    * @param columns
    */
  def request(
      unitsToRequest: Int,
      resourceColumn: Int,
      processRequesting: Int,
      maxClaims: Array[Array[Int]],
      currAllocations: Array[Array[Int]],
      availableResources: Array[Int],
      rows: Int,
      columns: Int
  ): Boolean = {
    val c4 = condition4(
      unitsToRequest = unitsToRequest,
      resourceAvailable = availableResources(resourceColumn)
    );
    var testRequest =
      currAllocations(processRequesting)(
        resourceColumn
      ); // save current allocation state at row and column
    currAllocations(processRequesting)(
      resourceColumn
    ) += unitsToRequest; // update allocation state
    var available = availableResources(resourceColumn);
    availableResources(resourceColumn) -= unitsToRequest;
    if ( // if our updated allocation state does not pass our conditions, we reject the request
      !condition3(
        maxClaims = maxClaims,
        currAllocations = currAllocations,
        availableResources = availableResources,
        rows = rows,
        columns = columns
      ) && c4
      && condition1(
        allocation = currAllocations,
        maximum = maxClaims
      )
    ) {
      // availableResources(
      //   resourceColumn
      // ) -= unitsToRequest; // finalize the allocation by altering allocated resources
      println("\nRequest granted.");
      return true;
    } else {
      currAllocations(processRequesting)(resourceColumn) =
        unitsToRequest; // revert the allocation

      availableResources(resourceColumn) = available;
      System.out.println("Unsafe request made and rejected.")
      return false;
    }

  }

  /** function that handles releasing resources. should always release unless it
    * attempts to release too many
    *
    * @param unitsToRelease
    * @param resourceColumn
    * @param availableResources
    * @param processReleasing
    * @param allocations
    */
  def release(
      unitsToRelease: Int,
      resourceColumn: Int,
      availableResources: Array[Int],
      processReleasing: Int,
      allocations: Array[Array[Int]]
  ): Boolean = {
    if (allocations(processReleasing)(resourceColumn) >= unitsToRelease) {
      allocations(processReleasing)(resourceColumn) -= unitsToRelease;
      availableResources(resourceColumn) += unitsToRelease;
      return true;
    } else {
      println("Too many units being released. Release rejected.");
      return false;
    }
  }

  /** generated a random number in range.
    *
    * @param min
    * @param max
    * @return
    */
  def genRandInt(min: Int, max: Int): Int = {
    if (max == 0) {
      return genRandInt(min, 1);
    }
    val r = new scala.util.Random
    return r.between(minInclusive = min, maxExclusive = max);
  }

  def acquireSemaphore(semaphore: Semaphore) = {
    try {
      semaphore.acquire();
    } catch {
      case e: InterruptedException => {}
    }
  }
}
