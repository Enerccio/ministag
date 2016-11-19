package cz.upol.inf.vanusanik.ministag.schedule;

public class CoordinateTranslator {

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

		public double encodeX(double xp) {
			return doEncodeX(xp);
		}

		private double relativeX(int absoluteX) {
			return this.wspace * absoluteX;
		}

		private double doEncodeX(double xp) {
			return this.x + (this.w * xp);
		}

		public double encodeY(double yp) {
			return doEncodeY(yp);
		}

		private double relativeY(int absoluteY) {
			return this.hspace * absoluteY;
		}

		private double doEncodeY(double yp) {
			return this.y + (this.h * yp);
		}
	}

	private InnerSection top;

	public CoordinateTranslator(int w, int h) {
		top = new InnerSection(null, 0, 0, w, h);
	}

	public void push(double xpos, double ypos, double w, double h) {
		int ax = (int) Math.floor(top.doEncodeX(xpos));
		int ay = (int) Math.floor(top.doEncodeY(ypos));
		int aw = (int) Math.floor(top.doEncodeX(xpos + w));
		int ah = (int) Math.floor(top.doEncodeY(ypos + h));
		top = new InnerSection(top, ax, ay, aw - ax, ah - ay);
	}

	public void pop() {
		top = top.parent;
	}

	public int encodeX(double x) {
		return (int) Math.floor(top.encodeX(x));
	}

	public int encodeY(double y) {
		return (int) Math.floor(top.encodeY(y));
	}

	public double relativeX(int x) {
		return top.relativeX(x);
	}

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
