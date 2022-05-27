package piquebinaries.runnable;

public class Wrapper {


    private static String getHelpMenu(){
        return  "Usage: docker run pique-bin-docker [arguments] <file to scan>" +
                "\n Optional Arguments:" +
                "\n\t -h, --help\t\t\t help menu" +
                "\n\t -p, --properties\t\t\t input a properties file. If not properties file is input the default will be used." +
                "\n\t -v, --version\t\t\t print the version number.";
//                "\n" +
//                "\n Input" +
//                "\t -i, --input_file, INPUT_FILE \t\t\t path to input file." +
//                "\n Output" +
//                "\t -o, --output_file, OUTPUT_FILE \t\t\t path to output file. Only json file output supported currently.";
   }
    public static void main(String[] args){
        try{
            if (args == null || args.length < 1) {
                throw new IllegalArgumentException("Incorrect input parameters given. " +
                        getHelpMenu());
            }

            //go until the second to last arg because last arg is path to analyze
            for (int i = 0; i < args.length; i++) {
                switch(args[i]) {
                    case "-h":
                    case "--help":
                        System.out.println(getHelpMenu());
                        System.exit(0);
                        break;
                    case "-v":
                    case "--version":
                        System.out.println("PIQUE-bin version 1.0.0");
                        System.exit(0);
                        break;
                    case "-p":
                    case "--properties":
                        System.out.println("TODO");
                        System.exit(0);
                        new SingleProjectEvaluator(args[i + 1], args[args.length - 1]);
                        break;


                    /** -- commenting out for now
                     case "-d":
                     case "--derive-model":
                     //kick off new Deriver
                     System.out.println("This is hidden functionality, not ready for model derivation from docker support... Soooooon :)");
                     System.exit(0);
                     if (args.length >i+1) {
                     new QualityModelDeriver(args[i+1]);
                     }
                     else {
                     new QualityModelDeriver();
                     }

                     i++; //properties file is read as input, need to increment i to jump past it to the next argument
                     break;
                     */
                    /** -- commenting until I get normal operation up and running
                     case "-i":
                     case "--input_file":
                     case "INPUT_FILE":
                     //kick off model assessment
                     if (args.length >i+1) {
                     new SingleProjectEvaluator(args[i+1]);
                     }
                     else {
                     new SingleProjectEvaluator();
                     }
                     i++;
                     break;
                     */
                    default:
                        //System.out.println("System arguments not recognized, try --help or -h");
                }

            }
            //last argument, this is a file input
            new SingleProjectEvaluator(args[args.length-1]);


        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
