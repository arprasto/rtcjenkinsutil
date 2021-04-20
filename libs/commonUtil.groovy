import java.time.*
import java.time.format.DateTimeFormatter

def generateTagForRepo(TAG_PREFIX)
{

    def now = LocalDateTime.now()
    def buildDate = now.format(DateTimeFormatter.ofPattern("yy.MM"))
    def buildNumber = currentBuild.number
    def tagName = "${TAG_PREFIX}-${buildDate}.${buildNumber}"
    return tagName
}

def setBaseEnv()
{
    /*env.CURAM_DIR="${env.WORKSPACE}@2/spm-code"
    env.RESOURCE_HOME="${env.WORKSPACE}@2/spm-resources/devops-jenkins-pipeline"
    env.JAVAMAIL_HOME="${env.RESOURCE_HOME}/resources/dependencies"
    env.RELEASE_PATH = "${env.WORKSPACE}@2/spm-code/EJBServer/release/"
    env.STATIC_CONTENT= "${env.WORKSPACE}@2/spm-code/webclient/build"
    env.RELEASE_FOLDER="${env.WORKSPACE}@2/resources"
    env.SPM_HOME="${env.WORKSPACE}@2/dockerstage"
    env.BASE_IMAGES="${env.DOCKER_REGISTRY}/${env.BASE_IMAGES_PATH}"
    env.CURAM_IMAGES="${env.DOCKER_REGISTRY}/${env.CURAM_IMAGES_PATH}/${params.BRANCH}"
    env.SERVER_DIR="${env.CURAM_DIR}/EJBServer"
    env.CLIENT_DIR="${env.CURAM_DIR}/webclient"
    env.PATH="${env.PATH}:${env.HELM_HOME}"*/
}


def loadConfiguration(PATH)
{
    properties = new Properties()
    def propertiesFile = new File(PATH)
    properties.load(propertiesFile.newDataInputStream())
    Set<Object> keys = properties.keySet();
    for(Object k:keys){
        String key = (String)k;
        String value =(String) properties.getProperty(key)

        env."${key}" = "${value}"
    }
}

def downloadSourceCode(USE_TAG){
    if ( env.SCM_TYPE == "GIT" ) {
        dir(env.CURAM_DIR) {
            def gitUtil = new gitUtility()
            gitUtil.getGITSource(env.SOURCE_REPO,env.SOURCE_CREDS,USE_TAG)
        }

    }
    else if ( env.SCM_TYPE == "RTC" ) {
        dir(env.CURAM_DIR) {
            // TODO : create appropriate util for RTC
        }

    }
    else {

        // Implement custom code repo retrieval

    }
}


def getRTCSource(SOURCE_REPO,CRED_SOURCE)
{

}
def buildCommand(BUILD_FOLDER,COMMAND)
{
    dir(BUILD_FOLDER){
        sh """
            #!/bin/bash
            chmod +x build.sh
            ./build.sh ${COMMAND}
        """
    }
}




def dockerlogin()
{
    withCredentials([usernamePassword(credentialsId: env.DOCKER_CREDS, passwordVariable: 'DOCKER_PASS', usernameVariable: 'DOCKER_REG')])
    {
        echo "Logging into docker repo"
        sh """ docker login ${env.DOCKER_REGISTRY} -u=$DOCKER_REG -p=$DOCKER_PASS """
    }
}

def dockerbuild(IMAGE,FILE,tagName,BUILD_ARG)
{
    echo "create ${IMAGE} image"
	dir('dockerfiles'){
	    if(IMAGE != 'httpd' && IMAGE != 'uawebapp'){
	        dir('Liberty'){
		    if(BUILD_ARG != '')
		    sh """ docker build --tag ${CURAM_IMAGES}/${IMAGE}:${tagName} --file ${FILE}.Dockerfile ${BUILD_ARG} . """
		    else
		    sh """ docker build --tag ${CURAM_IMAGES}/${IMAGE}:${tagName} --file ${FILE}.Dockerfile . """
		}
	    }
	    else{
		dir('HTTPServer'){
		    sh """ docker build --tag ${CURAM_IMAGES}/${IMAGE}:${tagName} --file ${FILE}.Dockerfile ${BUILD_ARG} . """
		}
	    }
	}

}

def dockerpush(IMAGE,tagName)
{
    echo "push ${IMAGE} image"
    sh """docker push  ${CURAM_IMAGES}/${IMAGE}:${tagName}"""
}

def scale(POD_NAME)
{
    sh """ kubectl scale --replicas=0 ${POD_NAME} --kubeconfig ~/kubeconfig """
}


def updateProperties(UPDATE_FILE_PATH,PREFIX_PROPERTY){
    def workspace = env.WORKSPACE
	def JOB_CONFIG= "${workspace}/config/jobs/${JOB_NAME}-config.properties"
    echo "${JOB_CONFIG}"
    echo "reading job properties"
    try{
        def properties = new Properties()
        File propertiesFile = new File(JOB_CONFIG)
        properties.load(propertiesFile.newDataInputStream())

        echo " reading Bootstrap properties"

        def updateFileproperties = new Properties()

        File readPropFile = new File(UPDATE_FILE_PATH)
        updateFileproperties.load(readPropFile.newDataInputStream())
        def appserverProperties = new Properties()

        echo "changing properties in ${UPDATE_FILE_PATH}"
        Set<Object> keys = properties.keySet();


        for(Object k:keys){
            String key = (String)k;

            if(key.startsWith(PREFIX_PROPERTY)){
                String splitKey = key.substring(PREFIX_PROPERTY.length())
                String value = properties.getProperty(key)

                echo " setting ${splitKey} with value ${value} "
                updateFileproperties.setProperty(splitKey, value)
            }
        }
        echo "After update"
        //echo "${updateFileproperties}"
        echo "Writing file Bootstrap"
        File writeFileOut = new File(UPDATE_FILE_PATH)
        updateFileproperties.store(writeFileOut.newWriter(),null)
    }  catch(FileNotFoundException ex) {

        echo "NO property file found or property file with the wrong name,using existing properties"

    }
}

def tagOnComplete(){

    if ( env.SCM_TYPE == "GIT" ) {
        def gitUtil =  new gitUtility()
        gitUtil.createTAGOnRepo()
    }

}
