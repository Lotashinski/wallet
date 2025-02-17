const DARK_THEME = '.dark-theme'
const LIGHT_THEME = '.light-theme'

const rulesMap = {};

const addRule = rule => {
    if (rule.styleSheet !== undefined) {
        addRules(rule.styleSheet.rules)
    }
    
    if (rule.selectorText !== undefined) {
        rulesMap[rule.selectorText] = (rule)
    }
}

const addRules = rules => {
    for(let rule of rules)
        addRule(rule)
}

const parseStyleSheets = styleSheets => {
    for(let sh of styleSheets) 
        addRules(sh.cssRules)
}

const setTheme = theme => {
    console.log(rulesMap[theme])
    
    const ruleTheme = rulesMap[theme].style
    const root = rulesMap[":root"].style
    
    for(let prop in ruleTheme) {
        if (prop.startsWith('--color')) {
            const val = ruleTheme[prop]
            root[prop] = val
        }
    }
}

const selector = document.getElementById("theme_selector")
const currentTheme = localStorage.getItem("theme") ?? DARK_THEME;

parseStyleSheets(document.styleSheets)
setTheme(currentTheme)

