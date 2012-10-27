# mSIM [![Build Status](https://secure.travis-ci.org/m1kc/msim.png)](http://travis-ci.org/m1kc/msim)

m1kc-and-Solkin's instant messaging protocol. Server.

## Building and running

To build it, type:

    ant jar
    
JDK 7 is strongly required.

To run:

    cd dist
    java -jar mSIM_Server.jar
    
or

    ant run

Database is stored in the working directory. A sample database will be created if it doesn't exist. Note that the `dist` folder is cleaned every time you build mSIM server.