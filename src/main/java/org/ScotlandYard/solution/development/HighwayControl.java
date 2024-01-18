package org.ScotlandYard.solution.development;

import java.io.File;

public class HighwayControl {

    public static void main(String[] args) {

		if(args.length != 1){
			System.err.println("Must pass in an input image map");
			System.exit(1);
		}

		final String filePath = args[0];
		File file = new File(filePath);

		if(!file.exists()){
			System.err.println("Incorrect file path passed in: "+ filePath);
			System.exit(1);
		}

		HighwayControlUI highwayControlUi = new HighwayControlUI(file);

		highwayControlUi.setVisible(true);

    }
}
