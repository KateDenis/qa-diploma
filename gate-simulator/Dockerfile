FROM node:latest
RUN mkdir -p /usr/src/gate-simulator
WORKDIR /usr/src/gate-simulator
COPY package.json /usr/src/gate-simulator/
RUN npm install
COPY . /usr/src/gate-simulator
EXPOSE 9999