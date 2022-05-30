/**
 * MIT License
 * Copyright (c) 2019 Montana State University Software Engineering Labs
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package piquebinaries.runnable;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pique.analysis.ITool;
import pique.evaluation.Project;
import pique.runnable.ASingleProjectEvaluator;
import pique.utility.PiqueProperties;
import tool.CVEBinToolWrapper;
import tool.CWECheckerToolWrapper;
import tool.YaraRulesToolWrapper;

/**
 * Behavioral class responsible for running TQI evaluation of a single project
 */
// TODO (1.0): turn into static methods (maybe unless logger problems)
public class SingleProjectEvaluator extends ASingleProjectEvaluator {
	private static final Logger LOGGER = LoggerFactory.getLogger(SingleProjectEvaluator.class);

    //default properties location
    @Getter @Setter
    private String propertiesLocation = "src/main/resources/pique-properties-default.properties";

    public SingleProjectEvaluator(String propertiesPath, String projectToAnalyze){
        propertiesLocation = propertiesPath;
    	init(projectToAnalyze);
    }

    public SingleProjectEvaluator(String projectToAnalyze){
        init(projectToAnalyze);
    }
    
    public SingleProjectEvaluator(){ 	
    	init("/input");
    }

    public void init(String projectLocation){
    	LOGGER.info("Starting Analysis");
    	Properties prop = null;
		try {
			prop = propertiesLocation==null ? PiqueProperties.getProperties() : PiqueProperties.getProperties(propertiesLocation);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


        Path projectRoot = Paths.get(projectLocation);
        Path resultsDir = Paths.get(prop.getProperty("results.directory"));

        LOGGER.info("Project to analyze: " + projectRoot.toString());
        
        Path qmLocation = Paths.get(prop.getProperty("derived.qm"));
        Path resources = Paths.get(prop.getProperty("blankqm.filepath"));
        resources = resources.toAbsolutePath().getParent();
        
        ITool cveBinTool = new CVEBinToolWrapper();
        ITool cweCheckerTool = new CWECheckerToolWrapper();
        ITool yaraRulesWrapper = new YaraRulesToolWrapper(resources);
        Set<ITool> tools = Stream.of(cveBinTool,cweCheckerTool, yaraRulesWrapper).collect(Collectors.toSet());
        Path outputPath = runEvaluator(projectRoot, resultsDir, qmLocation, tools);
        LOGGER.info("output: " + outputPath.getFileName());
        System.out.println("output: " + outputPath.getFileName());

        System.out.println("exporting compact: " + project.exportToJson(resultsDir, true));

    }
    //region Get / Set
    public Project getEvaluatedProject() {
        return project;
    }

    
}
