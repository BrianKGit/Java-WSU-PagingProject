
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

/**
 * Author : Brian Klein Date : 4/3/19 Description : CS 405 -- PROJECT 3 Write a
 * simple paging project. •When first invoked, the user selects whether to use
 * the LRU or the FIFO page replacement algorithm.(done) •The user next
 * specifies the number of frames in main storage -- 1..10(done) •The module
 * then processes a page reference string, either from a file or entered
 * interactively.(done) •Create and use data structures / objects to model main
 * store, the page table, free frame list, queues, etc •You may choose how to
 * implement the various algorithms, but DO NOT implement LRU using any form of
 * the 2nd chance algorithm -- it must be a strict implementation of LRU. •After
 * each page fault (NOT page reference), the module should print out the
 * contents of main storage, showing what actual page each main store page frame
 * contains. It should look like this:FramePage 0 3 1 2 2 7 3 1 4 0 •After
 * processing the entire reference string, the module should print out the total
 * number of page faults required to process the string -- including the faults
 * needed to read each page in the first time. •Note that main store starts out
 * empty, so each page must be faulted in the first time it is referenced. •Use
 * the following page reference string: 1, 2, 3, 4, 2, 1, 5, 6, 2, 1, 2, 3, 7,
 * 6, 3, 2, 1, 2, 3, 6 •Run the reference string on "systems" that contain
 * three, four, five, six, and seven frames of main store, for both LRU and FIFO
 * algorithms. •Turn a listing and all sample runs, plus a summary, like the
 * following =================================== | # FRAMES | # FAULTS | #
 * FAULTS | | | LRU | FIFO | |-----------|----------|----------| | 3 | | |
 * |-----------|----------|----------| | 4 | | |
 * |-----------|----------|----------| | 5 | | |
 * |-----------|----------|----------| | 6 | | |
 * |-----------|----------|----------| | 7 | | |
 * ===================================
 */
public class PagingClient {

    static Scanner console = new Scanner(System.in);
    private static int frames;
    private static int[] mainStore;
    private static int[] priority;

    public static void main(String args[]) {

        //variables
        String command;
//        int frames = 0;
        ArrayList<Integer> pagesToRef;

        //Introduction
        System.out.println("Brian Klein \nCS 405 \nProject 1\n\n\n"
                + "Welcome to Brian Klein's Paging Simulator!\n");

        do {

            //Get user input to select the scheduling algorithm to use here
            System.out.println("\nSelect the paging algorithm by entering a number:"
                    + "\n  1. First In First Out (FIFO)"
                    + "\n  2. Least Recently Used (LRU)\n");

            command = console.next();

            //algorithm switch
            switch (command) {

                //choose FIFO
                case "1":

                    //while loop until frames in main store are between 1-10
                    while (frames < 1 || frames > 10) {
                        System.out.println("\nEnter the main store size in pages (1-10):\n");
                        frames = console.nextInt();
                    }

                    System.out.println("\nUsing FIFO page replacement, with " + frames + " pages of main memory");

                    //call method to create page references and assign it to pagesToRef
                    pagesToRef = pageRefenceString();

                    //call First In First Out algorithm
                    FIFO(frames, pagesToRef);
                    break;

                //choose LRU
                case "2":

                    //while loop until frames in main store are between 1-10
                    while (frames < 1 || frames > 10) {
                        System.out.println("\nEnter the main store size in pages (1-10):\n");
                        frames = console.nextInt();
                    }

                    System.out.println("\nUsing LRU page replacement, with " + frames + " pages of main memory");

                    //call method to create page references and assign it to pagesToRef
                    pagesToRef = pageRefenceString();

                    mainStore = new int[frames];
                    priority = new int[frames];

                    //call Least Recently Used algorithm
                    LRU(pagesToRef);
                    break;

                //invalid input
                default:

                    System.out.println("\nInvalid entry, please select the "
                            + "paging algorithm by entering a number:");
                    break;

            }//end algorithm switch

            //loops while the user input is anything but 1 or 2
        } while (!command.equals("1") && !command.equals("2"));
        //end do-while loop

    }//end main method   

    //method to return an ArrayList of Integers created by user input
    public static ArrayList<Integer> pageRefenceString() {

        //local variables
        int input;
        ArrayList<Integer> pagesToRef = new ArrayList();
        boolean flag = true;

        //hard code pages to reference
        pagesToRef.addAll(Arrays.asList(1, 2, 3, 4, 2, 1, 5, 6, 2, 1, 2, 3, 7, 6, 3, 2, 1, 2, 3, 6));

        return pagesToRef;

    }//end pageRefernceString method

    //simulate a First In First Out paging algorithm
    public static void FIFO(int frames, ArrayList<Integer> pagesToRef) {

        //local variables
        int[] mainStore = new int[frames];
        int count = 0;
        String name = "FIFO";

        //loop for all the page numbers in the pagesToRef ArrayList
        for (int i = 0; i < pagesToRef.size(); i++) {

            //j is the frame in the mainstore that we will replace
            int j = count % frames;
            count++;

            //flag is true if the page number being referenced is found in any of the frames
            boolean flag = pageCheck(mainStore, pagesToRef.get(i));

            //if the flag is false then we place the new page number into the frame that was updated first
            if (!flag) {
                mainStore[j] = pagesToRef.get(i);
                System.out.println(faultPrint(count, mainStore));
            } else {
                //no fault, so do not increase count
                count--;
            }
        }//end for

        //print final status
        finalPrint(count, frames, name);
    }//end FIFO method

    //simulate a Least Recently Used paging algorithm
    public static void LRU(ArrayList<Integer> pagesToRef) {

        //local variables
        int faultNum = 0;
        String name = "LRU";

        //run through all the paging references
        for (int i = 0; i < pagesToRef.size(); i++) {

            //int j = 0;
            faultNum++;

            //flag is true if the page number being referenced is found in any of the frames
            boolean noFault = pageCheck(mainStore, pagesToRef.get(i));
            if (!noFault) {
                int victimFrame = findVictim(i, pagesToRef);
                mainStore[victimFrame] = pagesToRef.get(i);
                priority[victimFrame] = 0;
                System.out.println(faultPrint(faultNum, mainStore));

            } else {
                faultNum--;
            }

        }//end for

        //print final status
        finalPrint(faultNum, frames, name);

    }//end LRU method

    public static String faultPrint(int fault, int[] mainStore) {
        String status = "";
        int count = 0;

        status = "Fault # " + fault
                + "\nMainstore Contents:"
                + "\n       Frame   Page"
                + "\n       -----   ----";

        for (int i = 0; i < mainStore.length; i++) {
            if (mainStore[i] != 0) {
                status += "\n       " + count + "       " + mainStore[i];
            } else {
                status += "\n       " + count + "       EMPTY";
            }
            count++;
        }

        return status;
    }

    //method to check if we need a fault
    public static boolean pageCheck(int[] mainStore, int page) {
        boolean flag = false;

        for (int i = 0; i < mainStore.length; i++) {
            if (mainStore[i] == page) {
                flag = true;
            }
        }
        return flag;
    }

    //method to print the final accessment
    public static void finalPrint(int faults, int frames, String name) {
        String finalString = "=======================================================\n"
                + "Paging Simulation with " + frames + " pages of mainstore\n"
                + "Using " + name + " algorithm\n"
                + "Total page faults = " + faults + "\n"
                + "=======================================================";

        System.out.println(finalString);
    }

    //method to return the index of the least recently used frame
    public static int findVictim(int index, ArrayList<Integer> pagesToRef) {

        //victim is the frame that will be selected to swap pages
        int victim = 0;
        int doWeHaveVictim = 0;
        int victimCheck = mainStore.length - 1;
        boolean checkPriority[] = new boolean[mainStore.length];

        //start from where we are in pagesToRef and go back to the beginning
        for (int j = index; j > -1; j--) {

            //mainstore.length is number of frames
            for (int k = 0; k < mainStore.length; k++) {

                if (mainStore[k] == 0) {
                    victim = k;
                    return victim;

                } else if (mainStore[k] == pagesToRef.get(j) && checkPriority[k] == false) {

                    checkPriority[k] = true;
                    doWeHaveVictim++;

                }
                if (doWeHaveVictim == victimCheck) {
                    for(int x=0;x<checkPriority.length;x++){
                        if(checkPriority[x]==false){
                            victim = x;
                            return victim;

                        }
                    }
                    
                }

            }//end inner for

        }//end outer for

        return victim;
    }//end findVictim method

}//end PagingClient
