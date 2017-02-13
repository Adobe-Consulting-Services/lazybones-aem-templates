/*
 * #%L
 * ACS AEM Lazybones Template
 * %%
 * Copyright (C) 2017 Adobe
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
 * Write a String to a file with the given name in the given subdirectory, creating if necessary
 */
def writeToFileInSubDir(File dir, String subDirName, String fileName, String content) {
    def subDir = new File(dir, subDirName);
    subDir.mkdir();
    writeToFile(subDir, fileName, content);
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

props.writeAcls = false;

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
def apiDep = dependency("com.adobe.aem", "uber-jar", "6.3.0-summit", "jar", "provided", "apis")
def slf4j = dependency("org.slf4j", "slf4j-api", "1.7.21")
def slf4jSimple = dependency("org.slf4j", "slf4j-simple", "1.7.21", "jar", "test")
def wcmTaglib = dependency("com.day.cq.wcm", "cq-wcm-taglib", "5.6.4")
def slingTaglib = dependency("org.apache.sling", "org.apache.sling.scripting.jsp.taglib", "2.2.6")

// core dependencies
props.rootDependencies.addAll([osgiCore, osgiCompendium, scrAnnotations, bndAnnotations, servletApi, commonsLang3, commonsLang2, commonsCodec, commonsIo, jstl, jsp, jcr, junit, junitAddons, wcmTaglib, slingTaglib, apiDep, slf4j, slf4jSimple])
props.bundleDependencies.addAll([osgiCore, osgiCompendium, scrAnnotations, bndAnnotations, servletApi, commonsLang3, commonsLang2, commonsCodec, commonsIo, jsp, jcr, junit, junitAddons, apiDep, slf4j, slf4jSimple])
props.contentDependencies.addAll([osgiCore, osgiCompendium, servletApi, commonsLang3, commonsLang2, jstl, jsp, jcr, wcmTaglib, slingTaglib, apiDep, slf4j, slf4jSimple])

// Core Maven Information
props.groupId = ask("Maven group ID for the generated project [com.myco]: ", "com.myco", "groupId")
props.artifactId = ask("Maven artifact ID for the generated reactor project [example-project]: ", "example-project", "artifactId")
def defaultBundleArtifactId = "${props.artifactId}.core";
props.bundleArtifactId = ask("Maven artifact ID for the generated bundle project [${defaultBundleArtifactId}]: ", defaultBundleArtifactId as String, "bundleArtifactId")
def defaultContentArtifactId = "${props.artifactId}.ui.apps";
props.contentArtifactId = ask("Maven artifact ID for the generated content package project [${defaultContentArtifactId}]: ", defaultContentArtifactId as String, "contentArtifactId")
props.version = ask("Maven version for generated project [0.0.1-SNAPSHOT]: ", "0.0.1-SNAPSHOT", "version")
props.projectName = ask("Human readable project name [My AEM Project]: ", "My AEM Project", "projectName")
props.packageGroup = ask("Group name for Content Package [my-packages]: ", "my-packages", "packageGroup")

// Folder Names
def defaultFolderName = transformText(props.projectName, from: NameType.NATURAL, to: NameType.HYPHENATED).toLowerCase()
props.appsFolderName = ask("Folder name under /apps for components and templates [${defaultFolderName}]: ", defaultFolderName, "appsFolderName")
props.contentFolderName = ask("Folder name under /content which will contain your site [${defaultFolderName}] (Don't worry, you can always add more, this is just for some default configuration.): ", defaultFolderName, "contentFolderName")

props.createDefaultPageStructureComponent = askBoolean("Would you like to create a default page structure component? [yes]:", "yes", "createDefaultPageStructureComponent")

// Create AEM 6.2 Editable Templates folders? 
props.createEditableTemplatesStructure = askBoolean("Would you like to create AEM 6.2 Editable Templates folders? [yes]: ", "yes", "createEditableTemplatesStructure");
props.confFolderName = ask("Folder name under /conf for editable templates [${defaultFolderName}]: ", defaultFolderName, "confFolderName")

props.createDesign = askBoolean("Create a site design (under /etc/designs)? [yes]: ", "yes", "createDesign")
if (props.createDesign) {
    props.designFolderName = ask("Folder name under /etc/designs which will contain your design settings [${defaultFolderName}] (Don't worry, you can always add more, this is just for some default configuration.): ", defaultFolderName, "designFolderName")
}

if (props.createDefaultPageStructureComponent && props.createEditableTemplatesStructure && props.createDesign) {
    props.createSiteRoot = askBoolean("Create a site root? [yes]: ", "yes", "createSiteRoot")
} else {
    props.createSiteRoot = false
}

// Client Libraries
props.createMainClientLib = askBoolean("Do you want to create 'main' client library (at /etc/clientlibs/${props.appsFolderName}/main having the category ${props.appsFolderName}.main)? [yes]: ", "yes", "createMainClientLib")
props.createDependenciesClientLib = askBoolean("Do you want to create 'dependencies' client library (at /etc/clientlibs/${props.appsFolderName}/dependencies having the category ${props.appsFolderName}.dependencies)? [yes]: ", "yes", "createDependenciesClientLib")


props.usingSlingModels = askBoolean("Will you be using Sling Models? [yes]: ", "yes", "usingSlingModels")
if (props.usingSlingModels) {
    def injectDep = dependency("javax.inject", "javax.inject", "1")
    props.rootDependencies.add(injectDep)
    props.bundleDependencies.add(injectDep)
    def defaultSlingModelsPackage = props.groupId + ".models";
    props.slingModelsPackage = ask("What package will contain your Sling Models?: [" + defaultSlingModelsPackage + "]", defaultSlingModelsPackage as String, "slingModelsPackage")
}

println "Processing README..."
processTemplates "README.md", props

println "Processing pom files..."
processTemplates "**/pom.xml", props

println "Processing package metafiles..."
processTemplates "ui.apps/src/main/content/META-INF/vault/properties.xml", props
processTemplates "ui.apps/src/main/content/META-INF/vault/filter.xml", props
processTemplates "ui.apps/src/main/content/META-INF/vault/definition/.content.xml", props

println "Creating folders..."
def componentsDir = new File(projectDir, "ui.apps/src/main/content/jcr_root/apps/${props.appsFolderName}/components")
componentsDir.mkdirs()
new File(componentsDir, "content").mkdir()
def structureDir = new File(componentsDir, "structure")
structureDir.mkdir()

if (props.createDefaultPageStructureComponent) {
    def pageComponentDir = new File(structureDir, "page");
    pageComponentDir.mkdir();
    writeToFile(pageComponentDir, ".content.xml", """\
<?xml version="1.0" encoding="UTF-8"?>
<jcr:root xmlns:sling="http://sling.apache.org/jcr/sling/1.0" xmlns:cq="http://www.day.com/jcr/cq/1.0" xmlns:jcr="http://www.jcp.org/jcr/1.0"
    jcr:primaryType="cq:Component"
    jcr:title="${props.projectName} Page"
    jcr:description="Page component with ${props.projectName} specific header and footer libs"
    sling:resourceSuperType="core/wcm/components/page/v1/page"
    componentGroup=".hidden"/>
""")
    if (props.createDependenciesClientLib) {
        writeToFile(pageComponentDir, "customheaderlibs.html", """\
<sly data-sly-use.clientLib="/libs/granite/sightly/templates/clientlib.html"
     data-sly-call="\${clientlib.css @ categories='${props.appsFolderName}.dependencies'}"/>
""")
    }
}

def templatesDir = new File(projectDir, "ui.apps/src/main/content/jcr_root/apps/${props.appsFolderName}/templates")
templatesDir.mkdirs()

def configDir = new File(projectDir, "ui.apps/src/main/content/jcr_root/apps/${props.appsFolderName}/config")
configDir.mkdirs()
def authorConfigDir = new File(projectDir, "ui.apps/src/main/content/jcr_root/apps/${props.appsFolderName}/config.author")
authorConfigDir.mkdirs()
def publishConfigDir = new File(projectDir, "ui.apps/src/main/content/jcr_root/apps/${props.appsFolderName}/config.publish")
publishConfigDir.mkdirs()

def installDir = new File(projectDir, "ui.apps/src/main/content/jcr_root/apps/${props.appsFolderName}/install")
installDir.mkdirs()
writeToFile(installDir, ".vltignore", "*.jar")

// Creating AEM 6.2 Editable Templates folders 
if (props.createEditableTemplatesStructure) {
    println "Creating AEM 6.2 Editable Templates folders..."

    def confDir = new File(projectDir, "ui.apps/src/main/content/jcr_root/conf")
    confDir.mkdirs()
    writeToFile(confDir, ".content.xml", """\
<?xml version="1.0" encoding="UTF-8"?>
<jcr:root xmlns:sling="http://sling.apache.org/jcr/sling/1.0" xmlns:jcr="http://www.jcp.org/jcr/1.0" xmlns:rep="internal"
    jcr:mixinTypes="[rep:AccessControllable]"
    jcr:primaryType="sling:Folder"/>
""")

    def confProjectDir = new File(confDir, "${props.confFolderName}")
    confProjectDir.mkdirs()
    writeToFile(confProjectDir, ".content.xml", """\
<?xml version="1.0" encoding="UTF-8"?>
<jcr:root xmlns:sling="http://sling.apache.org/jcr/sling/1.0" xmlns:jcr="http://www.jcp.org/jcr/1.0"
    jcr:primaryType="sling:Folder"
    jcr:title="${props.projectName}"/>
""")

    def confSettingsDir = new File(confProjectDir, "settings")
    confSettingsDir.mkdirs()
    writeToFile(confSettingsDir, ".content.xml", """\
<?xml version="1.0" encoding="UTF-8"?>
<jcr:root xmlns:sling="http://sling.apache.org/jcr/sling/1.0" xmlns:jcr="http://www.jcp.org/jcr/1.0"
    jcr:primaryType="sling:Folder"/>
""")

    def confWcmDir = new File(confSettingsDir, "wcm")
    confWcmDir.mkdirs()
    writeToFile(confWcmDir, ".content.xml", """\
<?xml version="1.0" encoding="UTF-8"?>
<jcr:root xmlns:cq="http://www.day.com/jcr/cq/1.0" xmlns:jcr="http://www.jcp.org/jcr/1.0"
    jcr:primaryType="cq:Page"/>
""")

    def confCqPageContent = """\
<?xml version="1.0" encoding="UTF-8"?>
<jcr:root xmlns:cq="http://www.day.com/jcr/cq/1.0" xmlns:jcr="http://www.jcp.org/jcr/1.0" xmlns:rep="internal"
    jcr:mixinTypes="[rep:AccessControllable]"
    jcr:primaryType="cq:Page"/>
""";

    def confRepPolicyTemplatesContent = """\
<?xml version="1.0" encoding="UTF-8"?>
<jcr:root xmlns:crx="http://www.day.com/crx/1.0" xmlns:jcr="http://www.jcp.org/jcr/1.0" xmlns:rep="internal"
    jcr:primaryType="rep:ACL">
    <allow
        jcr:primaryType="rep:GrantACE"
        rep:principalName="everyone"
        rep:privileges="{Name}[jcr:read]"/>
    <allow0
        jcr:primaryType="rep:GrantACE"
        rep:principalName="content-authors"
        rep:privileges="{Name}[crx:replicate]"/>
    <allow1
        jcr:primaryType="rep:GrantACE"
        rep:principalName="template-authors"
        rep:privileges="{Name}[jcr:versionManagement,crx:replicate,rep:write,jcr:lockManagement]"/>
    <allow2
        jcr:primaryType="rep:GrantACE"
        rep:principalName="version-manager-service"
        rep:privileges="{Name}[jcr:versionManagement,rep:write]"/>
</jcr:root>
""";

    def confTemplatesDir = new File(confWcmDir, "templates")
    confTemplatesDir.mkdirs()
    writeToFile(confTemplatesDir, ".content.xml", confCqPageContent)
    if (props.writeAcls) {
        writeToFile(confTemplatesDir, "_rep_policy.xml", confRepPolicyTemplatesContent)
    }

    def confPoliciesDir = new File(confWcmDir, "policies")
    confPoliciesDir.mkdirs()
    writeToFile(confPoliciesDir, ".content.xml", confCqPageContent)
    if (props.writeAcls) {
        writeToFile(confPoliciesDir, "_rep_policy.xml", confRepPolicyTemplatesContent)
    }

    def confTemplateTypesDir = new File(confWcmDir, "template-types")
    confTemplateTypesDir.mkdirs()
    writeToFile(confTemplateTypesDir, ".content.xml", confCqPageContent)
    if (props.writeAcls) {
        writeToFile(confTemplateTypesDir, "_rep_policy.xml", """\
<?xml version="1.0" encoding="UTF-8"?>
<jcr:root xmlns:jcr="http://www.jcp.org/jcr/1.0" xmlns:rep="internal"
    jcr:primaryType="rep:ACL">
    <allow
        jcr:primaryType="rep:GrantACE"
        rep:principalName="template-authors"
        rep:privileges="{Name}[jcr:read]"/>
</jcr:root>
""")
    }
    def redirectPageTemplateTypeDir = new File(confTemplateTypesDir, "redirect-page");
    redirectPageTemplateTypeDir.mkdir();
    writeToFile(redirectPageTemplateTypeDir, ".content.xml", """\
<?xml version="1.0" encoding="UTF-8"?>
<jcr:root xmlns:cq="http://www.day.com/jcr/cq/1.0" xmlns:jcr="http://www.jcp.org/jcr/1.0"
    jcr:primaryType="cq:Template">
    <jcr:content
        jcr:description="Template for pages that redirect"
        jcr:primaryType="cq:PageContent"
        jcr:title="${props.projectName} Redirect Page"
        status="enabled"/>
</jcr:root>
""")
    writeToFileInSubDir(redirectPageTemplateTypeDir, "initial", ".content.xml", """\
<?xml version="1.0" encoding="UTF-8"?>
<jcr:root xmlns:sling="http://sling.apache.org/jcr/sling/1.0" xmlns:cq="http://www.day.com/jcr/cq/1.0" xmlns:jcr="http://www.jcp.org/jcr/1.0"
    jcr:primaryType="cq:Page">
    <jcr:content
        jcr:primaryType="cq:PageContent"
        sling:redirect="{Boolean}true"
        sling:redirectStatus="{Long}302"
        sling:resourceType="foundation/components/redirect"/>
</jcr:root>
""")
    writeToFileInSubDir(redirectPageTemplateTypeDir, "policies", ".content.xml", """\
<?xml version="1.0" encoding="UTF-8"?>
<jcr:root xmlns:sling="http://sling.apache.org/jcr/sling/1.0" xmlns:cq="http://www.day.com/jcr/cq/1.0" xmlns:jcr="http://www.jcp.org/jcr/1.0" xmlns:nt="http://www.jcp.org/jcr/nt/1.0"
    jcr:primaryType="cq:Page">
    <jcr:content
        jcr:primaryType="nt:unstructured"
        sling:resourceType="wcm/core/components/policies/mappings"/>
</jcr:root>
""")
    writeToFileInSubDir(redirectPageTemplateTypeDir, "structure", ".content.xml", """\
<?xml version="1.0" encoding="UTF-8"?>
<jcr:root xmlns:sling="http://sling.apache.org/jcr/sling/1.0" xmlns:cq="http://www.day.com/jcr/cq/1.0" xmlns:jcr="http://www.jcp.org/jcr/1.0"
    jcr:primaryType="cq:Page">
    <jcr:content
        jcr:primaryType="cq:PageContent"
        sling:resourceType="foundation/components/redirect"/>
</jcr:root>
""")

    if (props.createDefaultPageStructureComponent) {
        def emptyPageTemplateTypeDir = new File(confTemplateTypesDir, "empty-page");
        emptyPageTemplateTypeDir.mkdir();
        writeToFile(emptyPageTemplateTypeDir, ".content.xml", """\
<?xml version="1.0" encoding="UTF-8"?>
<jcr:root xmlns:cq="http://www.day.com/jcr/cq/1.0" xmlns:jcr="http://www.jcp.org/jcr/1.0"
    jcr:primaryType="cq:Template">
    <jcr:content
        jcr:description="Generic template for web pages"
        jcr:primaryType="cq:PageContent"
        jcr:title="${props.projectName} Empty Page"
        status="enabled"/>
</jcr:root>
""")
        writeToFileInSubDir(emptyPageTemplateTypeDir, "initial", ".content.xml", """\
<?xml version="1.0" encoding="UTF-8"?>
<jcr:root xmlns:sling="http://sling.apache.org/jcr/sling/1.0" xmlns:cq="http://www.day.com/jcr/cq/1.0" xmlns:jcr="http://www.jcp.org/jcr/1.0"
    jcr:primaryType="cq:Page">
    <jcr:content
        jcr:primaryType="cq:PageContent"
        sling:resourceType="${props.appsFolderName}/components/structure/page"/>
</jcr:root>
""")
        writeToFileInSubDir(emptyPageTemplateTypeDir, "policies", ".content.xml", """\
<?xml version="1.0" encoding="UTF-8"?>
<jcr:root xmlns:sling="http://sling.apache.org/jcr/sling/1.0" xmlns:cq="http://www.day.com/jcr/cq/1.0" xmlns:jcr="http://www.jcp.org/jcr/1.0" xmlns:nt="http://www.jcp.org/jcr/nt/1.0"
    jcr:primaryType="cq:Page">
    <jcr:content
        jcr:primaryType="nt:unstructured"
        sling:resourceType="wcm/core/components/policies/mappings"/>
</jcr:root>
""")
        writeToFileInSubDir(emptyPageTemplateTypeDir, "structure", ".content.xml", """\
<?xml version="1.0" encoding="UTF-8"?>
<jcr:root xmlns:sling="http://sling.apache.org/jcr/sling/1.0" xmlns:cq="http://www.day.com/jcr/cq/1.0" xmlns:jcr="http://www.jcp.org/jcr/1.0" xmlns:nt="http://www.jcp.org/jcr/nt/1.0"
    jcr:primaryType="cq:Page">
    <jcr:content
        cq:deviceGroups="[/etc/mobile/groups/responsive]"
        jcr:primaryType="cq:PageContent"
        sling:resourceType="${props.appsFolderName}/components/structure/page">
        <root
            jcr:primaryType="nt:unstructured"
            sling:resourceType="wcm/foundation/components/responsivegrid"/>
        <cq:responsive jcr:primaryType="nt:unstructured">
            <breakpoints jcr:primaryType="nt:unstructured">
                <phone
                    jcr:primaryType="nt:unstructured"
                    title="Smaller Screen"
                    width="{Long}650"/>
                <tablet
                    jcr:primaryType="nt:unstructured"
                    title="Tablet"
                    width="{Long}1200"/>
            </breakpoints>
        </cq:responsive>
    </jcr:content>
</jcr:root>
""")
    }
}

if (props.createDesign) {
    println "Creating design..."
    def designDir = new File(projectDir, "ui.apps/src/main/content/jcr_root/etc/designs/${props.designFolderName}")
    designDir.mkdirs()
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

if (props.createSiteRoot) {
    println "Creating site root..."
    def contentDir = new File(projectDir, "ui.apps/src/main/content/jcr_root/content/${props.contentFolderName}")
    contentDir.mkdirs()
    writeToFile(contentDir, ".content.xml", """\
<?xml version="1.0" encoding="UTF-8"?>
<jcr:root xmlns:sling="http://sling.apache.org/jcr/sling/1.0" xmlns:cq="http://www.day.com/jcr/cq/1.0" xmlns:jcr="http://www.jcp.org/jcr/1.0" xmlns:nt="http://www.jcp.org/jcr/nt/1.0"
    jcr:primaryType="cq:Page">
    <jcr:content
        cq:allowedTemplates="[/conf/${props.confFolderName}/settings/wcm/templates/.*]"
        cq:designPath="/etc/designs/${props.designFolderName}"
        cq:deviceGroups="[/etc/mobile/groups/responsive]"
        jcr:primaryType="cq:PageContent"
        jcr:title="${props.projectName}"
        sling:redirect="true"
        sling:redirectStatus="{Long}302"
        sling:resourceType="foundation/components/redirect"
        redirectTarget="/content/${props.contentFolderName}/en">
    </jcr:content>
</jcr:root>
""")
}

if (props.createMainClientLib || props.createDependenciesClientLib) {
    println "Creating clientlibs..."

    def clientLibFolder = new File(projectDir, "ui.apps/src/main/content/jcr_root/etc/clientlibs/${props.appsFolderName}")
    clientLibFolder.mkdirs()
    if (props.createMainClientLib) {
        def mainClientLibFolder = new File(clientLibFolder, "main")
        mainClientLibFolder.mkdirs()
        writeToFile(mainClientLibFolder, ".content.xml", """\
<?xml version="1.0" encoding="UTF-8"?>
<jcr:root xmlns:cq="http://www.day.com/jcr/cq/1.0" xmlns:jcr="http://www.jcp.org/jcr/1.0"
    jcr:primaryType="cq:ClientLibraryFolder"
    categories="[${props.appsFolderName}.main]"
    embed="[core.wcm.components.image.v1]"/>
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
grid.less
""")
        writeToFileInSubDir(mainClientLibFolder, "css", "grid.less", """\
@import (once) "/etc/clientlibs/wcm/foundation/grid/grid_base.less";

@max_col: 12; /* maximum amount of grid cells */

/* default breakpoint */
.aem-Grid {
    .generate-grid(default, @max_col);
}

/* phone breakpoint */
@media (max-width: 768px) {
    .aem-Grid {
        .generate-grid(phone, @max_col);
    }
}

/* tablet breakpoint */
@media (min-width: 769px) and (max-width: 1200px) {
    .aem-Grid {
        .generate-grid(tablet, @max_col);
    }
}
""")
    }
    if (props.createDependenciesClientLib) {
        def depClientLibFolder = new File(clientLibFolder, "dependencies")
        depClientLibFolder.mkdirs()
        writeToFile(depClientLibFolder, ".content.xml", """\
<?xml version="1.0" encoding="UTF-8"?>
<jcr:root xmlns:cq="http://www.day.com/jcr/cq/1.0" xmlns:jcr="http://www.jcp.org/jcr/1.0"
    jcr:primaryType="cq:ClientLibraryFolder"
    categories="[${props.appsFolderName}.dependencies]"
    embed="[jquery,granite.utils,granite.jquery,cq.jquery,cq.wcm.foundation,cq.wcm.foundation-main]"/>
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

if (props.usingSlingModels && props.slingModelsPackage) {
    def modelDir = new File(projectDir, "core/src/main/java/" + props.slingModelsPackage.replace('.', '/'))
    println "Creating ${modelDir}"
    modelDir.mkdirs()
}
