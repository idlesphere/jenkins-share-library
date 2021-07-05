import com.libs.util.Conf

def call() {
    // For Github
    // def GIT_RAW_URL = GIT_URL.replace("github.com","raw.githubusercontent.com").replace(".git$","")
    // def GIT_BUILDYAML = "${GIT_RAW_URL}/${GIT_BRANCH}/${BUILD_YAML}"

    // For Bitbucket
    def GIT_RAW_URL = GIT_URL.replace(".git$","")
    def GIT_BUILDYAML = "${GIT_RAW_URL}/raw/${GIT_BRANCH}/${BUILD_YAML}"

    withCredentials([string(credentialsId: ENV_GLOBAL.credential.git, variable: 'TOKEN')]) {
        r = httpRequest consoleLogResponseBody: true, 
                        contentType: 'APPLICATION_JSON', 
                        httpMode: 'GET', 
                        url: GIT_BUILDYAML, 
                        validResponseCodes: '200',
                        customHeaders: [[name: "Authorization", value: "Bearer ${TOKEN}"]]
    }
    def config_full = new Conf().loadYaml(r.content)

    log.info("Translate build yaml")
    if (config_full == null || config_full.isEmpty() || config_full.job.isEmpty() ) {
        log.error("${GIT_BUILDYAML} is empty, please check!")
        return false
    }
    def config = new Conf().translateMap(config_full.job)

    return config
}