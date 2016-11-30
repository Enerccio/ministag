package cz.upol.inf.vanusanik.ministag.schedule;

/**
 * Coordinate translator helper.
 * 
 * This helper class transforms between integer coordinate to real coordinated.
 * It supports subspaces push-ins.
 * @author enerccio
 *
 */
public class CoordinateTranslator {

	/**
	 * Represents inner section of whole 1.0x1.0 space
	 * @author enerccio
	 *
	 */
	private static class InnerSection {
		public final InnerSection parent;

		public final int w, h;
		public final int x, y;
		public final double wspace, hspace;

		public InnerSection(InnerSection parent, int x, int y, int w, int h) {
			this.parent = parent;
			this.x = x;
			this.y = y;
			this.w = w;
			this.h = h;
			this.wspace = 1.0 / w;
			this.hspace = 1.0 / h;
		}

		/**
		 * Encodes the real coordinate X into integer coordinate
		 * @param xp
		 * @return
		 */
		public double encodeX(double xp) {
			return doEncodeX(xp);
		}

		/**
		 * Transforms integer coordinate X into real coordinate
		 * @param absoluteX
		 * @return
		 */
		private double relativeX(int absoluteX) {
			return this.wspace * absoluteX;
		}

		private double doEncodeX(double xp) {
			return this.x + (this.w * xp);
		}

		/**
		 * Encodes the real coordinate Y into integer coordinate
		 * @param yp
		 * @return
		 */
		public double encodeY(double yp) {
			return doEncodeY(yp);
		}

		/**
		 * Transforms integer coordinate Y into real coordinate
		 * @param absoluteY
		 * @return
		 */
		private double relativeY(int absoluteY) {
			return this.hspace * absoluteY;
		}

		private double doEncodeY(double yp) {
			return this.y + (this.h * yp);
		}
	}

	/** Outer top bounds */
	private InnerSection top;

	/**
	 * Creates the coordinate translator with specified integer width/height
	 * @param w
	 * @param h
	 */
	public CoordinateTranslator(int w, int h) {
		top = new InnerSection(null, 0, 0, w, h);
	}

	/**
	 * Pushes subspace of specified real coordinates as new space
	 * @param xpos
	 * @param ypos
	 * @param w
	 * @param h
	 */
	public void push(double xpos, double ypos, double w, double h) {
		int ax = (int) Math.floor(top.doEncodeX(xpos));
		int ay = (int) Math.floor(top.doEncodeY(ypos));
		int aw = (int) Math.floor(top.doEncodeX(xpos + w));
		int ah = (int) Math.floor(top.doEncodeY(ypos + h));
		top = new InnerSection(top, ax, ay, aw - ax, ah - ay);
	}

	/**
	 * Removes last pushed subspace
	 */
	public void pop() {
		top = top.parent;
	}

	/**
	 * Encodes the real coordinate X into integer coordinate
	 * @param x
	 * @return
	 */
	public int encodeX(double x) {
		return (int) Math.floor(top.encodeX(x));
	}

	/**
	 * Encodes the real coordinate Y into integer coordinate
	 * @param y
	 * @return
	 */
	public int encodeY(double y) {
		return (int) Math.floor(top.encodeY(y));
	}

	/**
	 * Transforms integer coordinate X into real coordinate
	 * @param x
	 * @return
	 */
	public double relativeX(int x) {
		return top.relativeX(x);
	}

	/**
	 * Transforms integer coordinate Y into real coordinate
	 * @param y
	 * @return
	 */
	public double relativeY(int y) {
		return top.relativeY(y);
	}

	public static void main(String[] args) {
		// small tests
		CoordinateTranslator c = new CoordinateTranslator(1000, 100);
		System.out.println(c.encodeX(0.5));
		System.out.println(c.encodeY(0.5));

		c.push(0.25, 0, 0.5, 1);
		System.out.println(c.encodeX(0.5));
		System.out.println(c.encodeY(0.5));

		c.pop();

		c.push(0.1, 0, 0.1, 1);
		System.out.println(c.encodeX(0.5));
		System.out.println(c.encodeY(0.5));

		c.pop();

		c.push(0, 0, 0.5, 0.5);
		System.out.println(c.encodeX(0.5));
		System.out.println(c.encodeY(0.5));
		c.push(0, 0, 0.5, 0.5);
		System.out.println(c.encodeX(0.5));
		System.out.println(c.encodeY(0.5));
		c.pop();
		c.push(0.5, 0.5, 0.5, 0.5);
		System.out.println(c.encodeX(0.5));
		System.out.println(c.encodeY(0.5));
	}
}
