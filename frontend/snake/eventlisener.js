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
        key === "Space"
    ) {
        event.preventDefault();
    }
    //
    if (event.key === "w" || event.key === "W" || event.key === "ArrowUp") {
        if (direction == "Down") {

        } else {
            direction = "Up"
        }

    }
    if (event.key === "A" || event.key === "a" || event.key === "ArrowLeft") {
        if (direction == "Right") {

        } else {
            direction = "left"
        }

    }
    if (event.key === "S" || event.key === "s" || event.key === "ArrowDown") {
        if (direction == "Up") {

        } else {
            direction = "Down"
        }

    }
    if (event.key === "D" || event.key === "d" || event.key === "ArrowRight") {
        if (direction == "left") {

        } else {
            direction = "Right"
        }
    }

    if (event.key === " ") {
        if (pause === "true") {
            console.log("gamespeed", gameSpeedMs)
            reloadgame = setInterval(game, gameSpeedMs);
            pause = "false";
        } else {
            console.log("reloadgame", reloadgame)
            clearInterval(reloadgame);
            pause = "true";
        }
    }
});