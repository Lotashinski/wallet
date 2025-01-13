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