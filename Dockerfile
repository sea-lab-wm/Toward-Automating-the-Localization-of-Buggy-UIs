FROM ubuntu:latest

WORKDIR /app

RUN apt-get update
RUN apt-get -y install sudo
RUN sudo apt-get -y install python3
RUN sudo apt-get -y install python3-pip
RUN sudo apt-get -y install default-jdk
RUN sudo apt-get -y install maven

COPY requirements.txt ./
RUN pip install -r requirements.txt --break-system-packages

COPY study_1 ./study_1
COPY study_2 ./study_2
COPY run_cmnd.sh ./