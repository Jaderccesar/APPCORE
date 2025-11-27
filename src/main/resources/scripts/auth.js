function getUserId() {
    return localStorage.getItem("userId");
}

function getUserType() {
    return localStorage.getItem("userType");
}

function requireUser() {
    const id = getUserId();
    if (!id) {
        window.location.href = "login.html";
    }
    return id;
}

function logout() {
    localStorage.removeItem("userId");
    localStorage.removeItem("userType");
    window.location.href = "index.html";
}