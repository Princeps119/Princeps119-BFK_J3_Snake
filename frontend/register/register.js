const form = document.getElementById("registerForm");
const usernameInput = document.getElementById("username");
const emailInput = document.getElementById("email");
const passwordInput = document.getElementById("password");
const confirmPasswordInput = document.getElementById("confirmPassword");
const successMsg = document.getElementById("successMsg");

form.addEventListener("submit", async (e) => {
  e.preventDefault();

  let isValid = true;
  document
    .querySelectorAll(".error")
    .forEach((err) => (err.style.display = "none"));
  successMsg.style.display = "none";

  if (usernameInput.value.length < 3) {
    document.getElementById("usernameError").style.display = "block";
    isValid = false;
  }

  if (!emailInput.value.includes("@")) {
    document.getElementById("emailError").style.display = "block";
    isValid = false;
  }

  if (passwordInput.value.length < 6) {
    document.getElementById("passwordError").style.display = "block";
    isValid = false;
  }

  if (passwordInput.value !== confirmPasswordInput.value) {
    document.getElementById("confirmPasswordError").style.display = "block";
    isValid = false;
  }

  if (isValid) {
    try {
      const response = await fetch("http://localhost:8080/api/register", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({
          username: usernameInput.value,
          email: emailInput.value,
          password: passwordInput.value,
        }),
      });

      const data = await response.json();

      if (response.ok) {
        successMsg.style.display = "block";
        form.reset();
        localStorage.setItem("username", result.username);
        //token wird zurückgesendet vom backend. und dieser wird dann benutzt, damit man sich nicht nochmal anmelden muss.
        //logik muss hier noch eingebunden werden.
        setTimeout(() => (window.location.href = "snake.html"), 2000);
      } else {
        alert("Fehler: " + (data.message || "Registrierung fehlgeschlagen"));
      }
    } catch (error) {
      alert("Fehler beim Verbinden mit dem Server: " + error.message);
    }
  }
});
