function buttoncklicked(id) {
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

  /*fetch("https://serververbindung"),{

        method: "POST",
        body: JSON.stringify({
            user_name: button.value,
            user_key: schluessel.value,
        }),
        headers: {
        "Content-type": "application/json; charset=UTF-8"
        }
    }*/

  console.log(
    JSON.stringify({
      user_mail: button.value,
      user_pw: schluessel.value,
    })
  );
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
