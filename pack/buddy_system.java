package pack;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

public class buddy_system {

    public static int[] res1;
    public static int[][] res2;
    public static int mem;
    //public static int[] seq;

    public static LinkedList<alloc> seq = new LinkedList<alloc>();
    public static LinkedList<alloc> current = new LinkedList<alloc>();

    public static File log = new File("Output.txt");

    public static void log_creator() {                                       //Creating log file
        try {
            if (log.exists()==false) {
                log.createNewFile();
            }

            else {
                try {
                    PrintWriter logger = new PrintWriter(new FileWriter(log, false));           //To overwrite any existing file with same name
                    logger.write(" ");
                    logger.close();
                }
        
                catch (IOException e) {
                    System.out.println("System Exception: " + e);
                }
            }

        } catch (Exception e) {
            System.out.println("Exception: "+e);
        }
    }

    public static void logging(CharSequence s) {                             //Log Writer
        try {
            PrintWriter logger = new PrintWriter(new FileWriter(log, true));
            logger.append(s);
            logger.close();
        } 
        catch (IOException e) {
            System.out.println("System Exception: " + e);
        }
    }

    public static void allocator (alloc e1) {

        //boolean flag = true;
        /*for (int i = res.length; i >=0; i--) {
            if(res[i]*Math.pow(2,i) >= instruct) {
                res[i] -= (int)(instruct/Math.pow(2,i));
                return;
            }
        }*/

        int r = (int)Math.floor((int)(Math.log(e1.num)/Math.log(2))) + 2;       //Calculate block size required
        //System.out.println("Value of r is: "+r);
        r = res1.length - r;                                        
        //System.out.println("Value of r is: "+r);

        if (res1[r]>0) {                                              //If the block is available
            res1[r] -= 1;
            for (int i = 0; i < res2[r].length; i++) {
                if (res2[r][i] != -1) {
                    alloc e2 = new alloc(res2[r][i], e1.alph);
                    e2.num2 = res2[r][i] + (int)Math.pow(2, res1.length-r-1);
                    current.add(e2);                                                //add the allocation 
                    res2[r][i] = -1;                                                //Indicate unavailability of the block for future
                    break; 
                }
            }
            return;
        }
        else {                                                                      //Spilt the next available blocks 
            for (int i = r-1; i >= 0; i--) {
                if (res1[i] > 0) {          
                    res1[i] -= 1;                                                       //decreament available blocks
                    for (int j = 0; j < res2[i].length; j++) {
                        if (res2[i][j] != -1) {
                            res2[i+1][0] = res2[i][j];                                  //Add block location
                            res2[i][j] = -1;
                            res2[i+1][1] = res2[i+1][0] + (int)Math.pow(2, res1.length-i-2);      //res1.length-i-1
                            //logging("\nInternal Calculation: "+(int)Math.pow(2, res1.length-i-1)+"\n");
                            res1[i+1] += 2;
                            break;
                        }
                    }
                    break;
                    
                }
            }
            //System.out.println("Call Allocator again");
            allocator(e1);                                                          //After splitting block, call allocator again.
        }

    }

    public static void deallocator (alloc e1) {
        e1.num = -1*e1.num;
        /*for (int i = res1.length-1; i >= 0; i--) {
            if(Math.pow(2, res1.length-i-1)>= e1.num) {
                res1[i] += 1;

            }
        }*/
        int r = (int)Math.floor((int)(Math.log(e1.num)/Math.log(2)));               //calculate allocated block size
        r = res1.length - r - 2;                                
        //System.out.println("Value of r: "+r);

        for (int i = 0; i < current.size(); i++) {
            if (current.get(i).alph.equals(e1.alph)) {                              //Finding allocation
                //System.out.println("Enters if");
                res1[r] += 1;
                for (int j = 0; j < res2[r].length; j++) {
                    if (res2[r][j] == -1) {
                        res2[r][j] = current.get(i).num;                                //Add allocation location
                        current.remove(i);                                              //remove allocation from current
                        break;
                    }
                }
                break;
            }
        }
        //System.out.println("The 1D Array is: "+Arrays.toString(res1));
        //To combine spaces
        for(int i = res2.length-1; i > 0; i--) {
            if(res1[i]>1) {
                //System.out.println("Enters IF: "+i);
                //System.out.println("The 1D Array is: "+Arrays.toString(res1));
                //System.out.println("Enters Second IF if: "+Math.pow(2, res1.length-i));
                for (int j = 0; j < res2[i].length-1; j++) {
                    Arrays.sort(res2[i]);
                    //System.out.println("The difference is: "+(res2[i][j+1]-res2[i][j]));
                    if(res2[i][j] != -1 && res2[i][j+1] != -1 && ((res2[i][j+1]-res2[i][j] == (int)(Math.pow(2, res1.length-i-1))))) {          //|| (res2[i][j]-res2[i][j+1] == (int)(Math.pow(2, res1.length-i-1)
                        //System.out.println("Enters Second IF: "+Math.pow(2, res1.length-i));
                        //System.out.println("res2 values: "+res2[i][j]+" "+res2[i][j+1]);
                        for (int k = 0; k < res2[i-1].length; k++) {
                            if(res2[i-1][k]== -1) {
                                res2[i-1][k] = Math.min(res2[i][j], res2[i][j+1]);
                                res2[i][j] = -1;
                                res2[i][j+1] = -1;
                                res1[i] -= 2;
                                res1[i-1] += 1;
                                //System.out.println("Updated value of i and i-1 is: "+res1[i]+" "+res1[i-1]);
                                break;
                            }
                        }
                        
                    }
                    /*if(res1[i]<=1) {
                        break;
                    }*/
                    //break;
                }
            }
                
        }
    }

    public static void output() {
        for (int i = 0; i < res1.length; i++) {
            logging((int)Math.pow(2, res1.length-i-1)+" bytes: "+res1[i]+" Available");
            if (res1[i]>0) {
                logging(": ");
                for (int j = 0; j < res2[i].length; j++) {
                    if (res2[i][j] != -1) {
                        logging(res2[i][j]+", ");
                    }
                }
            }
            logging("\n");
        }

        for (int i = 0; i < current.size(); i++) {
            logging("Process "+current.get(i).alph+" has memory from "+current.get(i).num+" to "+current.get(i).num2+"\n");
        }
        logging("\n");
    }
    public static void main(String[] args) {
        boolean flag = true;
        //seq = new int[0];
        Scanner reader = new Scanner(System.in);
        System.out.print("Enter The available memory (MB): ");
        mem = Integer.parseInt(reader.nextLine());
        mem = mem*1024;
        System.out.println("Mem is: "+mem);

        System.out.println("Enter the Sequence in KB (eg: A 70) (0 to exit): ");
        while (flag) {
            String t1 = reader.nextLine();
            //System.out.println("")
            String[] t2 = t1.split(" ");
            //String[] t2 = t1.StringUtils.split("\\s+");
            //System.out.println("The Spilt is: "+Arrays.toString(t2));
            //System.out.println("The type is: "+t2[0].getClass().getName());
            //System.out.println("The Spilt is: "+Arrays.toString(t1.split("\\s+")));
            //System.out.println("Output: "+t2[0].equals("0"));

            if (t2[0].equals("0")) {
                //System.out.println("Enters if");
                break;
            } 
            
            else {
                int t = Integer.parseInt(t2[1]);
                alloc e = new alloc(t, t2[0]);
                seq.add(e);
                //seq = Arrays.copyOf(seq, seq.length+1);
                //seq[seq.length-1] = t;
            }
        }

        //System.out.print("The Array is: "+Arrays.toString(seq));

        res1 = new int [((int)(Math.log(mem)/Math.log(2)))+1];                          
        //System.out.println("Res1 Size is: "+res1.length);

        res2 = new int [((int)(Math.log(mem)/Math.log(2)))+1][mem];
        //System.out.println();

        res1[0] = 1;
        for (int i = 1; i < res1.length; i++) {                             //Initializing res1
            res1[i] = 0;
        }

        res2[0][0] = 0;
        for (int i = 0; i < res2.length; i++) {                             //Initializing res2
            for (int j = 0; j < res2[i].length; j++) {
                if(i==0 && j==0) {
                    
                }
                else {
                    res2[i][j] = -1;
                }
            }
        }

        //System.out.println("The 1D Array is: "+Arrays.toString(res1));
        logging("\n\n\nOutput:\n\n");
        while(seq.size()>0) {
            if (seq.getFirst().num > 0) {
                //System.out.println("The 1D Array is: "+Arrays.toString(res1));
                //System.out.println("Allocator: "+seq.getFirst().alph+" : "+seq.getFirst().num);
                logging("Allocator: "+seq.getFirst().alph+" : "+seq.getFirst().num);
                allocator(seq.poll());
                /*for (int i = 0; i < res2.length; i++) {
                    System.out.println("The Free Allocation space for "+i+" is: "+Arrays.toString(res2[i]));
                }*/
            }

            else {
                //System.out.println("The 1D Array is: "+Arrays.toString(res1));
                //System.out.println("Deallocator: "+seq.getFirst().alph+" : "+seq.getFirst().num);
                logging("Deallocator: "+seq.getFirst().alph+" : "+seq.getFirst().num);
                deallocator(seq.poll());
                //System.out.println("The 1D Array is: "+Arrays.toString(res1));
                /*for (int i = 0; i < res2.length; i++) {
                    System.out.println("The Free Allocation space for "+i+" is: "+Arrays.toString(res2[i]));
                }*/
                //System.out.println("The 1D Array after deallocation is: "+Arrays.toString(res1));
            }
            logging("\n");
            output();
        }

        //System.out.println("The 1D Array is: "+Arrays.toString(res1));
        /*for (int i = 0; i < res2.length; i++) {
            System.out.println("The Free Allocation space for "+i+" is: "+res2[i][0]+" "+res2[i][1]+" "+res2[i][3]);
        }*/


        reader.close();
    }
}