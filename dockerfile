FROM ubuntu:20.04

# need for tzdata config
ENV DEBIAN_FRONTEND=noninteractive TZ=Etc/UTC
ENV RUST_VERSION=1.60.0
#add rust to PATH
ENV PATH="/root/.cargo/bin:/opt/apache-maven-3.8.5/bin:$PATH"

RUN apt-get update && apt-get install -y \
	## pique bin
	openjdk-8-jdk \
	## commenting for now because no operational PIQUE model uses GAMs
	# r-base \
	# r-base-core \
	# r-recommended \
	# r-base-dev \
	## yara
	libssl-dev \
	automake \
	libtool \
	make \
	build-essential \
	gcc \
	pkg-config \
	wget \
	git \
	## cwe-checker
	unzip \
	#java 11 for ghidra
	openjdk-11-jdk \
	## cve-bin-tool
	python3-pip

# move to home for a fresh start
WORKDIR "/home"

## cwe-checker installs

# Rust install -- mostly taken from Rust documentation at https://forge.rust-lang.org/infra/other-installation-methods.html
RUN wget "https://static.rust-lang.org/rustup/dist/x86_64-unknown-linux-gnu/rustup-init"
RUN chmod +x rustup-init
RUN ./rustup-init -y --no-modify-path --profile minimal --default-toolchain $RUST_VERSION --default-host x86_64-unknown-linux-gnu
RUN rm rustup-init

# Ghidra install -- most taken from https://github.com/blacktop/docker-ghidra/blob/master/alpine/Dockerfile
RUN wget "https://github.com/NationalSecurityAgency/ghidra/releases/download/Ghidra_10.1.3_build/ghidra_10.1.3_PUBLIC_20220421.zip"
RUN unzip "ghidra_10.1.3_PUBLIC_20220421.zip"
RUN rm "ghidra_10.1.3_PUBLIC_20220421.zip"
RUN mv ghidra_10.1.3_PUBLIC /ghidra
RUN chmod +x /ghidra/ghidraRun

# cwe-checker -- difficult because the latest release was a while ago and changes are not being updated
RUN git clone "https://github.com/fkie-cad/cwe_checker.git"
WORKDIR "/home/cwe_checker"
RUN make all GHIDRA_PATH=/ghidra


## cve-bin-tool installs
WORKDIR "/home"
RUN pip install cve-bin-tool
# update cve database upon image build
#RUN cve-bin-tool -u now

## yara installs
# taken from https://yara.readthedocs.io/en/stable/gettingstarted.html#compiling-and-installing-yara
RUN wget "https://github.com/VirusTotal/yara/archive/refs/tags/v4.2.3.tar.gz"
RUN tar -zxf v4.2.3.tar.gz
RUN rm v4.2.3.tar.gz
WORKDIR "/home/yara-4.2.3"
RUN ./bootstrap.sh
RUN ./configure
RUN make
RUN make install
RUN make check
# configure libyara links
RUN echo "/usr/local/lib" >> /etc/ld.so.config
RUN ldconfig

WORKDIR "/home"
## yara rules
RUN git clone https://github.com/Yara-Rules/rules.git
# hacky workaround I found on https://github.com/TheHive-Project/Cortex-Analyzers/issues/354 for unsupported yara rule MALW_
RUN for i in `grep -H -R "MALW_AZORULT\.yar" /home/rules/ | grep -Eio "^.+\.yar:" | sed 's/:$//' | sort -u`; do sed -i '/MALW_AZORULT\.yar/d' $i; done
RUN chmod -R +x rules/

## pique-bin
# maven install - install in opt
WORKDIR "/opt"
RUN wget "https://archive.apache.org/dist/maven/maven-3/3.8.5/binaries/apache-maven-3.8.5-bin.tar.gz"
RUN tar xzvf apache-maven-3.8.5-bin.tar.gz
RUN rm apache-maven-3.8.5-bin.tar.gz
RUN export PATH="/opt/apache-maven-3.8.5/bin:$PATH"

# pique install
WORKDIR "/home"
RUN git clone https://github.com/MSUSEL/msusel-pique.git
WORKDIR "/home/msusel-pique"
RUN mvn install

## pique bin (docker) install
WORKDIR "/home"
RUN git clone https://github.com/MSUSEL/msusel-pique-bin-docker
WORKDIR "/home/msusel-pique-bin-docker"
RUN mvn package -Dmaven.test.skip

# input for project files
VOLUME ["/input"]

# output for model
VOLUME ["/output"]

RUN chmod -R +x /input
RUN chmod -R +x /output

##### secret sauce
ENTRYPOINT ["java", "-jar", "/home/msusel-pique-bin-docker/target/msusel-pique-bin-0.0.1-jar-with-dependencies.jar"]