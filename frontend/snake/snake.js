// schlangen start variablen
let snakeColor = "red";
let isWaiting = false;
let snakeX = 0;
let snakeY = 0;
let snake = [{x: 0, y: 0},
  {x: -1, y: 0},
  {x: -2, y: 0}
];
let zwischen = [{}];

let storedDataForSnake = sessionStorage.getItem("userData");

let foodColor = "green";
let foodX = 10;
let foodY = 10;

let scale = 20; // hier skalieren felder

let direction = "Right";
let pause = "false";
let gameSpeedMs = 400;
let reloadgame = setInterval(game, gameSpeedMs); //Spiel geschwindigkeit
let score = 0;

let n = 0;
let u = 1;

function init() {
  const canvas = document.getElementById("gameCanvas");
  let canvash = (canvas.attributes.height.value);
  let canvasw = (canvas.attributes.width.value);
  //console.log(canvasw);
  //console.log(canvash);
  const playgroundh = parseInt(canvash) / scale;
  const playgroundw = parseInt(canvasw) / scale;

  //console.log(playgroundh);
  //console.log(playgroundw);
  const ctx = canvas.getContext("2d");
  ctx.fillStyle = "rgb(0 0 0)";
  let i = 0;
  while (-50 < canvasw) {
    canvasw = canvasw - playgroundw
    canvash = (canvas.attributes.height.value);
    canvash = canvash - (playgroundh);

    i++;
    if (i % 2 === 0) {
      canvash = canvash - playgroundh;
    }
    while (-50 < canvash) {
      ctx.fillRect(canvasw, canvash, playgroundw, playgroundh)
      canvash = canvash - (playgroundh * 2);
    }
  }

}

document.getElementById("saveSettings").addEventListener("click", () => {
  const newSpeed = parseInt(document.getElementById("speed").value, 10);
  const newScale = parseInt(document.getElementById("gridSize").value, 10);

  gameSpeedMs = newSpeed;
  scale = newScale;

  resetGameState()
  clearInterval(reloadgame);
  reloadgame = setInterval(game, gameSpeedMs);
});


function reload() {
  const canvas = document.getElementById("gameCanvas");
  const ctx = canvas.getContext("2d");
  ctx.clearRect(0, 0, canvas.width, canvas.height);
}

function game() {
  reload();
  init();

  //bewegen
  move(direction);

  //Schlange zeichnen
  snake_generate(snake);

  //Essen zeichnen
  food(foodX, foodY, foodColor);

  Snake_eat();
  checkWallCollision();
  checkSnakeCollision(snake);
}

//pop und unshift benutzen im array umbauen
function move(direction) {
  if (direction == "Up") {
    zwischen = snake[snake.length - 1];
    snake.pop();
    snakeY = snakeY - 1;
    snake.unshift({x: snakeX, y: snakeY});
    checkWallCollision();

    //console.log(snake);
  } else if (direction == "Right") {
    zwischen = snake[snake.length - 1];
    snake.pop();
    snakeX = snakeX + 1;

    snake.unshift({x: snakeX, y: snakeY});
    checkWallCollision();
  } else if (direction == "Down") {
    zwischen = snake[snake.length - 1];
    snake.pop();
    snakeY = snakeY + 1;

    snake.unshift({x: snakeX, y: snakeY});
    checkWallCollision();
  } else if (direction == "left") {
    zwischen = snake[snake.length - 1];
    snake.pop();
    snakeX = snakeX - 1;

    snake.unshift({x: snakeX, y: snakeY});
    checkWallCollision();
  }
}

function food(x, y, color) {

  const canvas = document.getElementById("gameCanvas");
  let canvash = (canvas.attributes.height.value);
  let canvasw = (canvas.attributes.width.value);
  const playgroundh = parseInt(canvash) / scale;
  const playgroundw = parseInt(canvasw) / scale;
  const ctx = canvas.getContext("2d");
  ctx.beginPath();
  ctx.rect(x * playgroundh, y * playgroundw, playgroundh, playgroundw);
  ctx.fillStyle = color;
  ctx.fill();
  ctx.closePath();

}

//array snake neu zeichnen
function snake_generate(snake) {
  let color = "red";
  const canvas = document.getElementById("gameCanvas");
  let canvash = (canvas.attributes.height.value);
  let canvasw = (canvas.attributes.width.value);
  const playgroundh = parseInt(canvash) / scale;
  const playgroundw = parseInt(canvasw) / scale;
  const ctx = canvas.getContext("2d");
  ctx.beginPath();
  while (n != snake.length) {
    ctx.rect(snake[n].x * playgroundh, snake[n].y * playgroundw, playgroundh, playgroundw);
    n = n + 1;
  }
  n = 0;
  ctx.fillStyle = color;
  ctx.fill();
  ctx.closePath();

}

function Snake_eat() {
  if (snakeX == foodX && snakeY == foodY) {
    score = score + 1;
    document.getElementById("Score").innerHTML = score;
    snake.push(zwischen);
    snake_generate(snake);
    generateNewFood();

  }
}

function generateNewFood() {
  foodX = Math.floor(Math.random() * scale);
  foodY = Math.floor(Math.random() * scale);

}

function gameOver() {
  clearInterval(reloadgame);
  alert("Verloren mit dem Score: " + score);
  resetGameState()
  reloadgame = setInterval(game, gameSpeedMs);
}

function checkWallCollision() {
  // in X direction
  //console.log("checkwallcolison x: "+snakeX+" y: "+snakeY);
  if (snakeX < 0 || snakeX > scale - 1) {
    console.log("Collision with snakeX: ", snakeX);
    gameOver();
  }

  // in Y direction
  if (snakeY < 0 || snakeY > scale - 1) {
    console.log("Collision with snakeY: ", snakeY);
    gameOver();
  }
}

function checkSnakeCollision() {
  u = 1;
  while (u != snake.length) {


    if (snake[u].x == snakeX && snake[u].y == snakeY) {
      gameOver();

    }
    u = u + 1;
  }
}

async function senddata() {


  let test = JSON.stringify({
    snakeposition: snake,
  });
  console.log("Response data:", test);

  const response = await fetch("http://localhost:8080/api/save", {
    method: "PATCH",
    headers: {
      "Content-Type": "application/json",
      Authorization: "Bearer " + storedDataForSnake,
    },
    body: JSON.stringify({
      snakeposition: snake,

    }),
  });

  if (response.ok) { // true for status 200-299
    alert("Save successful!");
  } else {
    alert("Save failed: " + response.status);
  }
}

if (storedDataForSnake) {
  let button = document.getElementById("saveButton");
  button.removeAttribute("hidden");
  button.color.white = "#FFFFFF";
}

function resetGameState() {
    snake = [{x: 0, y: 0},
        {x: -1, y: 0},
        {x: -2, y: 0}
    ];
    snakeX = 0;
    snakeY = 0;
    direction = "Right";
    pause = "false";
    score = 0;
    document.getElementById("Score").innerHTML = "Score: " + score;
    generateNewFood();
}