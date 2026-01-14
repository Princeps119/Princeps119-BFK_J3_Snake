package data;

import java.util.List;
/**
 * Represents the current state of the snake in the game.
 *
 * @param snakeposition  a list of {@link Position} objects, where each Position holds the x and y coordinates
 *              of a segment of the snake's body in order from head to tail.
 */
//todo depending if Frontend gets its act together use this or adapt it for the received data
public record SnakePositionData(List<Position> snakeposition) {
}
