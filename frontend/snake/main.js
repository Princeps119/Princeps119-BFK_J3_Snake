const storedData = sessionStorage.getItem("userData");
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

// ---- Dialog öffnen: aktuelle Werte anzeigen ----
document.getElementById("openSettings").addEventListener("click", () => {
  // immer die aktuell aktiven Werte zeigen
  // speedInput.value = gameSpeed;
  // gridSizeSelect.value = String(gridSize);
  settingsDialog.style.display = "block";
});

// ---- Speichern & Schließen: übernehmen + persistieren ----
document.getElementById("saveSettings").addEventListener("click", () => {
  // Werte lesen und validieren
  // const newSpeed = Math.max(50, Math.min(500, parseInt(speedInput.value, 10) || DEFAULTS.speed));
  // const newGridSize = parseInt(gridSizeSelect.value, 10) || DEFAULTS.gridSize;

  // aktiv übernehmen
  // gameSpeed = newSpeed;
  // gridSize = newGridSize;

  // in LocalStorage speichern
  // saveSettingsToStorage({ speed: gameSpeed, gridSize: gridSize });

  // Dialog schließen
  settingsDialog.style.display = "none";

  // console.log(`Übernommen & gespeichert: Geschwindigkeit=${gameSpeed}ms, Grid=${gridSize}`);

  // Optional: Spiel neu starten / Loop anpassen
  // restartGameLoop();
});

async function logout() {
  //Name für das Objekt "Logiktoken".
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
      // localStorage.setItem("username", responseData.username);
      sessionStorage.removeItem("userData");
      window.location.reload();
    //   setTimeout(
    //     () => (window.location.href = "/frontend/snake/snake.html"),
    //     2000
    //   );
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
        "Authorization": "Bearer " + storedData,
      },
    });

    console.log("Response status:", response);

    if (response.ok) {
      // localStorage.setItem("username", responseData.username);
      sessionStorage.removeItem("userData");
      window.location.reload();
    //   setTimeout(
    //     () => (window.location.href = "/frontend/snake/snake.html"),
    //     2000
    //   );
    } else {
      alert("Fehler: Löschen fehlgeschlagen");
    }
  } catch (error) {
    alert("Fehler beim Verbinden mit dem Server: " + error.message);
  }
}

// document.getElementById("logoutBtn").addEventListener("click", logout);
