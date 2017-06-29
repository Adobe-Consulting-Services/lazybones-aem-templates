import uk.co.cacoethes.util.NameType
import org.apache.commons.io.FileUtils

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

def createPackageFolders(props){
    def base = "bundle/src/main/java"
    def tokens = props.groupId.split("\\.")

    props.pkgPlaceholder1 = tokens[1]
    props.pkgPlaceholder2 = tokens[2]

    def src = rename(base + "/apps", base + "/" + tokens[0])
    src = rename(src + "/pkgPlaceholder1", src + "/" + tokens[1])
    rename(src + "/pkgPlaceholder2", src + "/" + tokens[2])
}

def rename(src, dest){
    def folder = new File(projectDir, src)
    folder.renameTo (new File(projectDir, dest))
    return dest
}

def VERSION_62 = "6.2"
def VERSION_63 = "6.3"

def props = [:]

props.groupId = ask("Maven group ID for the generated project [com.myco.group]: ", "com.myco.group", "groupId")
props.artifactId = ask("Maven artifact ID for the generated reactor project [myco-project]: ", "myco-project", "artifactId")
props.projectName = ask("Human readable project name [My AEM Project]: ", "My AEM Project", "projectName")
props.packageGroup = ask("Group name for Content Package [my-packages]: ", "my-packages", "packageGroup")
props.aemVersion = askFromList("Target AEM version [${VERSION_63}]: ", VERSION_62, "aemVersion", [VERSION_62, VERSION_63])

def defaultFolderName = transformText(props.projectName, from: NameType.NATURAL, to: NameType.HYPHENATED).toLowerCase()
props.appsFolderName = ask("Folder name under /apps for extensions [${defaultFolderName}]: ", defaultFolderName, "appsFolderName")

if (props.aemVersion == VERSION_62) {
    props.apiDependency = "6.2.0"
}else if (props.aemVersion == VERSION_63) {
    props.apiDependency = "6.3.0"
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
createPackageFolders(props)
processTemplates "**/*.java", props

def contentFolderDir = new File(projectDir, "content/src/main/content/jcr_root/apps/${props.appsFolderName}")
contentFolderDir.mkdirs()

def installDir = new File(projectDir, "content/src/main/content/jcr_root/apps/${props.appsFolderName}/install")
installDir.mkdirs()
writeToFile(installDir, ".vltignore", "*.jar")

def clientLibFolder = new File(projectDir, "content/src/main/content/jcr_root/apps/${props.appsFolderName}/clientlib")
clientLibFolder.mkdirs()

writeToFile(clientLibFolder, ".content.xml", """\
<?xml version="1.0" encoding="UTF-8"?>
<jcr:root xmlns:cq="http://www.day.com/jcr/cq/1.0" xmlns:jcr="http://www.jcp.org/jcr/1.0"
    jcr:primaryType="cq:ClientLibraryFolder"
    dependencies="[underscore]"
    categories="[cq.authoring.dialog.all]"/>
""")

writeToFile(clientLibFolder, "readme.txt", """\
This client library should be used to store your site's JavaScript and CSS.
In general, you should load the CSS in the head and the JS just before the end of the body.
""")

writeToFile(clientLibFolder, "js.txt", "")
writeToFile(clientLibFolder, "css.txt", "")