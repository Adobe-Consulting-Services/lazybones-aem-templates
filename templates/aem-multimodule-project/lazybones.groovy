import uk.co.cacoethes.util.NameType
import org.apache.commons.io.FileUtils

def toBoolean(String val) {
    val = val.toLowerCase()
    if (val.startsWith("n") || val.equals("false")) {
        return ''
    } else {
        return val
    }
}

def askBoolean(String message, String defaultValue, String propertyName) {
    String val = ask(message, defaultValue, propertyName)
    val = toBoolean(val)
    parentParams[propertyName] = val
    return val
}

def askBoolean(String message, String defaultValue) {
    String val = ask(message, defaultValue)
    return toBoolean(val)
}

def askFromList(String message, String defaultValue, String propertyName, options) {
    String fullMessage = "${message} Choices are ${options}: "
    String val = ""
    while (!options.contains(val)) {
        val = ask(fullMessage, defaultValue, propertyName)
    }
    return val
}

def writeToFile(File dir, String fileName, String content) {
    FileUtils.write(new File(dir, fileName), content, fileEncoding)
}

def dependency(groupId, artifactId, version, type = "jar", scope = "provided") {
    return [groupId:groupId, artifactId:artifactId, version:version, type:type, scope:scope]
}

def props = [:]

// Constants
def ACS_AEM_COMMONS_VERSION = "1.7.2"
def AEM_API_VERSION = "6.0.0.1"

props.groupId = ask("Maven group ID for the generated project [com.myco]: ", "com.myco", "groupId")
props.artifactId = ask("Maven artifact ID for the generated reactor project [example-project]: ", "example-project", "artifactId")
props.version = ask("Maven version for generated project [0.0.1-SNAPSHOT]: ", "0.0.1-SNAPSHOT", "version")
props.projectName = ask("Human readable project name [My AEM Project]:", "My AEM Project", "projectName")
props.packageGroup = ask("Group name for Content Package [my-packages]: ", "my-packages", "packageGroup")
props.aemVersion = askFromList("Target AEM version [6.0]", "6.0", "aemVersion", ["5.6.1", "6.0"])

props.rootDependencies = [ ]
props.bundleDependencies = []
props.contentDependencies = []

def osgiCore = dependency("org.osgi", "org.osgi.core", "4.2.0")
def osgiCompendium = dependency("org.osgi", "org.osgi.compendium", "4.2.0")
def slf4j = dependency("org.slf4j", "slf4j-api", "1.6.4")
def scrAnnotations = dependency("org.apache.felix", "org.apache.felix.scr.annotations", "1.9.8")

props.rootDependencies.addAll([osgiCore, osgiCompendium, slf4j, scrAnnotations])
props.bundleDependencies.addAll([osgiCore, osgiCompendium, slf4j, scrAnnotations])
props.contentDependencies.addAll([osgiCore, osgiCompendium, slf4j])

def junit = dependency("junit", "junit", "4.11", "jar", "test")
props.rootDependencies.add(junit)
props.bundleDependencies.add(junit)

if (props.aemVersion == "6.0") {
    def apiDep = dependency("com.adobe.aem", "aem-api", AEM_API_VERSION)
    props.rootDependencies.add(apiDep)
    props.bundleDependencies.add(apiDep)
    props.contentDependencies.add(apiDep)
}

def defaultFolderName = transformText(props.projectName, from: NameType.NATURAL, to: NameType.HYPHENATED).toLowerCase()
props.appsFolderName = ask("Folder name under /apps for components and templates [${defaultFolderName}]: ", defaultFolderName, "appsFolderName")
props.contentFolderName = ask("Folder name under /content which will contain your site [${defaultFolderName}] (Don't worry, you can always add more, this is just for some default configuration.): ", defaultFolderName, "contentFolderName")

props.bundleArtifactId = ask("Maven artifact ID for the generated bundle project [${props.artifactId}-bundle]: ", "${props.artifactId}-bundle" as String, "bundleArtifactId")
props.contentArtifactId = ask("Maven artifact ID for the generated content package project [${props.artifactId}-content]: ", "${props.artifactId}-content" as String, "contentArtifactId")

props.includeAcsAemCommons = askBoolean("Include ACS AEM Commons as a dependency? [yes]: ", "yes", "includeAcsAemCommons")
if (props.includeAcsAemCommons) {
    def bundle = dependency("com.adobe.acs", "acs-aem-commons-bundle", ACS_AEM_COMMONS_VERSION)

    props.rootDependencies.add(bundle)
    props.bundleDependencies.add(bundle)
    props.contentDependencies.add(bundle)

    props.includeAcsAemCommonsSubPackage = askBoolean("Include ACS AEM Commons as a sub-package? [yes]: ", "yes", "includeAcsAemCommonsSubPackage")

    if (props.includeAcsAemCommonsSubPackage) {
        def content = dependency("com.adobe.acs", "acs-aem-commons-content", ACS_AEM_COMMONS_VERSION, "content-package")
        props.rootDependencies.add(content)
        props.contentDependencies.add(content)
    }
    props.enableErrorHandler = askBoolean("Do you want to enable the ACS AEM Commons Error Handler? [yes]: ", "yes", "enableErrorHandler")

    if (props.enableErrorHandler) {
        props.errorHandler = [:]
        String defaultErrorPath = "/content/${props.contentFolderName}/errors/404"
        props.errorHandler.defaultErrorsPath = ask("What is the path to your default error page? [${defaultErrorPath}]: ", defaultErrorPath);
        def defineErrorPageFolder = askBoolean("Do you want to specify a error page folder for /content/${props.contentFolderName}? [no]: ", "no")
        if (defineErrorPageFolder) {
            props.errorHandler.sitePath = "/content/${props.contentFolderName}" as String
            props.errorHandler.errorFolder = ask("What is it? [errors]: ", "errors");
        }
    }

    props.enablePagesReferenceProvider = askBoolean("Do you want to enable the ACS AEM Commons Pages Reference Provider? [yes]: ", "yes");
    props.enableDesignReferenceProvider = askBoolean("Do you want to enable the ACS AEM Commons Design Reference Provider? [yes]: ", "yes");
}

props.createRunModeConfigFolders = askBoolean("Do you want to create run-mode config directories? [yes]: ", "yes", "createRunModeConfigFolders")

processTemplates "README.md", props
processTemplates "**/pom.xml", props
processTemplates "content/src/main/content/META-INF/vault/properties.xml", props
processTemplates "content/src/main/content/META-INF/vault/filter.xml", props
processTemplates "content/src/main/content/META-INF/vault/definition/.content.xml", props

def componentsDir = new File(projectDir, "content/src/main/content/jcr_root/apps/${props.appsFolderName}/components")
componentsDir.mkdirs()
new File(componentsDir, "content").mkdir()
new File(componentsDir, "page").mkdir()

def templatesDir = new File(projectDir, "content/src/main/content/jcr_root/apps/${props.appsFolderName}/templates")
templatesDir.mkdirs()

def configDir = new File(projectDir, "content/src/main/content/jcr_root/apps/${props.appsFolderName}/config")
configDir.mkdirs()

def installDir = new File(projectDir, "content/src/main/content/jcr_root/apps/${props.appsFolderName}/install")
installDir.mkdirs()
writeToFile(installDir, ".vltignore", "*.jar")

if (props.enableErrorHandler) {
    def errorHandlerDir = new File(projectDir, "content/src/main/content/jcr_root/apps/sling/servlet/errorhandler")
    errorHandlerDir.mkdirs()

    writeToFile(errorHandlerDir, "404.jsp", """<%@page session="false"%><%
%><%@include file="/apps/acs-commons/components/utilities/errorpagehandler/404.jsp" %>""")
    writeToFile(errorHandlerDir, "default.jsp", """<%@page session="false"%><%
%><%@include file="/apps/acs-commons/components/utilities/errorpagehandler/default.jsp" %>""")

    def errorHandlerConfig = """\
<jcr:root xmlns:sling="http://sling.apache.org/jcr/sling/1.0" xmlns:jcr="http://www.jcp.org/jcr/1.0"
    jcr:primaryType="sling:OsgiConfig"
    enabled="{Boolean}true"
    error-page.system-path="${props.errorHandler.defaultErrorsPath}"
""";

   if (props.errorHandler.sitePath && props.errorHandler.errorFolder) {
       errorHandlerConfig += """\
    paths="[${props.errorHandler.sitePath}:${props.errorHandler.errorFolder}]"
""";
   }
   errorHandlerConfig += """\
    serve-authenticated-from-cache="{Boolean}true"/>
""";
    writeToFile(configDir, "com.adobe.acs.commons.errorpagehandler.impl.ErrorPageHandlerImpl.xml", errorHandlerConfig)
}

def emptyConfig = """\
<jcr:root xmlns:sling="http://sling.apache.org/jcr/sling/1.0" xmlns:jcr="http://www.jcp.org/jcr/1.0"
    jcr:primaryType="sling:OsgiConfig"/>
"""

if (props.enablePagesReferenceProvider) {
    writeToFile(configDir, "com.adobe.acs.commons.wcm.impl.PagesReferenceProvider.xml", emptyConfig);
}

if (props.enableDesignReferenceProvider) {
    writeToFile(configDir, "com.adobe.acs.commons.wcm.impl.DesignReferenceProvider.xml", emptyConfig);
}