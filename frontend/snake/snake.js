
// schlangen start variablen
let snakeColor = "red";
let snakeX = 0;
let snakeY = 0;
let snake_length = 0;
let foodColor = "green";
let foodX = 10;
let foodY = 10;

let scale = 20; // hier skallieren felder

let direction = "Right";

let relodegame = setInterval(game, 500); //Spiel geschindigkeit
let score = 0;
function init() {
  const canvas = document.getElementById("x");
  let canvash = canvas.attributes.height.value;
  let canvasw = canvas.attributes.width.value;
  //console.log(canvasw);
  //console.log(canvash);
  const playgroundh = parseInt(canvash) / scale;
  const playgroundw = parseInt(canvasw) / scale;

  //console.log(playgroundh);
  //console.log(playgroundw);
  const ctx = canvas.getContext("2d");
  ctx.fillStyle = "rgb(0 0 0)";
  let i = 1;
  while (-50 < canvasw) {
    canvasw = canvasw - playgroundw;
    canvash = canvas.attributes.height.value;
    canvash = canvash - playgroundh;

    i++;
    if (i % 2 === 0) {
      canvash = canvash - playgroundh;
    }
    while (-50 < canvash) {
      ctx.fillRect(canvasw, canvash, playgroundw, playgroundh);
      canvash = canvash - playgroundh * 2;
    }
  }
}
function relode() {
  const canvas = document.getElementById("x");
  const ctx = canvas.getContext("2d");
  ctx.clearRect(0, 0, canvas.width, canvas.height);
}

function game() {
  relode();
  init();
  //Schlange zeichnen
  snake_food(snakeX, snakeY, snakeColor);
  //bewegen
  move(direction);
  //Essen zeichnen
  snake_food(foodX, foodY, foodColor);

  Snake_eat();
  checkWallCollision();
}

function move(direction) {
  if (direction == "Up") {
    snakeY = snakeY - 1;
  } else if (direction == "Right") {
    snakeX = snakeX + 1;
  } else if (direction == "Down") {
    snakeY = snakeY + 1;
  } else if (direction == "left") {
    snakeX = snakeX - 1;
  }
}

function snake_food(x, y, color) {
  const canvas = document.getElementById("x");
  let canvash = canvas.attributes.height.value;
  let canvasw = canvas.attributes.width.value;
  const playgroundh = parseInt(canvash) / scale;
  const playgroundw = parseInt(canvasw) / scale;
  const ctx = canvas.getContext("2d");
  ctx.beginPath();
  ctx.rect(x * playgroundh, y * playgroundw, playgroundh, playgroundw);
  ctx.fillStyle = color;
  ctx.fill();
  ctx.closePath();
}

function Snake_eat() {
  if (snakeX == foodX && snakeY == foodY) {
    score = score + 1;
    document.getElementById("Score").innerHTML = "Score: " + score;
    generateNewFood();

    if (snake_length < 0) {
      if (direction == "Up") {
        snake_new_Y_Up = snake_new_Y_Up - 1;
        snake_food(snake_new_Y_Up, snakeX, snakeColor);
      } else if (direction == "Right") {
        snake_new_X = snake_new_X + 1;
      } else if (direction == "Down") {
        snake_new_Y = snake_new_Y + 1;
      } else if (direction == "left") {
        snake_new_x_down = snake_new_x_down - 1;
      }
      snake_length = snake_length + 1;
    } else {
      if (direction == "Up") {
        snake_new_Y_Up = snakeY - 1;
      } else if (direction == "Right") {
        snake_new_X = snakeX + 1;
      } else if (direction == "Down") {
        snake_new_Y = snakeY + 1;
      } else if (direction == "left") {
        snake_new_x_down = snakeX - 1;
      }
      snake_length = snake_length + 1;
    }
  }
}

function generateNewFood() {
  foodX = Math.floor(Math.random() * scale);
  foodY = Math.floor(Math.random() * scale);
}

function gameOver() {
  clearInterval(relodegame);
  alert("Verloren mit dem Score: " + score);
}

function checkWallCollision() {
  // in X direction
  if (snakeX < -1 || snakeX > scale) {
    gameOver();
  }

  // in Y direction
  if (snakeY < -1 || snakeY > scale) {
    gameOver();
  }
}
