import org.contralib.Utils


def stageVars(String ciMessage) {
    def utils = new Utils()
    def message = readJSON text: ciMessage
    def buildVars = [:]

    def branches = utils.setBuildBranch(message['request'][1])
    def fed_repo = utils.repoFromRequest(message['request'][0])
    def stages = 
                ["koji-build"                                     : [
                    PROVIDED_KOJI_TASKID      : message['task_id'],
                    fed_branch                : branches[1],
                    fed_repo                  : fed_repo,
                    fed_rev                   : message['rev'],
                    rpm_repo                  : "${env.WORKSPACE}/${fed_repo}_repo"
            ],
             "repoquery"                                      : [
                    fed_branch                : branches[1],
                    fed_repo                  : fed_repo,
                    fed_rev                   : message['rev'],
                    rpm_repo                  : "${env.WORKSPACE}/${fed_repo}_repo"
            ],
             "cloud-image-compose"                            : [
                     rpm_repo                  : "${env.WORKSPACE}/${fed_repo}_repo",
                     package                  : fed_repo,
                     branch                   : branches[0],
                     fed_branch               : branches[1]

             ],
             "nvr-verify"                                     : [
                     python3                  : "yes",
                     rpm_repo                 : "/etc/yum.repos.d/${fed_repo}",
                     TEST_SUBJECTS            : "${env.WORKSPACE}/images/test_subject.qcow2"
             ],
             "package-tests"                                   : [
                     package                  : fed_repo,
                     python3                  : "yes",
                     TAG                      : "classic",
                     branch                   : branches[0],
                     build_pr_id              : (env.fed_pr_id) ?: ''
             ]
            ]


    return stages
}

def buildVars(String ciMessage) {
    def utils = new Utils()
    def message = readJSON text: ciMessage

    def branches = utils.setBuildBranch(message['request'][1])
    def fed_repo = utils.repoFromRequest(message['request'][0])

    def vars = [:]
    vars['displayName'] = "Build #${env.BUILD_NUMBER} - Branch: ${branches[0]} - Package: ${fed_repo}"
    return vars
}

