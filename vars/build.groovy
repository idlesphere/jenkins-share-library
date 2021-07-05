import com.libs.util.PodTemplates
import com.libs.util.Conf
import com.libs.util.Lint

def call() {
    // Init
    def label = "k8sagent-${UUID.randomUUID().toString()}"
    
    // Fetch global env from resource/config/global.yaml
    env["ENV_GLOBAL"] = new Conf().loadYaml(libraryResource 'config/global.yaml')
    // Init and lint configuration from build.yaml. require (git_url, git_branch, build_yaml)
    env["ENV_BUILD"]  = configuration()

    // Generate podTemplate
    // https://plugins.jenkins.io/kubernetes/
    // Pod and container template configuration
    def containers = new PodTemplates().Create(ENV_BUILD.containers)
    podTemplate(label: label, containers: containers, 
                volumes: [emptyDirVolume(mountPath: '/home/jenkins', memory: true)], 
                imagePullSecrets: [], 
                showRawYaml: false) 
    {
        node(label) {
            // https://plugins.jenkins.io/ansicolor/
            ansiColor('xterm') {
                timeout(time: timeoutMinutes, unit: 'MINUTES') {
                    log.title("Start Pipeline")
                    // sh 'printenv'
                    body()
                }
            }
        }
    }
}