package data;

/**
 * used to combine Highscore and Snakeposition, when the frontend gives me both
 * @param highscore {@link Highscore}
 * @param snakePositionData {@link SnakePositionData}
 */
public record GameSave(Highscore highscore, SnakePositionData snakePositionData) {
}
