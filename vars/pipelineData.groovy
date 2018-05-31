import org.contralib.Utils


def buildVars(String ciMessage) {
    def utils = new Utils()
    def message = readJSON text: ciMessage
    def buildVars = [:]

    def (branch, fed_branch) = utils.setBuildBranch(message['request'][1])
    buildVars['fed_repo'] = utils.repoFromRequest(message['request'][0])
    buildVars['fed_branch'] = fed_branch
    buildVars['fed_rev'] = "kojitask-${message['task_id']}"
    buildVars['branch'] = branch
    buildVars['package'] = buildVars['fed_repo']
    buildVars['rpm_repo'] = "${env.WORKSPACE}/${buildVars['fed_repo']}_repo"

    return buildVars
}


