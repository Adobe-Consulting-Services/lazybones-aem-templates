/*
 * #%L
 * ACS AEM Lazybones Template
 * %%
 * Copyright (C) 2014 Adobe
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
import uk.co.cacoethes.util.NameType
import org.apache.commons.io.FileUtils

// Helper Functions
/**
 * Convert a String value to a Groovy truthy or falsy (blank) value.
 */
def toBoolean(String val) {
    val = val.toLowerCase()
    if (val.startsWith("n") || val.equals("false")) {
        return ''
    } else {
        return val
    }
}

/**
 * Ask the user a question expecting a yes/no/true/false response.
 */
def askBoolean(String message, String defaultValue, String propertyName) {
    String val = ask(message, defaultValue, propertyName)
    val = toBoolean(val)
    parentParams[propertyName] = val
    return val
}


/**
 * Ask the user a question expecting a yes/no/true/false response.
 */
def askBoolean(String message, String defaultValue) {
    String val = ask(message, defaultValue)
    return toBoolean(val)
}

/**
 * Ask the user a question expecting one of a set list of options.
 */
def askFromList(String message, String defaultValue, String propertyName, options) {
    String fullMessage = "${message} Choices are ${options}: "
    String val = ""
    while (!options.contains(val)) {
        val = ask(fullMessage, defaultValue, propertyName)
    }
    return val
}

/**
 * Write a String to a file with the given name in the given directory
 */
def writeToFile(File dir, String fileName, String content) {
    FileUtils.write(new File(dir, fileName), content, fileEncoding)
}

/**
 * Define a dependency map object.
 */
def dependency(groupId, artifactId, version, type = "jar", scope = "provided", classifier = "") {
    return [groupId:groupId, artifactId:artifactId, version:version, type:type, scope:scope, classifier:classifier]
}

// initialization
def props = [:]
props.rootDependencies = []
props.bundleDependencies = []
props.contentDependencies = []

// common dependencies
def osgiCore = dependency("org.osgi", "org.osgi.core", "4.2.0")
def osgiCompendium = dependency("org.osgi", "org.osgi.compendium", "4.2.0")
def scrAnnotations = dependency("org.apache.felix", "org.apache.felix.scr.annotations", "1.9.8")
def bndAnnotations = dependency("biz.aQute.bnd", "annotation", "2.3.0")
def servletApi = dependency("javax.servlet", "servlet-api", "2.5")
def commonsLang3 = dependency("org.apache.commons", "commons-lang3", "3.0.1")
def commonsLang2 = dependency("commons-lang", "commons-lang", "2.5")
def commonsCodec = dependency("commons-codec", "commons-codec", "1.5")
def commonsIo = dependency("commons-io", "commons-io", "2.4")
def jstl = dependency("com.day.commons", "day-commons-jstl", "1.1.4")
def jsp = dependency("javax.servlet.jsp", "jsp-api", "2.1")
def jcr = dependency("javax.jcr", "jcr", "2.0")
def junit = dependency("junit", "junit", "4.11", "jar", "test")
def junitAddons = dependency("junit-addons", "junit-addons", "1.4", "jar", "test")

// core dependencies which span 5.6.1 and 6.0
props.rootDependencies.addAll([osgiCore, osgiCompendium, scrAnnotations, bndAnnotations, servletApi, commonsLang3, commonsLang2, commonsCodec, commonsIo, jstl, jsp, jcr, junit, junitAddons])
props.bundleDependencies.addAll([osgiCore, osgiCompendium, scrAnnotations, bndAnnotations, servletApi, commonsLang3, commonsLang2, commonsCodec, commonsIo, jsp, jcr, junit, junitAddons])
props.contentDependencies.addAll([osgiCore, osgiCompendium, servletApi, commonsLang3, commonsLang2, jstl, jsp, jcr])

// Constants
def ACS_AEM_COMMONS_VERSION_5 = "1.10.4"
def ACS_AEM_COMMONS_VERSION_6 = "2.4.2"
def ACS_AEM_COMMONS_VERSION_62 = "3.0.2"
def AEM60_API_VERSION = "6.0.0.1"
def AEM61_API_VERSION = "6.1.0"
def AEM62_API_VERSION = "6.2.0"

def VERSION_561 = "5.6.1"
def VERSION_60 = "6.0"
def VERSION_61 = "6.1"
def VERSION_62 = "6.2"

// Core Maven Information
props.groupId = ask("Maven group ID for the generated project [com.myco]: ", "com.myco", "groupId")
props.artifactId = ask("Maven artifact ID for the generated reactor project [example-project]: ", "example-project", "artifactId")
props.useNewNamingConvention = askBoolean("Use new module naming conventions (core, ui.apps vs. bundle, content) [yes]: ", "yes", "useNewNamingConvention")
def defaultBundleArtifactId = "${props.artifactId}${props.useNewNamingConvention ? '.core' : '-bundle'}";
props.bundleArtifactId = ask("Maven artifact ID for the generated bundle project [${defaultBundleArtifactId}]: ", defaultBundleArtifactId as String, "bundleArtifactId")
def defaultContentArtifactId = "${props.artifactId}${props.useNewNamingConvention ? '.ui.apps' : '-content'}";
props.contentArtifactId = ask("Maven artifact ID for the generated content package project [${defaultContentArtifactId}]: ", defaultContentArtifactId as String, "contentArtifactId")
props.version = ask("Maven version for generated project [0.0.1-SNAPSHOT]: ", "0.0.1-SNAPSHOT", "version")
props.projectName = ask("Human readable project name [My AEM Project]: ", "My AEM Project", "projectName")
props.packageGroup = ask("Group name for Content Package [my-packages]: ", "my-packages", "packageGroup")
props.aemVersion = askFromList("Target AEM version [${VERSION_61}]: ", VERSION_61, "aemVersion", [VERSION_561, VERSION_60, VERSION_61, VERSION_62])

if (props.aemVersion == VERSION_60) {
    def apiDep = dependency("com.adobe.aem", "aem-api", AEM60_API_VERSION)
    def slf4j = dependency("org.slf4j", "slf4j-api", "1.7.6")
    def slf4jSimple = dependency("org.slf4j", "slf4j-simple", "1.7.6", "jar", "test")
    def wcmTaglib = dependency("com.day.cq.wcm", "cq-wcm-taglib", "5.6.4")
    def slingTaglib = dependency("org.apache.sling", "org.apache.sling.scripting.jsp.taglib", "2.2.0")

    props.rootDependencies.addAll([apiDep, slf4j, slf4jSimple, wcmTaglib, slingTaglib])
    props.bundleDependencies.addAll([apiDep, slf4j, slf4jSimple])
    props.contentDependencies.addAll([wcmTaglib, slingTaglib, apiDep, slf4j, slf4jSimple])
} else if (props.aemVersion == VERSION_61) {
    def apiDep = dependency("com.adobe.aem", "uber-jar", AEM61_API_VERSION, "jar", "provided", "obfuscated-apis")
    def slf4j = dependency("org.slf4j", "slf4j-api", "1.7.6")
    def slf4jSimple = dependency("org.slf4j", "slf4j-simple", "1.7.6", "jar", "test")
    def wcmTaglib = dependency("com.day.cq.wcm", "cq-wcm-taglib", "5.6.4")
    def slingTaglib = dependency("org.apache.sling", "org.apache.sling.scripting.jsp.taglib", "2.2.4")

    props.rootDependencies.addAll([wcmTaglib, slingTaglib, apiDep, slf4j, slf4jSimple])
    props.bundleDependencies.addAll([apiDep, slf4j, slf4jSimple])
    props.contentDependencies.addAll([wcmTaglib, slingTaglib, apiDep, slf4j, slf4jSimple])
} else if (props.aemVersion == VERSION_62) {
    def apiDep = dependency("com.adobe.aem", "uber-jar", AEM62_API_VERSION, "jar", "provided", "obfuscated-apis")
    def slf4j = dependency("org.slf4j", "slf4j-api", "1.7.6")
    def slf4jSimple = dependency("org.slf4j", "slf4j-simple", "1.7.6", "jar", "test")
    def wcmTaglib = dependency("com.day.cq.wcm", "cq-wcm-taglib", "5.6.4")
    def slingTaglib = dependency("org.apache.sling", "org.apache.sling.scripting.jsp.taglib", "2.2.4")

    props.rootDependencies.addAll([wcmTaglib, slingTaglib, apiDep, slf4j, slf4jSimple])
    props.bundleDependencies.addAll([apiDep, slf4j, slf4jSimple])
    props.contentDependencies.addAll([wcmTaglib, slingTaglib, apiDep, slf4j, slf4jSimple])
} else if (props.aemVersion == VERSION_561) {
    def slf4j = dependency("org.slf4j", "slf4j-api", "1.6.4")
    def slf4jSimple = dependency("org.slf4j", "slf4j-simple", "1.6.4", "jar", "test")
    def wcmTaglib = dependency("com.day.cq.wcm", "cq-wcm-taglib", "5.6.4")
    def slingTaglib = dependency("org.apache.sling", "org.apache.sling.scripting.jsp.taglib", "2.1.8")
    def slingApi = dependency("org.apache.sling", "org.apache.sling.api", "2.4.3-R1488084")
    def cqCommons = dependency("com.day.cq", "cq-commons", "5.6.4")
    def wcmCommons = dependency("com.day.cq.wcm", "cq-wcm-commons", "5.6.4")
    def wcmApi = dependency("com.day.cq.wcm", "cq-wcm-api", "5.6.6")
    def xssApi = dependency("com.adobe.granite", "com.adobe.granite.xssprotection", "5.5.24")

    props.rootDependencies.addAll([slf4j, wcmTaglib, slingTaglib, slf4jSimple, slingApi, cqCommons, wcmCommons, wcmApi, xssApi])
    props.bundleDependencies.addAll([slf4j, slf4jSimple, slingApi, cqCommons, wcmCommons, wcmApi, xssApi])
    props.contentDependencies.addAll([slf4j, slingApi, cqCommons, wcmCommons, wcmApi, xssApi, wcmTaglib, slingTaglib])
}

// Folder Names
def defaultFolderName = transformText(props.projectName, from: NameType.NATURAL, to: NameType.HYPHENATED).toLowerCase()
props.appsFolderName = ask("Folder name under /apps for components and templates [${defaultFolderName}]: ", defaultFolderName, "appsFolderName")
props.contentFolderName = ask("Folder name under /content which will contain your site [${defaultFolderName}] (Don't worry, you can always add more, this is just for some default configuration.): ", defaultFolderName, "contentFolderName")

props.createDesign = askBoolean("Create a site design (under /etc/designs)? [yes]: ", "yes", "createDesign")
if (props.createDesign) {
    props.designFolderName = ask("Folder name under /etc/designs which will contain your design settings [${defaultFolderName}] (Don't worry, you can always add more, this is just for some default configuration.): ", defaultFolderName, "designFolderName")
    props.enableDhlm = ''
}

// Client Libraries
props.createMainClientLib = askBoolean("Do you want to create 'main' client library (at /etc/clientlibs/${props.appsFolderName}/main having the category ${props.appsFolderName}.main)? [yes]: ", "yes", "createMainClientLib")
props.createDependenciesClientLib = askBoolean("Do you want to create 'dependencies' client library (at /etc/clientlibs/${props.appsFolderName}/dependencies having the category ${props.appsFolderName}.dependencies)? [yes]: ", "yes", "createDependenciesClientLib")

// Code Quality
props.enableCodeQuality = askBoolean("Include ACS standard code quality settings (PMD, Findbugs, Checkstyle, JSLint, jacoco)? [yes]: ", "yes", "enableCodeQuality")
if (props.enableCodeQuality) {
    def jsr305 = dependency("com.google.code.findbugs", "jsr305", "3.0.0")

    props.rootDependencies.add(jsr305)
    props.bundleDependencies.add(jsr305)
}

// Configurations
def createEnvRunModeConfigFolders = askBoolean("Do you want to create run-mode config directories for each environment? [yes]: ", "yes", "createRunModeConfigFolders")
def envNames = []
def createAuthorAndPublishPerEnv = ''
if (createEnvRunModeConfigFolders) {
    envNames = ask("What are the environment names (comma-delimited list)? [localdev,dev,qa,stage,prod]: ", "localdev,dev,qa,stage,prod", "envNames").split(/,/)
    for (int i = 0; i < envNames.length; i++) {
        envNames[i] = envNames[i].trim()
    }
    createAuthorAndPublishPerEnv = askBoolean("Create author and publish runmode directories per environment? [yes]: ", "yes", "createAuthorAndPublishPerEnv")
}

def defaultReconfigureRootMapping = props.aemVersion == VERSION_561 ? "yes" : "no"
props.reconfigureRootMapping = askBoolean("Do you want to set the default root mapping to /welcome (Classic UI)? [${defaultReconfigureRootMapping}]: ", defaultReconfigureRootMapping, "reconfigureRootMapping")

props.enableClassicAuthoringAsDefault = ''
if (props.aemVersion == VERSION_60) {
    props.enableClassicAuthoringAsDefault = askBoolean("Do you want to set the default authoring UI to Classic UI? [yes]: ", "yes", )
}

// ACS AEM Commons
props.includeAcsAemCommons = askBoolean("Include ACS AEM Commons as a dependency? [yes]: ", "yes", "includeAcsAemCommons")
if (props.includeAcsAemCommons) {
    def bundle;
    if (props.aemVersion == VERSION_561) {
        bundle = dependency("com.adobe.acs", "acs-aem-commons-bundle", ACS_AEM_COMMONS_VERSION_5)
    } else if (props.aemVersion == VERSION_62) {
        bundle = dependency("com.adobe.acs", "acs-aem-commons-bundle", ACS_AEM_COMMONS_VERSION_62)
    } else {
        bundle = dependency("com.adobe.acs", "acs-aem-commons-bundle", ACS_AEM_COMMONS_VERSION_6)
    }
    props.rootDependencies.add(bundle)
    props.bundleDependencies.add(bundle)
    props.contentDependencies.add(bundle)

    props.includeAcsAemCommonsSubPackage = askBoolean("Include ACS AEM Commons as a sub-package? [yes]: ", "yes", "includeAcsAemCommonsSubPackage")

    if (props.includeAcsAemCommonsSubPackage) {
        def content;
        if (props.aemVersion == VERSION_561) {
            content = dependency("com.adobe.acs", "acs-aem-commons-content", ACS_AEM_COMMONS_VERSION_5, "content-package")
        } else if (props.aemVersion == VERSION_62) {
            content = dependency("com.adobe.acs", "acs-aem-commons-content", ACS_AEM_COMMONS_VERSION_62, "content-package")
        } else {
            content = dependency("com.adobe.acs", "acs-aem-commons-content", ACS_AEM_COMMONS_VERSION_6, "content-package")
        }
        props.rootDependencies.add(content)
        props.contentDependencies.add(content)
    }
    props.enableErrorHandler = askBoolean("Do you want to enable the ACS AEM Commons Error Handler? [yes]: ", "yes", "enableErrorHandler")

    if (props.enableErrorHandler) {
        props.errorHandler = [:]
        String defaultErrorPath = "/content/${props.contentFolderName}/errors/404"
        props.errorHandler.defaultErrorsPath = ask("What is the path to your default error page? [${defaultErrorPath}]: ", defaultErrorPath);
        def defineErrorPageFolder = askBoolean("Do you want to specify a error page folder for /content/${props.contentFolderName}? [no]: ", "no", "defineErrorPageFolder")
        if (defineErrorPageFolder) {
            props.errorHandler.sitePath = "/content/${props.contentFolderName}" as String
            props.errorHandler.errorFolder = ask("What is it? [errors]: ", "errors", "errorFolder");
        }
    }

    props.enablePagesReferenceProvider = askBoolean("Do you want to enable the ACS AEM Commons Pages Reference Provider? [yes]: ", "yes", "enablePagesReferenceProvider");
    props.enableDesignReferenceProvider = askBoolean("Do you want to enable the ACS AEM Commons Design Reference Provider? [yes]: ", "yes", "enableDesignReferenceProvider");

    if (props.createDesign && (props.createMainClientLib || props.createDependenciesClientLib)) {
        props.enableDhlm = askBoolean("Do you want to enable the ACS AEM Commons Design Html Library Manager? [yes]: ", "yes", "enableDhlm")
    }

    props.enableVersionedClientLibs = askBoolean("Do you want to enable the ACS AEM Commons Versioned Clientlib Rewriter? [yes]: ", "yes", "enableVersionedClientLibs")
}

props.usingSlingModels = askBoolean("Will you be using Sling Models? [yes]: ", "yes", "usingSlingModels")
if (props.usingSlingModels) {
    def injectDep = dependency("javax.inject", "javax.inject", "1")
    props.rootDependencies.add(injectDep)
    props.bundleDependencies.add(injectDep)
    if (props.aemVersion == VERSION_561) {
        def modelsApiDependency = dependency("org.apache.sling", "org.apache.sling.models.api", "1.1.0")
        props.rootDependencies.add(modelsApiDependency)
        props.bundleDependencies.add(modelsApiDependency)
    }
    props.slingModelsPackage = ask("What package will contain your Sling Models?: ", "", "slingModelsPackage")
}

props.purgeDamWorkflows = askBoolean("Would you like automatic purging of the DAM workflows? [yes]: ", "yes", "purgeDamWorkflows");
if (props.purgeDamWorkflows) {
    props.purgeDamWorkflowRetention = ask("How many days should the DAM workflows be retained [7]: ", "7", "purgeDamWorkflowRetention")
}

println "Processing README..."
processTemplates "README.md", props

println "Processing pom files..."
processTemplates "**/pom.xml", props

println "Processing package metafiles..."
processTemplates "content/src/main/content/META-INF/vault/properties.xml", props
processTemplates "content/src/main/content/META-INF/vault/filter.xml", props
processTemplates "content/src/main/content/META-INF/vault/definition/.content.xml", props

println "Creating folders..."
def componentsDir = new File(projectDir, "content/src/main/content/jcr_root/apps/${props.appsFolderName}/components")
componentsDir.mkdirs()
new File(componentsDir, "content").mkdir()
new File(componentsDir, "page").mkdir()

def templatesDir = new File(projectDir, "content/src/main/content/jcr_root/apps/${props.appsFolderName}/templates")
templatesDir.mkdirs()

def configDir = new File(projectDir, "content/src/main/content/jcr_root/apps/${props.appsFolderName}/config")
configDir.mkdirs()
def authorConfigDir = new File(projectDir, "content/src/main/content/jcr_root/apps/${props.appsFolderName}/config.author")
authorConfigDir.mkdirs()
def publishConfigDir = new File(projectDir, "content/src/main/content/jcr_root/apps/${props.appsFolderName}/config.publish")
publishConfigDir.mkdirs()

if (createEnvRunModeConfigFolders) {
    for (int i = 0; i < envNames.length; i++) {
        def dir = new File(projectDir, "content/src/main/content/jcr_root/apps/${props.appsFolderName}/config.${envNames[i]}")
        dir.mkdir()
        if (createAuthorAndPublishPerEnv) {
            dir = new File(projectDir, "content/src/main/content/jcr_root/apps/${props.appsFolderName}/config.author.${envNames[i]}")
            dir.mkdir()
            dir = new File(projectDir, "content/src/main/content/jcr_root/apps/${props.appsFolderName}/config.publish.${envNames[i]}")
            dir.mkdir()
        }
    }
}

def installDir = new File(projectDir, "content/src/main/content/jcr_root/apps/${props.appsFolderName}/install")
installDir.mkdirs()
writeToFile(installDir, ".vltignore", "*.jar")

if (props.createDesign) {
    println "Creating design..."
    def designDir = new File(projectDir, "content/src/main/content/jcr_root/etc/designs/${props.designFolderName}")
    designDir.mkdirs()
    if (props.enableDhlm) {
        def headCss = '', headJs = '', bodyJs = ''
        if (props.createMainClientLib) {
            headCss = "${props.appsFolderName}.main"
            bodyJs = "${props.appsFolderName}.main"
        }
        if (props.createDependenciesClientLib) {
            headJs = "${props.appsFolderName}.dependencies"
        }

        writeToFile(designDir, ".content.xml", """\
<?xml version="1.0" encoding="UTF-8"?>
<jcr:root xmlns:sling="http://sling.apache.org/jcr/sling/1.0" xmlns:cq="http://www.day.com/jcr/cq/1.0" xmlns:jcr="http://www.jcp.org/jcr/1.0" xmlns:nt="http://www.jcp.org/jcr/nt/1.0"
    jcr:primaryType="cq:Page">
    <jcr:content
        cq:doctype="html_5"
        cq:template="/libs/wcm/core/templates/designpage"
        jcr:primaryType="cq:PageContent"
        jcr:title="${props.projectName}"
        sling:resourceType="wcm/core/components/designer">
        <clientlibs
            jcr:lastModified="{Date}2013-10-21T08:55:12.602-04:00"
            jcr:lastModifiedBy="admin"
            jcr:primaryType="nt:unstructured"
            sling:resourceType="acs-commons/components/utilities/designer/clientlibsmanager">
            <head
                jcr:primaryType="nt:unstructured"
                css="${headCss}"
                js="${headJs}"/>
            <body
                jcr:primaryType="nt:unstructured"
                js="${bodyJs}"/>
        </clientlibs>
    </jcr:content>
</jcr:root>
""")
    } else {
        writeToFile(designDir, ".content.xml", """\
<?xml version="1.0" encoding="UTF-8"?>
<jcr:root xmlns:sling="http://sling.apache.org/jcr/sling/1.0" xmlns:cq="http://www.day.com/jcr/cq/1.0" xmlns:jcr="http://www.jcp.org/jcr/1.0" xmlns:nt="http://www.jcp.org/jcr/nt/1.0"
    jcr:primaryType="cq:Page">
    <jcr:content
        cq:doctype="html_5"
        cq:template="/libs/wcm/core/templates/designpage"
        jcr:primaryType="cq:PageContent"
        jcr:title="${props.projectName}"
        sling:resourceType="wcm/core/components/designer" />
</jcr:root>
""")
    }
}

if (props.createMainClientLib || props.createDependenciesClientLib) {
    println "Creating clientlibs..."

    def clientLibFolder = new File(projectDir, "content/src/main/content/jcr_root/etc/clientlibs/${props.appsFolderName}")
    clientLibFolder.mkdirs()
    if (props.createMainClientLib) {
        def mainClientLibFolder = new File(clientLibFolder, "main")
        mainClientLibFolder.mkdirs()
        writeToFile(mainClientLibFolder, ".content.xml", """\
<?xml version="1.0" encoding="UTF-8"?>
<jcr:root xmlns:cq="http://www.day.com/jcr/cq/1.0" xmlns:jcr="http://www.jcp.org/jcr/1.0"
    jcr:primaryType="cq:ClientLibraryFolder"
    categories="${props.appsFolderName}.main"/>
""")
        writeToFile(mainClientLibFolder, "readme.txt", """\
This client library should be used to store your site's JavaScript and CSS.
In general, you should load the CSS in the head and the JS just before the end of the body.
""")
        new File(mainClientLibFolder, "css").mkdir()
        new File(mainClientLibFolder, "js").mkdir()

        writeToFile(mainClientLibFolder, "js.txt", """\
#base=js
""")
        writeToFile(mainClientLibFolder, "css.txt", """\
#base=css
""")
    }
    if (props.createDependenciesClientLib) {
        def depClientLibFolder = new File(clientLibFolder, "dependencies")
        depClientLibFolder.mkdirs()
        writeToFile(depClientLibFolder, ".content.xml", """\
<?xml version="1.0" encoding="UTF-8"?>
<jcr:root xmlns:cq="http://www.day.com/jcr/cq/1.0" xmlns:jcr="http://www.jcp.org/jcr/1.0"
    jcr:primaryType="cq:ClientLibraryFolder"
    categories="${props.appsFolderName}.dependencies"
    embed="[jquery,granite.utils,granite.jquery,cq.jquery,granite.shared,cq.shared,underscore]"/>
""")
        writeToFile(depClientLibFolder, "readme.txt", """\
This client library should be used to embed dependencies. It is pre-stocked with a handful of
common AEM dependencies, but you should modify to meet your needs. In general, this will need
to be loaded in the head of your page in order to reduce extra HTTP calls.
""")
        writeToFile(depClientLibFolder, "css.txt", "")
        writeToFile(depClientLibFolder, "js.txt", "")
    }
}

if (props.enableErrorHandler) {
    println "Enabling error handler..."
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

if (props.enableDhlm) {
    println "Enabling DHLM..."
    writeToFile(authorConfigDir, "com.adobe.acs.commons.util.impl.DelegatingServletFactoryImpl-DesignerClientLibsManager.xml", """\
<?xml version="1.0" encoding="UTF-8"?>
<jcr:root xmlns:sling="http://sling.apache.org/jcr/sling/1.0" xmlns:jcr="http://www.jcp.org/jcr/1.0"
    jcr:primaryType="sling:OsgiConfig"
    prop.target-resource-type="acs-commons/components/utilities/designer"
    sling.servlet.extensions="html"
    sling.servlet.methods="GET"
    sling.servlet.resourceTypes="wcm/core/components/designer"
    sling.servlet.selectors=""/>
""")
}

def emptyConfig = """\
<jcr:root xmlns:sling="http://sling.apache.org/jcr/sling/1.0" xmlns:jcr="http://www.jcp.org/jcr/1.0"
    jcr:primaryType="sling:OsgiConfig"/>
"""

if (props.enablePagesReferenceProvider) {
    println "Enabling Pages Reference Provider..."
    writeToFile(configDir, "com.adobe.acs.commons.wcm.impl.PagesReferenceProvider.xml", emptyConfig);
}

if (props.enableDesignReferenceProvider) {
    println "Enabling Design Reference Provider..."
    writeToFile(configDir, "com.adobe.acs.commons.wcm.impl.DesignReferenceProvider.xml", emptyConfig);
}

if (props.enableVersionedClientLibs) {
    println "Enabling Versioned Client Library Rewriter Rule..."
    def rewriterDir = new File(configDir, "rewriter")
    rewriterDir.mkdirs()
    writeToFile(rewriterDir, ".content.xml", """\
<?xml version="1.0" encoding="UTF-8"?>
<jcr:root xmlns:jcr="http://www.jcp.org/jcr/1.0" xmlns:nt="http://www.jcp.org/jcr/nt/1.0"
    jcr:primaryType="sling:Folder"/>
""");

    def defaultDir = new File(rewriterDir, "default")
    defaultDir.mkdir()
    writeToFile(defaultDir, ".content.xml", """\
<?xml version="1.0" encoding="UTF-8"?>
<jcr:root xmlns:jcr="http://www.jcp.org/jcr/1.0" xmlns:nt="http://www.jcp.org/jcr/nt/1.0"
    jcr:primaryType="nt:unstructured"
    contentTypes="[text/html]"
    enabled="{Boolean}true"
    generatorType="htmlparser"
    order="-1"
    serializerType="htmlwriter"
    transformerTypes="[linkchecker,mobile,mobiledebug,contentsync,versioned-clientlibs]">
    <transformer-mobile
        jcr:primaryType="nt:unstructured"
        component-optional="{Boolean}true"/>
    <transformer-mobiledebug
        jcr:primaryType="nt:unstructured"
        component-optional="{Boolean}true"/>
    <transformer-contentsync
        jcr:primaryType="nt:unstructured"
        component-optional="{Boolean}true"/>
    <generator-htmlparser
        jcr:primaryType="nt:unstructured"
        includeTags="[A,/A,IMG,AREA,FORM,BASE,LINK,SCRIPT,BODY,/BODY]"/>
</jcr:root>
""")
}

if (props.reconfigureRootMapping) {
    println "Enabling Root Mapping..."
    writeToFile(configDir, "com.day.cq.commons.servlets.RootMappingServlet.xml", """\
<jcr:root xmlns:sling="http://sling.apache.org/jcr/sling/1.0" xmlns:jcr="http://www.jcp.org/jcr/1.0"
    jcr:primaryType="sling:OsgiConfig"
    rootmapping.target="/welcome"/>
""");
}

if (props.aemVersion == VERSION_60 && props.enableClassicAuthoringAsDefault) {
    println "Enabling Classic UI Authoring..."
    writeToFile(configDir, "com.day.cq.wcm.core.impl.AuthoringUIModeServiceImpl.xml", """\
<jcr:root xmlns:sling="http://sling.apache.org/jcr/sling/1.0" xmlns:jcr="http://www.jcp.org/jcr/1.0"
    jcr:primaryType="sling:OsgiConfig"
    authoringUIModeService.default="CLASSIC"/>
""");
}

if (props.purgeDamWorkflows) {
    println "Enabling DAM Workflow Purging..."
    if (props.aemVersion == VERSION_561) {
        writeToFile(configDir, "com.adobe.granite.workflow.purge.Scheduler-dam.xml", """\
<jcr:root xmlns:sling="http://sling.apache.org/jcr/sling/1.0" xmlns:jcr="http://www.jcp.org/jcr/1.0"
    jcr:primaryType="sling:OsgiConfig"
    scheduledpurge.cron="0 0 2 ? * SAT *"
    scheduledpurge.daysold="${props.purgeDamWorkflowRetention}"
    scheduledpurge.modelIds="[/etc/workflow/models/dam/update_asset/jcr:content/model,/etc/workflow/models/dam-xmp-writeback/jcr:content/model]"
    scheduledpurge.name="DAM Workflows"
    scheduledpurge.workflowStatus="COMPLETED"
/>
""");
    } else {
        writeToFile(configDir, "com.adobe.granite.workflow.purge.Scheduler-dam.xml", """\
<jcr:root xmlns:sling="http://sling.apache.org/jcr/sling/1.0" xmlns:jcr="http://www.jcp.org/jcr/1.0"
    jcr:primaryType="sling:OsgiConfig"
    scheduledpurge.daysold="${props.purgeDamWorkflowRetention}"
    scheduledpurge.modelIds="[/etc/workflow/models/dam/update_asset/jcr:content/model,/etc/workflow/models/dam-xmp-writeback/jcr:content/model]"
    scheduledpurge.name="DAM Workflows"
    scheduledpurge.workflowStatus="COMPLETED"
/>
""");
    }
}

if (props.useNewNamingConvention) {
    new File(projectDir, "bundle").renameTo(new File(projectDir, "core"))
    new File(projectDir, "content").renameTo(new File(projectDir, "ui.apps"))
}