function checkLogin() {
    var username = document.getElementById("username").value;
    var password = document.getElementById("password").value;

    // Check if username and password match
    if (username === "user" && password === "password") {
        console.log("username= "+username+" password= "+password);
        window.location.href = "/searchurl"; // Redirect to /searchurl
        return false; // Prevent form submission
    } else {
        alert("Invalid username or password. Please try again.");
        return false; // Prevent form submission
    }
}

checkLogin()