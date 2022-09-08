[![CodeQL](https://github.com/MSUSEL/msusel-pique-bin/actions/workflows/codeql-analysis.yml/badge.svg)](https://github.com/MSUSEL/msusel-pique-bin/actions/workflows/codeql-analysis.yml)

# MSUSEL-PIQUE-BIN-DOCKER
## Introduction
This project is a docker-wrapped operationalized PIQUE model for the assessment of security quality in binary files. 

Because of numerous environment-related issues when running [PIQUE-bin](https://github.com/msusel/msusel-pique-bin), we have decided to package the entirety of the aforementioned project in a single standalone docker container.
___
## Tools
These will be automatically installed when the docker image is built.

- [YARA](http://virustotal.github.io/yara/) and the [Yara-Rules repository](https://github.com/Yara-Rules/rules)
- [CVE-Bin-Tool](https://github.com/intel/cve-bin-tool)
- [CWE_Checker](https://github.com/fkie-cad/cwe_checker)
___

## Run Environment
Docker 20.10.14+

## Running
1. Download the file msusel-pique-bin-docker.zip (shown in tagged releases), extract it, and open a terminal window in the base directory (msusel-pique-bin-docker/)
2. With Docker 20.10.14+ installed, run the following command to construct the docker image: `docker build --no-cache -t pique-bin-docker:1.1 .`. This command takes approximately 10 minutes to run.
3. Place binary files you would like to analyze in the "msusel-pique-bin-docker/input/projects" directory
4. Place a text file that contains your NVD API key in the "msusel-pique-bin-docker/input/keys" directory. The NVD API key is necessary for cve-bin-tool to communicate with the NVD database. A key can be generated [here](https://nvd.nist.gov/developers/request-an-api-key).
5. Run the docker image with `docker run -it --rm -v /path/to/input/directory:/input -v /path/to/output/directory:/output pique-bin-docker:1.1`. This command can take a while to run if the size of your binaries are larger than >750 kb.
6. Output (two .json files) will be generated in the "msusel-pique-bin-docker/output" directory. 
