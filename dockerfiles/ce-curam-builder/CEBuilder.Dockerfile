# Image based out of ubuntu with git, python, node-gyp pre-installed
# Node 12.19.0 Pre installed and added to path
# spm-kubernetes folder added to the root dir
FROM ubuntu:14.04
RUN apt-get update
RUN apt-get install --yes git python node-gyp
RUN git clone https://github.com/IBM/spm-kubernetes
RUN apt-get install --yes curl xz-utils && \
 curl -O https://nodejs.org/dist/v14.16.1/node-v14.16.1-linux-x64.tar.xz && \
 tar -xf node-v14.16.1-linux-x64.tar.xz
ENV PATH=/node-v14.16.1-linux-x64/bin:${PATH}
