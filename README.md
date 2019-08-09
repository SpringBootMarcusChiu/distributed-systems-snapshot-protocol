## Configuration Files
- /config directory is where the application would read the configuration.txt file
- /output directory is where the application would output the snapshots to

## Installing and Starting Server
- install Java Version 1.8
- if maven binary is installed
  - <code>mvn clean package</code>
  - <code>mvn spring-boot:run -Drun.arguments=--node.id=0</code>
- if no maven binary is installed, use the maven wrapper provided
  - <code>./mvnw spring-boot:run clean package</code>
  - <code>./mvnw spring-boot:run -Drun.arguments=--node.id=0</code>
  - the command above will both:
    - download the dependent java libraries
    - then start the application
    
- running it on Google Cloud Compute
  - if you have own truststore configured
    - <code>./mvnw -Djavax.net.ssl.trustStore=/home/marcus/mytruststore -Djavax.net.ssl.trustStorePassword=foobar clean package</code>
    - <code>./mvnw -Djavax.net.ssl.trustStore=/home/marcus/mytruststore -Djavax.net.ssl.trustStorePassword=foobar spring-boot:run -Drun.arguments=--node.id=0</code>
  - if you don't have truststore (https://stackoverflow.com/questions/21252800/how-to-tell-maven-to-disregard-ssl-errors-and-trusting-all-certs
    - -Dmaven.wagon.http.ssl.insecure=true - enable use of relaxed SSL check for user generated certificates
    - -Dmaven.wagon.http.ssl.allowall=true - enable match of the server's X.509 certificate with hostname. If disabled, a browser like check will be used
    - -Dmaven.wagon.http.ssl.ignore.validity.dates=true
      - <code>./mvnw -Dmaven.wagon.http.ssl.insecure=true clean package</code>
      - <code>./mvnw -Dmaven.wagon.http.ssl.insecure=true spring-boot:run -Drun.arguments=--node.id=0</code>