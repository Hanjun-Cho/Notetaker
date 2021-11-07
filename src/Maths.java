public class Maths {
	public static float Lerp(float a, float b, float t) {
		return (float)((1.0 - t) * a + b * t);
	}
}
