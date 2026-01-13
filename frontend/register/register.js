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
          mail: emailInput.value,
          password: passwordInput.value,
        }),
      });

      if (response.ok) {
        successMsg.style.display = "block";
        form.reset();
        setTimeout(() => (window.location.href = "/login/login.html"), 1000);
      } else {
        alert("Fehler: Registrierung fehlgeschlagen");
      }
    } catch (error) {
      alert("Fehler beim Verbinden mit dem Server: " + error.message);
    }
  }
});
