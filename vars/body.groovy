import com.libs.util.Parallel
import com.libs.exe.Git

def call() {
    def tasks = [:]
    def git = new Git()

    container('jnlp') {
        gitInfo = git.gitFetch()
    }
}
