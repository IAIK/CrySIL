pkcs11_private
==============

to build shared object: 

build.xml
swig_preproc: processes pkcs11t.h for swig
swig: generates Proxy classes and jni interface
JAVA-compile: 
compile: JAVA-compile, C-compile
clean: JAVA-clean, C-clean, SWIG-clean
C-compile: führt make im C ordner aus; übergibt Variablen JAR (pfad zum .jar) und DEBUG (ob VM im debug mode anstarten)

Makefiles:
C/Makefile
	targets for swig wrapper lib and pkcs11 lib
	calls test/Makefile for test targets
	commandline Variable:
		 DEBUG=true/false (default true) 
		 JAR=path_to_JAR (default ../lib/PKCS11.jar)
C/test/Makefile


Skytrust-element compile
maven
musste java version händisch auf 1.7 setzen 
in skytrust-element-java/pom.xml
    <build>
    <plugins>
      <plugin>
        <groupId>org.apache.maven.plugins</groupId>
        <artifactId>maven-compiler-plugin</artifactId>
        <version>2.3.2</version>
        <configuration>
          <source>1.7</source>
          <target>1.7</target>
        </configuration>
      </plugin>
    </plugins>
  </build>
  
packete: (über apt)
 libjackson-json-java
 libspring-web-java
 libspring-core-java
 
 compile mit 
 		mvn compile -pl common -am
 		mvn compile -pl core -am
 		falls von maven aufgefordert iaikjce händisch installieren
