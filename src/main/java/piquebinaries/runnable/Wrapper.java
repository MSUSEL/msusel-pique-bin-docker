package piquebinaries.runnable;

public class Wrapper {

    public static void main(String[] args){
        try{
            if (args == null || args.length < 1) {
                throw new IllegalArgumentException("Incorrect input parameters given. Usage: docker run pique-bin-docker [arguments] <file to scan>" +
                    "\n Optional Arguments:" +
                    "\n\t -h, --help\t\t\t help menu" +
                    "\n\t -p, --properties\t\t\t input a properties file. If not properties file is input the default will be used." +
                    "\n\t -v, --version\t\t\t print the version number." +
                    "\n" +
                    "\n Input" +
                    "\t -i, --input_file, INPUT_FILE \t\t\t path to input file." +
                    "\n Output" +
                    "\t -o, --output_file, OUTPUT_FILE \t\t\t path to output file. Only json file output supported currently.");
            }

            for (int i = 0; i < args.length; i++) {
                switch(args[i]){
                    case "-h":
                    case "--help":
                        //TODO
                        System.out.println("Run the image with docker run pique-bin-docker [arguments] <file to scan>");
                        System.out.println("\t\tModel derivation involves populating a model with proper edge weights, threshold values, and structure" +
                            " to prepare for a model evaluation");
                        System.out.println("Run the jar file with the --evaluate-model (-e) to evaluate a quality model");
                        System.out.println("\t\tModel evaluation involves executing the model on a system under analysis to generate quality scores.");
                        break;
                    case "--version":
                    case "-v":
                        System.out.println("PIQUE-bin version 1.0.0");
                        break;
                    case "--derive-model":
                    case "-d":
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
                    case "--evaluate-model":
                    case "-e":
                        //kick off model assessment
                    	if (args.length >i+1) {
                    		new SingleProjectEvaluator(args[i+1]);
                    	}
                    	else {
                    		new SingleProjectEvaluator();
                    	}
                    	
                        i++;
                        break;

                    default:
                        System.out.println("System arguments not recognized, try --help or -h");

                }
            }


        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
