package data;

import java.util.List;
/**
 * Represents the current state of the snake in the game.
 *
 * @param snakeposition  a list of {@link Position} objects, where each Position holds the x and y coordinates
 *              of a segment of the snake's body in order from head to tail.
 */
public record SnakePositionData(List<Position> snakeposition, Settings settings, int highscore) {
}
