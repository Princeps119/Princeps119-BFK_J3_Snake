document.addEventListener("keydown", function (event) {
    // Prüfen, ob die Taste "w" oder "W" gedrückt wurde
    if (event.key === "w" || event.key === "W" || event.key === "ArrowUp") {
        direction="Up"
    }
    if (event.key === "A" || event.key === "a" || event.key === "ArrowLeft") {
        direction="left"
    }
    if (event.key === "S" || event.key === "s" || event.key === "ArrowDown") {
        direction="Down"
    }
    if (event.key === "D" || event.key === "d" || event.key === "ArrowRight") {
        direction="Right"
    }
    game();
});