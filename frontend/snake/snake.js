
// schlangen start variablen
let snakeColor = "red";
let snakeX = 0;
let snakeY = 0;
let snake_length = 0;
let foodColor = "green";
let foodX = 10;
let foodY = 10;


let direction = "Right";

let scale = 20;                
let gameSpeedMs = 400;         
let relodegame = null;         
function startGameLoop() {
  if (relodegame !== null) clearInterval(relodegame);
  relodegame = setInterval(game, gameSpeedMs);
}

startGameLoop();


let score = 0;
function init() {
  const canvas = document.getElementById("gameCanvas");
  let canvash = canvas.attributes.height.value;
  let canvasw = canvas.attributes.width.value;
  const playgroundh = parseInt(canvash) / scale;
  const playgroundw = parseInt(canvasw) / scale;

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
  const canvas = document.getElementById("gameCanvas");
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
  const canvas = document.getElementById("gameCanvas");
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

document.getElementById("saveSettings").addEventListener("click", () => {
  const newSpeed = parseInt(document.getElementById("speed").value, 10);
  const newScale = parseInt(document.getElementById("gridSize").value, 10);

  gameSpeedMs = newSpeed;
  scale = newScale;

  resetGameState();

  startGameLoop();
});

function resetGameState() {
  snakeX = 0;
  snakeY = 0;
  direction = "Right";
  score = 0;
  document.getElementById("Score").innerHTML = "Score: " + score;

  generateNewFood();
}


