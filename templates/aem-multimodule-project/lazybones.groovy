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
def ACS_AEM_COMMONS_VERSION = "1.7.4"
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

// common across 5.6.1 and 6.0
def osgiCore = dependency("org.osgi", "org.osgi.core", "4.2.0")
def osgiCompendium = dependency("org.osgi", "org.osgi.compendium", "4.2.0")
def scrAnnotations = dependency("org.apache.felix", "org.apache.felix.scr.annotations", "1.9.8")
def servletApi = dependency("javax.servlet", "servlet-api", "2.5")
def commonsLang3 = dependency("org.apache.commons", "commons-lang3", "3.0.1")
def commonsLang2 = dependency("commons-lang", "commons-lang", "2.5")
def commonsCodec = dependency("commons-codec", "commons-codec", "1.5")
def commonsIo = dependency("commons-io", "commons-io", "2.4")
def jstl = dependency("com.day.commons", "day-commons-jstl", "1.1.4")
def jsp = dependency("javax.servlet.jsp", "jsp-api", "2.1")
def jcr = dependency("javax.jcr", "jcr", "2.0")

props.rootDependencies.addAll([osgiCore, osgiCompendium, scrAnnotations, servletApi, commonsLang3, commonsLang2, commonsCodec, commonsIo, jstl, jsp, jcr])
props.bundleDependencies.addAll([osgiCore, osgiCompendium, scrAnnotations, servletApi, commonsLang3, commonsLang2, commonsCodec, commonsIo, jsp, jcr])
props.contentDependencies.addAll([osgiCore, osgiCompendium, servletApi, commonsLang3, commonsLang2, jstl, jsp, jcr])

def junit = dependency("junit", "junit", "4.11", "jar", "test")
def junitAddons = dependency("junit-addons", "junit-addons", "1.4", "jar", "test")
props.rootDependencies.addAll([junit, junitAddons])
props.bundleDependencies.addAll([junit, junitAddons])

if (props.aemVersion == "6.0") {
    def apiDep = dependency("com.adobe.aem", "aem-api", AEM_API_VERSION)
    def slf4j = dependency("org.slf4j", "slf4j-api", "1.7.6")
    def slf4jSimple = dependency("org.slf4j", "slf4j-simple", "1.7.6", "jar", "test")
    def wcmTaglib = dependency("com.day.cq.wcm", "cq-wcm-taglib", "5.6.4")
    def slingTaglib = dependency("org.apache.sling", "org.apache.sling.scripting.jsp.taglib", "2.2.0")

    props.rootDependencies.addAll([apiDep, slf4j, slf4jSimple, wcmTaglib, slingTaglib])
    props.bundleDependencies.addAll([apiDep, slf4j, slf4jSimple])
    props.contentDependencies.addAll([apiDep, slf4j, wcmTaglib, slingTaglib])
} else if (props.aemVersion == "5.6.1") {
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

props.bundleArtifactId = ask("Maven artifact ID for the generated bundle project [${props.artifactId}-bundle]: ", "${props.artifactId}-bundle" as String, "bundleArtifactId")
props.contentArtifactId = ask("Maven artifact ID for the generated content package project [${props.artifactId}-content]: ", "${props.artifactId}-content" as String, "contentArtifactId")

def defaultFolderName = transformText(props.projectName, from: NameType.NATURAL, to: NameType.HYPHENATED).toLowerCase()
props.appsFolderName = ask("Folder name under /apps for components and templates [${defaultFolderName}]: ", defaultFolderName, "appsFolderName")
props.contentFolderName = ask("Folder name under /content which will contain your site [${defaultFolderName}] (Don't worry, you can always add more, this is just for some default configuration.): ", defaultFolderName, "contentFolderName")

props.createDesign = askBoolean("Create a site design (under /etc/designs)? [yes]: ", "yes", "createDesign")
if (props.createDesign) {
    props.designFolderName = ask("Folder name under /etc/designs which will contain your design settings [${defaultFolderName}] (Don't worry, you can always add more, this is just for some default configuration.): ", defaultFolderName, "designFolderName")
    props.enableDhlm = ''
}

props.createMainClientLib = askBoolean("Do you want to create 'main' client library (at /etc/clientlibs/${props.appsFolderName}/main having the category ${props.appsFolderName}.main)? [yes]: ", "yes", "createMainClientLib")
props.createDependenciesClientLib = askBoolean("Do you want to create 'dependencies' client library (at /etc/clientlibs/${props.appsFolderName}/dependencies having the category ${props.appsFolderName}.dependencies)? [yes]: ", "yes", "createDependenciesClientLib")

props.enableCodeQuality = askBoolean("Include ACS standard code quality settings (PMD, Findbugs, Checkstyle, JSLint, jacoco)? [yes]: ", "yes", "enableCodeQuality")
if (props.enableCodeQuality) {
    def jsr305 = dependency("com.google.code.findbugs", "jsr305", "3.0.0")

    props.rootDependencies.add(jsr305)
    props.bundleDependencies.add(jsr305)
}

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
    categories="${props.appsFolderName}.dependencies
    embed="[jquery,granite.utils,granite.jquery,cq.jquery,granite.shared,cq.shared,underscore]"/>
""")
        writeToFile(depClientLibFolder, "readme.txt", """\
This client library should be used to embed dependencies. It is pre-stocked with a handful of
common AEM dependencies, but you should modify to meet your needs. In general, this will need
to be loaded in the head of your page in order to reduce extra HTTP calls.
""")
    }
}

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

if (props.enableDhlm) {
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
    writeToFile(configDir, "com.adobe.acs.commons.wcm.impl.PagesReferenceProvider.xml", emptyConfig);
}

if (props.enableDesignReferenceProvider) {
    writeToFile(configDir, "com.adobe.acs.commons.wcm.impl.DesignReferenceProvider.xml", emptyConfig);
}

if (props.enableVersionedClientLibs) {
    def rewriterDir = new File(configDir, "rewriter/default")
    rewriterDir.mkdirs()
    writeToFile(rewriterDir, ".content.xml", """\
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