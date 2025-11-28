const username = localStorage.getItem("username");
const defaultUsername = "Gast";

if (!username) {
  document.getElementById("username").innerText = defaultUsername;
} else {
  document.getElementById("username").innerText = username;
}
