# encoding-converter
<pre><code>
#!/bin/bash
 
JAVA_OPTS="$JAVA_OPTS -Dencoding.converter.source.dir=/Users/nices96/Desktop/convert/source"
JAVA_OPTS="$JAVA_OPTS -Dencoding.converter.target.dir=/Users/nices96/Desktop/convert/target"
JAVA_OPTS="$JAVA_OPTS -Dencoding.converter.file.extensions=java,xml,properties"
 
if [ "x$JAVA_HOME" != "x" ]; then
    JAVA="$JAVA_HOME/bin/java"
else
    JAVA="java"
fi
 
$JAVA -jar $JAVA_OPTS encoding-converter-1.0.jar
</code></pre>
