/*
	Jialing Li and Kevin Solis
*/
package pathfinder.informed;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Queue;
import java.util.LinkedList;
import java.util.Map;
import java.util.*;

/**
 * Maze Pathfinding algorithm that implements a basic, uninformed, breadth-first
 * tree search.
 */
public class Pathfinder {

	/**
	 * Given a MazeProblem, which specifies the actions and transitions available in
	 * the search, returns a solution to the problem as a sequence of actions that
	 * leads from the initial to a goal state.
	 * 
	 * @param problem A MazeProblem that specifies the maze, actions, transitions.
	 * @return An ArrayList of Strings representing actions that lead from the
	 *         initial to the goal state, of the format: ["R", "R", "L", ...]
	 */

	public static ArrayList<String> solve(MazeProblem problem) {

		// [!] Note: Hyper-Commenting below for illustrative purposes; you should not
		// have
		// had / needed nearly as much as demonstrated below

		// Implementing BFS, so frontier is a Queue (which, in JCF, is an interface that
		// can be used atop a LinkedList implementation)

		ArrayList<String> initialToKeyPath = solveForPath(problem, true);
		if (initialToKeyPath==null) {
			return null;
		}
		
		ArrayList<String> keyPathToGoalPath = solveForPath(problem, false);
		
		System.out.println(keyPathToGoalPath);
		ArrayList<String> combinedPath = new ArrayList<String>(initialToKeyPath);
		combinedPath.addAll(keyPathToGoalPath);
		
		System.out.println(combinedPath);
		return combinedPath;

	}

	private static ArrayList<String> solveForPath(MazeProblem problem, boolean lookingForKey) {
		// Continue expanding nodes as long as the frontier is not empty
		// (not strictly necessary for this assignment because a solution was
		// always assumed to be

		Map<MazeState, SearchTreeNode> graveyard = new Hashtable<>();
		PriorityQueue<SearchTreeNode> frontier = new PriorityQueue<SearchTreeNode>(10,
			new Comparator<SearchTreeNode>() {
				public int compare(SearchTreeNode first, SearchTreeNode second) {
					Integer intOne = first.cost;
					Integer intTwo = second.cost;
					return intOne.compareTo(intTwo);
				}
			});
		
		if (lookingForKey) {
			frontier.add(new SearchTreeNode(problem.INITIAL_STATE, null, null,
					getFutureCostKey(problem, problem.INITIAL_STATE, problem.KEY_STATE)));
		} else {
			frontier.add(new SearchTreeNode(problem.KEY_STATE, null, null,
					getFutureCost(problem, problem.KEY_STATE, problem.GOAL_STATE.get(0))));
		}
		

		while (!frontier.isEmpty()) {
			// System.out.println("hello");
			// Grab the front node of the queue - this is the node we're expanding
			SearchTreeNode expanding = frontier.poll();// ADD to graveyard!!!! right after polled it.

			// graveyard.add((expanding.state.row.toString() +
			// problem.state.col.toString()).parseInt());
			// put (maze state, node)
			graveyard.put(expanding.state, expanding);

			System.out.println("x, y is " + expanding.state.row + " " + expanding.state.col);
			// If it's a goal state, we're done!
			if (lookingForKey) {
				if (problem.hasKey(expanding.state)) {
					return getPath(expanding);
				}
				
			}else if (problem.isGoal(expanding.state)) {
                return getPath(expanding);
            }
			

			// Otherwise, must generate children
			Map<String, MazeState> transitions = problem.getTransitions(expanding.state);
			// For each action:MazeState pair in the transitions...
			for (Map.Entry<String, MazeState> transition : transitions.entrySet()) {
				System.out.println(transition);
				// ...create a new STN and add that to the frontier
				if (graveyard.get(transition.getValue()) == null) {
					if (lookingForKey) {
						frontier.add(new SearchTreeNode(transition.getValue(), transition.getKey(), expanding,
								getFutureCostKey(problem, expanding.state, problem.KEY_STATE)));
					}else {
						frontier.add(new SearchTreeNode(transition.getValue(), transition.getKey(), expanding,
								getFutureCost(problem, expanding.state, problem.GOAL_STATE.get(0))));
					}
				}
			}
		}
		return null;
	}

	
	/**
	 * Given a leaf node in the search tree (a goal), returns a solution by
	 * traversing up the search tree, collecting actions along the way, until
	 * reaching the root
	 * 
	 * @param last SearchTreeNode to start the upward traversal at (a goal node)
	 * @return ArrayList sequence of actions; solution of format ["U", "R", "U",
	 *         ...]
	 */
	private static ArrayList<String> getPath(SearchTreeNode last) {
		ArrayList<String> result = new ArrayList<>();
		for (SearchTreeNode current = last; current.parent != null; current = current.parent) {
			result.add(current.action);
		}
		Collections.reverse(result);
		return result;
	}
	
	
	public static int getFutureCostKey(MazeProblem problem, MazeState state, MazeState endState) {
		
		int cost = 0;
		int storage = Math.abs(problem.KEY_STATE.row - state.row) + Math.abs(problem.KEY_STATE.col - state.col);
		if (storage < cost) {
			System.out.println("go off");
			cost = storage;
			
		}
		
		System.out.println(storage);
		return storage;
	}

			
		
	public static int getFutureCost(MazeProblem problem, MazeState state, MazeState endState) { // ask for correct
																								// formatting
		int cost = 0;
		int storage;
//		for (int i = 0; i < problem.GOAL_STATE.size(); i++) {
//			storage = Math.abs(problem.GOAL_STATE.get(i).row - state.row) + Math.abs(problem.GOAL_STATE.get(i).col - state.col);
//			if (storage < cost) {
//				cost = storage;
//			}
//		}
//		return storage;

		storage = Math.abs(problem.GOAL_STATE.get(0).row - state.row) + Math.abs(problem.GOAL_STATE.get(0).col - state.col);
	
		return storage;
	}

}

/**
 * SearchTreeNode that is used in the Search algorithm to construct the Search
 * tree.
 */
class SearchTreeNode {

	MazeState state;
	String action;
	SearchTreeNode parent;
	int cost;

	/**
	 * Constructs a new SearchTreeNode to be used in the Search Tree.
	 * 
	 * @param state  The MazeState (row, col) that this node represents.
	 * @param action The action that *led to* this state / node.
	 * @param parent Reference to parent SearchTreeNode in the Search Tree.
	 */
	SearchTreeNode(MazeState state, String action, SearchTreeNode parent, int ManhattanH) {
		this.state = state;
		this.action = action;
		this.parent = parent;
		this.cost = ManhattanH;
	}

}