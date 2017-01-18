# ${projectName}

This a content package project generated using the AEM Multimodule Lazybones template.

## Building

This project uses Maven for building. Common commands:

From the root directory, run ``mvn -PautoInstallPackage clean install`` to build the bundle and content package and install to a CQ instance.

From the bundle directory, run ``mvn -PautoInstallBundle clean install`` to build *just* the bundle and install to a CQ instance.

## Installing the Uber Jar

This project depends upon the AEM 6.3 UberJar which is not yet publicly released. You can download this from the Beta site and install it using this Maven command:

    mvn install:install-file -Dfile=<filename> -DgroupId=com.adobe.aem -DartifactId=uber-jar -Dversion=6.3.0-summit -Dclassifier=apis -Dpackaging=jar

## Using with AEM Developer Tools for Eclipse

To use this project with the AEM Developer Tools for Eclipse, import the generated Maven projects via the Import:Maven:Existing Maven Projects wizard. Then add them to a running Server.

## Using with VLT

To use vlt with this project, first build and install the package to your local CQ instance as described above. Then cd to `content/src/main/content/jcr_root` and run

    vlt --credentials admin:admin checkout -f ../META-INF/vault/filter.xml --force http://localhost:4502/crx

Once the working copy is created, you can use the normal ``vlt up`` and ``vlt ci`` commands.

## Specifying CRX Host/Port

The CRX host and port can be specified on the command line with:
mvn -Dcrx.host=otherhost -Dcrx.port=5502 <goals>


