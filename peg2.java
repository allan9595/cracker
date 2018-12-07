import java.util.*;

public class peg2 {

	final public static int SIZE = 5;
	final public static boolean DEBUG = false;

	// Bottom middle.
	final public static int HOLE_R = 4;
	final public static int HOLE_C = 2;

	// Stores pairs of the form (board position, previous board position).
	public static HashMap<Integer,Integer> prev;

	// Possible jump directions.
	final public static int[] DX = {-1,-1,0,0,1,1};
	final public static int[] DY = {-1,0,-1,1,0,1};

	public static void main(String[] args) {

		// Change this to be whereever you want the hole.
		int start = initBoard(HOLE_R, HOLE_C);
		int bestEnd = getBestEnd(HOLE_R, HOLE_C);

		// Starting position.
		prev = new HashMap<Integer,Integer>();
		prev.put(start, start);

		// Queue for BFS.
		LinkedList<Integer> q = new LinkedList<Integer>();
		q.offer(start);

		// Run BFS.
		while (q.size() > 0) {

			// Get next move.
			int cur = q.poll();

			// Get all possible next moves.
			ArrayList<Integer> nextList = getNextPos(cur);

			// Apply them - only add if overall board position is unique.
			for (int i=0; i<nextList.size(); i++) {
				if (!prev.containsKey(nextList.get(i))) {
					prev.put(nextList.get(i), cur);
					q.offer(nextList.get(i));
				}
			}
		}

		// Only print full path of jumps for the perfect ending position
		// (single peg left in initially empty hole.)
		if (prev.containsKey(bestEnd)) {

			ArrayList<Integer> path = buildpath(prev, bestEnd);

			// Go through each move.
			for (int i=0; i<path.size(); i++) {

				// Print this move.
				print(path.get(i));
				System.out.println();

				// Wait one second.
				long cur = System.currentTimeMillis();
				while (System.currentTimeMillis() - cur < 1000);
			}
		}

		// Error message.
		else
			System.out.println("Sorry, there's no way to win this setting of the game.");
	}

	// Returns the path of moves ending in end, dictated by previous moves stored in prev.
	public static ArrayList<Integer> buildpath(HashMap<Integer,Integer> prev, int end) {

		// Set up our list.
		ArrayList<Integer> res = new ArrayList<Integer>();
		int cur = end;

		// Keep on going till the first item, adding to front each time.
		while (prev.get(cur) != cur) {
			res.add(0, cur);
			cur = prev.get(cur);
		}

		// Here is our whole path.
		return res;
	}

	// Returns the mask of a full board with only an empty hole at (holeR, holeC).
	public static int initBoard(int holeR, int holeC) {

		// This is the full board.
		int mask = 0;
		for (int i=0; i<SIZE; i++)
			for (int j=0; j<=i; j++)
				mask = mask | (1<<(SIZE*i+j));

		// Just subtract out the current hole.
		return mask - (1<<(SIZE*holeR+holeC));
	}

	// Returns the ideal ending board for a starting board with hole at (holeR, holeC).
	public static int getBestEnd(int holeR, int holeC) {
		return (1<<(SIZE*holeR+holeC));
	}

	// Returns all possible future positions from the board position represented by mask.
	public static ArrayList<Integer> getNextPos(int mask) {

		ArrayList<Integer> pos = new ArrayList<Integer>();

		// Try each potential starting position.
		for (int r =0; r<SIZE; r++) {
			for (int c=0; c<=SIZE; c++) {

				// Now try each move.
				for (int dir=0; dir<DX.length; dir++) {

					// Ending square is out of bounds.
					if (!inbounds(r+2*DX[dir], c+2*DY[dir])) continue;

					// A move is valid only if the first two holes have pegs and the destination doesn't.
					if (on(mask, SIZE*r+c) && on(mask, SIZE*(r+DX[dir]) + c + DY[dir]) && !on(mask, SIZE*(r+2*DX[dir]) + c + 2*DY[dir])) {
						int newpos = apply(mask, dir, r, c);
						pos.add(newpos);
					}
				}
			}
		}

		// Here is a list of all the possible moves.
		return pos;
	}

	// Prints the board position corresponding to mask with an extra blank line at the end.
	public static void print(int mask) {

		// Go through each row.
		for (int i=0; i<SIZE; i++) {

			// To make it look like a triangle.
			for (int j=0; j<SIZE-1-i; j++) System.out.print(" ");

			// Here are the actual holes.
			for (int j=0; j<=i; j++) {
				if (on(mask, SIZE*i+j)) System.out.print("X ");
				else					System.out.print("_ ");
			}
			System.out.println();
		}
		System.out.println();
	}

	// Returns the result of appplying the move from (r,c) in direction dir on the board stored in mask.
	public static int apply(int mask, int dir, int r, int c) {

		// Get the bit location of the starting hole, middle hole and ending hole.
		int start = SIZE*r + c;
		int mid = SIZE*(r+DX[dir]) + c + DY[dir];
		int end = SIZE*(r+2*DX[dir]) + c + 2*DY[dir];

		// We get rid of the start and midding and add in the end to apply a jump.
		return mask - (1<<start) - (1<<mid) + (1<<end);
	}

	// Returns true iff bit is on in mask.
	public static boolean on(int mask, int bit) {
		return (mask & (1<<bit)) != 0;
	}

	// Returns true iff (myr, myc) is inbounds.
	public static boolean inbounds(int myr, int myc) {
		return myr >= 0 && myr < SIZE && myc >= 0 && myc <= myr;
	}
}
