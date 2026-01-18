const storedData = sessionStorage.getItem("userData");
let isUserDelete = false;
let username = null;
console.log(storedData);
if (storedData) {
  const parsedData = JSON.parse(storedData);
  console.log(parsedData.username);
  username = parsedData.username;
}

const userActions = document.getElementById("user-actions");
const loginDialog = document.getElementById("login-dialog");
const defaultUsername = "Gast";

if (username) {
  document.getElementById("username").innerText = username;
  userActions.style.display = "block";
  loginDialog.style.display = "none";
} else {
  document.getElementById("username").innerText = defaultUsername;
  userActions.style.display = "none";
  loginDialog.style.display = "block";
}

const settingsDialog = document.getElementById("settings-dialog");

function openSettings() {
  settingsDialog.style.display = "block";
}

function closeSettings() {
  closeConfirmDialog();
  settingsDialog.style.display = "none";
}

async function logout() {
  try {
    const response = await fetch("http://localhost:8080/api/logout", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: storedData,
    });

    console.log("Response status:", response);

    if (response.ok) {
      sessionStorage.removeItem("userData");
      window.location.reload();
    } else {
      alert("Fehler: Ausloggen fehlgeschlagen");
    }
  } catch (error) {
    alert("Fehler beim Verbinden mit dem Server: " + error.message);
  }
}

async function deleteUser() {
  try {
    console.log("Delete:", JSON.parse(storedData));
    const response = await fetch("http://localhost:8080/api/delete", {
      method: "DELETE",
      headers: {
        "Content-Type": "application/json",
        Authorization: "Bearer " + storedData,
      },
    });

    console.log("Response status:", response);

    if (response.ok) {
      sessionStorage.removeItem("userData");
      window.location.reload();
      alert("Benutzerkonto gelöscht");
    } else {
      alert("Fehler: Löschen fehlgeschlagen");
    }
  } catch (error) {
    alert("Fehler beim Verbinden mit dem Server: " + error.message);
  } finally {
    document.getElementById("confirm-dialog").style.display = "none";
  }
}

function openConfirmDialog() {
  isUserDelete = true;
  document.getElementById("confirm-actions").style.display = "block";
  document.getElementById("user-actions").style.display = "none";
}

function confirmAction() {
  if (isUserDelete) {
    deleteUser();
  }
}

function closeConfirmDialog() {
  isUserDelete = false;
  document.getElementById("confirm-actions").style.display = "none";
  if (username) {
    document.getElementById("user-actions").style.display = "block";
  }
}
