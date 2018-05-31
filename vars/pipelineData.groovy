import org.contralib.Utils


def buildVars(String ciMessage) {
    def utils = new Utils()
    def message = readJSON text: ciMessage
    def buildVars = [:]

    def branches = utils.setBuildBranch(message['request'][1])
    buildVars['fed_repo'] = utils.repoFromRequest(message['request'][0])
    buildVars['fed_branch'] = branches[1]
    buildVars['fed_rev'] = "kojitask-${message['task_id']}"
    buildVars['branch'] = branches[0]
    buildVars['package'] = buildVars['fed_repo']
    buildVars['package_name'] = buildVars['fed_repo']
    buildVars['rpm_repo'] = "${env.WORKSPACE}/${buildVars['fed_repo']}_repo"
    buildVars['displayName'] = buildVars['fed_repo']

    return buildVars
}


