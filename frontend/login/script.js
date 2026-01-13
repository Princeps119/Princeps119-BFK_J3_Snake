async function buttoncklicked(id) {
  let button = document.getElementById(id);
  console.log(button.value);
  let schluessel = document.getElementById("user_pw");
  console.log(schluessel.value);
  const e_mail = button.value;
  if (!e_mail.includes("@")) {
    console.log("kein @ da");
    error(1);
    return;
  } else {
    error_cleaner(1);
  }
  if (schluessel.value == "") {
    console.log("Passwort eingeben");
    error(2);
    return;
  } else {
    error_cleaner(2);
  }

  //login anbindung max
  try {
    const response = await fetch("http://localhost:8080/api/login", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify({
        mail: button.value,
        password: schluessel.value,
      }),
    });

    console.log("Response status:", response);

    if (response.ok) {
      const responseData = await response.json();
      // localStorage.setItem("username", responseData.username);
      sessionStorage.setItem("userData", JSON.stringify(responseData));

      console.log(responseData);
      setTimeout(
        () => (window.location.href = "/frontend/snake/snake.html"),
        2000
      );
    } else {
      alert("Fehler: Anmeldung fehlgeschlagen");
    }
  } catch (error) {
    alert("Fehler beim Verbinden mit dem Server: " + error.message);
  }
}
//errorr sachen ab hir einmal anschallten
function error(error_code) {
  switch (error_code) {
    case 1:
      error_element = document.getElementById("error_massage_email");
      error_element.hidden = false;
      error_element.innerHTML = "Kein @vorhanden";
      break;
    case 2:
      error_element = document.getElementById("error_massage_pw");
      error_element.hidden = false;
      error_element.innerHTML = "kein Passwort eingegeben";
      break;
  }
}
// und hier ausschalten
function error_cleaner(error_code) {
  switch (error_code) {
    case 1:
      error_element = document.getElementById("error_massage_email");
      error_element.hidden = true;
      break;
    case 2:
      error_element = document.getElementById("error_massage_pw");
      error_element.hidden = true;
      break;
  }
}
