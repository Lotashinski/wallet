function freeElement(e, name) {
    e.preventDefault();
    document.getElementById(name)?.remove()
    return false
}

document.addEventListener("keydown", function(ev) {
    const target = ev.target
    const pattern = target.pattern ?? null

    if (null == pattern) {
        return
    }

    const current = target.value
    setTimeout(() => {
        const regex = new RegExp(pattern, 'g')
        if (!regex.test(target.value)) {
            target.value = current
        }
    }, 0);

})

function checkPassword() {
    const passwordMain = document.getElementById("password")
    const passwordRepeat = document.getElementById("password_repeat")
    const passwordRepeatError = document.getElementById("password_repeat_error")
    const passwordLengthError = document.getElementById("password_lenght_error")
    
    if (passwordMain === null || passwordRepeat === null) {
        console.error("'checkPassword()' can`t find  input fields.")
        return
    } 
    
    passwordRepeatError.hidden = passwordMain.value === passwordRepeat.value 
    passwordLengthError.hidden = passwordMain.value.length > 5
     
    return passwordMain.value === passwordRepeat.value
        && passwordMain.value.length > 5
}