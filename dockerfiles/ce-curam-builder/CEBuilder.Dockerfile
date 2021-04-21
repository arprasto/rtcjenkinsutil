# Image based out of ubuntu with git, python, node-gyp pre-installed
# Node 12.19.0 Pre installed and added to path
# spm-kubernetes folder added to the root dir
FROM ubuntu:14.04
RUN apt-get update
RUN apt-get install --yes git python node-gyp
RUN git clone https://github.com/IBM/spm-kubernetes
RUN apt-get install --yes curl && \
 curl -O https://nodejs.org/dist/latest-v12.x/node-v12.19.0-linux-x64.tar.gz && \
 tar xzf node-v12.19.0-linux-x64.tar.gz 
ENV PATH=/node-v12.19.0-linux-x64/bin:${PATH}
