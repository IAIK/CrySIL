pkcs11_private
==============


to build shared object: 

build.xml
swig_preproc: processes pkcs11t.h for swig
swig: generates Proxy classes and jni interface
JAVA-compile: 
compile: JAVA-compile, C-compile
clean: JAVA-clean, C-clean, SWIG-clean

skytrust

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
