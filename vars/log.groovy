import groovy.json.JsonOutput;

// Color
def red(message) {
    echo "\033[1;31m${message} \033[0m"
}

def green(message) {
    echo "\033[1;32m${message} \033[0m"
}

def blue(message) {
    echo "\033[1;34m${message} \033[0m"
}

def yellow(message) {
    echo "\033[1;33m${message} \033[0m"
}

// Log
def title(message) {
    blue "[INFO] >>> ${message}"
}

def info(message) {
    green "[INFO] ${message}"
}

def warn(message) {
    yellow "[WARN] ${message}"
}

def error(message) {
    red "[ERROR] ${message}"
}

def prettyPrint(data) {
    def json = JsonOutput.toJson(data)
    def pretty = JsonOutput.prettyPrint(json)
    println (pretty)
}

// def prettify(item) {
//     return groovy.json.JsonOutput.prettyPrint(toJson(item)).replace('\\n', System.getProperty('line.separator'))
// }

// def getColorizedString(msg, color) {
//     def colorMap = [
//         'red'   : '\u001B[31m',
//         'black' : '\u001B[30m',
//         'green' : '\u001B[32m',
//         'yellow': '\u001B[33m',
//         'blue'  : '\u001B[34m',
//         'purple': '\u001B[35m',
//         'cyan'  : '\u001B[36m',
//         'white' : '\u001B[37m',
//         'reset' : '\u001B[0m'
//     ]

//     return "${colorMap[color]}${msg}${colorMap.reset}"
// }

// def printMsg(msg, color) {
//     print getColorizedString(msg, color)
// }

// /**
//  * Print sensitivity message
//  *
//  * @param msg Message to be printed
//  * @param color Color to use for output
//  * @param replacing List with maps for deletion (passwords, logins, etc).
//  *                  The first () matching is mandatory !
//  *                  Example:
//  *                  [/ (OS_PASSWORD=)(.*?)+ /,
//  *                   / (password = )(.*?)+ /,
//  *                   / (password )(.*?) / ]
//  */
// def printSensitivityMsg(msg, color, replacing = []) {
//     for (i in replacing) {
//         msg = msg.replaceAll(i, '******')
//     }
//     printMsg(msg, color)
// }