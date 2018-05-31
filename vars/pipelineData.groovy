import org.contralib.Utils


def buildVars(String ciMessage) {
    def utils = new Utils()
    def message = readJSON text: ciMessage
    def buildVars = [:]

    def branches = utils.setBuildBranch(message['request'][1])
    def fed_repo = utils.repoFromRequest(message['request'][0])
    def stages = 
                ["koji-build"                                     : [
                    fed_branch                : branches[0]
                    fed_repo                  : fed_repo,
                    fed_rev                   : message['rev']
                    rpm_repo                  : "${env.WORKSPACE}/${fed_repo}_repo",
            ],
             "repoquery"                                      : [
                    fed_branch                : branches[0]
                    fed_repo                  : fed_repo,
                    fed_rev                   : message['rev']
                    rpm_repo                  : "${env.WORKSPACE}/${fed_repo}_repo",
            ],
             "cloud-image-compose"                            : [
                     rpm_repo                  : "${env.WORKSPACE}/${fed_repo}_repo",
                     package                  : fed_repo,
                     branch                   : branches[1],
                     fed_branch               : branches[0],

             ],
             "nvr-verify"                                     : [
                     python3                  : "yes",
                     rpm_repo                 : "/etc/yum.repos.d/${fed_repo}",
             ],
             "package-tests"                                   : [
                     package                  : fed_repo,
                     python3                  : "yes",
                     TAG                      : "classic",
                     branch                   : branches[1],
                     build_pr_id              : (env.fed_pr_id) ?: ''
             ]
            ]
    buildVars['displayName'] = "Build #${env.BUILD_NUMBER} - Branch: ${buildVars['branch']} - Package: ${buildVars['fed_repo']}"

    return stages
}


