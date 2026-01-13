document.addEventListener("keydown", function (event) {
    //anpassung max
    const key = event.key;
     if (
      key === "w" || key === "W" ||
      key === "a" || key === "A" ||
      key === "s" || key === "S" ||
      key === "d" || key === "D" ||
      key === " " ||
      key === "ArrowUp" ||
      key === "ArrowDown" ||
      key === "ArrowLeft" ||
      key === "ArrowRight"
    ) {
      event.preventDefault();
    }
    //
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